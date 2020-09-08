package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private int getWidth(Image image) {
		return (int) Math.ceil(image.getWidth());
	}

	private int getHeight(Image image) {
		return (int) Math.ceil(image.getHeight());
	}

	private WritableImage imageToWriteable(Image image) {
		return new WritableImage(image.getPixelReader(),
			getWidth(image), getHeight(image));
	}

	private double normalize(double value, double... elements) {
		return value / Arrays.stream(elements).sum();
	}

	public Image grayScale(Image image, double redWeight, double greenWeight, double blueWeight) {
		WritableImage writableImage = imageToWriteable(image);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		PixelReader pixelReader = writableImage.getPixelReader();
		double normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		double normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		double normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
		for (int i = 0; i < getWidth(image); i++) {
			for (int j = 0; j < getHeight(image); j++) {
				Color color = pixelReader.getColor(i, j);
				double intensity = normalizedRedWeight * color.getRed() +
					normalizedGreenWeight * color.getGreen() +
					normalizedBlueWeight * color.getBlue();
				if (intensity > 1) intensity = 1;
				pixelWriter.setColor(i, j, new Color(intensity, intensity, intensity, 1d));
			}
		}
		saveToFile(writableImage, new File("C:\\Users\\6rayWa1cher\\IdeaProjects\\image-processing\\image-processing-javafx\\2.png"));
		return writableImage;
	}

	public void saveToFile(Image img, File file) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
		try {
			ImageIO.write(bImage, "png", file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
