package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.PseudocolorConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
public class PseudocolorController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;


	public CheckBox previewCheckbox;
	public Button applyButton;
	public Spinner<Integer> q1chooser;
	public ColorPicker picker1;
	public Spinner<Integer> q2chooser;
	public ColorPicker picker2;
	public Spinner<Integer> q3chooser;
	public ColorPicker picker3;
	public ColorPicker picker4;
	private volatile boolean updating;

	@Autowired
	public PseudocolorController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized PseudocolorConfig stateToInformation() {
		return new PseudocolorConfig(picker1.getValue(), picker2.getValue(), picker3.getValue(), picker4.getValue(),
			q1chooser.getValue(), q2chooser.getValue(), q3chooser.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(PseudocolorConfig pseudocolorConfig) {
		updating = true;
		try {
			q1chooser.getValueFactory().setValue(pseudocolorConfig.getQ1());
			q2chooser.getValueFactory().setValue(pseudocolorConfig.getQ2());
			q3chooser.getValueFactory().setValue(pseudocolorConfig.getQ3());
			picker1.setValue(pseudocolorConfig.getColor1());
			picker2.setValue(pseudocolorConfig.getColor2());
			picker3.setValue(pseudocolorConfig.getColor3());
			picker4.setValue(pseudocolorConfig.getColor4());
			previewCheckbox.setSelected(pseudocolorConfig.isPreview());
		} finally {
			updating = false;
		}
	}

	public void initialize() {
		q1chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q2chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q3chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q1chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		q2chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		q3chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), PseudocolorConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), PseudocolorConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(PseudocolorConfig.class)) return;
		Platform.runLater(() -> {
			PseudocolorConfig pseudocolorConfig = (PseudocolorConfig) event.getConfig();
			informationToState(pseudocolorConfig);
		});
	}
}
