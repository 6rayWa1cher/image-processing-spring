package com.a6raywa1cher.imageprocessingspring.config;

import com.a6raywa1cher.imageprocessingspring.model.Config;
import com.a6raywa1cher.imageprocessingspring.transformations.Transformation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class RepositoryConfig {
	@Bean
	public List<Class<? extends Transformation>> transformationManifest() throws ClassNotFoundException {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter((metadataReader, metadataReaderFactory) ->
			Arrays.asList(metadataReader.getClassMetadata().getInterfaceNames()).contains("Transformation"));
		Map<Class<? extends Transformation>, Integer> orderMap = new HashMap<>();
		for (BeanDefinition beanDefinition :
			provider.findCandidateComponents("com.a6raywa1cher.imageprocessingspring.transformations")) {
			Class<? extends Transformation> cl = (Class<? extends Transformation>) Class.forName(beanDefinition.getBeanClassName());
			int order = 0;
			if (cl.isAnnotationPresent(Order.class)) {
				order = cl.getAnnotation(Order.class).value();
			}
			orderMap.put(cl, order);
		}
		return orderMap.keySet().stream()
			.sorted(Comparator.comparingInt(orderMap::get))
			.collect(Collectors.toList());
	}

	@Bean
	public Set<Config> container() throws ClassNotFoundException, NoSuchMethodException,
		IllegalAccessException, InvocationTargetException, InstantiationException {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
				return Arrays.stream(metadataReader.getClassMetadata().getInterfaceNames())
					.anyMatch(interfaceName -> interfaceName.equals("com.a6raywa1cher.imageprocessingspring.model.Config")
						|| interfaceName.equals("com.a6raywa1cher.imageprocessingspring.model.GenericConfig"));
			}
		);
		Set<Config> set = new HashSet<>();
		for (BeanDefinition bd : provider.findCandidateComponents("com.a6raywa1cher.imageprocessingspring.model")) {
			Config config = (Config) Class.forName(bd.getBeanClassName()).getDeclaredConstructor().newInstance();
			set.add(config);
		}
		return set;
	}
}
