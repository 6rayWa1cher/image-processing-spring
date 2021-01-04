package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.SobelKernelTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SobelConfig implements GenericConfig {
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, SobelConfig.class);
	}

	@Override
	public Transformation getTransformation() {
		return new SobelKernelTransformation(this);
	}
}
