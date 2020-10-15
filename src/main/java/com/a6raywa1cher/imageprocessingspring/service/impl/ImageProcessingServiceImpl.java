package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.Config;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;
import com.a6raywa1cher.imageprocessingspring.util.HeapExecutor;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.util.Pair;
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
import java.util.Comparator;
import java.util.List;
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
	private final List<Class<?>> order;

	@Autowired
	public ImageProcessingServiceImpl(ImageRepository imageRepository, List<Pair<Class<?>, Config>> container) {
		this.imageRepository = imageRepository;
		this.order = container.stream().map(Pair::getKey).collect(Collectors.toList());
	}

	private ImageBundle convertImage(Image before, boolean preview) {
		Map<Class<?>, Config> allConfigs = imageRepository.getAllConfigs();
		Image[] after = {before};
		if (preview) {
			allConfigs
				.entrySet()
				.stream()
				.sorted(Comparator.comparing(e -> order.indexOf(e.getKey())))
				.map(Map.Entry::getValue)
				.forEach(o -> {
					if (o.isPreviewEnabled()) {
						Transformation transformation = o.getTransformation();
						after[0] = transformation.transform(after[0]);
						log.info("Appended " + transformation.getClass().getSimpleName());
					}
				});
		}
		return new ImageBundle(before, after[0], calculateHistogram(after[0]));
	}

	private void convertAndSave(Image before, boolean preview) {
		CompletableFuture.runAsync(() -> {
			int version = imageRepository.getImageBundleVersion();
			imageRepository.setImageBundle(convertImage(before, preview), version);
		}, executor)
			.exceptionally(e -> {
				log.error("Exception during converting", e);
				return null;
			});
	}

	private double[] calculateHistogram(Image image) {
		PixelReader pixelReader = image.getPixelReader();
		long start = System.currentTimeMillis();
		int[] statistics = new int[256];
		int width = getWidth(image);
		int height = getHeight(image);
		ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * 4);
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), byteBuffer, width * 4);
		byte[] pixels = byteBuffer.array();
		for (int i = 0; i < width * height; i++) {
			int offset = 4 * i;
			int intensity = AlgorithmUtils.intensity(
				Byte.toUnsignedInt(pixels[offset + 2]),
				Byte.toUnsignedInt(pixels[offset + 1]),
				Byte.toUnsignedInt(pixels[offset])
			);
			statistics[intensity] += 1;
		}
		double[] out = new double[256];
		for (int i = 0; i < statistics.length; i++) {
			out[i] = normalize(statistics[i], statistics);
		}
		if (log.isTraceEnabled())
			log.trace("histogram: {}", Arrays.stream(out).mapToObj(Double::toString).map(s -> s.substring(0, Math.min(4, s.length()))).collect(Collectors.joining(",")));
		log.info("histogram: {}ms", System.currentTimeMillis() - start);
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
		Transformation transformation = config.getTransformation();
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
