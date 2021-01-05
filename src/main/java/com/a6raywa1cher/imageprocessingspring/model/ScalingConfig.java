package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.scaling.DelegatingScalingTransformation;
import javafx.geometry.Point2D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ScalingConfig implements GenericConfig {
	private Point2D fromP1, fromP2;
	private Point2D toP1, toP2;
	private ScalingAlgorithm algorithm = ScalingAlgorithm.NEAREST_NEIGHBOR;
	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, ScalingConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return DelegatingScalingTransformation.class;
	}

	public enum ScalingAlgorithm {
		NEAREST_NEIGHBOR
	}
}
