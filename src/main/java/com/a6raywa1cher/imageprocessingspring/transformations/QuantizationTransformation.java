package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.QuantizationConfig;

public class QuantizationTransformation extends AbstractLookupChannelCachedTransformation<QuantizationConfig> {
	private final double segmentSize;
	private final int segments;

	public QuantizationTransformation(QuantizationConfig config) {
		segments = config.getSegments();
		segmentSize = 256d / segments;
	}

	@Override
	int transform(int channelIntensity) {
		for (int leftBorder = 0, rightBorder = (int) segmentSize;
			 leftBorder <= 256;
			 leftBorder = rightBorder, rightBorder += segmentSize) {
			for (int i = leftBorder; i < rightBorder; i++) {
				if (i == channelIntensity) {
					return leftBorder;
				}
			}
		}
		return segments;
	}
}
