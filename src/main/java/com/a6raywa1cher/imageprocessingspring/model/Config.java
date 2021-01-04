package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;

public interface Config {
	boolean isPreviewAvailable();

	default boolean isPreviewEnabled() {
		return false;
	}

	ConfigModifiedEvent<?> getEvent();

	Class<? extends Transformation> getMainTransformation();
}
