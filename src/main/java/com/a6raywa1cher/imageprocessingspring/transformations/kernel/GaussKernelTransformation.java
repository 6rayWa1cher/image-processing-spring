package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.model.GaussConfig;

import java.awt.image.Kernel;

public class GaussKernelTransformation extends AbstractKernelTransformation {
	private final int gaussDegree;

	public GaussKernelTransformation(GaussConfig gaussConfig) {
		this.gaussDegree = gaussConfig.getGaussDegree();
	}

	private static int[] calculateFactors(int n) {
		int[] out = new int[n];
		out[0] = 1;
		for (int i = 1; i < n; i++) {
			for (int j = n - 1; j >= 1; j--) {
				out[j] = out[j - 1] + out[j];
			}
		}
		return out;
	}

	@Override
	protected Kernel getKernel() {
		float[] kernel = new float[gaussDegree * gaussDegree];
		int[] factors = calculateFactors(gaussDegree);
		float p = (float) Math.pow(0.5d, gaussDegree - 1);
		for (int x = 0; x < gaussDegree; x++) {
			for (int y = 0; y < gaussDegree; y++) {
				int kernelCoords = y * gaussDegree + x;
				kernel[kernelCoords] = p * p * factors[x] * factors[y];
			}
		}
		return new Kernel(gaussDegree, gaussDegree, kernel);
	}
}
