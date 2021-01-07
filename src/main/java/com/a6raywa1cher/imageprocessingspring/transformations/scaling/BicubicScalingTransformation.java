package com.a6raywa1cher.imageprocessingspring.transformations.scaling;

import com.a6raywa1cher.imageprocessingspring.transformations.SlaveTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.*;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@SlaveTransformation
@Slf4j
public class BicubicScalingTransformation implements Transformation {
	private final Point2D p1, p2, p3, p4;
	private ObjectProperty<Double> progressBarProperty;

	public BicubicScalingTransformation(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	private double det(double[][] m) {
		return (
			m[0][0] * (
				m[1][1] * m[2][2] - m[1][2] * m[2][1]
			)
		) - (
			m[0][1] * (
				m[1][0] * m[2][2] - m[1][2] * m[2][0]
			)
		) + (
			m[0][2] * (
				m[1][0] * m[2][1] - m[1][1] * m[2][0]
			)
		);
	}

	private double[] kramerSolver(double[][] matrixData, double[] vectorData) {
		double[][][] subMatrix = new double[3][3][];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				subMatrix[i][j] = Arrays.copyOf(matrixData[j], 3);
			}
			for (int j = 0; j < 3; j++) {
				subMatrix[i][j][i] = vectorData[j];
			}
		}
		double det = det(matrixData);
		return new double[]{
			det(subMatrix[0]) / det,
			det(subMatrix[1]) / det,
			det(subMatrix[2]) / det
		};
	}

	private double cubicInterpolation(Map<Integer, Double> coordToVal, double targetCoord) {
		List<Integer> xValues = coordToVal.keySet().stream().sorted().collect(Collectors.toList());
		double[][] matrixData = new double[3][3];
		double[] vectorData = new double[3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int b = 3 - j;
				matrixData[i][j] = Math.pow(xValues.get(i + 1), b) - Math.pow(xValues.get(0), b);
			}
			vectorData[i] = coordToVal.get(xValues.get(i + 1)) - coordToVal.get(xValues.get(0));
		}
		double[] abc = kramerSolver(matrixData, vectorData);
		double a = abc[0], b = abc[1], c = abc[2];
		int x1 = xValues.get(0);
		double y1 = coordToVal.get(x1);
		double d = y1 - (a * Math.pow(x1, 3) + b * Math.pow(x1, 2) + c * x1);
		return a * Math.pow(targetCoord, 3) + b * Math.pow(targetCoord, 2) + c * targetCoord + d;
	}

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		PixelReader pixelReader = writableImage.getPixelReader();
		WritablePixelFormat<ByteBuffer> pixelFormat = WritablePixelFormat.getByteBgraPreInstance();
		long start = System.currentTimeMillis();
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);

		WritableImage extendedImage = extendImageSecondStrategy(image, 1);
		int coordCorrectionDelta = 1;
		int extendedWidth = getWidth(extendedImage);
		int extendedHeight = getHeight(extendedImage);
		PixelReader extendedImagePixelReader = extendedImage.getPixelReader();

		int fromX = (int) p1.getX() + coordCorrectionDelta;
		int fromY = (int) p1.getY() + coordCorrectionDelta;
		int fromW = (int) p2.getX() - (fromX - coordCorrectionDelta);
		int fromH = (int) p2.getY() - (fromY - coordCorrectionDelta);

		int toX = (int) p3.getX();
		int toY = (int) p3.getY();
		int toW = (int) p4.getX() - toX;
		int toH = (int) p4.getY() - toY;

		double scaleConstX = ((double) fromW) / toW;
		double scaleConstY = ((double) fromH) / toH;

		byte[] source = new byte[extendedWidth * extendedHeight * 4];
		byte[] target = new byte[width * height * 4];
		extendedImagePixelReader.getPixels(0, 0, extendedWidth, extendedHeight, pixelFormat, source, 0, extendedWidth * 4);
		pixelReader.getPixels(0, 0, width, height, pixelFormat, target, 0, width * 4);

		double currentPercent = 0d;

		for (int x = toX; x < toX + toW; x++) {
			for (int y = toY; y < toY + toH; y++) {
				for (int channel = 0; channel < 3; channel++) {
					int targetCoord = toCoord(x, y, width, channel);
					double sourceX = fromX + (x - toX) * scaleConstX;
					double sourceY = fromY + (y - toY) * scaleConstY;

					int gridStartX = (int) sourceX - 1;
					int gridStartY = (int) sourceY - 1;

					double[] verticalInterpolated = new double[4];
					for (int i = 0; i < 4; i++) {
						Map<Integer, Double> values = new HashMap<>();
						for (int j = 0; j < 4; j++) {
							values.put(j, (double) getPixel(source, gridStartX + i, gridStartY + j, extendedWidth, channel));
						}
						verticalInterpolated[i] = cubicInterpolation(values, sourceY - gridStartY);
					}
					target[targetCoord] = borderPixel((int) Math.round(cubicInterpolation(Map.of(
						0, verticalInterpolated[0],
						1, verticalInterpolated[1],
						2, verticalInterpolated[2],
						3, verticalInterpolated[3]
					), sourceX - gridStartX)));
				}
				double p = (double) ((x - toX) * toH + (y - toY)) / (toW * toH);
				if (p - currentPercent >= 0.01d) {
					currentPercent = p;
					progressBarProperty.set(currentPercent);
				}
			}
		}
		writableImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), target, 0, 4 * width);

		log.info("{}: {}ms", this.getClass().getSimpleName(), System.currentTimeMillis() - start);
		return writableImage;
	}

	@Override
	public void setProgressBarProperty(ObjectProperty<Double> progressBarProperty) {
		this.progressBarProperty = progressBarProperty;
	}
}
