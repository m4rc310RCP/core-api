package br.com.m4rc310.core.graphql.mappers;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import br.com.m4rc310.core.graphql.mappers.annotations.MMapper;
import io.leangen.graphql.ExtensionProvider;
import io.leangen.graphql.GeneratorConfiguration;
import io.leangen.graphql.generator.mapping.TypeMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MMapperLoader {
	
	@Bean
	ExtensionProvider<GeneratorConfiguration, TypeMapper> pageableInputField() {
		return (config, defaults) -> {
			
			final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
			provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
			final Set<BeanDefinition> classes = provider.findCandidateComponents("br");
			for (BeanDefinition bean : classes) {
				try {
					Class<?> clazz = Class.forName(bean.getBeanClassName());
					if (clazz.isAnnotationPresent(MMapper.class)) {
						Constructor<?> constructor = clazz.getDeclaredConstructor();
						TypeMapper mapper = (TypeMapper) constructor.newInstance();
						defaults.prepend(mapper);
						log.info("~~> Prepend mapper: {}", mapper);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			return defaults;
		};
	}
	
}
