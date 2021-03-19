package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.Circle;
import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.*;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getHeight;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getWidth;

@Service
@Slf4j
public class VisionServiceImpl implements VisionService {
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public VisionServiceImpl(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

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

//	private List<Line> getLinesOnBorder(int[][] lineStatistics, int fi, int r, int currMax) {
//		List<Line> out = new ArrayList<>();
//		for (int currFi = fi - BORDER_SIZE; currFi <= fi + BORDER_SIZE; currFi++) {
//			for (int currR = r - BORDER_SIZE; currR <= r + BORDER_SIZE; currR++) {
//				if (currFi == fi && currR == r) continue;
//				int val = lineStatistics[Math.floorMod(currFi, 180)][currR];
//				if (currMax == val) {
//					out.add(new Line(Math.floorMod(currFi, 180), currR));
//				}
//			}
//		}
//		return out;
//	}

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

//	private int getLineMaxSeries(Image image, Line line) {
//		int width = getWidth(image);
//		int height = getHeight(image);
//		PixelReader pixelReader = image.getPixelReader();
//
//		byte[] source = new byte[width * height * 4];
//		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);
//
//		int currSeries = 0;
//		int maxSeries = 0;
//
//		for (Point2D p : line.getPoints(width, height)) {
//			int x = (int) p.getX();
//			int y = (int) p.getY();
//
//			if (isBlack(x, y, source, width)) {
//				currSeries++;
//				if (maxSeries < currSeries) maxSeries = currSeries;
//			} else {
//				currSeries = 0;
//			}
//		}
//		return maxSeries;
//	}

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
					double doubleRadius = x * Math.cos(fiRads) + y * Math.sin(fiRads);
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
				int maxOnBorder = getMaxOnBorder(lineStatistics, fi, rawR, borderSize, true);
				Line line = new Line(fi, rawR - diag);
				if (value > 1 && value > avg) {
					if (value > maxOnBorder) {
						log.info("Added {}:{} with statistic:{} maxBorderVal:{} segment:\n{}",
							line.getFi(), line.getRadius(), value, maxOnBorder,
							segmentToString(lineStatistics, fi, rawR, borderSize, true)
						);
						foundLines.add(line);
					}
//					else if (value == maxOnBorder) {
//						log.info("Averaging");
//						List<Line> lines = getLinesOnBorder(lineStatistics, fi, r, maxOnBorder);
//						Line newLine = new Line(
//							(int) IntStream.concat(lines.stream().mapToInt(Line::getFi), IntStream.of(fi))
//								.average().orElseThrow(),
//							(int) IntStream.concat(lines.stream().mapToInt(Line::getRadius), IntStream.of(r))
//								.average().orElseThrow()
//						);
//						foundLines.removeAll(lines);
//						foundLines.add(newLine);
//					}
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
				if (value > 1 && value > avg) {
					int maxOnBorder = getMaxOnBorder(circleStatistics, y, x, borderSize, false);
					if (value > maxOnBorder) {
						log.info("Added {},{} with statistic:{} maxBorderVal:{}",
							x, y, value, maxOnBorder
						);
						circles.add(circle);
					}
//					else if (value == maxOnBorder) {
//						log.info("Averaging");
//						List<Line> lines = getLinesOnBorder(lineStatistics, fi, r, maxOnBorder);
//						Line newLine = new Line(
//							(int) IntStream.concat(lines.stream().mapToInt(Line::getFi), IntStream.of(fi))
//								.average().orElseThrow(),
//							(int) IntStream.concat(lines.stream().mapToInt(Line::getRadius), IntStream.of(r))
//								.average().orElseThrow()
//						);
//						foundLines.removeAll(lines);
//						foundLines.add(newLine);
//					}
				}
			}
		}

//		int max = 0;
//		Circle maxCircle = null;
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				int value = circleStatistics[y][x];
//				if (value > max) {
//					max = value;
//					maxCircle = new Circle(x, y, radius);
//				}
//			}
//		}
//		circles.add(maxCircle);

		log.info("Found {} circles", circles.size());
		return circles;
	}
}
