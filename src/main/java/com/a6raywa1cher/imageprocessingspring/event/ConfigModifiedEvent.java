package com.a6raywa1cher.imageprocessingspring.event;

import org.springframework.context.ApplicationEvent;

public class ConfigModifiedEvent<T> extends ApplicationEvent {
	private Class<T> clazz;

	public ConfigModifiedEvent(T source, Class<T> clazz) {
		super(source);
		this.clazz = clazz;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public T getConfig() {
		return (T) source;
	}
}
