package com.a6raywa1cher.imageprocessingspring.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

public class AlgorithmUtils {
	public static List<Rectangle> partition(int w, int h, int cellW, int cellH) {
		List<Rectangle> out = new LinkedList<>();
		for (int i = 0; i < w; i += cellW) {
			for (int j = 0; j < h; j += cellH) {
				out.add(new Rectangle(i, j, (i + cellW > w ? w - i : cellW), (j + cellH > h ? h - j : cellH)));
			}
		}
		return out;
	}

	@Data
	@AllArgsConstructor
	public static final class Rectangle {
		private final int x;
		private final int y;
		private final int w;
		private final int h;
	}
}
