package com.a6raywa1cher.imageprocessingspring.event;

import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import org.springframework.context.ApplicationEvent;

public class ImageModifiedEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public ImageModifiedEvent(ImageBundle source) {
		super(source);
	}

	public ImageBundle getImageBundle() {
		return (ImageBundle) source;
	}
}
