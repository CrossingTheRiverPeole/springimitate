package com.song.springimitate.mvcframework;

import org.springframework.beans.factory.FactoryBean;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/18
 **/
public class Test  implements FactoryBean{
    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
