package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.LowHighFrequencyConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller
public class LowHighFrequencyController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;
	public ChoiceBox<String> kernelChooser;
	public CheckBox previewCheckbox;
	public Button applyButton;
	private volatile boolean updating;

	@Autowired
	public LowHighFrequencyController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized LowHighFrequencyConfig stateToInformation() {
		return new LowHighFrequencyConfig(LowHighFrequencyConfig.KernelType.valueOf(kernelChooser.getValue()), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(LowHighFrequencyConfig lowHighFrequencyConfig) {
		updating = true;
		try {
			kernelChooser.setValue(lowHighFrequencyConfig.getType().name());
			previewCheckbox.setSelected(lowHighFrequencyConfig.isPreview());
		} finally {
			updating = false;
		}
	}

	public void initialize() {
		kernelChooser.setItems(new ImmutableObservableList<>(
			Arrays.stream(LowHighFrequencyConfig.KernelType.values())
				.map(LowHighFrequencyConfig.KernelType::name)
				.toArray(String[]::new)
		));
		kernelChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), LowHighFrequencyConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), LowHighFrequencyConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(LowHighFrequencyConfig.class)) return;
		Platform.runLater(() -> {
			LowHighFrequencyConfig lowHighFrequencyConfig = (LowHighFrequencyConfig) event.getConfig();
			informationToState(lowHighFrequencyConfig);
		});
	}
}
