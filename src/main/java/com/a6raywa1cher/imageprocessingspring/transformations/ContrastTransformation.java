package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.ContrastConfig;

public class ContrastTransformation extends AbstractLookupChannelCachedTransformation<ContrastConfig> {
	private final double leftBorder;
	private final double rightBorder;
	private final boolean decrease;

	public ContrastTransformation(ContrastConfig contrastConfig) {
		leftBorder = contrastConfig.getLeftBorder();
		rightBorder = contrastConfig.getRightBorder();
		decrease = contrastConfig.isDecrease();
	}

	@Override
	int transform(int channelIntensity) {
		if (channelIntensity < leftBorder) {
			return 0;
		} else if (channelIntensity > rightBorder) {
			return 255;
		} else if (!decrease) {
			return (int) Math.round((channelIntensity - leftBorder) / (rightBorder - leftBorder) * 255);
		} else {
			return (int) Math.round(leftBorder + channelIntensity * (rightBorder - leftBorder) / 255);
		}
	}
}
