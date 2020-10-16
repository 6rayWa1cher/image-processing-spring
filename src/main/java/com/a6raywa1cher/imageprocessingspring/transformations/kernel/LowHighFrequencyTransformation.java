package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.model.LowHighFrequencyConfig;

import java.awt.image.Kernel;

public class LowHighFrequencyTransformation extends AbstractKernelTransformation {
	private final LowHighFrequencyConfig.KernelType kernelType;

	public LowHighFrequencyTransformation(LowHighFrequencyConfig config) {
		kernelType = config.getType();
	}

	@Override
	protected Kernel getKernel() {
		switch (kernelType) {
			case L1 -> {
				return new Kernel(3, 3, new float[]{
					1f / 9, 1f / 9, 1f / 9,
					1f / 9, 1f / 9, 1f / 9,
					1f / 9, 1f / 9, 1f / 9});
			}
			case L2 -> {
				return new Kernel(3, 3, new float[]{
					1f / 10, 1f / 10, 1f / 10,
					1f / 10, 2f / 10, 1f / 10,
					1f / 10, 1f / 10, 1f / 10});
			}
			case L3 -> {
				return new Kernel(3, 3, new float[]{
					1f / 16, 2f / 16, 1f / 16,
					2f / 16, 4f / 16, 2f / 16,
					1f / 16, 2f / 16, 1f / 16
				});
			}
			case H1 -> {
				return new Kernel(3, 3, new float[]{
					-1f, -1f, -1f,
					-1f, 9f, -1f,
					-1f, -1f, -1f
				});
			}
			case H2 -> {
				return new Kernel(3, 3, new float[]{
					0, -1f, 0,
					-1f, 5f, -1f,
					0, -1f, 0
				});
			}
			case H3 -> {
				return new Kernel(3, 3, new float[]{
					1f, -2f, 1f,
					-2f, 5f, -2f,
					1f, -2f, 1f
				});
			}
		}
		throw new IllegalArgumentException();
	}
}
