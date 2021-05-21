package com.a6raywa1cher.imageprocessingspring.service.dto;

import lombok.Data;
import lombok.ToString;

public record ObjectSearchResult(int x, int y, double rotate, int targetWidth, int targetHeight, double confidenceFactor) {
	@Override
	public String toString() {
		return "ObjectSearchResult{" +
			"x=" + x +
			", y=" + y +
			", rotate=" + rotate +
			", targetWidth=" + targetWidth +
			", targetHeight=" + targetHeight +
			", confidenceFactor=" + confidenceFactor +
			'}';
	}
}
