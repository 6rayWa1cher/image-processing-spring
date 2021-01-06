package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;

public interface MouseOnImageRepository {
	void pushEvent(MouseInnerEvent event);

	MouseInnerEvent getHead();
}
