package com.a6raywa1cher.imageprocessingspring.transformations;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

import static com.a6raywa1cher.imageprocessingspring.util.JavaFXUtils.imageToWriteable;

@Slf4j
public abstract class AbstractLookupTransformation<T> implements Transformation<T> {
	protected abstract int[] transform(int[] src, int[] dest);

	@Override
	public Image transform(Image image) {
		WritableImage writableImage = imageToWriteable(image);
		long start = System.currentTimeMillis();
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
		LookupTable lookupTable = new LookupTable(0, 4) {
			@Override
			public int[] lookupPixel(int[] src, int[] dest) {
				return transform(src, dest);
			}
		};
		LookupOp op = new LookupOp(lookupTable, new RenderingHints(null));
		op.filter(bufferedImage, bufferedImage);
		WritableImage out = SwingFXUtils.toFXImage(bufferedImage, writableImage);

		log.info("{}: {}ms", this.getEvent().getClazz().getSimpleName(), System.currentTimeMillis() - start);
		return out;
	}
}
