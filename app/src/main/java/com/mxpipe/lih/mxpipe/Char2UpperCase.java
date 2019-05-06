package com.mxpipe.lih.mxpipe;

import android.text.method.ReplacementTransformationMethod;

/*
 *Created by LiHuan at 15:19 on 2019/1/5
 */

//输入框小写变大写工具
public class Char2UpperCase extends ReplacementTransformationMethod {
    @Override
    protected char[] getOriginal() {
        char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return aa;

    }

    @Override
    protected char[] getReplacement() {
        char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return cc;
    }
}
