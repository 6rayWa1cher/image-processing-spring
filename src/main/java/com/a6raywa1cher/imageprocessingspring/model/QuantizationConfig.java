package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.QuantizationTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantizationConfig implements Config {
	private int segments = 8;

	private boolean preview;

	@Override
	public boolean isPreviewAvailable() {
		return true;
	}

	@Override
	public boolean isPreviewEnabled() {
		return preview;
	}

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, QuantizationConfig.class);
	}

	@Override
	public Transformation<?> getTransformation() {
		return new QuantizationTransformation(this);
	}
}