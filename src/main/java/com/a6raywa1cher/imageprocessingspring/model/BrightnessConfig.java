package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.BrightnessTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrightnessConfig implements GenericConfig {
	private double delta = 0;

	private boolean preview = false;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, BrightnessConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return BrightnessTransformation.class;
	}
}
