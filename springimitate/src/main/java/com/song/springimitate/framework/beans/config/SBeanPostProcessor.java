package com.song.springimitate.framework.beans.config;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/29
 **/
public class SBeanPostProcessor {

    public Object postProcessorBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    public Object postProcessorAfterInitilization(Object bean, String beanName){
        return bean;
    }
}
