package com.mxpipe.lih.mxpipe;

import java.io.File;

/*
 *Created by LiHuan at 16:05 on 2019/4/4
 */
public class DimenGenerator {

    /**
     * 设计稿尺寸(将自己设计师的设计稿的宽度填入)
     */
    private static final int DESIGN_WIDTH = 390;

    /**
     * 设计稿的高度  （将自己设计师的设计稿的高度填入）
     */
    private static final int DESIGN_HEIGHT = 693;

    public static void main(String[] args) {
        int smallest = DESIGN_WIDTH>DESIGN_HEIGHT? DESIGN_HEIGHT:DESIGN_WIDTH;  //     求得最小宽度
        DimenTypes[] values = DimenTypes.values();
        for (DimenTypes value : values) {
            File file = new File("");
            MakeUtils.makeAll(smallest, value, file.getAbsolutePath());
        }

    }
}
