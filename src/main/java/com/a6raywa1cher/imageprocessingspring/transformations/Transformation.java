package com.a6raywa1cher.imageprocessingspring.transformations;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;

public interface Transformation {
	Image transform(Image image);

	default void setProgressBarProperty(ObjectProperty<Double> progressBarProperty) {

	}

	default void setStatusProperty(ObjectProperty<String> statusProperty) {

	}
}
