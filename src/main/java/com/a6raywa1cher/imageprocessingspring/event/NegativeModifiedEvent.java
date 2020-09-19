package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.NegativeInformation;
import org.springframework.context.ApplicationEvent;

public class NegativeModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public NegativeModifiedEvent(NegativeInformation source) {
		super(source);
	}

	public NegativeInformation getNegativeInformation() {
		return (NegativeInformation) source;
	}
}
