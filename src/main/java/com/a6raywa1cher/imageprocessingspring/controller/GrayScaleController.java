package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleConfig;
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
public class GrayScaleController implements ApplicationListener<ConfigModifiedEvent> {
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

	private synchronized GrayScaleConfig stateToInformation() {
		GrayScaleConfig.BaseColor baseColor;
		switch (colorChooser.getValue()) {
			case "Red" -> baseColor = GrayScaleConfig.BaseColor.RED;
			case "Green" -> baseColor = GrayScaleConfig.BaseColor.GREEN;
			case "Blue" -> baseColor = GrayScaleConfig.BaseColor.BLUE;
			default -> baseColor = GrayScaleConfig.BaseColor.BLACK;
		}
		return new GrayScaleConfig(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), baseColor, previewCheckbox.isSelected());
	}

	private synchronized void informationToState(GrayScaleConfig information) {
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
		service.setConfig(stateToInformation(), GrayScaleConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), GrayScaleConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(GrayScaleConfig.class)) return;
		Platform.runLater(() -> {
			GrayScaleConfig information = (GrayScaleConfig) event.getConfig();
			informationToState(information);
		});
	}

}
