package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessConfig;

public class BrightnessTransformation extends AbstractLookupTransformation<BrightnessConfig> {
	private final double delta;

	public BrightnessTransformation(BrightnessConfig brightnessConfig) {
		delta = brightnessConfig.getDelta();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			double value = src[i] + delta;
			dest[i] = value < 0 ? 0 : (value > 255 ? 255 : (int) value);
		}
		return dest;
	}
}
