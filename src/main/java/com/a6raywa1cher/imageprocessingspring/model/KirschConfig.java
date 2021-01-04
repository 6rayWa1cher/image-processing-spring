package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.KirschKernelTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KirschConfig implements GenericConfig {
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, KirschConfig.class);
	}

	@Override
	public Transformation getTransformation() {
		return new KirschKernelTransformation(this);
	}
}
