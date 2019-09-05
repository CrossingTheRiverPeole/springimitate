package com.song.springimitate.framework.beans.config;

import lombok.Data;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/23
 **/
@Data
public class SBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;

}
