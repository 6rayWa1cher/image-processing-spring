package com.a6raywa1cher.imageprocessingspring.model;

public interface GenericConfig extends Config {
	@Override
	default boolean isPreviewAvailable() {
		return true;
	}

	@Override
	default boolean isPreviewEnabled() {
		return isPreview();
	}

	boolean isPreview();
}
