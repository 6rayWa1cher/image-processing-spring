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
	public CheckBox previewCheckbox;
	public Button applyButton;
	public ChoiceBox<String> colorChooser;

	private volatile boolean updating;


	public GrayScaleController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized GrayScaleInformation stateToInformation() {
		GrayScaleInformation.BaseColor baseColor;
		switch (colorChooser.getValue()) {
			case "Red" -> baseColor = GrayScaleInformation.BaseColor.RED;
			case "Green" -> baseColor = GrayScaleInformation.BaseColor.GREEN;
			case "Blue" -> baseColor = GrayScaleInformation.BaseColor.BLUE;
			default -> baseColor = GrayScaleInformation.BaseColor.BLACK;
		}
		return new GrayScaleInformation(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), baseColor, previewCheckbox.isSelected());
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
		colorChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
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
			String value;
			switch (information.getBaseColor()) {
				case RED -> value = "Red";
				case GREEN -> value = "Green";
				case BLUE -> value = "Blue";
				default -> value = "Black";
			}
			colorChooser.setValue(value);
			previewCheckbox.setSelected(information.isPreview());
		} finally {
			updating = false;
		}
	}
}
