package com.a6raywa1cher.imageprocessingspring.util;

public class AlgorithmUtils {
	public static int intensity(int r, int g, int b) {
		return Math.min(255, (int) (0.3 * r + 0.59 * g + 0.11 * b));
	}
}
