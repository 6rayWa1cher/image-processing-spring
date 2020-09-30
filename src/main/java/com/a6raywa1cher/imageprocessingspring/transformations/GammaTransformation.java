package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.GammaConfig;

public class GammaTransformation extends AbstractLookupTransformation<GammaConfig> {
	private final double gamma;

	public GammaTransformation(GammaConfig gammaConfig) {
		gamma = gammaConfig.getGamma();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			dest[i] = (int) Math.round(255 * Math.pow(src[i] / 255d, gamma));
		}
		return dest;
	}
}
