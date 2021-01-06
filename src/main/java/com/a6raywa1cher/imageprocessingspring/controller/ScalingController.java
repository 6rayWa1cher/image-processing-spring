package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ScalingAlgorithmModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.ScalingConfig;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller
public class ScalingController {
	private final ApplicationEventPublisher applicationEventPublisher;
	public ChoiceBox<String> algorithmChooser;
	private volatile boolean updating;

	public ScalingController(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void initialize() {
		algorithmChooser.setItems(new ImmutableObservableList<>(
			Arrays.stream(ScalingConfig.ScalingAlgorithm.values())
				.map(ScalingConfig.ScalingAlgorithm::name)
				.toArray(String[]::new)
		));
		algorithmChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) submitEvent();
		});
	}

	protected synchronized void informationToState(ScalingConfig scalingConfig) {
		updating = true;
		try {
			algorithmChooser.setValue(scalingConfig.getAlgorithm().name());
		} finally {
			updating = false;
		}
	}

	private void submitEvent() {
		applicationEventPublisher.publishEvent(
			new ScalingAlgorithmModifiedEvent(ScalingConfig.ScalingAlgorithm.valueOf(algorithmChooser.getValue()))
		);
	}

	@EventListener(ConfigModifiedEvent.class)
	public void onApplicationEvent(ConfigModifiedEvent<ScalingConfig> event) {
		if (!event.getClazz().equals(ScalingConfig.class)) return;
		Platform.runLater(() -> {
			ScalingConfig tConfig = event.getConfig();
			informationToState(tConfig);
		});
	}
}
