package com.a6raywa1cher.imageprocessingspring.transformations.vision;

import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;

public abstract class AbstractVisionVisualizationTransformation implements Transformation {
	protected final VisionService visionService;

	protected AbstractVisionVisualizationTransformation(VisionService visionService) {
		this.visionService = visionService;
	}
}
