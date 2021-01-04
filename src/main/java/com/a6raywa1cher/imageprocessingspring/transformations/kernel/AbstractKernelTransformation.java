package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.Kernel;

@Slf4j
public abstract class AbstractKernelTransformation extends AbstractKernelCallbackTransformation {
	private float[] kernelData;

	public AbstractKernelTransformation(int kernelSize) {
		super(kernelSize);
	}

	protected abstract Kernel getKernel();

	@Override
	protected float getKernelCallback(int[] pixelData) {
		if (kernelData == null) {
			kernelData = getKernel().getKernelData(kernelData);
		}
		return processKernel(pixelData, kernelData);
	}

	public static float processKernel(int[] pixelData, float[] kernelData) {
		float sum = 0;
		for (int i = 0; i < pixelData.length; i++) {
			sum += kernelData[i] * pixelData[i];
		}
		return sum;
	}
}
