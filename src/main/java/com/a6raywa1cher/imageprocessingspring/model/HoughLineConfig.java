package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.vision.HoughLineVisualizationTransformation;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoughLineConfig implements GenericConfig {
	private Color lineColor = Color.RED;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, HoughLineConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return HoughLineVisualizationTransformation.class;
	}
}
