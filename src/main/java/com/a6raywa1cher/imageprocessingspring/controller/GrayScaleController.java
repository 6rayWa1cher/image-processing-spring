package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class GrayScaleController implements ApplicationListener<GrayScaleModifiedEvent> {
	private final ImageProcessingService service;
	public Slider redSlider;
	public Label redSliderLabel;
	public Slider greenSlider;
	public Label greenSliderLabel;
	public Slider blueSlider;
	public Label blueSliderLabel;
	public ColorPicker baseColor;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;


	public GrayScaleController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized GrayScaleInformation stateToInformation() {
		return new GrayScaleInformation(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), baseColor.getValue(), previewCheckbox.isSelected());
	}

	public void initialize() {
		redSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		greenSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		blueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setGrayScaleInformation(stateToInformation());
	}

	@FXML
	public void apply() {
		service.applyGrayScaleInformation(stateToInformation());
	}

	@Override
	public void onApplicationEvent(GrayScaleModifiedEvent event) {
		Platform.runLater(() -> {
			GrayScaleInformation information = event.getGrayScaleInformation();
			informationToState(information);
		});
	}

	private synchronized void informationToState(GrayScaleInformation information) {
		updating = true;
		try {
			redSlider.setValue(information.getRedSlider());
			redSliderLabel.setText(Integer.toString((int) information.getRedSlider()));
			greenSlider.setValue(information.getGreenSlider());
			greenSliderLabel.setText(Integer.toString((int) information.getGreenSlider()));
			blueSlider.setValue(information.getBlueSlider());
			blueSliderLabel.setText(Integer.toString((int) information.getBlueSlider()));
			baseColor.setValue(information.getBaseColor());
			previewCheckbox.setSelected(information.isPreview());
		} finally {
			updating = false;
		}
	}
}
