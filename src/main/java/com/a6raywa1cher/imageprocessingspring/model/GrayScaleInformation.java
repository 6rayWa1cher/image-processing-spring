package com.a6raywa1cher.imageprocessingspring.model;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrayScaleInformation {
	private double redSlider = 30d;

	private double greenSlider = 59d;

	private double blueSlider = 11d;

	private Color baseColor = Color.BLACK;

	private boolean preview = false;
}
