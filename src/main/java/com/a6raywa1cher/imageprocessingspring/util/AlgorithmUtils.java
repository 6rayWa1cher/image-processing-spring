package com.a6raywa1cher.imageprocessingspring.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

public class AlgorithmUtils {
	public static int intensity(int r, int g, int b) {
		return intensity(r, g, b, 0.3, 0.59, 0.11);
	}

	public static int channelToInt(double val) {
		return (int) (val * 255);
	}

	public static int intensity(int r, int g, int b, double rw, double gw, double bw) {
		return Math.min(255, (int) Math.round(rw * r + gw * g + bw * b));
	}

	public static byte borderPixel(int pixel) {
		return pixel > 255 ? (byte) 255 : (pixel < 0 ? 0 : (byte) pixel);
	}

	public static int getPixel(byte[] image, int x, int y, int width, int channel) {
		return Byte.toUnsignedInt(image[toCoord(x, y, width, channel)]);
	}

	public static double getDiagonal(int width, int height) {
		return Math.sqrt(width * width + height * height);
	}

	public static WritableImage extendImageSecondStrategy(Image image, int byPixels) {
		WritableImage writableImage = imageToWriteable(image);
		PixelReader pixelReader = writableImage.getPixelReader();
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);

		// generate extended image
		WritableImage extendedImage = new WritableImage(width + 2 * byPixels, height + 2 * byPixels);
		int extendedHeight = getHeight(extendedImage);
		int extendedWidth = getWidth(extendedImage);
		PixelWriter extendedImagePixelWriter = extendedImage.getPixelWriter();
		extendedImagePixelWriter.setPixels(byPixels, byPixels, width, height, pixelReader, 0, 0);
		// copy above line
		for (int i = 0; i < byPixels; i++) {
			extendedImagePixelWriter.setPixels(
				byPixels, i,
				width, 1,
				pixelReader,
				0, 0);
		}
		// copy bottom line
		for (int i = 0; i < byPixels; i++) {
			extendedImagePixelWriter.setPixels(
				byPixels, extendedHeight - 1 - i,
				width, 1,
				pixelReader,
				0, height - 1);
		}
		PixelReader extendedImagePixelReader = extendedImage.getPixelReader();
		// copy left line
		for (int i = 0; i < byPixels; i++) {
			extendedImagePixelWriter.setPixels(
				i, 0,
				1, extendedHeight,
				extendedImagePixelReader,
				byPixels, 0);
		}
		// copy right line
		for (int i = 0; i < byPixels; i++) {
			extendedImagePixelWriter.setPixels(
				extendedWidth - 1 - i, 0,
				1, extendedHeight,
				extendedImagePixelReader,
				extendedWidth - 1 - byPixels, 0);
		}
		return extendedImage;
	}
}
