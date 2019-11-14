package com.mxpipe.lih.mxpipe;

import android.util.Log;
import android.widget.Spinner;

import com.MxDraw.MrxDbgSelSet;
import com.MxDraw.MxFunction;
import com.MxDraw.MxResbuf;

/*
 *Created by LiHuan at 15:10 on 2019/5/30
 */
class DataUtil {

    /*
     *将点实体封装的属性信息保存至图中
     * @param bp 点实体对象
     * @param bid 图上点的ID
     */
    static void bmpoint2xdata(BmPoint bmPoint, long bid) {
        MxFunction.setxDataString(bid, "unicode", bmPoint.getExploration_dot());
        MxFunction.setxDataString(bid, "code", bmPoint.getMap_dot());
        MxFunction.setxDataString(bid, "x", String.valueOf(bmPoint.getY()));
        MxFunction.setxDataString(bid, "y", String.valueOf(bmPoint.getX()));
        MxFunction.setxDataString(bid, "type", bmPoint.getPipeline_type());
        MxFunction.setxDataString(bid, "type_item", bmPoint.getPipetype());
        MxFunction.setxDataString(bid, "tezheng", bmPoint.getFeature());
        MxFunction.setxDataString(bid, "fushuwu", bmPoint.getAppendages());

        MxFunction.setxDataString(bid, "jdmsh", String.valueOf(bmPoint.getBottom_hole_depth()));
        MxFunction.setxDataString(bid, "jgxzh", bmPoint.getWell_shape());
        MxFunction.setxDataString(bid, "jgchc", bmPoint.getManhole_size());
        MxFunction.setxDataString(bid, "jgczh", bmPoint.getManhole_material());
        MxFunction.setxDataString(bid, "jgzht", bmPoint.getManhole_type());
        MxFunction.setxDataString(bid, "jczh", bmPoint.getWell_material());
        MxFunction.setxDataString(bid, "jchc", bmPoint.getWell_size());

        MxFunction.setxDataString(bid, "dmgch", String.valueOf(bmPoint.getGround_elevation()));
        MxFunction.setxDataString(bid, "shyzht", bmPoint.getUsed_status());
        MxFunction.setxDataString(bid, "shjly", bmPoint.getData_source());
        MxFunction.setxDataString(bid, "bzh", bmPoint.getBeizhu());

    }

    /*
     *将线实体封装的属性信息保存至图中
     * @param bl 线实体对象
     * @param lid 图上线的ID
     */
    static void Bmline2xdata(BmLine bl, long lid) {
        MxFunction.setxDataString(lid, "scode", bl.getTushangqidian());
        MxFunction.setxDataString(lid, "ecode", bl.getTushangzhongdian());
        MxFunction.setxDataString(lid, "qd_unicode", bl.getStart_point());
        MxFunction.setxDataString(lid, "zhd_unicode", bl.getConn_direction());
        MxFunction.setxDataString(lid, "type", bl.getPipetype());
        MxFunction.setxDataString(lid, "qdmsh", String.valueOf(bl.getStart_depth()));
        MxFunction.setxDataString(lid, "zhdmsh", String.valueOf(bl.getEnd_depth()));
        MxFunction.setxDataString(lid, "mshfsh", bl.getBurial_type());
        String gj = bl.getPipe_diameter();
        String gj1, gj2;
        if (gj != null && gj.contains("X")) {
            int x = gj.indexOf("X");
            gj1 = gj.substring(0, x);
            gj2 = gj.substring(x + 1);
        } else if (gj != null && gj.contains("*")) {
            int x = gj.indexOf("*");
            gj1 = gj.substring(0, x);
            gj2 = gj.substring(x + 1);
        } else {
            gj1 = gj;
            gj2 = " ";
        }
        MxFunction.setxDataString(lid, "gj1", gj1);
        MxFunction.setxDataString(lid, "gj2", gj2);
        MxFunction.setxDataString(lid, "dlmch", bl.getRoad_name());
        MxFunction.setxDataString(lid, "czh", bl.getMaterial());
        MxFunction.setxDataString(lid, "lx", bl.getFlow_direction());
        MxFunction.setxDataString(lid, "tsh", bl.getCable_count());
        MxFunction.setxDataString(lid, "zksh", bl.getHole_count());
        MxFunction.setxDataString(lid, "yyksh", bl.getUsed_holecount());
        MxFunction.setxDataString(lid, "yl", bl.getVoltage_pressure());
    }

    static void set(Spinner s, String[] data, String value) {
        for (int x = 0; x < data.length; x++) {
            if (data[x].equals(value)) {
                s.setSelection(x);
            }
        }
    }

    //根据大类获取颜色,参数type:大类
    static long[] getMyColor(String type) {
        long[] color = new long[3];
        switch (type) {
            case "给水":
                color[0] = 0;
                color[1] = 63;
                color[2] = 255;
                break;
            case "排水":
                color[0] = 51;
                color[1] = 51;
                color[2] = 51;
                break;
            case "燃气":
                color[0] = 204;
                color[1] = 0;
                color[2] = 153;
                break;
            case "热力":
                color[0] = 204;
                color[1] = 153;
                color[2] = 0;
                break;
            case "电力":
                color[0] = 255;
                color[1] = 0;
                color[2] = 0;
                break;
            case "通讯":
                color[0] = 0;
                color[1] = 76;
                color[2] = 0;
                break;
            case "工业":
                color[0] = 255;
                color[1] = 127;
                color[2] = 0;
                break;
            case "综合管沟":
                color[0] = 128;
                color[1] = 128;
                color[2] = 128;
                break;
            case "人防":
                color[0] = 128;
                color[1] = 128;
                color[2] = 128;
                break;
            case "地铁":
                color[0] = 255;
                color[1] = 255;
                color[2] = 0;
                break;
            case "不明":
                color[0] = 128;
                color[1] = 128;
                color[2] = 128;
                break;
        }
        return color;
    }

    /*获得管点相关埋深
     *@param falg 标识：1-起点埋深 2-终点埋深
     *@param code 管点点号
     *@param type 管点类型
     */
    static String getDeep(int flag, String code, String type) {
        Log.i("getDeep", flag + "--" + code + "--" + type);
        String result = null;
        MrxDbgSelSet ss = new MrxDbgSelSet();
        MxResbuf filter = new MxResbuf();
        filter.addString(TypeItemUtil.getPre(type) + "LINE", 8);
        ss.allSelect(filter);
        Log.i("ss", ss.size() + "");
        switch (flag) {
            case 1:
                for (int i = 0; i < ss.size(); i++) {
                    String sec = MxFunction.getxDataString(ss.at(i), "ecode");
                    Log.i("ecode", sec);
                    if (code.equals(sec)) {
                        result = MxFunction.getxDataString(ss.at(i), "zhdmsh");
                    }
                }
                break;
            case 2:
                for (int i = 0; i < ss.size(); i++) {
                    String ssc = MxFunction.getxDataString(ss.at(i), "scode");
                    Log.i("scode", ssc);
                    if (code.equals(ssc)) {
                        result = MxFunction.getxDataString(ss.at(i), "qdmsh");
                    }
                }
                break;
        }
        return result;
    }

    /*根据物探点号和类别匹配图上点，得到对应管点ID
     * @param unicode 物探点号
     * @param ti 管点类别(小类)
     */
    static long unicode2point(String unicode, String ti) {
        Log.i("unicode2point", unicode + "--" + ti);
        long re = 0;

        MrxDbgSelSet ss = new MrxDbgSelSet();
        MxResbuf filter = new MxResbuf();
        filter.addString(TypeItemUtil.getPre(ti) + "POINT", 8);
        ss.allSelect(filter);
        for (int i = 0; i < ss.size(); i++) {
            String puni = MxFunction.getxDataString(ss.at(i), "unicode");
            if (unicode.equals(puni)) {
                re = ss.at(i);
            }
        }
        return re;
    }

}
