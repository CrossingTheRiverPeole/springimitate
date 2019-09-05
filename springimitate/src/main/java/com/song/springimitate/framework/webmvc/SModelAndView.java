package com.song.springimitate.framework.webmvc;

import lombok.Data;

import java.util.Map;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/1
 **/
@Data
public class SModelAndView {
    private String viewName;
    private Map<String,?> model;

    public SModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public SModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
