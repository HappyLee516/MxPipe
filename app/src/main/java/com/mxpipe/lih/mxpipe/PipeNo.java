package com.mxpipe.lih.mxpipe;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/*
 *Created by LiHuan at 15:09 on 2019/2/14
 */

@Entity
public class PipeNo implements Serializable {
    @Id
    private long id;

    private String type;
    private int no;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "PipeNo{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", no=" + no +
                '}';
    }
}
