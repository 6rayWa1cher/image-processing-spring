package com.a6raywa1cher.imageprocessingspring.config;

import com.a6raywa1cher.imageprocessingspring.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RepositoryConfig {
	@Bean
	public Map<Class<?>, Config> container() {
		Map<Class<?>, Config> container = new HashMap<>();
		container.put(BinaryConfig.class, new BinaryConfig());
		container.put(BrightnessConfig.class, new BrightnessConfig());
		container.put(GrayScaleConfig.class, new GrayScaleConfig());
		container.put(NegativeConfig.class, new NegativeConfig());
		container.put(ContrastConfig.class, new ContrastConfig());
		return container;
	}
}
