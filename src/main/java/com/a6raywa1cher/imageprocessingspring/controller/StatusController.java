package com.a6raywa1cher.imageprocessingspring.controller;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class StatusController {
	private final ObjectProperty<Double> progressBarProperty;
	private final ObjectProperty<String> statusProperty;
	public ProgressBar progressBar;
	public Label rightStatus;
	private volatile boolean updating;

	public StatusController(@Qualifier("progressBarProperty") ObjectProperty<Double> progressBarProperty,
							@Qualifier("statusProperty") ObjectProperty<String> statusProperty) {
		this.progressBarProperty = progressBarProperty;
		this.statusProperty = statusProperty;
	}

	public void initialize() {
		progressBarProperty.addListener((observable, oldValue, newValue) -> {
			if (!updating) updateProgressBar();
		});
		statusProperty.addListener((observable, oldValue, newValue) -> {
			updateStatus(newValue);
		});
		progressBar.setProgress(progressBarProperty.getValue());
		rightStatus.setText(statusProperty.getValue());
	}

	private void updateProgressBar() {
		updating = true;
		Platform.runLater(() -> {
			try {
				progressBar.setProgress(progressBarProperty.getValue());
			} finally {
				updating = false;
			}
		});
	}

	private void updateStatus(String status) {
		Platform.runLater(() -> rightStatus.setText(status));
	}
}
