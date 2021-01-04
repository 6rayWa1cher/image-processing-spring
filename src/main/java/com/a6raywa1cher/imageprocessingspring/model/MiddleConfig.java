package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.MiddleTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiddleConfig implements GenericConfig {
	private int windowSize = 3;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, MiddleConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return MiddleTransformation.class;
	}
}
