package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.BinaryClusterTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryClusterConfig implements GenericConfig {
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, GenericConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return BinaryClusterTransformation.class;
	}
}
