package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessInformation;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.model.NegativeInformation;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.a6raywa1cher.imageprocessingspring.util.HeapExecutor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import lombok.SneakyThrows;
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
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	private double normalize(double value, int... elements) {
		return value / Arrays.stream(elements).sum();
	}

	private ImageBundle convertImage(Image before, boolean preview) {
		GrayScaleInformation grayScaleInformation = imageRepository.getGrayScaleInformation();
		BrightnessInformation brightnessInformation = imageRepository.getBrightnessInformation();
		NegativeInformation negativeInformation = imageRepository.getNegativeInformation();
		Image after = before;
		if (preview) {
			if (grayScaleInformation.isPreview()) {
				after = grayScale(after,
					grayScaleInformation.getRedSlider(),
					grayScaleInformation.getGreenSlider(),
					grayScaleInformation.getBlueSlider(),
					grayScaleInformation.getBaseColor());
			}
			if (brightnessInformation.isPreview()) {
				after = brightness(after,
					brightnessInformation.getDelta());
			}
			if (negativeInformation.isPreview()) {
				after = negative(after,
					negativeInformation.getThreshold());
			}
		}
		return new ImageBundle(before, after, calculateHistogram(after));
	}

	private void convertAndSave(Image before, boolean preview) {
		CompletableFuture.runAsync(() -> {
			int version = imageRepository.getImageBundleVersion();
			imageRepository.setImageBundle(convertImage(before, preview), version);
		}, executor);
	}

	private double[] calculateHistogram(Image image) {
		PixelReader pixelReader = image.getPixelReader();
//		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//		int[] buffer = bufferedImage.getData().getPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[]) null);
		int[] statistics = new int[256];
		int width = getWidth(image);
		int height = getHeight(image);
		ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * 4);
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), byteBuffer, width * 4);
		byte[] pixels = byteBuffer.array();
		for (int i = 0; i < width * height; i++) {
			int offset = 4 * i;
			int intensity = (int) Math.min(
				0.30d * Byte.toUnsignedInt(pixels[offset + 2]) +
					0.59d * Byte.toUnsignedInt(pixels[offset + 1]) +
					0.11d * Byte.toUnsignedInt(pixels[offset]),
				255
			);
			statistics[intensity] += 1;
		}
		double[] out = new double[256];
		for (int i = 0; i < statistics.length; i++) {
			out[i] = normalize(statistics[i], statistics);
		}
		log.info("histogram: {}", Arrays.stream(out).mapToObj(Double::toString).map(s -> s.substring(0, Math.min(4, s.length()))).collect(Collectors.joining(",")));
		return out;
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
	public void setBrightnessInformation(BrightnessInformation brightnessInformation) {
		imageRepository.setBrightnessInformation(brightnessInformation);
		convertAndSave(imageRepository.getImageBundle().getCurrentImage(), true);
	}

	@Override
	public void applyBrightnessInformation(BrightnessInformation brightnessInformation) {
		imageRepository.setBrightnessInformation(brightnessInformation);
		ImageBundle imageBundle = imageRepository.getImageBundle();
		Image newImage = brightness(imageBundle.getCurrentImage(), brightnessInformation.getDelta());
		convertAndSave(newImage, true);
	}

	@Override
	public void setNegativeInformation(NegativeInformation negativeInformation) {
		imageRepository.setNegativeInformation(negativeInformation);
		convertAndSave(imageRepository.getImageBundle().getCurrentImage(), true);
	}

	@Override
	public void applyNegativeInformation(NegativeInformation negativeInformation) {
		imageRepository.setNegativeInformation(negativeInformation);
		ImageBundle imageBundle = imageRepository.getImageBundle();
		Image newImage = negative(imageBundle.getCurrentImage(), negativeInformation.getThreshold());
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

	private Image lookupTransform(String transformName, Image image, BiFunction<int[], int[], int[]> transformation) {
		WritableImage writableImage = imageToWriteable(image);
		long start = System.currentTimeMillis();
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
		LookupTable lookupTable = new LookupTable(0, 4) {
			@Override
			public int[] lookupPixel(int[] src, int[] dest) {
				return transformation.apply(src, dest);
			}
		};
		LookupOp op = new LookupOp(lookupTable, new RenderingHints(null));
		op.filter(bufferedImage, bufferedImage);
		WritableImage out = SwingFXUtils.toFXImage(bufferedImage, writableImage);

		log.info("{}: {}ms", transformName, System.currentTimeMillis() - start);
		return out;
	}

	public Image grayScale(Image image, double redWeight, double greenWeight, double blueWeight, GrayScaleInformation.BaseColor baseColor) {
		double normalizedRedWeight = normalize(redWeight, redWeight, greenWeight, blueWeight);
		double normalizedGreenWeight = normalize(greenWeight, redWeight, greenWeight, blueWeight);
		double normalizedBlueWeight = normalize(blueWeight, redWeight, greenWeight, blueWeight);
		return lookupTransform("grayScale", image, (src, dest) -> {
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
		});
	}

	public Image brightness(Image image, double delta) {
		Function<Double, Integer> localNormalize = (value) -> value < 0 ? 0 : (value > 255 ? 255 : value.intValue());
		return lookupTransform("brightness", image, (src, dest) -> {
			dest[0] = localNormalize.apply((double) src[0] + delta);
			dest[1] = localNormalize.apply((double) src[1] + delta);
			dest[2] = localNormalize.apply((double) src[2] + delta);
			return dest;
		});
	}

	public Image negative(Image image, double threshold) {
		return lookupTransform("negative", image, (src, dest) -> {
			for (int i = 0; i < 3; i++) {
				dest[i] = src[i] >= threshold ? 255 - src[i] : src[i];
			}
			return dest;
		});
	}

	@SneakyThrows
	private void saveToFile(Image img, File file) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
		File file1 = new File(new URL(file.toString()).toURI());
		try {
			String extension = StringUtils.getFilenameExtension(file1.getAbsolutePath());
			if (!ImageIO.write(bImage, extension != null ? extension : "png", file1)) {
				ImageIO.write(bImage, "png", file1);
			}
		} catch (IOException e) {
			try {
				ImageIO.write(bImage, "png", file1);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
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
