package com.song.springimitate.framework.aop.config;

import lombok.Data;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
@Data
public class SAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String AspectAfterThrowingName;
}
