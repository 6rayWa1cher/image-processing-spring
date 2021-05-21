package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@Slf4j
public abstract class AbstractKernelCallbackTransformation implements Transformation {
	private final int kernelXOrigin;
	private final int kernelYOrigin;
	private final int kernelWidth;
	private final int kernelHeight;
	private ObjectProperty<Double> progressBarProperty;

	protected AbstractKernelCallbackTransformation(int kernelSize) {
		this.kernelWidth = kernelSize;
		this.kernelHeight = kernelSize;
		this.kernelXOrigin = (kernelSize - 1) / 2;
		this.kernelYOrigin = (kernelSize - 1) / 2;
	}

	protected abstract float getKernelCallback(int[] pixelData);

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		PixelReader pixelReader = writableImage.getPixelReader();
		WritablePixelFormat<ByteBuffer> pixelFormat = WritablePixelFormat.getByteBgraPreInstance();
		long start = System.currentTimeMillis();
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);

		WritableImage extendedImage = AlgorithmUtils.extendImageSecondStrategy(image, Math.max(kernelXOrigin, kernelYOrigin));
		int extendedWidth = getWidth(extendedImage);
		int extendedHeight = getHeight(extendedImage);
		PixelReader extendedImagePixelReader = extendedImage.getPixelReader();

		byte[] source = new byte[extendedWidth * extendedHeight * 4];
		byte[] target = new byte[width * height * 4];
		extendedImagePixelReader.getPixels(0, 0, extendedWidth, extendedHeight, pixelFormat, source, 0, extendedWidth * 4);
		pixelReader.getPixels(0, 0, width, height, pixelFormat, target, 0, width * 4);
		int[] dataToKernel = new int[kernelWidth * kernelHeight];

		double currentPercent = 0d;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int channel = 0; channel < 3; channel++) {
					int extendedCenterX = x + kernelXOrigin;
					int extendedCenterY = y + kernelYOrigin;
					for (int i = 0; i < kernelHeight; i++) {
						for (int j = 0; j < kernelWidth; j++) {
							int extendedCurrentX = extendedCenterX - kernelXOrigin + j;
							int extendedCurrentY = extendedCenterY - kernelYOrigin + i;
							int extendedCoord = toCoord(extendedCurrentX, extendedCurrentY, extendedWidth, channel);
							dataToKernel[kernelWidth * i + j] = Byte.toUnsignedInt(source[extendedCoord]);
						}
					}
					float sum = getKernelCallback(dataToKernel);
					int targetCoord = toCoord(x, y, width, channel);
					target[targetCoord] = (byte) (sum < 0 ? 0 : (sum > 255 ? 255 : sum));
				}
				double p = (double) (x * height + y) / (width * height);
				if (p - currentPercent >= 0.01d) {
					currentPercent = p;
					if (progressBarProperty != null) progressBarProperty.set(currentPercent);
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
