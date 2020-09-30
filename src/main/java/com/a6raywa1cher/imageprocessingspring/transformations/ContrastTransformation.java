package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.ContrastConfig;

public class ContrastTransformation extends AbstractLookupTransformation<ContrastConfig> {
	private final double leftBorder;
	private final double rightBorder;
	private final boolean decrease;

	public ContrastTransformation(ContrastConfig contrastConfig) {
		leftBorder = contrastConfig.getLeftBorder();
		rightBorder = contrastConfig.getRightBorder();
		decrease = contrastConfig.isDecrease();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			int intensity = src[i];
			if (intensity < leftBorder) {
				dest[i] = 0;
			} else if (intensity > rightBorder) {
				dest[i] = 255;
			} else if (!decrease) {
				dest[i] = (int) Math.round((src[i] - leftBorder) / (rightBorder - leftBorder) * 255);
			} else {
				dest[i] = (int) Math.round(leftBorder + src[i] * (rightBorder - leftBorder) / 255);
			}
		}
		return dest;
	}
}
