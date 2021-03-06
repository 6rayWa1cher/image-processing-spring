package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.BinaryTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryConfig implements GenericConfig {
	private double threshold = 1;
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, BinaryConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return BinaryTransformation.class;
	}
}
