package com.a6raywa1cher.imageprocessingspring.model;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.transformations.SelectTransformation;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectConfig implements GenericConfig {
	private int x1 = 0;

	private int y1 = 0;

	private int x2 = 0;

	private int y2 = 0;

	private Color color = Color.rgb(0x00, 0xB2, 0xFF);

	private boolean preview = false;

	@Override
	public ConfigModifiedEvent<?> getEvent() {
		return new ConfigModifiedEvent<>(this, SelectConfig.class);
	}

	@Override
	public Transformation getTransformation() {
		return new SelectTransformation(this);
	}
}
