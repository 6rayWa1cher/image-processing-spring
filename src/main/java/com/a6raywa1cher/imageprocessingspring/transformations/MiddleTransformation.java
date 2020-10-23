package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.model.MiddleConfig;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@Slf4j
public class MiddleTransformation implements Transformation {
	private final int windowSize;

	public MiddleTransformation(MiddleConfig middleConfig) {
		windowSize = middleConfig.getWindowSize();
	}

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		long start = System.currentTimeMillis();
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();
		int windowOffset = (windowSize - 1) / 2;

		WritableImage extendedImage = AlgorithmUtils.extendImageSecondStrategy(image, windowOffset);
		int extendedWidth = getWidth(extendedImage);
		int extendedHeight = getHeight(extendedImage);
		PixelReader extendedImagePixelReader = extendedImage.getPixelReader();

		byte[] source = new byte[extendedWidth * extendedHeight * 4];
		byte[] target = new byte[width * height * 4];
		extendedImagePixelReader.getPixels(0, 0, extendedWidth, extendedHeight, WritablePixelFormat.getByteBgraPreInstance(), source, 0, extendedWidth * 4);
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), target, 0, width * 4);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int extendedXCenter = x + windowOffset;
				int extendedYCenter = y + windowOffset;
				long[] pixels = new long[windowSize * windowSize];
				for (int i = 0; i < windowSize; i++) {
					for (int j = 0; j < windowSize; j++) {
						int extendedX = extendedXCenter - windowOffset + j;
						int extendedY = extendedYCenter - windowOffset + i;
						int extendedCoords = (extendedY * extendedWidth + extendedX) * 4;
						long pixelIntensity = AlgorithmUtils.intensity(Byte.toUnsignedInt(source[extendedCoords + 2]),
							Byte.toUnsignedInt(source[extendedCoords + 1]), Byte.toUnsignedInt(source[extendedCoords]));
						pixels[i * windowSize + j] = (pixelIntensity << 32) |
							(((long) source[extendedCoords] & 0xFFL) << 24) |
							(((long) source[extendedCoords + 1] & 0xFFL) << 16) |
							(((long) source[extendedCoords + 2] & 0xFFL) << 8) |
							(((long) source[extendedCoords + 3] & 0xFFL));
					}
				}
				Arrays.sort(pixels);
				long middlePixel = pixels[(windowSize * windowSize - 1) / 2];
				int targetCoords = (y * width + x) * 4;
				target[targetCoords] = (byte) ((middlePixel >> 24) & 0xFF);
				target[targetCoords + 1] = (byte) ((middlePixel >> 16) & 0xFF);
				target[targetCoords + 2] = (byte) ((middlePixel >> 8) & 0xFF);
				target[targetCoords + 3] = (byte) ((middlePixel) & 0xFF);
			}
		}
		writableImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), target, 0, 4 * width);

		log.info("{}: {}ms", this.getClass().getSimpleName(), System.currentTimeMillis() - start);
		return writableImage;
	}
}
