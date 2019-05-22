package com.mxpipe.lih.mxpipe;

/*
 * Created by LiHuan on 2018/11/6.
 */

import android.util.Log;

import com.MxDraw.McDbEntity;
import com.MxDraw.MrxDbgSelSet;
import com.MxDraw.MxFunction;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//导出工具类
class DaochuUtil {
    //点表
    static Map<String, String> pm = new HashMap<>();

    static {
        pm.put("给水", "JS_POINT");
        pm.put("原水", "XS_POINT");
        pm.put("中水", "ZS_POINT");
        pm.put("雨水", "YS_POINT");
        pm.put("污水", "WS_POINT");
        pm.put("雨污合流", "HS_POINT");
        pm.put("煤气", "MQ_POINT");
        pm.put("天然气", "TR_POINT");
        pm.put("液化气", "YH_POINT");
        pm.put("工业", "GY_POINT");
        pm.put("石油", "SY_POINT");
        pm.put("蒸汽", "ZQ_POINT");
        pm.put("热水", "RS_POINT");
        pm.put("供电", "GD_POINT");
        pm.put("路灯", "LD_POINT");
        pm.put("交通信号", "XH_POINT");
        pm.put("中国电信", "DX_POINT");
        pm.put("中国联通", "LT_POINT");
        pm.put("中国移动", "YD_POINT");
        pm.put("中国铁通", "TT_POINT");
        pm.put("电力通讯", "EX_POINT");
        pm.put("热力通讯", "RX_POINT");
        pm.put("中国网通", "WT_POINT");
        pm.put("长途传输局", "CX_POINT");
        pm.put("监控信号", "KX_POINT");
        pm.put("军用光缆", "JY_POINT");
        pm.put("保密及专用通讯", "BX_POINT");
        pm.put("有线电视", "DS_POINT");
        pm.put("广播", "GB_POINT");
        pm.put("人防", "RF_POINT");
        pm.put("地下铁路", "DT_POINT");
        pm.put("城市河道", "HD_POINT");
        pm.put("城市河渠", "HQ_POINT");
        pm.put("地下排水渠", "DQ_POINT");
        pm.put("综合管沟边线", "ZH_POINT");
        pm.put("城通管网", "CT_POINT");
        pm.put("不明管线", "BM_POINT");

    }

    //线表
    static Map<String, String> lm = new HashMap<>();

    static {
        lm.put("给水", "JS_LINE");
        lm.put("原水", "XS_LINE");
        lm.put("中水", "ZS_LINE");
        lm.put("雨水", "YS_LINE");
        lm.put("污水", "WS_LINE");
        lm.put("雨污合流", "HS_LINE");
        lm.put("煤气", "MQ_LINE");
        lm.put("天然气", "TR_LINE");
        lm.put("液化气", "YH_LINE");
        lm.put("工业", "GY_LINE");
        lm.put("石油", "SY_LINE");
        lm.put("蒸汽", "ZQ_LINE");
        lm.put("热水", "RS_LINE");
        lm.put("供电", "GD_LINE");
        lm.put("路灯", "LD_LINE");
        lm.put("交通信号", "XH_LINE");
        lm.put("中国电信", "DX_LINE");
        lm.put("中国联通", "LT_LINE");
        lm.put("中国移动", "YD_LINE");
        lm.put("中国铁通", "TT_LINE");
        lm.put("电力通讯", "EX_LINE");
        lm.put("热力通讯", "RX_LINE");
        lm.put("中国网通", "WT_LINE");
        lm.put("长途传输局", "CX_LINE");
        lm.put("监控信号", "KX_LINE");
        lm.put("军用光缆", "JY_LINE");
        lm.put("保密及专用通讯", "BX_LINE");
        lm.put("有线电视", "DS_LINE");
        lm.put("广播  ", "GB_LINE");
        lm.put("人防", "RF_LINE");
        lm.put("地下铁路", "DT_LINE");
        lm.put("城市河道", "HD_LINE");
        lm.put("城市河渠", "HQ_LINE");
        lm.put("地下排水渠", "DQ_LINE");
        lm.put("综合管沟边线", "ZH_LINE");
        lm.put("城通管网", "CT_LINE");
        lm.put("不明管线", "BM_LINE");

    }

    static boolean Daochu(File data) {
        MrxDbgSelSet ss = new MrxDbgSelSet();
        ss.allSelect();
        try {
            Database database = DatabaseBuilder.open(data);
            for (int i = 0; i < ss.size(); i++) {
                long lId = ss.at(i);
                McDbEntity ent = new McDbEntity(lId);
                if (ent.layerName().endsWith("POINT")) {
                    String code = MxFunction.getxDataString(lId, "code");
                    String unicode = MxFunction.getxDataString(lId, "unicode");
                    String s_type = MxFunction.getxDataString(lId, "type");
                    String s_typeitem = MxFunction.getxDataString(lId, "type_item");
                    String s_tezheng = MxFunction.getxDataString(lId, "tezheng");
                    String s_fushuwu = MxFunction.getxDataString(lId, "fushuwu");
                    String x = MxFunction.getxDataString(lId, "x");
                    String y = MxFunction.getxDataString(lId, "y");
                    String s_jdmsh = MxFunction.getxDataString(lId, "jdmsh");
                    String s_jgczh = MxFunction.getxDataString(lId, "jgczh");
                    String s_jgxzh = MxFunction.getxDataString(lId, "jgxzh");
                    String s_jgchc = MxFunction.getxDataString(lId, "jgchc");
                    String s_jgzht = MxFunction.getxDataString(lId, "jgzht");
                    String s_jczh = MxFunction.getxDataString(lId, "jczh");
                    String s_jchc = MxFunction.getxDataString(lId, "jchc");
                    String s_dmgch = MxFunction.getxDataString(lId, "dmgch");
                    String s_shyzht = MxFunction.getxDataString(lId, "shyzht");
                    String s_shjly = MxFunction.getxDataString(lId, "shjly");
                    String s_bzh = MxFunction.getxDataString(lId, "bzh");
                    //创建点实体对象，插入数据
                    BmPoint bp = new BmPoint();
                    bp.setMap_dot(""); //图上点号
                    bp.setExploration_dot(unicode);//物探点号
                    bp.setFeature(s_tezheng.equals(" ") ? "" : s_tezheng);
                    bp.setAppendages(s_fushuwu.equals(" ") ? "" : s_fushuwu);
                    bp.setX(Double.parseDouble(y));//X
                    bp.setY(Double.parseDouble(x));//Y
                    bp.setSign_rotation_angle(0);//符号旋转角
                    bp.setGround_elevation("".equals(s_dmgch) || " ".equals(s_dmgch)?0.0d:Double.parseDouble(s_dmgch));//地面高程
                    bp.setCommap_point_X(0.0D);//综合图点号X坐标
                    bp.setCommap_point_Y(0.0D);//综合图点号Y坐标
                    bp.setSpmap_point_X(0.0D);//专业图点号X坐标
                    bp.setSpmap_point_Y(0.0D);//专业图点号Y坐标
                    bp.setPoint_code("");//点要素编码
                    bp.setRoad_name("");//道路名称
                    bp.setPicture_number("");//图幅号
                    bp.setHelper_type("");//辅助类型
                    bp.setDelete_mark("");//删除标记
                    bp.setManhole_material(s_jgczh);//井盖材质
                    bp.setManhole_size(s_jgchc);//井盖尺寸
                    bp.setWell_shape(s_jgxzh);//井盖形状
                    bp.setWell_material(s_jczh); // 井材质
                    bp.setWell_size(s_jchc);// 井尺寸
                    bp.setUsed_status(s_shyzht);//使用状态
                    //                    bp.setPipeline_type(s_type);//管线类型-大类
                    bp.setManhole_type(s_jgzht);//井盖类型
                    bp.setEccentric_well_loc("");//偏心井位
                    bp.setEXPNO("");//EXPNO
                    bp.setBeizhu(s_bzh);//备注
                    bp.setOperator_library("");//操作库
                    if (" ".equals(s_jdmsh) || "".equals(s_jdmsh)) {   //井底深
                        bp.setBottom_hole_depth(0.0f);
                    } else {
                        bp.setBottom_hole_depth(Float.valueOf(s_jdmsh));
                    }
                    bp.setData_source(s_shjly);//数据来源
                    bp.setPipetype(s_typeitem);//管线性质-小类
                    Log.i("类名-表名", s_typeitem);
                    Log.i("bp", bp.toString());
                    if (pm.containsKey(s_typeitem)) {
                        Table ta = database.getTable(pm.get(s_typeitem));
                        Object[] pa = addPoint(ta, bp);
                        if (pa.length == 32) {
                            Log.i("添加成功", s_typeitem + "管点");
                        }
                    }
                } else if (ent.layerName().endsWith("LINE") && (!ent.layerName().equals("DIRECTIONLINE"))) {
                    String stype = MxFunction.getxDataString(lId, "type");
                    String sscode = MxFunction.getxDataString(lId, "qd_unicode");
                    String secode = MxFunction.getxDataString(lId, "zhd_unicode");
                    String sqdmsh = MxFunction.getxDataString(lId, "qdmsh");
                    String szhdmsh = MxFunction.getxDataString(lId, "zhdmsh");
                    String smshfsh = MxFunction.getxDataString(lId, "mshfsh");
                    String sczh = MxFunction.getxDataString(lId, "czh");
                    String sdlmch = MxFunction.getxDataString(lId, "dlmch");
                    String slx = MxFunction.getxDataString(lId, "lx");
                    String syl = MxFunction.getxDataString(lId, "yl");
                    String stsh = MxFunction.getxDataString(lId, "tsh");
                    String szksh = MxFunction.getxDataString(lId, "zksh");
                    String syyksh = MxFunction.getxDataString(lId, "yyksh");
                    String sgj1 = MxFunction.getxDataString(lId, "gj1");
                    String sgj2 = MxFunction.getxDataString(lId, "gj2");
                    Log.i("线数据", stype + sscode + secode + sqdmsh + szhdmsh + sczh + sdlmch + slx + syl);
                    //创建线实体对，插入数据
                    BmLine bl = new BmLine();
                    bl.setLine_code("");//线要素编码
                    bl.setStart_point(sscode);//起点点号
                    bl.setConn_direction(secode);//连接方向
                    bl.setStart_depth((" ".equals(sqdmsh) || "".equals(sqdmsh))?0.0f:Float.valueOf(sqdmsh));//起点埋深
                    bl.setEnd_depth((" ".equals(szhdmsh) || "".equals(szhdmsh))?0.0f:Float.valueOf(szhdmsh));//终点埋深
                    bl.setBurial_type(smshfsh);//埋设类型
                    bl.setMaterial(sczh);//材质
                    if (sgj1.equals(" ") && sgj2.equals(" ")) {//管径
                        bl.setPipe_diameter("");
                    } else if (!sgj1.equals(" ") && sgj2.equals(" ")) {
                        bl.setPipe_diameter(sgj1);
                    } else if (sgj1.equals(" ") && !sgj2.equals(" ")) {
                        bl.setPipe_diameter(sgj2);
                    } else {
                        bl.setPipe_diameter(sgj1 + "X" + sgj2);
                    }

                    bl.setFlow_direction(slx);//流向
                    if (syl.equals(" ")) {
                        bl.setVoltage_pressure("");//电压压力
                    } else {
                        bl.setVoltage_pressure(syl);//电压压力
                    }
                    if (!stsh.equals(" ")) {
                        bl.setCable_count(stsh);//电缆条数
                    } else {
                        bl.setCable_count("");
                    }
                    if (!szksh.equals(" ")) {
                        bl.setHole_count(szksh);//总孔数
                    } else {
                        bl.setHole_count("");
                    }

                    bl.setAllot_holecount("");//分配孔数
                    bl.setConstruction_year("");//建设年代
                    bl.setLnumber("");//LNUMBER
                    bl.setLinetype("");//线型
                    bl.setSp_ann_content("");//专业注记内容
                    bl.setSp_ann_X(0.0d);//专业注记X坐标
                    bl.setSp_ann_Y(0.0d);//专业注记Y坐标
                    bl.setSp_ann_angle(0);//专业注记角度
                    bl.setCom_ann_content("");//综合注记内容
                    bl.setCom_ann_X(0.0d);//综合注记X坐标
                    bl.setCom_ann_Y(0.0d);//综合注记Y坐标
                    bl.setCom_ann_angle(0);//综合注记角度
                    bl.setHelper_type("");//辅助类型
                    if (!syyksh.equals(" ")) {
                        bl.setUsed_holecount(syyksh);//已用孔数
                    } else {
                        bl.setUsed_holecount("");//已用孔数
                    }
                    bl.setDelete_mark("");//删除标记
                    bl.setCasing_size("");//套管尺寸
                    bl.setStart_pipe_topele(0.0d);//起点管顶高程
                    bl.setEnd_pipe_topele(0.0d);//终点管顶高程
                    bl.setPipeline_ower_code("");//管线权属代码
                    bl.setBeizhu("");//备注
                    bl.setOperator_library("");//操作库
                    bl.setRoad_name(sdlmch);//道路名称
                    bl.setGroove_conncode("");//管沟连接码
                    bl.setPipetype(stype);//管线类型
                    if (lm.containsKey(stype)) {
                        Table ta = database.getTable(lm.get(stype));
                        Object[] pa = addLine(ta, bl);
                        if (pa.length == 36) {
                            Log.i("添加成功", stype + "管线");
                        }
                    }
                } else {
                    Log.i("layername", ent.layerName());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 将读到的管点数据插入到对应的表
    static Object[] addPoint(Table ta, BmPoint bmpoint) throws IOException {
        int nvm = 0;
        for (Row row : ta) {
            nvm = row.getInt("ID");
        }

        int a = nvm + 1;
        Object[] pa = ta.addRow(a,bmpoint.getMap_dot(),bmpoint.getExploration_dot(),bmpoint.getFeature(),
                bmpoint.getAppendages(),bmpoint.getX(),bmpoint.getY(),bmpoint.getSign_rotation_angle(),
                bmpoint.getGround_elevation(),bmpoint.getCommap_point_X(),
                bmpoint.getCommap_point_Y(),bmpoint.getSpmap_point_X(),bmpoint.getSpmap_point_Y(),bmpoint.getPoint_code(),
                bmpoint.getRoad_name(),bmpoint.getPicture_number(),bmpoint.getHelper_type(), bmpoint.getDelete_mark(),
                bmpoint.getManhole_material(), bmpoint.getManhole_size(), bmpoint.getWell_shape(), bmpoint.getWell_material(),
                bmpoint.getWell_size(), bmpoint.getUsed_status(), bmpoint.getPipetype(), bmpoint.getManhole_type(),
                bmpoint.getEccentric_well_loc(), bmpoint.getEXPNO(), bmpoint.getBeizhu(), bmpoint.getOperator_library(),
                bmpoint.getBottom_hole_depth(), bmpoint.getData_source());
        return pa;
    }

    // 将读到的管线数据插入到对应的表
    static Object[] addLine(Table ta, BmLine bmline) throws IOException {
        int nvm = 0;
        for (Row row : ta) {
            nvm = row.getInt("ID");
        }
        int a = nvm + 1;
        Object[] pa = ta.addRow(a, bmline.getLine_code(), bmline.getStart_point(), bmline.getConn_direction(), bmline.getStart_depth(), bmline.getEnd_depth(),
                bmline.getBurial_type(), bmline.getMaterial(), bmline.getPipe_diameter(), bmline.getFlow_direction(), bmline.getVoltage_pressure(), bmline.getCable_count(),
                bmline.getHole_count(), bmline.getAllot_holecount(), bmline.getConstruction_year(), bmline.getLnumber(), bmline.getLinetype(), bmline.getSp_ann_content(),
                bmline.getSp_ann_X(), bmline.getSp_ann_Y(), bmline.getSp_ann_angle(), bmline.getCom_ann_content(), bmline.getCom_ann_X(), bmline.getCom_ann_Y(),
                bmline.getCom_ann_angle(), bmline.getHelper_type(), bmline.getUsed_holecount(), bmline.getDelete_mark(), bmline.getCasing_size(), bmline.getStart_pipe_topele(),
                bmline.getEnd_pipe_topele(), bmline.getPipeline_ower_code(), bmline.getBeizhu(), bmline.getOperator_library(), bmline.getRoad_name(),
                bmline.getGroove_conncode());
        String s = "";
        for (int x = 0; x < pa.length; x++) {
            s += pa[x];
        }
        Log.i("Object--------->", s);
        return pa;
    }

    static boolean addBp2mdb(long lId , Table table){
        boolean ret;
        String code = MxFunction.getxDataString(lId, "code");
        String unicode = MxFunction.getxDataString(lId, "unicode");
        String s_type = MxFunction.getxDataString(lId, "type");
        String s_typeitem = MxFunction.getxDataString(lId, "type_item");
        String s_tezheng = MxFunction.getxDataString(lId, "tezheng");
        String s_fushuwu = MxFunction.getxDataString(lId, "fushuwu");
        String x = MxFunction.getxDataString(lId, "x");
        String y = MxFunction.getxDataString(lId, "y");
        String s_jdmsh = MxFunction.getxDataString(lId, "jdmsh");
        String s_jgczh = MxFunction.getxDataString(lId, "jgczh");
        String s_jgxzh = MxFunction.getxDataString(lId, "jgxzh");
        String s_jgchc = MxFunction.getxDataString(lId, "jgchc");
        String s_jgzht = MxFunction.getxDataString(lId, "jgzht");
        String s_jczh = MxFunction.getxDataString(lId, "jczh");
        String s_jchc = MxFunction.getxDataString(lId, "jchc");
        String s_dmgch = MxFunction.getxDataString(lId, "dmgch");
        String s_shyzht = MxFunction.getxDataString(lId, "shyzht");
        String s_shjly = MxFunction.getxDataString(lId, "shjly");
        String s_bzh = MxFunction.getxDataString(lId, "bzh");
        //创建点实体对象，插入数据
        BmPoint bp = new BmPoint();
        bp.setMap_dot(code); //图上点号
        bp.setExploration_dot(unicode);//物探点号
        bp.setFeature(s_tezheng.equals(" ") ? "" : s_tezheng);
        bp.setAppendages(s_fushuwu.equals(" ") ? "" : s_fushuwu);
        bp.setX(Double.parseDouble(y));//X
        bp.setY(Double.parseDouble(x));//Y
        bp.setSign_rotation_angle(0);//符号旋转角
        bp.setGround_elevation("".equals(s_dmgch) || " ".equals(s_dmgch)?0.0d:Double.parseDouble(s_dmgch));//地面高程
        bp.setCommap_point_X(0.0D);//综合图点号X坐标
        bp.setCommap_point_Y(0.0D);//综合图点号Y坐标
        bp.setSpmap_point_X(0.0D);//专业图点号X坐标
        bp.setSpmap_point_Y(0.0D);//专业图点号Y坐标
        bp.setPoint_code("");//点要素编码
        bp.setRoad_name("");//道路名称
        bp.setPicture_number("");//图幅号
        bp.setHelper_type("");//辅助类型
        bp.setDelete_mark("");//删除标记
        bp.setManhole_material(s_jgczh);//井盖材质
        bp.setManhole_size(s_jgchc);//井盖尺寸
        bp.setWell_shape(s_jgxzh);//井盖形状
        bp.setWell_material(s_jczh); //井材质
        bp.setWell_size(s_jchc);//井尺寸
        bp.setUsed_status(s_shyzht);//使用状态
        //bp.setPipeline_type(s_type);//管线类型-大类
        bp.setManhole_type(s_jgzht);//井盖类型
        bp.setEccentric_well_loc("");//偏心井位
        bp.setEXPNO("");//EXPNO
        bp.setBeizhu(s_bzh);//备注
        bp.setOperator_library("");//操作库
        if (" ".equals(s_jdmsh) || "".equals(s_jdmsh)) {//井底深
            bp.setBottom_hole_depth(0.0f);
        } else {
            bp.setBottom_hole_depth(Float.valueOf(s_jdmsh));
        }
        bp.setData_source(s_shjly);//数据来源
        bp.setPipetype(s_typeitem);//管线性质-小类
        Log.i("类名-表名", s_typeitem);
        Log.i("bp", bp.toString());
        if (pm.containsKey(s_typeitem)) {
            Object[] pa = new Object[0];
            try {
                pa = addPoint(table, bp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pa.length == 32) {
                ret = true;
                Log.i("添加成功", s_typeitem + "管点");
            }else {
                ret = false;
            }
        }else {
            ret = false;
        }
        return  ret;
    }

    static boolean addBl2mdb(long lId , Table table){
        boolean ret ;
        String stype = MxFunction.getxDataString(lId, "type");
        String sscode = MxFunction.getxDataString(lId, "qd_unicode");
        String secode = MxFunction.getxDataString(lId, "zhd_unicode");
        String sqdmsh = MxFunction.getxDataString(lId, "qdmsh");
        String szhdmsh = MxFunction.getxDataString(lId, "zhdmsh");
        String smshfsh = MxFunction.getxDataString(lId, "mshfsh");
        String sczh = MxFunction.getxDataString(lId, "czh");
        String sdlmch = MxFunction.getxDataString(lId, "dlmch");
        String slx = MxFunction.getxDataString(lId, "lx");
        String syl = MxFunction.getxDataString(lId, "yl");
        String stsh = MxFunction.getxDataString(lId, "tsh");
        String szksh = MxFunction.getxDataString(lId, "zksh");
        String syyksh = MxFunction.getxDataString(lId, "yyksh");
        String sgj1 = MxFunction.getxDataString(lId, "gj1");
        String sgj2 = MxFunction.getxDataString(lId, "gj2");
        Log.i("线数据", stype + sscode + secode + sqdmsh + szhdmsh + sczh + sdlmch + slx + syl);
        //创建线实体对象，插入数据
        BmLine bl = new BmLine();
        bl.setLine_code("");//线要素编码
        bl.setStart_point(sscode);//起点点号
        bl.setConn_direction(secode);//连接方向
        bl.setStart_depth((" ".equals(sqdmsh) || "".equals(sqdmsh))?0.0f:Float.valueOf(sqdmsh));//起点埋深
        bl.setEnd_depth((" ".equals(szhdmsh) || "".equals(szhdmsh))?0.0f:Float.valueOf(szhdmsh));//终点埋深
        bl.setBurial_type(smshfsh);//埋设类型
        bl.setMaterial(sczh);//材质
        if (sgj1.equals(" ") && sgj2.equals(" ")) {//管径
            bl.setPipe_diameter("");
        } else if (!sgj1.equals(" ") && sgj2.equals(" ")) {
            bl.setPipe_diameter(sgj1);
        } else if (sgj1.equals(" ") && !sgj2.equals(" ")) {
            bl.setPipe_diameter(sgj2);
        } else {
            bl.setPipe_diameter(sgj1 + "X" + sgj2);
        }

        bl.setFlow_direction(slx);//流向
        if (syl.equals(" ")) {
            bl.setVoltage_pressure("");//电压压力
        } else {
            bl.setVoltage_pressure(syl);//电压压力
        }
        if (!stsh.equals(" ")) {
            bl.setCable_count(stsh);//电缆条数
        } else {
            bl.setCable_count("");
        }
        if (!szksh.equals(" ")) {
            bl.setHole_count(szksh);//总孔数
        } else {
            bl.setHole_count("");
        }

        bl.setAllot_holecount("");//分配孔数
        bl.setConstruction_year("");//建设年代
        bl.setLnumber("");//LNUMBER
        bl.setLinetype("");//线型
        bl.setSp_ann_content("");//专业注记内容
        bl.setSp_ann_X(0.0d);//专业注记X坐标
        bl.setSp_ann_Y(0.0d);//专业注记Y坐标
        bl.setSp_ann_angle(0);//专业注记角度
        bl.setCom_ann_content("");//综合注记内容
        bl.setCom_ann_X(0.0d);//综合注记X坐标
        bl.setCom_ann_Y(0.0d);//综合注记Y坐标
        bl.setCom_ann_angle(0);//综合注记角度
        bl.setHelper_type("");//辅助类型
        if (!syyksh.equals(" ")) {
            bl.setUsed_holecount(syyksh);//已用孔数
        } else {
            bl.setUsed_holecount("");//已用孔数
        }
        bl.setDelete_mark("");//删除标记
        bl.setCasing_size("");//套管尺寸
        bl.setStart_pipe_topele(0.0d);//起点管顶高程
        bl.setEnd_pipe_topele(0.0d);//终点管顶高程
        bl.setPipeline_ower_code("");//管线权属代码
        bl.setBeizhu("");//备注
        bl.setOperator_library("");//操作库
        bl.setRoad_name(sdlmch);//道路名称
        bl.setGroove_conncode("");//管沟连接码
        bl.setPipetype(stype);//管线类型
        if (lm.containsKey(stype)) {
            Object[] pa = new Object[0];
            try {
                pa = addLine(table, bl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pa.length == 36) {
                Log.i("添加成功", stype + "管线");
                ret = true;
            }else {
                ret = false;
            }
        }else {
            ret = false;
        }
        return ret;
    }

}
