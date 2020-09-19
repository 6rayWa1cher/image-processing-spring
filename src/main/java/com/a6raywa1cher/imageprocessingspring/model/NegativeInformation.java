package com.a6raywa1cher.imageprocessingspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegativeInformation {
	private double threshold = 0;

	private boolean preview;
}
