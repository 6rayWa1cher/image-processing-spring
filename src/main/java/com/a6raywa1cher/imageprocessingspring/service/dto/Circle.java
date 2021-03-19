package com.a6raywa1cher.imageprocessingspring.service.dto;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class Circle implements GeometryFigure {
	private final int x0;

	private final int y0;

	private final int radius;

	public Circle(int x0, int y0, int radius) {
		this.x0 = x0;
		this.y0 = y0;
		Assert.isTrue(radius > 0, "Circle with negative radius");
		this.radius = radius;
	}

	@Override
	public void draw(PixelWriter pixelWriter, int width, int height, Color color) {
		for (int currX = x0 - radius; currX <= x0 + radius; currX++) {
			double r = this.radius;

			if (currX < 0 || currX >= width) continue;

			double sqrt = Math.sqrt(r * r - Math.pow(currX - x0, 2d));
			double upperY = (double) y0 + sqrt;
			double lowerY = (double) y0 - sqrt;


			if (0 <= upperY && Math.ceil(upperY) < height) {
				pixelWriter.setColor(currX, (int) Math.floor(upperY), color);
				pixelWriter.setColor(currX, (int) Math.ceil(upperY), color);
			}
			if (0 <= lowerY && Math.ceil(lowerY) < height) {
				pixelWriter.setColor(currX, (int) Math.floor(lowerY), color);
				pixelWriter.setColor(currX, (int) Math.ceil(lowerY), color);
			}
		}
		for (int currY = y0 - radius; currY <= y0 + radius; currY++) {
			double r = this.radius;

			if (currY < 0 || currY >= height) continue;

			double sqrt = Math.sqrt(r * r - Math.pow(currY - y0, 2d));
			double upperX = (double) x0 + sqrt;
			double lowerX = (double) x0 - sqrt;

			if (0 <= upperX && Math.ceil(upperX) < width) {
				pixelWriter.setColor((int) Math.floor(upperX), currY, color);
				pixelWriter.setColor((int) Math.ceil(upperX), currY, color);
			}
			if (0 <= lowerX && Math.ceil(lowerX) < width) {
				pixelWriter.setColor((int) Math.floor(lowerX), currY, color);
				pixelWriter.setColor((int) Math.ceil(lowerX), currY, color);
			}
		}
	}
}
