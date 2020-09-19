package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.BinaryConfig;
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
public class BinaryController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider slider;
	public Label sliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public BinaryController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized BinaryConfig stateToInformation() {
		return new BinaryConfig(slider.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(BinaryConfig binaryConfig) {
		updating = true;
		try {
			slider.setValue(binaryConfig.getThreshold());
			sliderLabel.setText(Integer.toString((int) binaryConfig.getThreshold()));
			previewCheckbox.setSelected(binaryConfig.isPreview());
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
		service.setConfig(stateToInformation(), BinaryConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), BinaryConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(BinaryConfig.class)) return;
		Platform.runLater(() -> {
			BinaryConfig binaryConfig = (BinaryConfig) event.getConfig();
			informationToState(binaryConfig);
		});
	}
}
