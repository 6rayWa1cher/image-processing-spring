package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.Config;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.util.HeapExecutor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private final ImageRepository imageRepository;
	private final Executor executor = new HeapExecutor();

	@Autowired
	public ImageProcessingServiceImpl(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	private ImageBundle convertImage(Image before, boolean preview) {
		Map<Class<?>, Config> allConfigs = imageRepository.getAllConfigs();
		Image after = before;
		if (preview) {
			for (Map.Entry<Class<?>, Config> entry : allConfigs.entrySet()) {
				Config o = entry.getValue();
				if (o.isPreviewEnabled()) {
					Transformation<?> transformation = o.getTransformation();
					after = transformation.transform(after);
					log.info("Appended " + transformation.getClass().getSimpleName());
				}
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
	public <T extends Config> void setConfig(T config, Class<T> tClass) {
		imageRepository.setConfig(config, tClass);
		convertAndSave(imageRepository.getImageBundle().getCurrentImage(), true);
	}

	@Override
	public <T extends Config> void applyConfig(T config, Class<T> tClass) {
		imageRepository.setConfig(config, tClass);
		ImageBundle imageBundle = imageRepository.getImageBundle();
		Transformation<?> transformation = config.getTransformation();
		Image newImage = transformation.transform(imageBundle.getCurrentImage());
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
