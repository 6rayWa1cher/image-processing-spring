package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScale;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public GrayScaleController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized GrayScale stateToInformation() {
		GrayScale.BaseColor baseColor;
		switch (colorChooser.getValue()) {
			case "Red" -> baseColor = GrayScale.BaseColor.RED;
			case "Green" -> baseColor = GrayScale.BaseColor.GREEN;
			case "Blue" -> baseColor = GrayScale.BaseColor.BLUE;
			default -> baseColor = GrayScale.BaseColor.BLACK;
		}
		return new GrayScale(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), baseColor, previewCheckbox.isSelected());
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
		service.setGrayScale(stateToInformation());
	}

	@FXML
	public void apply() {
		service.applyGrayScale(stateToInformation());
	}

	@Override
	public void onApplicationEvent(GrayScaleModifiedEvent event) {
		Platform.runLater(() -> {
			GrayScale information = event.getGrayScaleInformation();
			informationToState(information);
		});
	}

	private synchronized void informationToState(GrayScale information) {
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
