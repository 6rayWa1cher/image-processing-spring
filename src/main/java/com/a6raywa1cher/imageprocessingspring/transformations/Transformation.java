package com.a6raywa1cher.imageprocessingspring.transformations;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import javafx.scene.image.Image;

public interface Transformation<T> {
	Image transform(Image image);

	ConfigModifiedEvent<T> getEvent();
}
