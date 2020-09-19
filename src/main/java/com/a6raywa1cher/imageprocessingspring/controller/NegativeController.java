package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.NegativeModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.Negative;
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
public class NegativeController implements ApplicationListener<NegativeModifiedEvent> {
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

	private synchronized Negative stateToInformation() {
		return new Negative(slider.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(Negative negative) {
		updating = true;
		try {
			slider.setValue(negative.getThreshold());
			sliderLabel.setText(Integer.toString((int) negative.getThreshold()));
			previewCheckbox.setSelected(negative.isPreview());
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
		service.setNegative(stateToInformation());
	}

	@FXML
	public void apply() {
		service.applyNegative(stateToInformation());
	}

	@Override
	public void onApplicationEvent(NegativeModifiedEvent event) {
		Platform.runLater(() -> {
			Negative negative = event.getNegativeInformation();
			informationToState(negative);
		});
	}
}
