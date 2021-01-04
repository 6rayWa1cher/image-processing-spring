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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
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
	private final List<Class<? extends Transformation>> order;

	@Autowired
	public ImageProcessingServiceImpl(ImageRepository imageRepository, List<Class<? extends Transformation>> transformations) {
		this.imageRepository = imageRepository;
		this.order = transformations;
	}

	private ImageBundle convertImage(Image before, boolean preview) {
		Map<Class<?>, Config> allConfigs = imageRepository.getAllConfigs();
		Image[] after = {before};
		if (preview) {
			allConfigs
				.entrySet()
				.stream()
				.sorted(Comparator.comparing(e -> order.indexOf(e.getValue().getMainTransformation())))
				.map(Map.Entry::getValue)
				.forEach(o -> {
					if (o.isPreviewEnabled()) {
						Transformation transformation = initTransformation(o.getMainTransformation(), allConfigs);
						after[0] = transformation.transform(after[0]);
						log.info("Appended " + transformation.getClass().getSimpleName());
					}
				});
		}
		return new ImageBundle(before, after[0], calculateHistogram(after[0]));
	}

	private <T extends Transformation> T initTransformation(Class<T> transformationClass, Map<Class<?>, Config> allConfigs) {
		for (Constructor<?> constructor : transformationClass.getDeclaredConstructors()) {
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			if (Arrays.stream(parameterTypes).allMatch(allConfigs::containsKey)) {
				try {
					return (T) constructor.newInstance(Arrays.stream(parameterTypes)
						.map(allConfigs::get)
						.toArray());
				} catch (Exception e) {
					log.error("Got exception during initialization", e);
				}
			}
		}
		throw new IllegalArgumentException("Couldn't initialize " + transformationClass.getSimpleName());
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
		Map<Class<?>, Config> allConfigs = imageRepository.getAllConfigs();
		ImageBundle imageBundle = imageRepository.getImageBundle();
		Transformation transformation = initTransformation(config.getMainTransformation(), allConfigs);
		Image newImage = transformation.transform(imageBundle.getCurrentImage());
		convertAndSave(newImage, true);
	}

	@SneakyThrows
	public static void saveToFile(Image img, File file) {
		BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
		File file1;
		try {
			file1 = new File(new URL(file.toString()).toURI());
		} catch (MalformedURLException e) {
			file1 = file;
		}
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
	public void saveFile() {
		saveToFile(imageRepository.getImageBundle().getCurrentImage(), new File(imageRepository.getImageURL()));
	}

	@Override
	public void saveToFile(File file) {
		saveToFile(imageRepository.getImageBundle().getCurrentImage(), file);
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
