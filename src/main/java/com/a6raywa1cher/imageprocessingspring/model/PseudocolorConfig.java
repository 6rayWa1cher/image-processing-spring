package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.PseudocolorTransformation;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PseudocolorConfig implements GenericConfig {

	private Color color1 = Color.RED;

	private Color color2 = Color.GREEN;

	private Color color3 = Color.BLUE;

	private Color color4 = Color.LIGHTBLUE;

	private int q1 = 255 / 4;

	private int q2 = 255 * 2 / 4;

	private int q3 = 255 * 3 / 4;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, PseudocolorConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return PseudocolorTransformation.class;
	}
}
