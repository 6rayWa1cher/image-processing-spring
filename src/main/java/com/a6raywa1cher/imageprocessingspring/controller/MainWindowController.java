package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.MalformedURLException;

@Controller
public class MainWindowController implements ApplicationListener<ImageModifiedEvent> {
	private final ImageProcessingService service;
	public ImageView image;

	@Autowired
	public MainWindowController(ImageProcessingService service) {
		this.service = service;
	}

	@FXML
	public void onOpenFile(ActionEvent ae) throws MalformedURLException {
		Stage stage = (Stage) image.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open image");
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			service.openFile(file.toURI().toURL().toString());
		}
	}

	@Override
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
