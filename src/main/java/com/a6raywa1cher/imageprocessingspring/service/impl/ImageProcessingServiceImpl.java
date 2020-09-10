package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.a6raywa1cher.imageprocessingspring.util.HeapExecutor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private final ImageRepository imageRepository;
	private final Executor executor = new HeapExecutor();

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
			after = grayScale(before, information.getRedSlider(), information.getGreenSlider(), information.getBlueSlider(),
				information.getBaseColor());
		}
		return new ImageBundle(before, after);
	}

	private void convertAndSave(Image before, boolean preview) {
		CompletableFuture.runAsync(() -> {
			int version = imageRepository.getImageBundleVersion();
			imageRepository.setImageBundle(convertImage(before, preview), version);
		}, executor);
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
		Image newImage = grayScale(imageBundle.getCurrentImage(), grayScaleInformation.getRedSlider(),
			grayScaleInformation.getGreenSlider(), grayScaleInformation.getBlueSlider(), grayScaleInformation.getBaseColor());
		convertAndSave(newImage, true);
	}

	@Override
	public void saveFile() {
		this.saveToFile(imageRepository.getImageBundle().getCurrentImage(), new File(imageRepository.getImageURL()));
	}

	@Override
	public void saveToFile(File file) {
		this.saveToFile(imageRepository.getImageBundle().getCurrentImage(), file);
	}

	public Image grayScale(Image image, double redWeight, double greenWeight, double blueWeight, GrayScaleInformation.BaseColor baseColor) {
		WritableImage writableImage = imageToWriteable(image);
		double normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		double normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		double normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
		long start = System.currentTimeMillis();

		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

		LookupTable lookupTable = new LookupTable(0, 4) {
			@Override
			public int[] lookupPixel(int[] src, int[] dest) {
				int intensity = (int) (normalizedRedWeight * src[0] +
					normalizedGreenWeight * src[1] +
					normalizedBlueWeight * src[2]);
				switch (baseColor) {
					case RED -> {
						dest[0] = intensity;
						dest[1] = 0x00;
						dest[2] = 0x00;
					}
					case GREEN -> {
						dest[0] = 0x00;
						dest[1] = intensity;
						dest[2] = 0x00;
					}
					case BLUE -> {
						dest[0] = 0x00;
						dest[1] = 0x00;
						dest[2] = intensity;
					}
					case BLACK -> {
						dest[0] = intensity;
						dest[1] = intensity;
						dest[2] = intensity;
					}
				}
				return dest;
			}
		};
		LookupOp op = new LookupOp(lookupTable, new RenderingHints(null));
		op.filter(bufferedImage, bufferedImage);
		WritableImage out = SwingFXUtils.toFXImage(bufferedImage, writableImage);

		log.info("grayscale: {}ms", System.currentTimeMillis() - start);
		return out;
	}

	private void saveToFile(Image img, File file) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
		try {
			String extension = StringUtils.getFilenameExtension(file.getAbsolutePath());
			if (!ImageIO.write(bImage, extension != null ? extension : "png", file)) {
				ImageIO.write(bImage, "png", file);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void openFile(Image image, String url) {
		convertAndSave(image, true);
		imageRepository.setImageURL(url);
	}

	@Override
	public void openFile(String url) {
		log.info("Trying to open url {}", url);
		Image image = new Image(url);
		openFile(image, url);
	}
}
