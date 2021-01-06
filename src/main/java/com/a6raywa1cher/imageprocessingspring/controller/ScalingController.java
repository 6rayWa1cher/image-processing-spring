package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.SelectChangedEvent;
import com.a6raywa1cher.imageprocessingspring.model.ScalingConfig;
import com.a6raywa1cher.imageprocessingspring.repository.model.ClickedMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedAreaMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Objects;

@Controller
public class ScalingController {
	public static final Point2D DUMMY_POINT = new Point2D(-1, -1);
	private final ImageProcessingService service;
	private Point2D p1, p2, p3, p4;
	public ChoiceBox<String> algorithmChooser;
	private volatile boolean updating;

	public ScalingController(ImageProcessingService service) {
		this.service = service;
	}

	public void initialize() {
		algorithmChooser.setItems(new ImmutableObservableList<>(
			Arrays.stream(ScalingConfig.ScalingAlgorithm.values())
				.map(ScalingConfig.ScalingAlgorithm::name)
				.toArray(String[]::new)
		));
		algorithmChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	protected synchronized ScalingConfig stateToInformation() {
		return new ScalingConfig(p1, p2, p3, p4, getScalingAlgorithm(), p1 != DUMMY_POINT && p3 != DUMMY_POINT);
	}


	protected synchronized void informationToState(ScalingConfig scalingConfig) {
		updating = true;
		try {
			p1 = scalingConfig.getFromP1();
			p2 = scalingConfig.getFromP2();
			p3 = scalingConfig.getToP1();
			p4 = scalingConfig.getToP2();
			algorithmChooser.setValue(scalingConfig.getAlgorithm().name());
		} finally {
			updating = false;
		}
	}

	private void onChange() {
		service.setConfig(stateToInformation(), ScalingConfig.class);
	}

	private void apply() {
		service.applyConfig(stateToInformation(), ScalingConfig.class);
	}

	private ScalingConfig.ScalingAlgorithm getScalingAlgorithm() {
		return ScalingConfig.ScalingAlgorithm.valueOf(algorithmChooser.getValue());
	}

	@EventListener(ConfigModifiedEvent.class)
	public void onApplicationEvent(ConfigModifiedEvent<ScalingConfig> event) {
		if (!event.getClazz().equals(ScalingConfig.class)) return;
		Platform.runLater(() -> {
			ScalingConfig tConfig = event.getConfig();
			informationToState(tConfig);
		});
	}

	@EventListener(SelectChangedEvent.class)
	public void onApplicationEvent(SelectChangedEvent event) {
		MouseInnerEvent innerEvent = event.getLastEvents().get(0);
		if (innerEvent instanceof ClickedMouseInnerEvent) {
			if (!((ClickedMouseInnerEvent) innerEvent).isPrimaryKey()) {
				apply();
			}
			p1 = p2 = p3 = p4 = DUMMY_POINT;
			onChange();
		} else if (innerEvent instanceof SelectedAreaMouseInnerEvent) {
			SelectedAreaMouseInnerEvent selectedEvent = (SelectedAreaMouseInnerEvent) innerEvent;
			if (Objects.equals(selectedEvent.getP1(), selectedEvent.getP2())) return;
			if (selectedEvent.isPrimaryKey()) {
				p1 = selectedEvent.getP1();
				p2 = selectedEvent.getP2();
				p3 = p4 = DUMMY_POINT;
			} else {
				p3 = selectedEvent.getP1();
				p4 = selectedEvent.getP2();
			}
			onChange();
		}
	}
}
