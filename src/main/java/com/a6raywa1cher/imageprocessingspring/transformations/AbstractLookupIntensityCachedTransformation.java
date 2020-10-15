package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLookupIntensityCachedTransformation<T> extends AbstractLookupTransformation<T> {
	private Map<Integer, Integer> transformationMap;

	abstract int transform(int intensity);

	private void makeTransformationMap() {
		transformationMap = new HashMap<>();
		for (int i = 0; i < 256; i++) {
			transformationMap.put(i, transform(i));
		}
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		if (transformationMap == null) {
			makeTransformationMap();
		}
		int intensity = AlgorithmUtils.intensity(src[0], src[1], src[2]);
		for (int i = 0; i < 3; i++) {
			dest[i] = transformationMap.get(intensity);
		}
		return dest;
	}
}
