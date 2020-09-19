package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.GrayScaleTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrayScale implements Config {
	private double redSlider = 30d;

	private double greenSlider = 59d;

	private double blueSlider = 11d;

	private BaseColor baseColor = BaseColor.BLACK;

	private boolean preview = false;

	public GrayScale(double redSlider, double greenSlider, double blueSlider, BaseColor baseColor, boolean preview) {
		this.redSlider = redSlider;
		this.greenSlider = greenSlider;
		this.blueSlider = blueSlider;
		this.baseColor = baseColor;
		this.preview = preview;
	}

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
		return new ConfigModifiedEvent<>(this, GrayScale.class);
	}

	@Override
	public Transformation<?> getTransformation() {
		return new GrayScaleTransformation(this);
	}

	public enum BaseColor {
		BLACK, RED, GREEN, BLUE
	}
}
