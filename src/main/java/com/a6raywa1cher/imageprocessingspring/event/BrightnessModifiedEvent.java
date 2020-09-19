package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessInformation;
import org.springframework.context.ApplicationEvent;

public class BrightnessModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public BrightnessModifiedEvent(BrightnessInformation source) {
		super(source);
	}

	public BrightnessInformation getBrightnessInformation() {
		return (BrightnessInformation) source;
	}
}
