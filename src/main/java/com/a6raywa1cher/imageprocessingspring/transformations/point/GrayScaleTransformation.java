package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleConfig;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.normalize;

public class GrayScaleTransformation extends AbstractLookupTransformation {
	private final double normalizedRedWeight;
	private final double normalizedGreenWeight;
	private final double normalizedBlueWeight;
	private final GrayScaleConfig.BaseColor baseColor;

	public GrayScaleTransformation(GrayScaleConfig grayScaleConfig) {
		double redWeight = grayScaleConfig.getRedSlider(), greenWeight = grayScaleConfig.getGreenSlider(), blueWeight = grayScaleConfig.getBlueSlider();
		baseColor = grayScaleConfig.getBaseColor();
		normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		int intensity = AlgorithmUtils.intensity(src[0], src[1], src[2], normalizedRedWeight, normalizedGreenWeight, normalizedBlueWeight);
		switch (baseColor) {
			case RED -> {
				dest[0] = intensity;
				dest[1] = 0x00;
				dest[2] = 0x00;
			}
			case GREEN -> {
				dest[0] = 0x00;
				dest[1] = intensity;
				dest[2] = 0x00;
			}
			case BLUE -> {
				dest[0] = 0x00;
				dest[1] = 0x00;
				dest[2] = intensity;
			}
			case BLACK -> {
				dest[0] = intensity;
				dest[1] = intensity;
				dest[2] = intensity;
			}
		}
		return dest;
	}
}
