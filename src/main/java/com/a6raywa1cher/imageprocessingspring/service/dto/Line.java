package com.a6raywa1cher.imageprocessingspring.service.dto;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getHeight;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getWidth;

@Data
@AllArgsConstructor
public class Line {
	private int fi;

	private int radius;

	public List<Point2D> getPoints(int width, int height) {
		double radius = this.getRadius();
		double fiRads = Math.toRadians(this.getFi());
		List<Point2D> out = new ArrayList<>();

		if (!(this.getFi() < 45 || this.getFi() > 135)) {
			for (int currX = 0; currX < width; currX++) {
				int currY = (int) Math.round((radius - Math.cos(fiRads) * currX) / Math.sin(fiRads));

				if (currY < 0 || currY >= height) continue;

				out.add(new Point2D(currX, currY));
			}
		} else {
			for (int currY = 0; currY < height; currY++) {
				int currX = (int) Math.round((radius - Math.sin(fiRads) * currY) / Math.cos(fiRads));

				if (currX < 0 || currX >= height) continue;

				out.add(new Point2D(currX, currY));
			}
		}

		return out;
	}

	public void drawLine(WritableImage writableImage, Color color) {
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		double radius = this.getRadius();
		double fiRads = Math.toRadians(this.getFi());

		if (!(this.getFi() < 45 || this.getFi() > 135)) {
			for (int currX = 0; currX < width; currX++) {
				int currY = (int) Math.round((radius - Math.cos(fiRads) * currX) / Math.sin(fiRads));

				if (currY < 0 || currY >= height) continue;

				pixelWriter.setColor(currX, currY, color);
			}
		} else {
			for (int currY = 0; currY < height; currY++) {
				int currX = (int) Math.round((radius - Math.sin(fiRads) * currY) / Math.cos(fiRads));

				if (currX < 0 || currX >= height) continue;

				pixelWriter.setColor(currX, currY, color);
			}
		}
	}
}
