package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.NegativeConfig;

public class NegativeTransformation extends AbstractLookupTransformation<NegativeConfig> {
	private final double threshold;

	public NegativeTransformation(NegativeConfig negativeConfig) {
		threshold = negativeConfig.getThreshold();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			dest[i] = src[i] >= threshold ? 255 - src[i] : src[i];
		}
		return dest;
	}
}
