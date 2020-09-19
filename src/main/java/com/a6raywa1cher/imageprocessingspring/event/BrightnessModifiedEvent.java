package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.Brightness;
import org.springframework.context.ApplicationEvent;

public class BrightnessModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public BrightnessModifiedEvent(Brightness source) {
		super(source);
	}

	public Brightness getBrightnessInformation() {
		return (Brightness) source;
	}
}
