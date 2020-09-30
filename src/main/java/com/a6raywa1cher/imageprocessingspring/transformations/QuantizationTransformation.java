package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.QuantizationConfig;

import java.util.HashMap;
import java.util.Map;

public class QuantizationTransformation extends AbstractLookupTransformation<QuantizationConfig> {
	private final Map<Integer, Integer> transformMap;

	public QuantizationTransformation(QuantizationConfig config) {
		transformMap = new HashMap<>();

		double segmentSize = 256d / config.getSegments();
		for (int leftBorder = 0, rightBorder = (int) segmentSize;
			 leftBorder <= 256;
			 leftBorder = rightBorder, rightBorder += segmentSize) {
			for (int i = leftBorder; i < rightBorder; i++) {
				transformMap.put(i, leftBorder);
			}
		}
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			dest[i] = transformMap.get(src[i]);
		}
		return dest;
	}
}
