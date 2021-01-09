package com.a6raywa1cher.imageprocessingspring.transformations;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.image.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.intensity;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;

@Slf4j
public class BinaryClusterTransformation implements Transformation {
	public final static int CLUSTERIZATION_MAX_ATTEMPTS = 20;
	public static final int CLUSTERIZATION_MIN_ATTEMPTS = 5;
	public static final boolean GRAY_SCALE_BEFORE = true;
	public static final int CLUSTER_DIM_MAX_DELTA = 100;
	public static final String STATUS_PREFIX = "BinaryClusterTransformation: ";
	private ObjectProperty<Double> progressBarProperty;
	private ObjectProperty<String> statusProperty;
	private Point3D whiteCluster;
	private Point3D blackCluster;

	public BinaryClusterTransformation() {
		do {
			generateRandomClusterPoints();
		} while (!checkClusterPoints());
	}

	private boolean checkClusterPoint(Point3D p) {
		double min = Stream.of(p.getX(), p.getY(), p.getZ()).min(Comparator.naturalOrder()).orElseThrow();
		double max = Stream.of(p.getX(), p.getY(), p.getZ()).max(Comparator.naturalOrder()).orElseThrow();
		return max - min < CLUSTER_DIM_MAX_DELTA;
	}

	private boolean checkClusterPoints() {
		return checkClusterPoint(whiteCluster) && checkClusterPoint(blackCluster);
	}

	private void generateRandomClusterPoints() {
		Random random = new Random();
		whiteCluster = new Point3D(
			random.nextInt(256),
			random.nextInt(256),
			random.nextInt(256)
		);
		blackCluster = new Point3D(
			random.nextInt(256),
			random.nextInt(256),
			random.nextInt(256)
		);
		if (blackCluster.distance(Point3D.ZERO) > whiteCluster.distance(Point3D.ZERO)) {
			Point3D copy = whiteCluster;
			whiteCluster = blackCluster;
			blackCluster = copy;
		}
	}

	private Map<Point3D, List<Point3D>> clusterize(List<Point3D> clusterCenters, byte[] image) {
		int totalPixels = image.length / 4;
		Map<Point3D, List<Point3D>> out = new HashMap<>();
		for (Point3D p : clusterCenters) {
			out.put(p, new ArrayList<>(totalPixels / clusterCenters.size()));
		}

		for (int i = 0; i < image.length; i += 4) {
			Point3D pixel = new Point3D(
				Byte.toUnsignedInt(image[i]),
				Byte.toUnsignedInt(image[i + 1]),
				Byte.toUnsignedInt(image[i + 2])
			);
			Point3D currCluster = clusterCenters.get(0);
			double currDistance = currCluster.distance(pixel);
			for (Point3D p : clusterCenters) {
				double distance = p.distance(pixel);
				if (distance < currDistance) {
					currCluster = p;
					currDistance = distance;
				}
			}
			out.get(currCluster).add(pixel);
		}

		return out;
	}

	private List<Point3D> centerClusterCenters(Map<Point3D, List<Point3D>> clusters) {
		List<Point3D> out = new ArrayList<>();
		for (Point3D p : clusters.keySet()) {
			Point3D currMiddle = p;
			for (Point3D p2 : clusters.get(p)) {
				currMiddle = currMiddle.midpoint(p2);
			}
			out.add(currMiddle);
		}
		return out;
	}

	private void calculateClusterCoords(byte[] source) {
		Point3D prevWhiteCluster, prevBlackCluster;
		int attempts = 0;
		do {
			statusProperty.set(STATUS_PREFIX + "cluster gen attempt " + (attempts + 1));
			prevWhiteCluster = whiteCluster;
			prevBlackCluster = blackCluster;
			List<Point3D> clusterCenters = List.of(this.whiteCluster, blackCluster);

			Map<Point3D, List<Point3D>> clusters = clusterize(clusterCenters, source);

			List<Point3D> centered = centerClusterCenters(clusters);

			whiteCluster = centered.get(0);
			blackCluster = centered.get(1);
			if (blackCluster.distance(Point3D.ZERO) > whiteCluster.distance(Point3D.ZERO)) {
				whiteCluster = centered.get(1);
				blackCluster = centered.get(0);
			}
			attempts++;
		} while (
			(
				prevWhiteCluster.distance(whiteCluster) > 0.5 ||
					prevBlackCluster.distance(blackCluster) > 0.5 ||
					attempts < CLUSTERIZATION_MIN_ATTEMPTS
			) && attempts < CLUSTERIZATION_MAX_ATTEMPTS
		);
		log.info("Clusters on {}: black:{}, white:{}", attempts, blackCluster, whiteCluster);
	}

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		long start = System.currentTimeMillis();
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		byte[] source = new byte[width * height * 4];
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);
		byte[] target = new byte[width * height * 4];

		calculateClusterCoords(source);

		statusProperty.set(STATUS_PREFIX + "image conversion");

		log.info("Clusters generation time: {}ms", System.currentTimeMillis() - start);

		double currentPercent = 0d;

		int pixelCount = source.length / 4;
		for (int i = 0; i < source.length; i += 4) {
			int c1 = Byte.toUnsignedInt(source[i]);
			int c2 = Byte.toUnsignedInt(source[i + 1]);
			int c3 = Byte.toUnsignedInt(source[i + 2]);
			if (GRAY_SCALE_BEFORE) {
				int intensity = intensity(c1, c2, c3);
				c1 = c2 = c3 = intensity;
			}
			Point3D pixel = new Point3D(c1, c2, c3);
			target[i] = pixel.distance(blackCluster) < pixel.distance(whiteCluster) ? (byte) 0 : (byte) 255;
			target[i + 1] = target[i];
			target[i + 2] = target[i];
			target[i + 3] = source[i + 3];

			double p = (double) i / pixelCount;
			if (p - currentPercent >= 0.01d) {
				currentPercent = p;
				progressBarProperty.set(currentPercent);
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

	@Override
	public void setStatusProperty(ObjectProperty<String> statusProperty) {
		this.statusProperty = statusProperty;
	}
}
