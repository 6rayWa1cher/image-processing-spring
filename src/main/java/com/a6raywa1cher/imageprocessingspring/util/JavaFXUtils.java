package com.a6raywa1cher.imageprocessingspring.util;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Arrays;

public class JavaFXUtils {
	public static int getWidth(Image image) {
		return (int) Math.ceil(image.getWidth());
	}

	public static int getHeight(Image image) {
		return (int) Math.ceil(image.getHeight());
	}

	public static WritableImage imageToWriteable(Image image) {
		return new WritableImage(image.getPixelReader(),
			getWidth(image), getHeight(image));
	}

	public static double normalize(double value, double... elements) {
		return value / Arrays.stream(elements).sum();
	}

	public static double normalize(double value, int... elements) {
		return value / Arrays.stream(elements).sum();
	}
}
