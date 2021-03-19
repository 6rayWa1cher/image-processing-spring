package com.a6raywa1cher.imageprocessingspring.transformations.vision;

import com.a6raywa1cher.imageprocessingspring.model.HoughConfig;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Set;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.imageToWriteable;

public class HoughLineVisualizationTransformation implements Transformation {
	private final Color lineColor;
	private final VisionService visionService;

	public HoughLineVisualizationTransformation(HoughConfig config, VisionService visionService) {
		this.lineColor = config.getLineColor();
		this.visionService = visionService;
	}

	@Override
	public Image transform(Image image) {
		Set<Line> lines = visionService.findAllLines(image);

		WritableImage writableImage = imageToWriteable(image);

		for (Line line : lines) {
			line.drawLine(writableImage, lineColor);
		}

		return writableImage;
	}
}
