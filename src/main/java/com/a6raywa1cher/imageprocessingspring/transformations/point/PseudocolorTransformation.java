package com.a6raywa1cher.imageprocessingspring.transformations.point;

import com.a6raywa1cher.imageprocessingspring.model.PseudocolorConfig;
import com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.channelToInt;

public class PseudocolorTransformation extends AbstractLookupTransformation {
	private final List<Pair<Color, Integer>> pairs;

	public PseudocolorTransformation(PseudocolorConfig config) {
		pairs = new ArrayList<>(List.of(
			new Pair<>(config.getColor1(), 0),
			new Pair<>(config.getColor2(), config.getQ1()),
			new Pair<>(config.getColor3(), config.getQ2()),
			new Pair<>(config.getColor4(), config.getQ3())
		));
		pairs.sort(Comparator.comparing(Pair::getValue));
	}
	@Override
	protected int[] transform(int[] src, int[] dest) {
		int intensity = AlgorithmUtils.intensity(src[0], src[1], src[2]);
		Pair<Color, Integer> requiredPair = pairs.stream()
			.filter(p -> p.getValue() <= intensity)
			.max(Comparator.comparing(Pair::getValue))
			.orElseThrow();
		Color c = requiredPair.getKey();
		dest[0] = channelToInt(c.getRed());
		dest[1] = channelToInt(c.getGreen());
		dest[2] = channelToInt(c.getBlue());
		return dest;
	}
}
