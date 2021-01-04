package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.SelectConfig;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.imageToWriteable;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SelectTransformation implements Transformation {
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final Color color;

	public SelectTransformation(SelectConfig config) {
		this.x = Math.min(config.getX1(), config.getX2());
		this.y = Math.min(config.getY1(), config.getY2());
		this.width = Math.abs(config.getX1() - config.getX2());
		this.height = Math.abs(config.getY1() - config.getY2());
		this.color = config.getColor();
	}

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		long start = System.currentTimeMillis();

		for (int currX = 0; currX < width; currX++) {
			pixelWriter.setColor(x + currX, y, color);
			pixelWriter.setColor(x + currX, y + height, color);
		}

		for (int currY = 0; currY < height; currY++) {
			pixelWriter.setColor(x, y + currY, color);
			pixelWriter.setColor(x + width, y + currY, color);
		}

		pixelWriter.setColor(x + width, y + height, color);

		log.info("{}: {}ms", this.getClass().getSimpleName(), System.currentTimeMillis() - start);
		return writableImage;
	}
}
