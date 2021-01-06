package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.service.SelectionService;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

// MOUSE_PRESSED, MOUSE_DRAGx(1+), MOUSE_RELEASED
@Controller
@Slf4j
public class ImageController {
	private final SelectionService selectService;
	public ImageView image;

	@Autowired
	public ImageController(SelectionService selectService) {
		this.selectService = selectService;
	}

	public void initialize() {
		image.setOnMousePressed(this::processEvent);
		image.setOnMouseDragged(this::processEvent);
		image.setOnMouseReleased(this::processEvent);
	}

	private void processEvent(MouseEvent mouseEvent) {
		if (!isPointInImage(mouseEvent.getX(), mouseEvent.getY())) return;
		selectService.processEvent(mouseEvent);
	}

	private boolean isPointInImage(double x, double y) {
		Image image = this.image.getImage();
		return x < image.getWidth() && y < image.getHeight();
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
}
