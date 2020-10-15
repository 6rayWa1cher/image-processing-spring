package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.SolarizationTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolarizationConfig implements Config {
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
		return new ConfigModifiedEvent<>(this, SolarizationConfig.class);
	}

	@Override
	public Transformation getTransformation() {
		return new SolarizationTransformation(this);
	}
}
