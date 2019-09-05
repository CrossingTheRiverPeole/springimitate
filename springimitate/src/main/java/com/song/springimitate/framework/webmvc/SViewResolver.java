package com.song.springimitate.framework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/1
 **/
public class SViewResolver {


    private final String DEFAULT_TEMPLATE_SUFIX = ".html";
    private File templateRootDir;

    public SViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public SView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new SView(templateFile);
    }
}
