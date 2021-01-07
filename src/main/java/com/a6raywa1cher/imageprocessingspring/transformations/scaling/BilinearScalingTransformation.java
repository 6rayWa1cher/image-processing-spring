package com.a6raywa1cher.imageprocessingspring.transformations.scaling;

import com.a6raywa1cher.imageprocessingspring.transformations.SlaveTransformation;
import javafx.geometry.Point2D;

import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.borderPixel;
import static com.a6raywa1cher.imageprocessingspring.util.AlgorithmUtils.getPixel;

@SlaveTransformation
public class BilinearScalingTransformation extends AbstractScalingTransformation {
	public BilinearScalingTransformation(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		super(p1, p2, p3, p4);
	}

	@Override
	protected byte calculateTarget(byte[] source, double sourceX, double sourceY, int channel, int width, int height) {
		int u = (int) sourceX;
		int v = (int) sourceY;
		double s = sourceX - u;
		double t = sourceY - v;
		return borderPixel((int) Math.round(
			(1 - s) * (1 - t) * getPixel(source, u, v, width, channel) +
				s * (1 - t) * getPixel(source, u + 1, v, width, channel) +
				(1 - s) * t * getPixel(source, u, v + 1, width, channel) +
				s * t * getPixel(source, u + 1, v + 1, width, channel)
		));
	}
}
