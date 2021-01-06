package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ScalingAlgorithmModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.ScalingConfig;
import com.a6raywa1cher.imageprocessingspring.model.SelectConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.stream.Stream;

// MOUSE_PRESSED, MOUSE_DRAGx(1+), MOUSE_RELEASED
@Controller
@Slf4j
public class ImageController {
	private final ImageProcessingService service;
	public ImageView image;
	private int selectAutomataState;
	private Point2D p1 = new Point2D(0, 0), p2 = new Point2D(0, 0), p3 = new Point2D(0, 0);
	private ScalingConfig.ScalingAlgorithm scalingAlgorithm;
	private boolean secondaryKey;
	private Color color;
	private volatile boolean updating;

	@Autowired
	public ImageController(ImageProcessingService service) {
		this.service = service;
	}

	public void initialize() {
		image.setOnMousePressed(this::onMousePressed);
		image.setOnMouseDragged(this::onMouseDragged);
		image.setOnMouseReleased(this::onMouseReleased);
	}

	private void onMousePressed(MouseEvent event) {
		log.debug("onMousePressed " + event.toString());
		if (!isPointInImage(event.getX(), event.getY())) {
			return;
		}
		Point2D eventPoint = new Point2D(event.getX(), event.getY());
		if (selectAutomataState == 2 &&
			eventPoint.distance(getNearestToBox(eventPoint)) <= 3d
		) {
			secondaryKey = event.isSecondaryButtonDown();
			Point2D farthestToBox = getFarthestToBox(eventPoint);
			Point2D nearestToBox = getNearestToBox(eventPoint);
			p1 = farthestToBox;
			p3 = p2 = nearestToBox;
			selectAutomataState = 0;
		} else if ((selectAutomataState == 0 || selectAutomataState == 2) && !event.isSecondaryButtonDown()) {
			secondaryKey = event.isSecondaryButtonDown();
			this.p1 = eventPoint;
			selectAutomataState = 0;
		}
		log.info("" + selectAutomataState);
		submitSelectUpdate();
	}

	private void onMouseDragged(MouseEvent event) {
		log.debug("onMouseDragged " + event.toString());
		if (!isPointInImage(event.getX(), event.getY())) {
			return;
		}
		Point2D eventPoint = new Point2D(event.getX(), event.getY());
		this.p2 = eventPoint;
		selectAutomataState = 1;
		log.info("" + selectAutomataState);
		submitSelectUpdate();
		if (secondaryKey)
			submitScaleUpdate();
	}

	private void onMouseReleased(MouseEvent event) {
		log.debug("onMouseReleased " + event.toString());
		if (!isPointInImage(event.getX(), event.getY())) {
			return;
		}
		Point2D eventPoint = new Point2D(event.getX(), event.getY());
		if (selectAutomataState == 1) {
			selectAutomataState = 2;
			this.p2 = eventPoint;
		} else if (secondaryKey) {
			submitScaleApply();
			selectAutomataState = 0;
		} else {
			this.p2 = eventPoint;
		}
		log.info("" + selectAutomataState);
		submitSelectUpdate();
		if (secondaryKey)
			submitScaleUpdate();
	}

	protected SelectConfig stateToSelectInformation() {
		return new SelectConfig((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(),
			color, selectAutomataState != 0);
	}

	protected synchronized void informationToSelectState(SelectConfig config) {
		updating = true;
		try {
			p1 = new Point2D(config.getX1(), config.getY1());
			p2 = new Point2D(config.getX2(), config.getY2());
			color = config.getColor();
		} finally {
			updating = false;
		}
	}

	protected ScalingConfig stateToScalingInformation() {
		return new ScalingConfig(p1, p3, p1, p2, scalingAlgorithm,
			secondaryKey && selectAutomataState != 0);
	}

	protected synchronized void informationToScalingState(ScalingConfig config) {
		updating = true;
		try {
			p1 = config.getFromP1();
			p3 = config.getFromP2();
			p2 = config.getToP2();
			scalingAlgorithm = config.getAlgorithm();
		} finally {
			updating = false;
		}
	}

	private boolean isPointInImage(double x, double y) {
		Image image = this.image.getImage();
		return x < image.getWidth() && y < image.getHeight();
	}


	private Point2D getNearestToBox(Point2D p) {
		double minX = Math.min(p1.getX(), p2.getX());
		double maxX = Math.max(p1.getX(), p2.getX());
		double minY = Math.min(p1.getY(), p2.getY());
		double maxY = Math.max(p1.getY(), p2.getY());

		return Stream.of(
			new Point2D(minX, minY),
			new Point2D(minX, maxY),
			new Point2D(maxX, minY),
			new Point2D(maxX, maxY)
		)
			.min(Comparator.comparingDouble(p::distance))
			.orElseThrow();
	}

	private Point2D getFarthestToBox(Point2D p) {
		double minX = Math.min(p1.getX(), p2.getX());
		double maxX = Math.max(p1.getX(), p2.getX());
		double minY = Math.min(p1.getY(), p2.getY());
		double maxY = Math.max(p1.getY(), p2.getY());

		return Stream.of(
			new Point2D(minX, minY),
			new Point2D(minX, maxY),
			new Point2D(maxX, minY),
			new Point2D(maxX, maxY)
		)
			.max(Comparator.comparingDouble(p::distance))
			.orElseThrow();
	}

	private void submitSelectUpdate() {
		if (!updating) {
			service.setConfig(stateToSelectInformation(), SelectConfig.class);
		}
	}

	private void submitScaleUpdate() {
		if (!updating) {
			service.setConfig(stateToScalingInformation(), ScalingConfig.class);
		}
	}

	private void submitScaleApply() {
		if (!updating) {
			service.applyConfig(stateToScalingInformation(), ScalingConfig.class);
		}
	}

	@EventListener(ImageModifiedEvent.class)
	public void onApplicationEvent(ImageModifiedEvent event) {
		Platform.runLater(() -> {
			Image currentViewImage = event.getImageBundle().getCurrentViewImage();
			image.setImage(currentViewImage);
			if (currentViewImage != null) {
				image.setFitWidth(currentViewImage.getWidth());
				image.setFitHeight(currentViewImage.getHeight());
			}
		});
	}

	@EventListener(ConfigModifiedEvent.class)
	public void onApplicationEvent(ConfigModifiedEvent<?> event) {
		if (event.getClazz().equals(SelectConfig.class)) {
			Platform.runLater(() -> {
				SelectConfig tConfig = (SelectConfig) event.getConfig();
				informationToSelectState(tConfig);
			});
		} else if (event.getClazz().equals(ScalingConfig.class)) {
			Platform.runLater(() -> {
				ScalingConfig tConfig = (ScalingConfig) event.getConfig();
				informationToScalingState(tConfig);
			});
		}
	}

	@EventListener(ScalingAlgorithmModifiedEvent.class)
	public void onApplicationEvent(ScalingAlgorithmModifiedEvent event) {
		scalingAlgorithm = (ScalingConfig.ScalingAlgorithm) event.getSource();
		if (selectAutomataState != 0)
			submitScaleUpdate();
	}
}
