package com.a6raywa1cher.imageprocessingspring.service.dto;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getHeight;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getWidth;

public interface GeometryFigure {
	default void draw(WritableImage writableImage, Color color) {
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		draw(pixelWriter, width, height, color);
	}

	void draw(PixelWriter pixelWriter, int width, int height, Color color);
}
