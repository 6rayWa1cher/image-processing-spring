package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.GrayScaleTransformation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrayScaleConfig implements GenericConfig {
	private double redSlider = 30d;

	private double greenSlider = 59d;

	private double blueSlider = 11d;

	private BaseColor baseColor = BaseColor.BLACK;

	private boolean preview = false;

	public GrayScaleConfig(double redSlider, double greenSlider, double blueSlider, BaseColor baseColor, boolean preview) {
		this.redSlider = redSlider;
		this.greenSlider = greenSlider;
		this.blueSlider = blueSlider;
		this.baseColor = baseColor;
		this.preview = preview;
	}

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, GrayScaleConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return GrayScaleTransformation.class;
	}

	public enum BaseColor {
		BLACK, RED, GREEN, BLUE
	}
}
