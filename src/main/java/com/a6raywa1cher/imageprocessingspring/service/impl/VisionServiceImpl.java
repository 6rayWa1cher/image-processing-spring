package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.model.BinaryConfig;
import com.a6raywa1cher.imageprocessingspring.model.KirschConfig;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.*;
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
import java.util.stream.Collectors;
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

		int[][] circleStatistics = new int[height][width];

		byte[] source = imageToArray(image);

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

		byte[] objectPxl = imageToArray(objectImg);

		int threadCount = 16;
		List<Thread> threads = new ArrayList<>(threadCount);
		ObjectSearchResult[] results = new ObjectSearchResult[threadCount];
		int[] resultMaxMatches = new int[threadCount];
		for (int i = 0; i < threadCount; i++) {
			int finalI = i;
			threads.add(new Thread(() -> {
				ObjectSearchResult currMax = new ObjectSearchResult(0, 0, 0, 1, 1, 0);
				int currMaxMatch = 0;
				for (int targetWidth = 1 + finalI; targetWidth <= tempWidth; targetWidth = targetWidth + threadCount) {
//				int targetWidth = tempWidth;
					int targetHeight = targetWidth == tempWidth ? tempHeight : tempHeight * targetWidth / tempWidth;
					if (targetHeight >= objHeight) break;
					Image scaledTemplate = new NearestNeighborScalingTransformation(
						new Point2D(0, 0), new Point2D(tempWidth, tempHeight),
						new Point2D(0, 0), new Point2D(targetWidth, targetHeight)
					).transform(template);
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

	private Rectangle calculateMinimalBorder(Image image) {
		int width = getWidth(image);
		int height = getHeight(image);
		int left = width, right = 0, up = height, down = 0;

		byte[] source = imageToArray(image);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int coord = toCoord(x, y, width, 0);
				if (source[coord] == 0) {
					if (x < left) {
						left = x;
					}
					if (right < x) {
						right = x;
					}
					if (y < up) {
						up = y;
					}
					if (down < y) {
						down = y;
					}
				}
			}
		}
		return new Rectangle(left, up, right - left, down - up);
	}

	private double[] calculateVector(Image image, int n, int m) {
		Rectangle rectangle = calculateMinimalBorder(image);
		double[][] cellStats = new double[n][m];
		byte[] source = imageToArray(image, rectangle.x(), rectangle.y(), rectangle.w(), rectangle.h());
		int width = rectangle.w();
		int height = rectangle.h();
		double stepX = ((double) width) / n;
		double stepY = ((double) height) / m;
		int totalBlackPixels = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				double cellX1 = stepX * i;
				double cellY1 = stepY * j;
//				double cellX2 = i == n - 1 ? width : cellX1 + stepX;
//				double cellY2 = j == m - 1 ? height : cellY1 + stepY;
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (source[toCoord(x, y, width, 0)] == 0) {
							totalBlackPixels++;
							cellStats[i][j] += overlappingArea(
								new DoubleRectangle(x, y, 1, 1),
								new DoubleRectangle(cellX1, cellY1, stepX, stepY)
							);
						}
					}
				}
			}
		}
		double[] out = new double[n * m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				out[i * m + j] = cellStats[i][j] / totalBlackPixels;
			}
		}
		return out;
	}

	private double euclidDistance(double[] a, double[] b) {
		if (a.length != b.length) throw new RuntimeException(a.length + "!=" + b.length);
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(sum);
	}

	@Override
	public DigitSearchResult findDigit(Image image, Map<Integer, Image> templateMap) {
		log.info("Starting");
		image = new BinaryTransformation(new BinaryConfig(127, false)).transform(image);
		templateMap.replaceAll((i, img) -> new BinaryTransformation(new BinaryConfig(127, false)).transform(img));
		int n = 5, m = 5;
		for (int i = 0; i < 10; i++) {
			if (!templateMap.containsKey(i)) throw new RuntimeException("" + i);
		}
		double[][] vectors = templateMap.entrySet().parallelStream()
			.sorted(Comparator.comparingInt(Map.Entry::getKey))
			.map(e -> calculateVector(e.getValue(), n, m))
			.toArray(double[][]::new);
		double[] imageVector = calculateVector(image, n, m);
		int currResult = -1;
		double currMax = Double.POSITIVE_INFINITY;
		double prevMax = Double.POSITIVE_INFINITY;
		for (int i = 0; i < vectors.length; i++) {
			double distance = euclidDistance(vectors[i], imageVector);
			if (distance <= currMax) {
				prevMax = currMax;
				currMax = distance;
				currResult = i;
			} else if (distance <= prevMax) {
				prevMax = distance;
			}
		}
		log.info("Completed");
		return new DigitSearchResult(currResult, (prevMax - currMax) / prevMax);
	}
}
