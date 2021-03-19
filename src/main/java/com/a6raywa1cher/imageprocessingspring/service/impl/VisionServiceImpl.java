package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.*;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getHeight;
import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.getWidth;

@Service
@Slf4j
public class VisionServiceImpl implements VisionService {
	private static final int BORDER_SIZE = 10;
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


	private int getMaxOnBorder(int[][] lineStatistics, int fi, int r) {
		int currMax = 0;
		for (int currFi = fi - BORDER_SIZE; currFi <= fi + BORDER_SIZE; currFi++) {
			for (int currR = r - BORDER_SIZE; currR <= r + BORDER_SIZE; currR++) {
				if (currFi == fi && currR == r) continue;
				int val = lineStatistics[Math.floorMod(currFi, 180)][currR];
				if (currMax < val) {
					currMax = val;
				}
			}
		}
		return currMax;
	}

	private String segmentToString(int[][] lineStatistics, int fi, int r) {
		StringBuilder sb = new StringBuilder();
		List<String> line = new ArrayList<>(BORDER_SIZE * 2 + 1);
		for (int currR = r - BORDER_SIZE; currR <= r + BORDER_SIZE; currR++) {
			for (int currFi = fi - BORDER_SIZE; currFi <= fi + BORDER_SIZE; currFi++) {
				int val = lineStatistics[Math.floorMod(currFi, 180)][currR];
				line.add(String.valueOf(val));
			}
			sb.append(String.join("\t", line)).append('\n');
			line.clear();
		}
		return sb.toString();
	}

	private int getLineMaxSeries(Image image, Line line) {
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		byte[] source = new byte[width * height * 4];
		pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getByteBgraPreInstance(), source, 0, width * 4);

		int currSeries = 0;
		int maxSeries = 0;

		for (Point2D p : line.getPoints(width, height)) {
			int x = (int) p.getX();
			int y = (int) p.getY();

			if (isBlack(x, y, source, width)) {
				currSeries++;
				if (maxSeries < currSeries) maxSeries = currSeries;
			} else {
				currSeries = 0;
			}
		}
		return maxSeries;
	}

	@Override
	public Set<Line> findAllLines(Image image) {
		log.info("Starting...");
		int width = getWidth(image);
		int height = getHeight(image);
		PixelReader pixelReader = image.getPixelReader();

		int diag = (int) Math.ceil(getDiagonal(width, height));
		int[][] lineStatistics = new int[180][2 * diag + BORDER_SIZE * 2];

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
//					int floor = (int) Math.floor(doubleRadius);
//					int ceil = (int) Math.ceil(doubleRadius);
					int r = (int) doubleRadius;
					lineStatistics[fi][r + diag + BORDER_SIZE]++;
//					lineStatistics[fi][ceil + diag + BORDER_SIZE]++;
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
//		avg = (int) ((double) avg * 3d / 2d);
//		log.info(String.valueOf(avg));
		for (int fi = 0; fi < 180; fi++) {
			for (int r = 0; r < 2 * diag; r++) {
				int value = lineStatistics[fi][r + BORDER_SIZE];
				int maxOnBorder = getMaxOnBorder(lineStatistics, fi, r + BORDER_SIZE);
				Line line = new Line(fi, r - diag);
				if (value > maxOnBorder && value > 1 && value > avg) {
//					int lineMaxSeries = getLineMaxSeries(image, line);
//					if (lineMaxSeries > 5) {
					log.info("Added {}:{} with statistic:{} maxBorderVal:{} segment:\n{}",
						line.getFi(), line.getRadius(), value, maxOnBorder,
						segmentToString(lineStatistics, fi, r + BORDER_SIZE)
					);
					foundLines.add(line);
//					} else if (lineMaxSeries > 3) {
//						log.info("Discarded {}:{} with lineMaxSeries:{}", line.getFi(), line.getRadius(), lineMaxSeries);
//					}
				}
			}
		}

//		int maxStat = -1;
//		Line currLine = null;
//
//		for (int fi = 0; fi < 180; fi++) {
//			for (int r = 0; r < 2 * diag; r++) {
//				int value = lineStatistics[fi + 1][r + 1];
//				if (maxStat < value) {
//					currLine = new Line(fi - 1, r - diag + 1);
//					maxStat = value;
//				}
//			}
//		}
//		foundLines.add(currLine);

		log.info("Found {} lines", foundLines.size());
		return foundLines;
	}
}
