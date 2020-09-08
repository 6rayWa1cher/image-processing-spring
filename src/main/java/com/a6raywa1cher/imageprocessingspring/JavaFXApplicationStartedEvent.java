package com.a6raywa1cher.imageprocessingspring;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class JavaFXApplicationStartedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public JavaFXApplicationStartedEvent(Stage source) {
		super(source);
	}

	public Stage getStage() {
		return (Stage) getSource();
	}
}
