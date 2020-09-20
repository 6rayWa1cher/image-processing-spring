package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.ContrastConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
public class ContrastController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider leftSlider;
	public Label leftSliderLabel;
	public Slider rightSlider;
	public Label rightSliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	public ChoiceBox<String> directionChoiceBox;
	private volatile boolean updating;

	@Autowired
	public ContrastController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized ContrastConfig stateToInformation() {
		return new ContrastConfig(leftSlider.getValue(), rightSlider.getValue(),
			directionChoiceBox.getValue().equals("Decrease"), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(ContrastConfig contrastConfig) {
		updating = true;
		try {
			leftSlider.setValue(contrastConfig.getLeftBorder());
			rightSlider.setValue(contrastConfig.getRightBorder());
			leftSliderLabel.setText(Integer.toString((int) contrastConfig.getLeftBorder()));
			rightSliderLabel.setText(Integer.toString((int) contrastConfig.getRightBorder()));
			previewCheckbox.setSelected(contrastConfig.isPreview());
			directionChoiceBox.setValue(contrastConfig.isDecrease() ? "Decrease" : "Increase");
		} finally {
			updating = false;
		}
	}

	public void initialize() {
		leftSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		rightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), ContrastConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), ContrastConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(ContrastConfig.class)) return;
		Platform.runLater(() -> {
			ContrastConfig contrastConfig = (ContrastConfig) event.getConfig();
			informationToState(contrastConfig);
		});
	}
}
