package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.ContrastTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContrastConfig implements GenericConfig {
	private double leftBorder;
	private double rightBorder;
	private boolean decrease;
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, ContrastConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return ContrastTransformation.class;
	}
}
