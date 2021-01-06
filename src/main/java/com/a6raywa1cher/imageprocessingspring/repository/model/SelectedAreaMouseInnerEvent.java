package com.a6raywa1cher.imageprocessingspring.repository.model;

import javafx.geometry.Point2D;
import lombok.Data;
import lombok.With;

@Data
@With
public class SelectedAreaMouseInnerEvent implements MouseInnerEvent {
	private final Point2D p1, p2;

	private final boolean primaryKey;

	private final boolean temporaryChange;
}
