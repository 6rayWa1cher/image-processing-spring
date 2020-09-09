package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private final ImageRepository imageRepository;

	@Autowired
	public ImageProcessingServiceImpl(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

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

	private ImageBundle convertImage(Image before, boolean preview) {
		GrayScaleInformation information = imageRepository.getGrayScaleInformation();
		Image after = before;
		if (preview && information.isPreview()) {
			after = grayScale(before, information.getRedSlider(), information.getGreenSlider(), information.getBlueSlider());
		}
		return new ImageBundle(before, after);
	}

	private void convertAndSave(Image before, boolean preview) {
		CompletableFuture.runAsync(() -> {
			int version = imageRepository.getImageBundleVersion();
			imageRepository.setImageBundle(convertImage(before, preview), version);
		});
	}

	@Override
	public void setGrayScaleInformation(GrayScaleInformation information) {
		imageRepository.setGrayScaleInformation(information);
		convertAndSave(imageRepository.getImageBundle().getCurrentImage(), true);
	}

	@Override
	public void applyGrayScaleInformation(GrayScaleInformation grayScaleInformation) {
		imageRepository.setGrayScaleInformation(grayScaleInformation);
		ImageBundle imageBundle = imageRepository.getImageBundle();
		Image newImage = grayScale(imageBundle.getCurrentImage(), grayScaleInformation.getRedSlider(), grayScaleInformation.getGreenSlider(), grayScaleInformation.getBlueSlider());
		convertAndSave(newImage, true);
	}

	public Image grayScale(Image image, double redWeight, double greenWeight, double blueWeight) {
		WritableImage writableImage = imageToWriteable(image);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		PixelReader pixelReader = writableImage.getPixelReader();
		double normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		double normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		double normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
//		List<AlgorithmUtils.Rectangle> regions = AlgorithmUtils.partition(
//			getWidth(writableImage), getHeight(writableImage), 250, 250);
//		ByteBuffer byteBuffer = ByteBuffer.allocate(writableImage.getPixelReader().getPixelFormat().getType().);
//		BufferedImage bImage = SwingFXUtils.fromFXImage(writableImage, null);
//		regions.parallelStream()
//			.map(rectangle -> {
//				int h = rectangle.getH();
//				int w = rectangle.getW();
//				PixelFormat pixelFormat = pixelWriter.getPixelFormat();
//				writableImage.getPixelReader().getPixelFormat().getType()
//				for (int i = 0; i < w; i++) {
//					for (int j = 0; j < h; j++) {
//						Color color = pixelReader.getColor(i, j);
//						double intensity = normalizedRedWeight * color.getRed() +
//							normalizedGreenWeight * color.getGreen() +
//							normalizedBlueWeight * color.getBlue();
//						if (intensity > 1) intensity = 1;
//
//					}
//				}
//			})
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

//		saveToFile(writableImage, new File("C:\\Users\\6rayWa1cher\\IdeaProjects\\image-processing\\image-processing-javafx\\2.png"));
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

	@Override
	public void openFile(Image image, String url) {
		convertAndSave(image, true);
	}

	@Override
	public void openFile(String url) {
		log.info("Trying to open url {}", url);
		Image image = new Image(url);
		convertAndSave(image, true);
	}
}
