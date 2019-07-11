package com.xzcmapi.util;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;


public class BeanUtil extends BeanUtils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        BeanUtil.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        List<String> list = Lists.newArrayList(ignoreProperties);
        list.addAll(Lists.newArrayList(getNullPropertyNames(source)));
        BeanUtil.copyProperties(source, target, getNullPropertyNames(list.toArray()));
    }
}
