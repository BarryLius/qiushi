package com.io.qiushi.bean;

import java.io.Serializable;

/**
 * Created by mhl on 2017/3/15.
 */

public class Image implements Serializable {
    private String src;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
