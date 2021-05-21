package com.a6raywa1cher.imageprocessingspring.service.dto;

public record DigitSearchResult(int d, double cf) {
	@Override
	public String toString() {
		return "DigitSearchResult{" +
			"d=" + d +
			", cf=" + cf +
			'}';
	}
}
