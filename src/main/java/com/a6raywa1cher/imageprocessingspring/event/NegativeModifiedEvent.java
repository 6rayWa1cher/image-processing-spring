package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.Negative;
import org.springframework.context.ApplicationEvent;

public class NegativeModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public NegativeModifiedEvent(Negative source) {
		super(source);
	}

	public Negative getNegativeInformation() {
		return (Negative) source;
	}
}
