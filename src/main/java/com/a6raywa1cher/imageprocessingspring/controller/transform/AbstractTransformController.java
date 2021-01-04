package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GenericConfig;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

public abstract class AbstractTransformController<T extends GenericConfig> {
	private final Class<T> tClass;
	private ImageProcessingService service;
	public CheckBox previewCheckbox;
	public Button applyButton;

	public AbstractTransformController(Class<T> tClass) {
		this.tClass = tClass;
	}

	@Autowired
	public void setService(ImageProcessingService service) {
		this.service = service;
	}

	protected abstract T stateToInformation();

	protected synchronized void informationToState(T tConfig) {
		previewCheckbox.setSelected(tConfig.isPreview());
	}

	@FXML
	public void onChange() {
		service.setConfig(stateToInformation(), tClass);
	}

	@FXML
	public void apply() {
		service.applyConfig(stateToInformation(), tClass);
	}

	@EventListener(ConfigModifiedEvent.class)
	public void onApplicationEvent(ConfigModifiedEvent<T> event) {
		if (!event.getClazz().equals(tClass)) return;
		Platform.runLater(() -> {
			T tConfig = event.getConfig();
			informationToState(tConfig);
		});
	}
}
