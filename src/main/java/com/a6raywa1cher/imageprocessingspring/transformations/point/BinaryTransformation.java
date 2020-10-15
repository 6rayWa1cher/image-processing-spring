package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.BinaryConfig;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleConfig;

public class BinaryTransformation extends AbstractLookupTransformation {
	private final double threshold;
	private final GrayScaleTransformation grayScaleTransformation;

	public BinaryTransformation(BinaryConfig binaryConfig) {
		threshold = binaryConfig.getThreshold();
		grayScaleTransformation = new GrayScaleTransformation(new GrayScaleConfig());
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		dest = grayScaleTransformation.transform(src, dest);
		for (int i = 0; i < 3; i++) {
			int value = src[i];
			dest[i] = value < threshold ? 0 : 255;
		}
		return dest;
	}
}
