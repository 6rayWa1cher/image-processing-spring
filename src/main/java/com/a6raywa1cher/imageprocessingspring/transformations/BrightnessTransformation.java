package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.Brightness;

public class BrightnessTransformation extends AbstractLookupTransformation<Brightness> {
	private final Brightness brightness;
	private final double delta;

	public BrightnessTransformation(Brightness brightness) {
		this.brightness = brightness;
		delta = brightness.getDelta();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			double value = src[i] + delta;
			dest[i] = value < 0 ? 0 : (value > 255 ? 255 : (int) value);
		}
		return dest;
	}

	@Override
	public ConfigModifiedEvent<Brightness> getEvent() {
		return new ConfigModifiedEvent<>(brightness, Brightness.class);
	}
}
