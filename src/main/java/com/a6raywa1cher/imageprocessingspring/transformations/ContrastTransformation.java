package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.ContrastConfig;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;

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
		int intensity = AlgorithmUtils.intensity(src[0], src[1], src[2]);
		if (intensity < leftBorder) {
			for (int i = 0; i < 3; i++) {
				dest[i] = 0;
			}
		} else if (intensity > rightBorder) {
			for (int i = 0; i < 3; i++) {
				dest[i] = 255;
			}
		} else if (!decrease) {
			for (int i = 0; i < 3; i++) {
				dest[i] = (int) ((src[i] - leftBorder) / (rightBorder - leftBorder) * 255);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				dest[i] = (int) (leftBorder + src[i] * (rightBorder - leftBorder) / 255);
			}
		}
		return dest;
	}
}
