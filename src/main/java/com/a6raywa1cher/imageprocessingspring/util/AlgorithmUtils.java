package com.a6raywa1cher.imageprocessingspring.util;

public class AlgorithmUtils {
	public static int intensity(int r, int g, int b) {
		return intensity(r, g, b, 0.3, 0.59, 0.11);
	}

	public static int channelToInt(double val) {
		return (int) (val * 255);
	}

	public static int intensity(int r, int g, int b, double rw, double gw, double bw) {
		return Math.min(255, (int) Math.round(rw * r + gw * g + bw * b));
	}
}
