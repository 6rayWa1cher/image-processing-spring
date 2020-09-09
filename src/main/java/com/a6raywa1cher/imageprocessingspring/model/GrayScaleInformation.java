package com.a6raywa1cher.imageprocessingspring.model;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrayScaleInformation {
	private double redSlider = 30d;

	private double greenSlider = 59d;

	private double blueSlider = 11d;

	private Color baseColor = Color.BLACK;

	private boolean preview = false;

	public GrayScaleInformation(double redSlider, double greenSlider, double blueSlider, Color baseColor, boolean preview) {
		this.redSlider = redSlider;
		this.greenSlider = greenSlider;
		this.blueSlider = blueSlider;
		this.baseColor = baseColor;
		this.preview = preview;
	}
}
