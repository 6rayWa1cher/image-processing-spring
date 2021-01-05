package com.a6raywa1cher.imageprocessingspring.transformations.scaling;

import com.a6raywa1cher.imageprocessingspring.transformations.SlaveTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.geometry.Point2D;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@SlaveTransformation
@Slf4j
public class NearestNeighborScalingTransformation implements Transformation {
	private final Point2D p1, p2, p3, p4;

	public NearestNeighborScalingTransformation(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		long start = System.currentTimeMillis();
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		byte[] source = new byte[width * height * 4];
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);
		byte[] target = Arrays.copyOf(source, width * height * 4);

		int fromX = (int) p1.getX();
		int fromY = (int) p1.getY();
		int fromW = (int) p2.getX() - fromX;
		int fromH = (int) p2.getY() - fromY;

		int toX = (int) p3.getX();
		int toY = (int) p3.getY();
		int toW = (int) p4.getX() - toX;
		int toH = (int) p4.getY() - toY;

		double scaleConstX = ((double) fromW) / toW;
		double scaleConstY = ((double) fromH) / toH;

		for (int x = toX; x < toX + toW; x++) {
			for (int y = toY; y < toY + toH; y++) {
				for (int channel = 0; channel < 3; channel++) {
					int targetCoord = toCoord(x, y, width, channel);
					double sourceX = fromX + (x - toX) * scaleConstX;
					double sourceY = fromY + (y - toY) * scaleConstY;
					int sourceCoord = toCoord((int) Math.round(sourceX), (int) Math.round(sourceY), width, channel);
					target[targetCoord] = source[sourceCoord];
				}
			}
		}
		writableImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), target, 0, 4 * width);

		log.info("{}: {}ms", this.getClass().getSimpleName(), System.currentTimeMillis() - start);
		return writableImage;
	}
}
