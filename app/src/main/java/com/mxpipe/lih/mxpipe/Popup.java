package com.mxpipe.lih.mxpipe;

import android.view.View;
import android.widget.PopupWindow;

/*
 *Created by LiHuan at 14:45 on 2019/3/14
 * 自定义弹窗类，重写dismiss方法，实现点击弹窗外部是否关闭弹窗可控
 */

public class Popup extends PopupWindow {
    private boolean isDismiss = false;

    public Popup(){
        super();
    }

    Popup(View viewGroup, int w, int h){
        super(viewGroup,w,h);
    }

    @Override
    public void dismiss() {
        if(isDismiss)
            super.dismiss();
    }

    void close(){
        super.dismiss();
    }

    void setIsdismiss(boolean is){
        isDismiss = is;
    }

}
