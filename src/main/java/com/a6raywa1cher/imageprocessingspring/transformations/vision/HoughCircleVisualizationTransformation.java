package com.a6raywa1cher.imageprocessingspring.transformations.vision;

import com.a6raywa1cher.imageprocessingspring.model.HoughCircleConfig;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.Circle;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Set;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.imageToWriteable;

public class HoughCircleVisualizationTransformation implements Transformation {
	private final Color lineColor;
	private final int radius;
	private final VisionService visionService;

	public HoughCircleVisualizationTransformation(HoughCircleConfig config, VisionService visionService) {
		this.lineColor = config.getLineColor();
		this.radius = config.getRadius();
		this.visionService = visionService;
	}

	@Override
	public Image transform(Image image) {
		Set<Circle> circles = visionService.findAllCircles(image, radius);

		WritableImage writableImage = imageToWriteable(image);

		for (Circle circle : circles) {
			circle.draw(writableImage, lineColor);
		}

		return writableImage;
	}
}
