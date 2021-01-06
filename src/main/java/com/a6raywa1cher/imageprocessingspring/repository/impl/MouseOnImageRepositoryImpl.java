package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.SelectChangedEvent;
import com.a6raywa1cher.imageprocessingspring.repository.MouseOnImageRepository;
import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedAreaMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedPointMouseInnerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class MouseOnImageRepositoryImpl implements MouseOnImageRepository {
	private final Deque<MouseInnerEvent> storage;
	private final ApplicationEventPublisher publisher;

	public MouseOnImageRepositoryImpl(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
		storage = new LinkedList<>();
	}

	@Override
	public void pushEvent(MouseInnerEvent event) {
		synchronized (storage) {
			while (storage.size() > 10) {
				storage.pollLast();
			}
			if (!storage.isEmpty()) {
				MouseInnerEvent head = storage.peek();
				if (head instanceof SelectedAreaMouseInnerEvent &&
					((SelectedAreaMouseInnerEvent) head).isTemporaryChange()) {
					storage.poll();
				} else if (head instanceof SelectedPointMouseInnerEvent) {
					storage.poll();
				}
			}
			storage.push(event);
			publisher.publishEvent(new SelectChangedEvent(
				this, storage.stream().collect(Collectors.toUnmodifiableList())
			));
			log.debug("\n\t\t\t" + String.join("\n\t\t\t", storage.stream().map(Object::toString).collect(Collectors.toUnmodifiableList())));
		}
	}

	@Override
	public MouseInnerEvent getHead() {
		synchronized (storage) {
			return storage.peek();
		}
	}
}
