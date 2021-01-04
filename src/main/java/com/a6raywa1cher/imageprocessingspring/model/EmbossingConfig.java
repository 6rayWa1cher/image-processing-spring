package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.EmbossingKernelTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbossingConfig implements GenericConfig {
	public enum EmbossingMatrix {
		PRESS_IN, PRESS_OUT
	}

	private EmbossingMatrix matrix = EmbossingMatrix.PRESS_IN;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, EmbossingConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return EmbossingKernelTransformation.class;
	}
}
