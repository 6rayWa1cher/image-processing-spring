package com.a6raywa1cher.imageprocessingspring.transformations.vision;

import com.a6raywa1cher.imageprocessingspring.model.SearchConfig;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.ObjectSearchResult;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.scaling.NearestNeighborScalingTransformation;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;
import static java.lang.Math.*;

@Slf4j
public class SearchVisualizationTransformation implements Transformation {
	private final Image template;
	private final VisionService visionService;
	private final Color color;

	public SearchVisualizationTransformation(SearchConfig searchConfig, VisionService visionService) {
		this.template = searchConfig.getTemplate();
		this.color = searchConfig.getColor();
		this.visionService = visionService;
	}

	@Override
	public Image transform(Image image) {
		ObjectSearchResult result = visionService.findObject(image, template);
		WritableImage writableImage = imageToWriteable(image);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		Image scaled = new NearestNeighborScalingTransformation(
			new Point2D(0, 0), new Point2D(getWidth(template), getHeight(template)),
			new Point2D(0, 0), new Point2D(result.targetWidth(), result.targetHeight())
		).transform(template);
		PixelReader pixelReader = scaled.getPixelReader();
		int width = getWidth(image);
		int height = getHeight(image);
		for (int x = 0; x < result.targetWidth(); x++) {
			for (int y = 0; y < result.targetHeight(); y++) {
				try {
					if (pixelReader.getColor(x, y).equals(Color.WHITE)) {
						double theta = toRadians(result.rotate());

						int rotatedX = (int) Math.round(cos(theta) * x - sin(theta) * y) + result.x();
						int rotatedY = (int) Math.round(sin(theta) * x + cos(theta) * y) + result.y();
						if (rotatedX < 0 || rotatedX >= width || rotatedY < 0 || rotatedY >= height) continue;
						pixelWriter.setColor(x + result.x(), y + result.y(), color);
					}
				} catch (Exception ignored) {

				}
			}
		}
		log.info("result: {}", result);
		return writableImage;
	}
}
