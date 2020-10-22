package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GaussConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;

@Controller
public class GaussController implements ApplicationListener<ConfigModifiedEvent> {
	private final ImageProcessingService service;

	public CheckBox previewCheckbox;
	public Button applyButton;
	public Spinner<Integer> chooser;
	private volatile boolean updating;

	@Autowired
	public GaussController(ImageProcessingService service) {
		this.service = service;
	}

	private synchronized GaussConfig stateToInformation() {
		return new GaussConfig(chooser.getValue(), previewCheckbox.isSelected());
	}

	private synchronized void informationToState(GaussConfig gaussConfig) {
		updating = true;
		try {
			chooser.getValueFactory().setValue(gaussConfig.getGaussDegree());
			previewCheckbox.setSelected(gaussConfig.isPreview());
		} finally {
			updating = false;
		}
	}

	public void initialize() {
		chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, Integer.MAX_VALUE, 3, 2));
		chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), GaussConfig.class);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), GaussConfig.class);
	}

	@Override
	public void onApplicationEvent(ConfigModifiedEvent event) {
		if (!event.getClazz().equals(GaussConfig.class)) return;
		Platform.runLater(() -> {
			GaussConfig gaussConfig = (GaussConfig) event.getConfig();
			informationToState(gaussConfig);
		});
	}
}
