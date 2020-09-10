package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
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
import java.util.List;

@Controller
public class MainWindowController implements ApplicationListener<ImageModifiedEvent> {
	private final ImageProcessingService service;
	public ImageView image;

	@Autowired
	public MainWindowController(ImageProcessingService service) {
		this.service = service;
	}

	@FXML
	public void onOpenFile() throws MalformedURLException {
		Stage stage = (Stage) image.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open image");
		FileChooser.ExtensionFilter anyFile = new FileChooser.ExtensionFilter("Compatible image", "*.png", "*.jpg", "*.jpeg", "*.gif");
		fileChooser.getExtensionFilters().add(anyFile);
		fileChooser.getExtensionFilters().addAll(getFilters());
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any file", "*"));
		fileChooser.setSelectedExtensionFilter(anyFile);
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			service.openFile(file.toURI().toURL().toString());
		}
	}

	@FXML
	public void onSaveFile() {
		service.saveFile();
	}

	@FXML
	public void onSaveFileAs() {
		Stage stage = (Stage) image.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save image");
		fileChooser.getExtensionFilters().addAll(getFilters());
		fileChooser.setSelectedExtensionFilter(getDefaultFilter());
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			service.saveToFile(file);
		}
	}

	private FileChooser.ExtensionFilter getDefaultFilter() {
		return new FileChooser.ExtensionFilter("PNG", "*.png");
	}

	private List<FileChooser.ExtensionFilter> getFilters() {
		return List.of(getDefaultFilter(),
			new FileChooser.ExtensionFilter("JPEG-image", "*.jpg", "*.jpeg"),
			new FileChooser.ExtensionFilter("GIF", "*.gif"));
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
