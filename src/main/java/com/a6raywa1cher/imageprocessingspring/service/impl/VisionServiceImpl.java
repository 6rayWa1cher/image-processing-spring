package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.BinaryConfig;
import com.a6raywa1cher.imageprocessingspring.model.KirschConfig;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.Circle;
import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import com.a6raywa1cher.imageprocessingspring.service.dto.ObjectSearchResult;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.KirschKernelTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.point.BinaryTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.scaling.NearestNeighborScalingTransformation;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.util.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static com.a6raywa1cher.imageprocessingspring.service.impl.ImageProcessingServiceImpl.saveToFile;
import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.*;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.*;
import static java.lang.Math.*;

@Service
@Slf4j
public class VisionServiceImpl implements VisionService {
	private boolean isBlack(int x, int y, byte[] source, int width) {
		int red = getPixel(source, x, y, width, 2);
		int green = getPixel(source, x, y, width, 1);
		int blue = getPixel(source, x, y, width, 0);
		int intensity = intensity(red, green, blue);

		return intensity < 128;
	}


	private int getMaxOnBorder(int[][] lineStatistics, int y0, int x0, int borderSize, boolean useDegModOnY) {
		int currMax = 0;
		int height = lineStatistics.length;
		int width = lineStatistics[0].length;
		for (int currY = y0 - borderSize; currY <= y0 + borderSize; currY++) {
			for (int currX = x0 - borderSize; currX <= x0 + borderSize; currX++) {
				if (currY == y0 && currX == x0) continue;
				if (currX < 0 || currX >= width || (currY < 0 || currY >= height) && !useDegModOnY) continue;
				int val = lineStatistics[useDegModOnY ? Math.floorMod(currY, 180) : currY][currX];
				if (currMax < val) {
					currMax = val;
				}
			}
		}
		return currMax;
	}

	private String segmentToString(int[][] lineStatistics, int y0, int x0, int borderSize, boolean useDegModOnY) {
		StringBuilder sb = new StringBuilder();
		int height = lineStatistics.length;
		int width = lineStatistics[0].length;
		List<String> line = new ArrayList<>(borderSize * 2 + 1);
		for (int currX = x0 - borderSize; currX <= x0 + borderSize; currX++) {
			for (int currY = y0 - borderSize; currY <= y0 + borderSize; currY++) {
				if (currX < 0 || currX >= width || (currY < 0 || currY >= height) && !useDegModOnY) continue;
				int val = lineStatistics[useDegModOnY ? Math.floorMod(currY, 180) : currY][currX];
				line.add(String.valueOf(val));
			}
			sb.append(String.join("\t", line)).append('\n');
			line.clear();
		}
		return sb.toString();
	}

	@Override
	public Set<Line> findAllLines(Image image) {
		int borderSize = 10;
		log.info("Starting...");
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		int diag = (int) Math.ceil(getDiagonal(width, height));
		int[][] lineStatistics = new int[180][2 * diag];

		byte[] source = new byte[width * height * 4];
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);
		long blackPixels = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!isBlack(x, y, source, width)) continue;

				blackPixels++;

				for (int fi = 0; fi < 180; fi++) {
					double fiRads = Math.toRadians(fi);
					double doubleRadius = x * cos(fiRads) + y * sin(fiRads);
					int r = (int) doubleRadius;
					lineStatistics[fi][r + diag]++;
				}
			}
		}

		log.info("Got statistics");
		Set<Line> foundLines = new HashSet<>();

		int avg = (int) Math.round(
			Arrays.stream(lineStatistics)
				.flatMapToInt(Arrays::stream)
				.distinct()
				.filter(i -> i > 0)
				.average()
				.orElseThrow()
		);
		for (int fi = 0; fi < 180; fi++) {
			for (int rawR = 0; rawR < 2 * diag; rawR++) {
				int value = lineStatistics[fi][rawR];
				Line line = new Line(fi, rawR - diag);
				if (value > 1 && value > avg) {
					int maxOnBorder = getMaxOnBorder(lineStatistics, fi, rawR, borderSize, true);
					if (value > maxOnBorder) {
						log.info("Added {}:{} with statistic:{} maxBorderVal:{} segment:\n{}",
							line.getFi(), line.getRadius(), value, maxOnBorder,
							segmentToString(lineStatistics, fi, rawR, borderSize, true)
						);
						foundLines.add(line);
					}
				}
			}
		}

		log.info("Found {} lines", foundLines.size());
		return foundLines;
	}

	private void setCircleStatistics(int[][] circleStatistics, byte[] source, int width, int height, int fromX, int toX, int radius) {
		for (int x = fromX; x < toX; x++) {
			for (int y = 0; y < height; y++) {
				if (!isBlack(x, y, source, width)) continue;

				for (int a = 0; a < width; a++) {
					for (int b = 0; b < height; b++) {
						double r = Math.sqrt(Math.pow(x - a, 2d) + Math.pow(y - b, 2d));
						if (Math.ceil(r) == radius || Math.floor(r) == radius) {
							circleStatistics[b][a]++;
						}
					}
				}
			}
		}
	}

	@Override
	@SneakyThrows
	public Set<Circle> findAllCircles(Image image, int radius) {
		int borderSize = 10;
		log.info("Starting...");
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		int[][] circleStatistics = new int[height][width];

		byte[] source = new byte[width * height * 4];
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);

		int cores = Runtime.getRuntime().availableProcessors();
		List<Thread> threads = new ArrayList<>(cores);
		int[][][] semiCircleStatistics = new int[cores][height][width];
		int fromX = 0;
		CountDownLatch latch = new CountDownLatch(cores);
		for (int i = 0; i < cores; i++) {
			int finalFromX = fromX;
			int toX = i == cores - 1 ? width : fromX + width / cores;
			int threadId = i;
			threads.add(new Thread(() -> {
				setCircleStatistics(
					semiCircleStatistics[threadId], source, width, height, finalFromX, toX, radius
				);
				latch.countDown();
			}));
			fromX = toX;
		}

		threads.forEach(Thread::start);
		latch.await();

		for (int i = 0; i < cores; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < width; k++) {
					circleStatistics[j][k] += semiCircleStatistics[i][j][k];
				}
			}
		}
		log.info("Got statistics");

		Set<Circle> circles = new HashSet<>();

		int avg = (int) Math.round(
			Arrays.stream(circleStatistics)
				.flatMapToInt(Arrays::stream)
				.distinct()
				.filter(i -> i > 0)
				.average()
				.orElseThrow()
		);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int value = circleStatistics[y][x];
				Circle circle = new Circle(x, y, radius);
				if (value > 1 && value > avg * 3 / 2) {
					int maxOnBorder = getMaxOnBorder(circleStatistics, y, x, borderSize, false);
					if (value > maxOnBorder) {
						log.info("Added {},{} with statistic:{} maxBorderVal:{}",
							x, y, value, maxOnBorder
						);
						circles.add(circle);
					}
				}
			}
		}

		log.info("Found {} circles", circles.size());
		return circles;
	}

	private Image prepareImageForSearch(Image image) {
		Image step1 = new KirschKernelTransformation(new KirschConfig())
			.transform(image);
		return new BinaryTransformation(new BinaryConfig(128, false))
			.transform(step1);
	}

	private int calculateWhitePixels(byte[] templatePxl) {
		int out = 0;
		for (int i = 0; i < templatePxl.length / 4; i++) {
			if (templatePxl[4 * i] == (byte) 255) {
				out++;
			}
		}
		return out;
	}

	@Override
	@SneakyThrows
	public ObjectSearchResult findObject(Image image, Image template) {
		log.info("Starting...");
		Image objectImg = prepareImageForSearch(image);
		int objWidth = getWidth(objectImg);
		int objHeight = getHeight(objectImg);
		int tempWidth = getWidth(template);
		int tempHeight = getHeight(template);
//		Image transformedTemplate = prepareImageForSearch(template);
		Image transformedTemplate = template;

		byte[] objectPxl = new byte[objWidth * objHeight * 4];
		objectImg.getPixelReader().getPixels(0, 0, objWidth, objHeight,
			WritablePixelFormat.getByteBgraPreInstance(), objectPxl, 0, objWidth * 4);

		int threadCount = 16;
		List<Thread> threads = new ArrayList<>(threadCount);
		ObjectSearchResult[] results = new ObjectSearchResult[threadCount];
		int[] resultMaxMatches = new int[threadCount];
		int[] targetWidths = new int[tempWidth + 1];
		for (int i = 0; i < threadCount; i++) {
			int finalI = i;
			threads.add(new Thread(() -> {
				ObjectSearchResult currMax = new ObjectSearchResult(0, 0, 0, 1, 1, 0);
				int currMaxMatch = 0;
				for (int targetWidth = 1 + finalI; targetWidth <= tempWidth; targetWidth = targetWidth + threadCount) {
					targetWidths[targetWidth] = 1;
//				int targetWidth = tempWidth;
					int targetHeight = targetWidth == tempWidth ? tempHeight : tempHeight * targetWidth / tempWidth;
					if (targetHeight >= objHeight) break;
					Image scaledTemplate = new NearestNeighborScalingTransformation(
						new Point2D(0, 0), new Point2D(tempWidth, tempHeight),
						new Point2D(0, 0), new Point2D(targetWidth, targetHeight)
					).transform(transformedTemplate);
					byte[] templatePxl = new byte[targetWidth * targetHeight * 4];
					scaledTemplate.getPixelReader().getPixels(0, 0, targetWidth, targetHeight,
						WritablePixelFormat.getByteBgraPreInstance(), templatePxl, 0, targetWidth * 4);
					int whitePixelsInTemplate = calculateWhitePixels(templatePxl);
					for (int tempX = 0; tempX < objWidth; tempX++) {
						for (int tempY = 0; tempY < objHeight; tempY++) {
//							for (int a = 0; a < 360; a++) {
							int a = 0;
//								double theta = toRadians(a);
//								double cos = cos(theta);
//								double sin = sin(theta);
							int currMatch = 0;
							for (int x = 0; x < targetWidth; x++) {
								for (int y = 0; y < targetHeight; y++) {
//									double theta = toRadians(a);
//
//										int rotatedX = (int) Math.round(cos * x - sin * y) + tempX;
//										int rotatedY = (int) Math.round(sin * x + cos * y) + tempY;

									int rotatedX = x + tempX;
									int rotatedY = y + tempY;

									if (rotatedX < 0 || rotatedX >= objWidth || rotatedY < 0 || rotatedY >= objHeight) {
										continue;
									}

									int coordInTemp = toCoord(x, y, targetWidth, 0);
									int coordInObj = toCoord(rotatedX, rotatedY, objWidth, 0);

									if (objectPxl[coordInObj] == templatePxl[coordInTemp] && objectPxl[coordInObj] == (byte) 255) {
										currMatch++;
									}
								}
							}
							if (currMatch > currMaxMatch) {
								currMaxMatch = currMatch;
								currMax = new ObjectSearchResult(tempX, tempY, a, targetWidth, targetHeight, (double) currMatch / whitePixelsInTemplate);
							}
						}
					}
				}
//				}
//		}
				results[finalI] = currMax;
				resultMaxMatches[finalI] = currMaxMatch;
			}));
		}
		threads.forEach(Thread::start);
		for (Thread thread : threads) {
			thread.join();
		}
		ObjectSearchResult currMax = IntStream.range(0, threadCount)
			.mapToObj(i -> new Pair<>(results[i], resultMaxMatches[i]))
			.max(Comparator.comparingInt(Pair::getValue))
			.orElseThrow()
			.getKey();
		log.info("Completed, cf:{}", currMax.confidenceFactor());
		return currMax;
	}
}
