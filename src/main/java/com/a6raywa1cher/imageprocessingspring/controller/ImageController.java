package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.SelectConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

// MOUSE_PRESSED, MOUSE_DRAGx(1+), MOUSE_RELEASED
@Controller
@Slf4j
public class ImageController {
	private final ImageProcessingService service;
	public ImageView image;
	private int selectAutomataState;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
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
		log.info("onMousePressed " + event.toString());
		if (!checkPoint(event.getX(), event.getY())) {
			return;
		}
		x1 = (int) event.getX();
		y1 = (int) event.getY();
		selectAutomataState = 0;
		submitSelectUpdate();
	}

	private void onMouseDragged(MouseEvent event) {
		log.info("onMouseDragged " + event.toString());
		if (!checkPoint(event.getX(), event.getY())) {
			return;
		}
		x2 = (int) event.getX();
		y2 = (int) event.getY();
		selectAutomataState = 1;
		submitSelectUpdate();
	}

	private void onMouseReleased(MouseEvent event) {
		log.info("onMouseReleased " + event.toString());
		if (!checkPoint(event.getX(), event.getY())) {
			return;
		}
		x2 = (int) event.getX();
		y2 = (int) event.getY();
		if (selectAutomataState == 1) {
			selectAutomataState = 2;
		}
		submitSelectUpdate();
	}

	protected SelectConfig stateToInformation() {
		return new SelectConfig(x1, y1, x2, y2, color, selectAutomataState != 0);
	}

	protected synchronized void informationToState(SelectConfig config) {
		updating = true;
		try {
			x1 = config.getX1();
			y1 = config.getY1();
			x2 = config.getX2();
			y2 = config.getY2();
			selectAutomataState = config.isPreview() ? 2 : 0;
			color = config.getColor();
		} finally {
			updating = false;
		}
	}

	private boolean checkPoint(double x, double y) {
		Image image = this.image.getImage();
		return x < image.getWidth() && y < image.getHeight();
	}

	private void submitSelectUpdate() {
		if (!updating) {
			service.setConfig(stateToInformation(), SelectConfig.class);
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
	public void onApplicationEvent(ConfigModifiedEvent<SelectConfig> event) {
		if (!event.getClazz().equals(SelectConfig.class)) return;
		Platform.runLater(() -> {
			SelectConfig tConfig = event.getConfig();
			informationToState(tConfig);
		});
	}
}
