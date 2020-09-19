package com.a6raywa1cher.imageprocessingspring.transformations;

import javafx.scene.image.Image;

public interface Transformation<T> {
	Image transform(Image image);
}
