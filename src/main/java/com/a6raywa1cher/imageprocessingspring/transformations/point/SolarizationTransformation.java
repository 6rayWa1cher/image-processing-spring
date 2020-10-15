package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.SolarizationConfig;

public class SolarizationTransformation extends AbstractLookupChannelCachedTransformation {
	@SuppressWarnings("unused")
	public SolarizationTransformation(SolarizationConfig ignored) {

	}

	@Override
	int transform(int channelIntensity) {
		return (int) Math.round(-255d / (128d * 127d) * channelIntensity * channelIntensity +
			255d * 255d / (128 * 127) * channelIntensity);
	}
}
