package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.Negative;

public class NegativeTransformation extends AbstractLookupTransformation<Negative> {
	private final Negative negative;
	private final double threshold;

	public NegativeTransformation(Negative negative) {
		this.negative = negative;
		threshold = negative.getThreshold();
	}

	@Override
	protected int[] transform(int[] src, int[] dest) {
		for (int i = 0; i < 3; i++) {
			dest[i] = src[i] >= threshold ? 255 - src[i] : src[i];
		}
		return dest;
	}

	@Override
	public ConfigModifiedEvent<Negative> getEvent() {
		return new ConfigModifiedEvent<>(negative, Negative.class);
	}
}
