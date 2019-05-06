package com.mxpipe.lih.mxpipe;

/*
 *Created by LiHuan at 15:24 on 2019/2/21
 */

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

//点号记录实体类
@Entity
public class CodeNumber implements Serializable {

    @Id
    private long id;

    private String filename;//文件名
    private String type;//管点类别
    private String pre;//前缀-根据类别自动获取
    private int no;//点号数字

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    @Override
    public String toString() {
        return "CodeNumber{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", pre='" + pre + '\'' +
                ", no=" + no +
                '}';
    }
}
