package com.a6raywa1cher.imageprocessingspring.repository.model;

import javafx.geometry.Point2D;
import lombok.Data;

@Data
public class SelectedPointMouseInnerEvent implements MouseInnerEvent {
	private final Point2D p;

	private final boolean primaryKey;
}
