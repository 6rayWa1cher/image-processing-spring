package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GammaConfig;
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
public class GammaController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public Slider slider;
	public Label sliderLabel;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public GammaController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized GammaConfig stateToInformation() {
		return new GammaConfig(rawGammaToDouble((int) slider.getValue()), previewCheckbox.isSelected());
	}

	private double rawGammaToDouble(int gamma) {
		return gamma >= 0 ? gamma + 1 : 1d / (-gamma + 1);
	}

	private int doubleGammaToRaw(double gamma) {
		return gamma >= 1 ? (int) (gamma - 1) : (int) (1d / (-gamma) + 1d);
	}

	private synchronized void informationToState(GammaConfig gammaConfig) {
		updating = true;
		try {
			int readyGamma = doubleGammaToRaw(gammaConfig.getGamma());
			slider.setValue(readyGamma);
			sliderLabel.setText(readyGamma >= 0 ? Integer.toString(readyGamma + 1) : "1/" + (-readyGamma + 1));
			previewCheckbox.setSelected(gammaConfig.isPreview());
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
		service.setConfig(stateToInformation(), GammaConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), GammaConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(GammaConfig.class)) return;
		Platform.runLater(() -> {
			GammaConfig gammaConfig = (GammaConfig) event.getConfig();
			informationToState(gammaConfig);
		});
	}
}
