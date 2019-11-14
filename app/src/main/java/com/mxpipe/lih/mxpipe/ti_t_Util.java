package com.mxpipe.lih.mxpipe;

import java.util.HashMap;
import java.util.Map;

/*
 *LiHuan
 *2018/11/15
 *15:26
 */

/*
 *辅助类，根据小类获取大类
 */
class ti_t_Util {

    private static Map<String, String> TYPE_FLAG = new HashMap<>();

    static {
        TYPE_FLAG.put("给水", "给水");
        TYPE_FLAG.put("原水", "给水");
        TYPE_FLAG.put("中水", "给水");
        TYPE_FLAG.put("雨水", "排水");
        TYPE_FLAG.put("污水", "排水");
        TYPE_FLAG.put("雨污合流", "排水");
        TYPE_FLAG.put("煤气", "燃气");
        TYPE_FLAG.put("天然气", "燃气");
        TYPE_FLAG.put("液化气", "燃气");
        TYPE_FLAG.put("工业", "工业");
        TYPE_FLAG.put("石油", "工业");
        TYPE_FLAG.put("蒸汽", "热力");
        TYPE_FLAG.put("热水", "热力");
        TYPE_FLAG.put("供电", "电力");
        TYPE_FLAG.put("路灯", "电力");
        TYPE_FLAG.put("交通信号", "电力");
        TYPE_FLAG.put("中国电信", "通讯");
        TYPE_FLAG.put("中国移动", "通讯");
        TYPE_FLAG.put("中国联通", "通讯");
        TYPE_FLAG.put("中国铁通", "通讯");
        TYPE_FLAG.put("电力通讯", "通讯");
        TYPE_FLAG.put("热力通讯", "通讯");
        TYPE_FLAG.put("中国网通", "通讯");
        TYPE_FLAG.put("长途传输局", "通讯");
        TYPE_FLAG.put("监控信号", "通讯");
        TYPE_FLAG.put("军用光缆", "通讯");
        TYPE_FLAG.put("保密及专用通讯", "通讯");
        TYPE_FLAG.put("有线电视", "通讯");
        TYPE_FLAG.put("广播", "通讯");
        TYPE_FLAG.put("人防", "人防");
        TYPE_FLAG.put("城通管网", "人防");
        TYPE_FLAG.put("地下铁路", "地铁");
        TYPE_FLAG.put("综合管沟边线", "综合管沟");
        TYPE_FLAG.put("不明管线", "不明");
    }

    static String getType(String ti) {
        return TYPE_FLAG.get(ti);
    }

}
