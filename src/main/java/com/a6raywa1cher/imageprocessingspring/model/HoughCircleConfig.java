package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.vision.HoughCircleVisualizationTransformation;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoughCircleConfig implements GenericConfig {
	private Color lineColor = Color.RED;

	private int radius = 42;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, HoughCircleConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return HoughCircleVisualizationTransformation.class;
	}
}
