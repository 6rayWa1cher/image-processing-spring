package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.SolarizationConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
public class SolarizationController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public CheckBox previewCheckbox;
	public Button applyButton;

	@Autowired
	public SolarizationController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized SolarizationConfig stateToInformation() {
		return new SolarizationConfig(previewCheckbox.isSelected());
	}

	private synchronized void informationToState(SolarizationConfig solarizationConfig) {
		previewCheckbox.setSelected(solarizationConfig.isPreview());
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), SolarizationConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), SolarizationConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(SolarizationConfig.class)) return;
		Platform.runLater(() -> {
			SolarizationConfig solarizationConfig = (SolarizationConfig) event.getConfig();
			informationToState(solarizationConfig);
		});
	}
}
