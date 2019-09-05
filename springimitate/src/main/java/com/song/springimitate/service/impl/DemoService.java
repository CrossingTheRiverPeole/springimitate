package com.song.springimitate.service.impl;

import com.song.springimitate.service.IDemoService;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/18
 **/
public class DemoService implements IDemoService {

    @Override
    public String get(String name) {
        return "My name is " + name;
    }
}
