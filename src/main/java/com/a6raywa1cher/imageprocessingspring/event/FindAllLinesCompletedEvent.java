package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class FindAllLinesCompletedEvent extends ApplicationEvent {
	private final Set<Line> foundLines;

	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public FindAllLinesCompletedEvent(Object source, Set<Line> foundLines) {
		super(source);
		this.foundLines = foundLines;
	}

	public Set<Line> getFoundLines() {
		return foundLines;
	}
}
