package com.a6raywa1cher.imageprocessingspring.config;

import com.a6raywa1cher.imageprocessingspring.model.*;
import javafx.util.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RepositoryConfig {
	@Bean
	public List<Pair<Class<?>, Config>> container() {
		List<Pair<Class<?>, Config>> container = new ArrayList<>();
		container.add(new Pair<>(GrayScaleConfig.class, new GrayScaleConfig()));
		container.add(new Pair<>(BrightnessConfig.class, new BrightnessConfig()));
		container.add(new Pair<>(NegativeConfig.class, new NegativeConfig()));
		container.add(new Pair<>(BinaryConfig.class, new BinaryConfig()));
		container.add(new Pair<>(ContrastConfig.class, new ContrastConfig()));
		container.add(new Pair<>(GammaConfig.class, new GammaConfig()));
		container.add(new Pair<>(QuantizationConfig.class, new QuantizationConfig()));
		container.add(new Pair<>(PseudocolorConfig.class, new PseudocolorConfig()));
		container.add(new Pair<>(SolarizationConfig.class, new SolarizationConfig()));
		container.add(new Pair<>(LowHighFrequencyConfig.class, new LowHighFrequencyConfig()));
		container.add(new Pair<>(GaussConfig.class, new GaussConfig()));
		container.add(new Pair<>(MiddleConfig.class, new MiddleConfig()));
		container.add(new Pair<>(SobelConfig.class, new SobelConfig()));
		container.add(new Pair<>(KirschConfig.class, new KirschConfig()));
		container.add(new Pair<>(EmbossingConfig.class, new EmbossingConfig()));
		return container;
	}
}
