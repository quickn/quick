package cn.quickj.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Action或者Action的Method使用的Filter Annotation。 其中 Filter的Name可以用分号;分割,并从左到右依次执行,
 * 每个Filter可以在:号后增加初始化的参数,参数的形式以name=value的形式出现， 参数之间使用,分割,如果没有参数，则可以不填。
 * 
 * @Filter(name="PageCacheFilter:timeout=3600;TimeFilter")
 * 
 * @author lbj
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
	String name() default "DummyFilter";
}
