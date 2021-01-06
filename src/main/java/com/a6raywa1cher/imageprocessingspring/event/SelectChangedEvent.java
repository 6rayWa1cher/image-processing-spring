package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;


public class SelectChangedEvent extends ApplicationEvent {
	private final List<MouseInnerEvent> lastEvents;

	public SelectChangedEvent(Object source, List<MouseInnerEvent> lastEvents) {
		super(source);
		this.lastEvents = lastEvents;
	}

	public List<MouseInnerEvent> getLastEvents() {
		return lastEvents;
	}
}
