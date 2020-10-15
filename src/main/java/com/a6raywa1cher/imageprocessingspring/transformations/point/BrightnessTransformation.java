package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessConfig;

public class BrightnessTransformation extends AbstractLookupChannelCachedTransformation {
	private final double delta;

	public BrightnessTransformation(BrightnessConfig brightnessConfig) {
		delta = brightnessConfig.getDelta();
	}

	@Override
	int transform(int channelIntensity) {
		double value = channelIntensity + delta;
		return value < 0 ? 0 : (value > 255 ? 255 : (int) value);
	}
}
