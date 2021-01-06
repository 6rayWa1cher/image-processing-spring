package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.repository.MouseOnImageRepository;
import com.a6raywa1cher.imageprocessingspring.repository.model.ClickedMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedAreaMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedPointMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.service.SelectionService;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class SelectionServiceImpl implements SelectionService {
	private final MouseOnImageRepository repository;

	public SelectionServiceImpl(MouseOnImageRepository repository) {
		this.repository = repository;
	}

	private static Point2D getNearestAtBox(Point2D p, SelectedAreaMouseInnerEvent select) {
		return getAtBox(p, select, true);
	}

	private static Point2D getFarthestAtBox(Point2D p, SelectedAreaMouseInnerEvent select) {
		return getAtBox(p, select, false);
	}

	private static Point2D getAtBox(Point2D p, SelectedAreaMouseInnerEvent select, boolean nearest) {
		Point2D p1 = select.getP1(), p2 = select.getP2();

		double minX = Math.min(p1.getX(), p2.getX());
		double maxX = Math.max(p1.getX(), p2.getX());
		double minY = Math.min(p1.getY(), p2.getY());
		double maxY = Math.max(p1.getY(), p2.getY());

		Stream<Point2D> s = Stream.of(
			new Point2D(minX, minY),
			new Point2D(minX, maxY),
			new Point2D(maxX, minY),
			new Point2D(maxX, maxY)
		);

		Comparator<Point2D> c = Comparator.comparingDouble(p::distance);

		return (nearest ? s.min(c) : s.max(c)).orElseThrow();
	}

	@Override
	public void processEvent(MouseEvent event) {
		MouseInnerEvent head = repository.getHead();

		Point2D eventPoint = new Point2D(event.getX(), event.getY());
		EventType eventType = event.getEventType().equals(MouseEvent.MOUSE_PRESSED) ? EventType.PRESSED :
			event.getEventType().equals(MouseEvent.MOUSE_DRAGGED) ? EventType.DRAG :
				EventType.RELEASED;
		boolean isPrimaryKey = event.isPrimaryButtonDown();

		if (head == null || head instanceof ClickedMouseInnerEvent) {
			repository.pushEvent(new SelectedAreaMouseInnerEvent(
				eventPoint, eventPoint, isPrimaryKey, true
			));
		} else if (head instanceof SelectedAreaMouseInnerEvent) {
			SelectedAreaMouseInnerEvent headEvent = (SelectedAreaMouseInnerEvent) head;
			if (headEvent.isTemporaryChange()) {
				if (headEvent.isPrimaryKey() != isPrimaryKey &&
					!(eventType == EventType.RELEASED && !event.isPrimaryButtonDown() && !event.isSecondaryButtonDown())) {
					return; // wrong key event
				}

				if (eventType == EventType.RELEASED && headEvent.getP1().equals(headEvent.getP2())) {
					repository.pushEvent(new ClickedMouseInnerEvent(headEvent.isPrimaryKey()));
				} else if (eventType == EventType.RELEASED) {
					repository.pushEvent(headEvent.withTemporaryChange(false));
				} else if (eventType == EventType.DRAG) {
					repository.pushEvent(headEvent.withP2(eventPoint));
				}
			} else {
				if (eventPoint.distance(getNearestAtBox(eventPoint, headEvent)) <= 2d) {
					repository.pushEvent(new SelectedAreaMouseInnerEvent(
						getFarthestAtBox(eventPoint, headEvent), eventPoint, isPrimaryKey, true)
					);
				} else {
					repository.pushEvent(new SelectedPointMouseInnerEvent(
						eventPoint, isPrimaryKey
					));
				}
			}
		} else if (head instanceof SelectedPointMouseInnerEvent) {
			SelectedPointMouseInnerEvent headEvent = (SelectedPointMouseInnerEvent) head;
			if (headEvent.isPrimaryKey() != isPrimaryKey &&
				!(eventType == EventType.RELEASED && !event.isPrimaryButtonDown() && !event.isSecondaryButtonDown())) {
				return; // wrong key event
			}
			if (eventType == EventType.DRAG) {
				repository.pushEvent(new SelectedAreaMouseInnerEvent(
					headEvent.getP(), eventPoint, headEvent.isPrimaryKey(), true
				));
			} else if (eventType == EventType.RELEASED) {
				repository.pushEvent(new ClickedMouseInnerEvent(headEvent.isPrimaryKey()));
			}
		}
	}


//	public void processEvent(MouseEvent event) {
//		if (event.isPrimaryButtonDown() == event.isSecondaryButtonDown()) return;
//
//		EventType eventType = event.getEventType().equals(MouseEvent.MOUSE_PRESSED) ? EventType.PRESSED :
//			event.getEventType().equals(MouseEvent.MOUSE_DRAGGED) ? EventType.DRAG :
//				EventType.RELEASED;
//		Point2D eventPoint = new Point2D(event.getX(), event.getY());
//
//		boolean isPrimaryKey = event.isPrimaryButtonDown();
//
//		if (currSelect != null && isPrimaryKey != currSelect.isPrimaryKey() && state != 3) return;
//
//		Select beforePrev = prevSelect, beforeCurr = currSelect;
//
//		switch (state) {
//			default -> {
//				if (eventType == EventType.PRESSED) {
//					prevSelect = null;
//					currSelect = new Select(eventPoint, isPrimaryKey);
//					state = 1;
//				}
//			}
//			case 1 -> {
//				if (eventType == EventType.DRAG) {
//					currSelect = currSelect.withP2(eventPoint);
//					state = 2;
//				} else if (eventType == EventType.RELEASED) {
//					prevSelect = null;
//					currSelect = null;
//					state = 0;
//				}
//			}
//			case 2 -> {
//				if (eventType == EventType.DRAG) {
//					currSelect = currSelect.withP2(eventPoint);
//					state = 2;
//				} else if (eventType == EventType.RELEASED) {
//					currSelect = currSelect.withP2(eventPoint);
//					state = 3;
//				}
//			}
//			case 3 -> {
//				if (eventType == EventType.PRESSED) {
//					prevSelect = currSelect;
//					if (eventPoint.distance(getNearestAtBox(eventPoint, currSelect)) <= 2d) {
//						currSelect = new Select(getFarthestAtBox(eventPoint, currSelect), eventPoint, isPrimaryKey);
//					} else {
//						currSelect = new Select(eventPoint, isPrimaryKey);
//					}
//					state = 1;
//				}
//			}
//		}
//
//		if (!Objects.equals(beforePrev, prevSelect) || !Objects.equals(beforeCurr, currSelect)) {
//			publishEvent();
//		}
//	}

	private enum EventType {
		PRESSED, DRAG, RELEASED
	}


}
