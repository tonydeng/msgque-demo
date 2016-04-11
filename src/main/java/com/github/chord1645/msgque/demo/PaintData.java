package com.github.chord1645.msgque.demo;

import com.github.chord1645.msgque.demo.ui.Apoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@org.msgpack.annotation.Message
public class PaintData implements Serializable{
    Integer count;
    List<Apoint> data = new ArrayList<>();

    public List<Apoint> getData() {
        return data;
    }

    public void setData(List<Apoint> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}