package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import com.a6raywa1cher.imageprocessingspring.transformations.kernel.KirschKernelTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.vision.SearchVisualizationTransformation;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchConfig implements GenericConfig {
	private Image template;

	private String templateUrl;

	private Color color = Color.RED;

	private boolean preview;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, SearchConfig.class);
	}

	@Override
	public Class<? extends Transformation> getMainTransformation() {
		return SearchVisualizationTransformation.class;
	}
}
