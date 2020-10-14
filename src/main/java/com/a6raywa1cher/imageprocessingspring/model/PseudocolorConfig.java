package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.PseudocolorTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PseudocolorConfig implements Config {

	private Color color1;

	private Color color2;

	private Color color3;

	private Color color4;

	private int q1;

	private int q2;

	private int q3;

	private boolean preview;

	@Override
	public boolean isPreviewAvailable() {
		return true;
	}

	@Override
	public boolean isPreviewEnabled() {
		return preview;
	}

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, PseudocolorConfig.class);
	}

	@Override
	public Transformation<?> getTransformation() {
		return new PseudocolorTransformation(this);
	}
}
