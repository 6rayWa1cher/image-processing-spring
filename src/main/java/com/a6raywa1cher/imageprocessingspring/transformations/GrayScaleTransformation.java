package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScale;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.normalize;

public class GrayScaleTransformation extends AbstractLookupTransformation<GrayScale> {
	private final GrayScale grayScale;
	private final double normalizedRedWeight;
	private final double normalizedGreenWeight;
	private final double normalizedBlueWeight;
	private final GrayScale.BaseColor baseColor;

	public GrayScaleTransformation(GrayScale grayScale) {
		this.grayScale = grayScale;
		double redWeight = grayScale.getRedSlider(), greenWeight = grayScale.getGreenSlider(), blueWeight = grayScale.getBlueSlider();
		baseColor = grayScale.getBaseColor();
		normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		int intensity = (int) (normalizedRedWeight * src[0] +
			normalizedGreenWeight * src[1] +
			normalizedBlueWeight * src[2]);
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

	@Override
	public ConfigModifiedEvent<GrayScale> getEvent() {
		return new ConfigModifiedEvent<>(grayScale, GrayScale.class);
	}
}
