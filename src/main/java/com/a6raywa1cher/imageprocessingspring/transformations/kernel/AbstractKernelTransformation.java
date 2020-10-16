package com.a6raywa1cher.imageprocessingspring.transformations.kernel;

import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.Kernel;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@Slf4j
public abstract class AbstractKernelTransformation implements Transformation {
	protected abstract Kernel getKernel();

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		PixelReader pixelReader = writableImage.getPixelReader();
		long start = System.currentTimeMillis();
		int width = getWidth(writableImage);
		int height = getHeight(writableImage);
		Kernel kernel = getKernel();

		// generate extended image
		WritableImage extendedImage = new WritableImage(width + 2 * kernel.getXOrigin(), height + 2 * kernel.getYOrigin());
		int extendedHeight = getHeight(extendedImage);
		int extendedWidth = getWidth(extendedImage);
		PixelWriter extendedImagePixelWriter = extendedImage.getPixelWriter();
		extendedImagePixelWriter.setPixels(kernel.getXOrigin(), kernel.getYOrigin(), width, height, pixelReader, 0, 0);
		// copy above line
		for (int i = 0; i < kernel.getYOrigin(); i++) {
			extendedImagePixelWriter.setPixels(
				kernel.getXOrigin(), i,
				width, 1,
				pixelReader,
				0, 0);
		}
		// copy bottom line
		for (int i = 0; i < kernel.getYOrigin(); i++) {
			extendedImagePixelWriter.setPixels(
				kernel.getXOrigin(), extendedHeight - 1 - i,
				width, 1,
				pixelReader,
				0, height - 1);
		}
		PixelReader extendedImagePixelReader = extendedImage.getPixelReader();
		// copy left line
		for (int i = 0; i < kernel.getXOrigin(); i++) {
			extendedImagePixelWriter.setPixels(
				i, 0,
				1, extendedHeight,
				extendedImagePixelReader,
				kernel.getXOrigin(), 0);
		}
		// copy right line
		for (int i = 0; i < kernel.getXOrigin(); i++) {
			extendedImagePixelWriter.setPixels(
				extendedWidth - 1 - i, 0,
				1, extendedHeight,
				extendedImagePixelReader,
				extendedWidth - 1 - kernel.getXOrigin(), 0);
		}
		byte[] source = new byte[extendedWidth * extendedHeight * 4];
		byte[] target = new byte[width * height * 4];
		extendedImagePixelReader.getPixels(0, 0, extendedWidth, extendedHeight, WritablePixelFormat.getByteBgraPreInstance(), source, 0, extendedWidth * 4);
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), target, 0, width * 4);

		float[] kernelData = new float[kernel.getWidth() * kernel.getHeight()];
		kernel.getKernelData(kernelData);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int channel = 0; channel < 3; channel++) {
					int extendedCenterX = x + kernel.getXOrigin();
					int extendedCenterY = y + kernel.getYOrigin();
					float sum = 0;
					for (int i = 0; i < kernel.getHeight(); i++) {
						for (int j = 0; j < kernel.getWidth(); j++) {
							int kernelCoord = i * kernel.getWidth() + j;
							int extendedCurrentX = extendedCenterX - kernel.getXOrigin() + j;
							int extendedCurrentY = extendedCenterY - kernel.getYOrigin() + i;
							int extendedCoord = (extendedCurrentY * extendedWidth + extendedCurrentX) * 4 + channel;
							sum += kernelData[kernelCoord] * Byte.toUnsignedInt(source[extendedCoord]);
						}
					}
					int targetCoord = (y * width + x) * 4 + channel;
					target[targetCoord] = (byte) (sum < 0 ? 0 : (sum > 255 ? 255 : sum));
//					target[targetCoord] = (byte) sum;
				}
			}
		}
		writableImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), target, 0, 4 * width);


		log.info("{}: {}ms", this.getClass().getSimpleName(), System.currentTimeMillis() - start);
		return writableImage;
	}
}
