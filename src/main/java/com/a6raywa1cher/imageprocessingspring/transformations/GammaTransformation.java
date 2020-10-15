package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.GammaConfig;

public class GammaTransformation extends AbstractLookupChannelCachedTransformation<GammaConfig> {
	private final double gamma;

	public GammaTransformation(GammaConfig gammaConfig) {
		gamma = gammaConfig.getGamma();
	}

	@Override
	int transform(int channelIntensity) {
		return (int) Math.round(255 * Math.pow(channelIntensity / 255d, gamma));
	}
}
