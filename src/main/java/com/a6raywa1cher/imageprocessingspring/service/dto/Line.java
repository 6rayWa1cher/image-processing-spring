package com.a6raywa1cher.imageprocessingspring.service.dto;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Line implements GeometryFigure {
	private final int fi;

	private final int radius;

	public Line(int fi, int radius) {
		this.fi = Math.floorMod(fi, 180);
		this.radius = radius;
	}

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

	@Override
	public void draw(PixelWriter pixelWriter, int width, int height, Color color) {
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
