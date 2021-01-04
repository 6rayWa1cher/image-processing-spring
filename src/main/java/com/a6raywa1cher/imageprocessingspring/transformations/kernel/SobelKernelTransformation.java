package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.model.SobelConfig;

import java.awt.image.Kernel;

public class SobelKernelTransformation extends AbstractKernelCallbackTransformation {
	private final float[] kernelData1 = new Kernel(3, 3, new float[]{
		-1f, 0, 1f,
		-2f, 0, 2f,
		-1f, 0, 1f
	}).getKernelData(null);

	private final float[] kernelData2 = new Kernel(3, 3, new float[]{
		1f, 2f, 1f,
		0, 0, 0,
		-1f, -2f, -1f
	}).getKernelData(null);

	public SobelKernelTransformation(SobelConfig config) {
		super(3);
	}

	@Override
	protected float getKernelCallback(int[] pixelData) {
		float h1 = AbstractKernelTransformation.processKernel(pixelData, kernelData1);
		float h2 = AbstractKernelTransformation.processKernel(pixelData, kernelData2);
		return (float) Math.sqrt(h1 * h1 + h2 * h2);
	}
}
