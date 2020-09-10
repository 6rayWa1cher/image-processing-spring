package com.a6raywa1cher.imageprocessingspring.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrayScaleInformation {
	private double redSlider = 30d;

	private double greenSlider = 59d;

	private double blueSlider = 11d;

	private BaseColor baseColor = BaseColor.BLACK;

	private boolean preview = false;

	public GrayScaleInformation(double redSlider, double greenSlider, double blueSlider, BaseColor baseColor, boolean preview) {
		this.redSlider = redSlider;
		this.greenSlider = greenSlider;
		this.blueSlider = blueSlider;
		this.baseColor = baseColor;
		this.preview = preview;
	}

	public enum BaseColor {
		BLACK, RED, GREEN, BLUE
	}
}
