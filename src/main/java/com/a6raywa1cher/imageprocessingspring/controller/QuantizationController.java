package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.QuantizationConfig;
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
public class QuantizationController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider slider;
	public Label sliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public QuantizationController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized QuantizationConfig stateToInformation() {
		return new QuantizationConfig((int) slider.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(QuantizationConfig quantizationConfig) {
		updating = true;
		try {
			slider.setValue(quantizationConfig.getSegments());
			sliderLabel.setText(Integer.toString(quantizationConfig.getSegments()));
			previewCheckbox.setSelected(quantizationConfig.isPreview());
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
		service.setConfig(stateToInformation(), QuantizationConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), QuantizationConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(QuantizationConfig.class)) return;
		Platform.runLater(() -> {
			QuantizationConfig quantizationConfig = (QuantizationConfig) event.getConfig();
			informationToState(quantizationConfig);
		});
	}
}
