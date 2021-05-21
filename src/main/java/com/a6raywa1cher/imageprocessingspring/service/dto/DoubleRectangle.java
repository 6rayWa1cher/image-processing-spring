package com.a6raywa1cher.imageprocessingspring.service.dto;

public record DoubleRectangle(double x, double y, double w, double h) {
	public final double x1() {
		return x;
	}

	public final double x2() {
		return x + w;
	}

	public final double y1() {
		return y;
	}

	public final double y2() {
		return y + h;
	}

	@Override
	public String toString() {
		return "Rectangle{" +
			"x=" + x +
			", y=" + y +
			", w=" + w +
			", h=" + h +
			'}';
	}
}