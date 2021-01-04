package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.model.KirschConfig;

public class KirschKernelTransformation extends AbstractKernelCallbackTransformation {
	private float[][] kernels;

	public KirschKernelTransformation(KirschConfig config) {
		super(3);
	}

	@Override
	protected float getKernelCallback(int[] pixelData) {
		if (kernels == null) {
			kernels = new float[8][9];
			int[] bypassOrder = new int[]{0, 1, 2, 5, 8, 7, 6, 3}; // 3x3 round over the border
			for (int i = 0; i < 8; i++) {
				int first = bypassOrder[i % bypassOrder.length];
				int second = bypassOrder[(i + 1) % bypassOrder.length];
				int third = bypassOrder[(i + 2) % bypassOrder.length];
				for (int index : bypassOrder) {
					if (index == first || index == second || index == third) {
						kernels[i][index] = 5f;
					} else {
						kernels[i][index] = -3f;
					}
				}
			}
		}
		float currMax = 0;
		for (float[] kernelData : kernels) {
			currMax = Math.max(currMax, Math.abs(AbstractKernelTransformation.processKernel(pixelData, kernelData)));
		}
		return currMax;
	}
}
