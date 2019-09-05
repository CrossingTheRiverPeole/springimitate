package com.song.springimitate.framework.webmvc;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/1
 **/
@Data
public class SHandlerMapping {

    private Pattern pattern;// url正则匹配
    private Object controller;//保存方法映射的实例
    private Method method;//映射方法

    public SHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }
}
