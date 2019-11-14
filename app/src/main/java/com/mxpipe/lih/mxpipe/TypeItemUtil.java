package com.mxpipe.lih.mxpipe;

import java.util.HashMap;
import java.util.Set;

/*
 *Created by LiHuan at 10:51 on 2019/2/15
 */

/*
 *辅助类:根据管线小类获得相应代号
 */
class TypeItemUtil {
    private static HashMap<String,String> pmap = new HashMap<>();
    static {
        pmap.put("给水","JS");
        pmap.put("原水","XS");
        pmap.put("中水","ZS");
        pmap.put("雨水","YS");
        pmap.put("污水","WS");
        pmap.put("雨污合流","HS");
        pmap.put("煤气","MQ");
        pmap.put("天然气","TR");
        pmap.put("液化气","YH");
        pmap.put("工业","GY");
        pmap.put("石油","SY");
        pmap.put("蒸汽","ZQ");
        pmap.put("热水","RS");
        pmap.put("供电","GD");
        pmap.put("路灯","LD");
        pmap.put("交通信号","XH");
        pmap.put("中国电信","DX");
        pmap.put("中国移动","YD");
        pmap.put("中国联通","LT");
        pmap.put("中国铁通","TT");
        pmap.put("电力通讯","EX");
        pmap.put("热力通讯","RX");
        pmap.put("中国网通","WT");
        pmap.put("长途传输局","CX");
        pmap.put("监控信号","KX");
        pmap.put("军用光缆","JY");
        pmap.put("保密及专用通讯","BX");
        pmap.put("有线电视","DS");
        pmap.put("广播","GB");
        pmap.put("人防","RF");
        pmap.put("地下铁路","DT");
        pmap.put("综合管沟边线","ZH");
        pmap.put("城通管网","CT");
        pmap.put("不明管线","BM");
    }

    static String getPre(String type){
        return pmap.get(type);
    }

    static String getType(String pre){
        String re = "";
        Set<String> keys = pmap.keySet();
        for (String key : keys) {
            if(pre.equals(pmap.get(key))){
                re = key;
            }
        }
        return re;
    }
}
