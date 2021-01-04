package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.model.EmbossingConfig;

import java.awt.image.Kernel;

public class EmbossingKernelTransformation extends AbstractKernelTransformation {
	private final EmbossingConfig.EmbossingMatrix matrix;

	public EmbossingKernelTransformation(EmbossingConfig config) {
		super(3);
		this.matrix = config.getMatrix();
	}

	@Override
	protected Kernel getKernel() {
		if (matrix == EmbossingConfig.EmbossingMatrix.PRESS_IN) {
			return new Kernel(3, 3, new float[]{
				0, -1f, 0,
				1f, 0, -1f,
				0, 1f, 0
			});
		} else {
			return new Kernel(3, 3, new float[]{
				0, 1f, 0,
				-1f, 0, 1f,
				0, -1f, 0
			});
		}
	}

	@Override
	protected float getKernelCallback(int[] pixelData) {
		return super.getKernelCallback(pixelData) + 127;
	}
}
