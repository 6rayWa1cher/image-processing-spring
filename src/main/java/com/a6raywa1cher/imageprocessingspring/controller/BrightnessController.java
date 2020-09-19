package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.Brightness;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
public class BrightnessController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider slider;
	public Label sliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public BrightnessController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized Brightness stateToInformation() {
		return new Brightness(slider.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(Brightness brightness) {
		updating = true;
		try {
			slider.setValue(brightness.getDelta());
			sliderLabel.setText(Integer.toString((int) brightness.getDelta()));
			previewCheckbox.setSelected(brightness.isPreview());
		} finally {
			updating = false;
		}
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), Brightness.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), Brightness.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(Brightness.class)) return;
		Platform.runLater(() -> {
			Brightness brightness = (Brightness) event.getConfig();
			informationToState(brightness);
		});
	}
}
