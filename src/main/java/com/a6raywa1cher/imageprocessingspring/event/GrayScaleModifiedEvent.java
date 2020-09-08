package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import org.springframework.context.ApplicationEvent;

public class GrayScaleModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public GrayScaleModifiedEvent(GrayScaleInformation source) {
		super(source);
	}

	public GrayScaleInformation getGrayScaleInformation() {
		return (GrayScaleInformation) source;
	}
}
