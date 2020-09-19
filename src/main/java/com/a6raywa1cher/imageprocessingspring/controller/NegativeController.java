package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.NegativeConfig;
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
public class NegativeController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider slider;
	public Label sliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public NegativeController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized NegativeConfig stateToInformation() {
		return new NegativeConfig(slider.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(NegativeConfig negativeConfig) {
		updating = true;
		try {
			slider.setValue(negativeConfig.getThreshold());
			sliderLabel.setText(Integer.toString((int) negativeConfig.getThreshold()));
			previewCheckbox.setSelected(negativeConfig.isPreview());
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
		service.setConfig(stateToInformation(), NegativeConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), NegativeConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(NegativeConfig.class)) return;
		Platform.runLater(() -> {
			NegativeConfig negativeConfig = (NegativeConfig) event.getConfig();
			informationToState(negativeConfig);
		});
	}
}
