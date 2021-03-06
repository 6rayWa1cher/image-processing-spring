package com.a6raywa1cher.imageprocessingspring.transformations.scaling;

import com.a6raywa1cher.imageprocessingspring.model.ScalingConfig;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.springframework.core.annotation.Order;

@Order(5)
public class DelegatingScalingTransformation implements Transformation {
	private final ScalingConfig.ScalingAlgorithm scalingAlgorithm;
	private final Point2D p1, p2, p3, p4;
	private ObjectProperty<Double> progressBarProperty;
	private ObjectProperty<String> statusProperty;

	public DelegatingScalingTransformation(ScalingConfig config) {
		this.scalingAlgorithm = config.getAlgorithm();
		Point2D fromP1 = config.getFromP1();
		Point2D fromP2 = config.getFromP2();
		Point2D toP1 = config.getToP1();
		Point2D toP2 = config.getToP2();
		this.p1 = new Point2D(Math.min(fromP1.getX(), fromP2.getX()), Math.min(fromP1.getY(), fromP2.getY()));
		this.p2 = new Point2D(Math.max(fromP1.getX(), fromP2.getX()), Math.max(fromP1.getY(), fromP2.getY()));
		this.p3 = new Point2D(Math.min(toP1.getX(), toP2.getX()), Math.min(toP1.getY(), toP2.getY()));
		this.p4 = new Point2D(Math.max(toP1.getX(), toP2.getX()), Math.max(toP1.getY(), toP2.getY()));
	}

	@Override
	public Image transform(Image image) {
		Transformation transformation = switch (scalingAlgorithm) {
			case NEAREST_NEIGHBOR -> new NearestNeighborScalingTransformation(p1, p2, p3, p4);
			case BILINEAR -> new BilinearScalingTransformation(p1, p2, p3, p4);
			case BICUBIC -> new BicubicScalingTransformation(p1, p2, p3, p4);
			default -> null;
		};
		if (transformation == null) throw new RuntimeException("Unknown scaling algorithm");
		transformation.setProgressBarProperty(progressBarProperty);
		transformation.setStatusProperty(statusProperty);
		return transformation.transform(image);
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
