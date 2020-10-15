package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.NegativeConfig;

public class NegativeTransformation extends AbstractLookupChannelCachedTransformation {
	private final double threshold;

	public NegativeTransformation(NegativeConfig negativeConfig) {
		threshold = negativeConfig.getThreshold();
	}

	@Override
	int transform(int channelIntensity) {
		return channelIntensity >= threshold ? 255 - channelIntensity : channelIntensity;
	}
}
