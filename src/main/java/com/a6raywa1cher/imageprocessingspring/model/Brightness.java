package com.a6raywa1cher.imageprocessingspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brightness {
	private double delta = 0;

	private boolean preview = false;
}
