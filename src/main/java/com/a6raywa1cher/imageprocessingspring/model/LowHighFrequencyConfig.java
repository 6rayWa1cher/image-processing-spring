package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.LowHighFrequencyTransformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowHighFrequencyConfig implements GenericConfig {
	private KernelType type = KernelType.L1;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, LowHighFrequencyConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return LowHighFrequencyTransformation.class;
	}

	public enum KernelType {
		L1, L2, L3, H1, H2, H3
	}
}
