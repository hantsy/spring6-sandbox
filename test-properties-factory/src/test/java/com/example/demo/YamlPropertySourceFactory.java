package com.example.demo;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.util.Properties;


//see: https://github.com/spring-projects/spring-framework/commit/04cce0bafd21c3f6b8f427ab2eac75ffadefb585
class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        Resource resource = encodedResource.getResource();
        if (!StringUtils.hasText(name)) {
            name = getNameForResource(resource);
        }
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        factoryBean.afterPropertiesSet();
        Properties properties = factoryBean.getObject();
        return new PropertiesPropertySource(name, properties);
    }

    private static String getNameForResource(Resource resource) {
        String name = resource.getDescription();
        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }

}