package com.a6raywa1cher.imageprocessingspring.transformations;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLookupChannelCachedTransformation<T> extends AbstractLookupTransformation<T> {
	private Map<Integer, Integer> transformationMap;

	abstract int transform(int channelIntensity);

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
		for (int i = 0; i < 3; i++) {
			dest[i] = transformationMap.get(src[i]);
		}
		return dest;
	}
}
