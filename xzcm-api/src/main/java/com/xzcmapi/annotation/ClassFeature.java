package com.xzcmapi.annotation;

import java.lang.annotation.*;

/**
 * @Description:  自定义类的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ClassFeature {
    public String name();
}
