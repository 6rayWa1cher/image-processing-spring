package com.a6raywa1cher.imageprocessingspring.service.dto;

public record Rectangle(int x, int y, int w, int h) {
	public final int x1() {
		return x;
	}

	public final int x2() {
		return x + w;
	}

	public final int y1() {
		return y;
	}

	public final int y2() {
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
