package com.a6raywa1cher.imageprocessingspring.transformations.scaling;

import com.a6raywa1cher.imageprocessingspring.transformations.SlaveTransformation;
import javafx.geometry.Point2D;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.toCoord;

@SlaveTransformation
public class NearestNeighborScalingTransformation extends AbstractScalingTransformation {
	public NearestNeighborScalingTransformation(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		super(p1, p2, p3, p4);
	}

	@Override
	protected byte calculateTarget(byte[] source, double sourceX, double sourceY, int channel, int width, int height) {
		int sourceCoord = toCoord((int) Math.round(sourceX), (int) Math.round(sourceY), width, channel);
		return source[sourceCoord];
	}
}
