package com.a6raywa1cher.imageprocessingspring.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBundle {
	private Image currentImage;

	private Image currentViewImage;
}
