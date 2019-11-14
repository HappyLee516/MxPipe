package com.mxpipe.lih.mxpipe;

import java.util.HashMap;
import java.util.Map;

/*
 *Created by LiHuan at 9:14 on 2018/12/5
 */
 class Mark_Util {

    static String[] marknames = {"1.dwg","03.dwg","13.dwg","BC.dwg","BDF.dwg","BDZ.dwg","BJ.dwg","CD.dwg",
        "CLD.dwg","CSJ.dwg","CTD.dwg","CXJ.dwg","CYD.dwg","DD.dwg","DG.dwg","DHT.dwg","DLSK.dwg",
        "DLYJ.dwg","DXSK.dwg","DXYJ.dwg","FM.dwg","FMJ.dwg","FMK.dwg","GDJ.dwg","GGP.dwg","GM.dwg",
        "GYT.dwg","GYYJ.dwg","JBD.dwg","JCJ.dwg","JKQ.dwg","JSFMJ.dwg","JSK.dwg","JSPQF.dwg","JSPSF.dwg",
        "JSYJ.dwg","JTXHD.dwg","JXX.dwg","JYBZ.dwg","NSG.dwg","NSJ.dwg","PCK.dwg","PDF.dwg","PSYJ.dwg",
        "RLYJ.dwg","RQTYX.dwg","RQTYZ.dwg","RQYJ.dwg","SB.dwg","SBJ.dwg","SG.dwg","SSQ.dwg","SZJCD.dwg",
        "TCD.dwg","TFJ.dwg","XFS.dwg","XG.dwg","YJCSZ.dwg","YLB.dwg","YLJ.dwg","YLK.dwg","YSB.dwg",
        "YSJ.dwg","ZKB.dwg","ZMJ.dwg"};

    private static Map<String,String> tzh_map = new HashMap<>();
    static {
        tzh_map.put("上杆","SG");
        tzh_map.put("探测点","TCD");
        tzh_map.put("变材点","BC");
        tzh_map.put("变径点","BJ");
        tzh_map.put("一井多阀","DFM");
        tzh_map.put("出地","CD");
        tzh_map.put("转折点","TCD");
        tzh_map.put("管帽","GM");
        tzh_map.put("出水口","CSK");
        tzh_map.put("进水口","JSK");
        tzh_map.put("非普查","03");
        tzh_map.put("弯头","TCD");
        tzh_map.put("预留口","YLK");
        tzh_map.put("井边点","JBD");
        tzh_map.put("图边点","QBD");
        tzh_map.put("直线点","ZXD");
    }
    
    private static Map<String,String> fshw_map = new HashMap<>();
    static {
        fshw_map.put("阀门","FM");
//        fshw_map.put("泵站","BZ");
        fshw_map.put("窨井","YJ11");//与类别有关：给水/燃气/排水/工业/热力
        fshw_map.put("阀门孔","FMK");
        fshw_map.put("水表井","SBJ");
        fshw_map.put("安全阀","AQF");
        fshw_map.put("变压器","BYQ");
        fshw_map.put("波形管","BXG");
        fshw_map.put("测流点","CLD");
        fshw_map.put("测压点","CTD");
        fshw_map.put("吹扫井","CSJ");
        fshw_map.put("地灯","DD");
        fshw_map.put("电杆","DG");
        fshw_map.put("电话亭","DHT");
        fshw_map.put("调压箱","RQTYX");
        fshw_map.put("调压站","RQTYZ");
        fshw_map.put("阀门井","FMJ21");//与类别有关：给水/燃气/热力/工业
        fshw_map.put("高压线塔架","GYT");
        fshw_map.put("固定节","GDJ");
        fshw_map.put("广告牌","GGP");
        fshw_map.put("加压泵站","JYBZ");
        fshw_map.put("监控器","JKQ");
        fshw_map.put("检查井","JCJ");
        fshw_map.put("检修井","JXJ31");//与类别有关:排水/热力
        fshw_map.put("交通信号灯","JTXHD");
        fshw_map.put("接线箱","JXX");
        fshw_map.put("景观灯","DG");
        fshw_map.put("路灯","DG");
        fshw_map.put("凝水缸","NSG");
        fshw_map.put("排潮孔","PCK");
        fshw_map.put("排气阀","JSPQF");
        fshw_map.put("排水泵站","PSBZ");
        fshw_map.put("排水阀","JSPSF");
        fshw_map.put("配电室","PDF");
        fshw_map.put("人孔","RK41");//与类别有关：电力/通讯
        fshw_map.put("手孔","SK51");//与类别有关：电力/通讯
        fshw_map.put("摄像头","SXT");
        fshw_map.put("疏水","SSQ");
        fshw_map.put("水表","SB");
        fshw_map.put("水质监测点","SZJCD");
        fshw_map.put("通风井","TFJ");
        fshw_map.put("污篦","YSB");
        fshw_map.put("污水井","PSYJ");
        fshw_map.put("消防栓","XFS");
        fshw_map.put("压力表","YLB");
        fshw_map.put("溢流井","YLJ");
        fshw_map.put("阴极测试桩","YJCSZ");
        fshw_map.put("雨篦","YSB");
        fshw_map.put("雨水井","YSJ");
        fshw_map.put("闸门井","ZMJ");
        fshw_map.put("真空表","ZKB");
    }

    private static Map<String,String> FMJ = new HashMap<>();
    static {
        FMJ.put("给水","JSFMJ");
        FMJ.put("燃气","RQYJ");
        FMJ.put("工业","GYYJ");
        FMJ.put("热力","RLYJ1");
        FMJ.put("热水","RLYJ");
        FMJ.put("蒸汽","FMJ");
    }
    
    private static Map<String,String> YJ = new HashMap<>();
    static {
        YJ.put("给水","JSYJ");
        YJ.put("燃气","RQYJ");
        YJ.put("排水","PSYJ");
        YJ.put("工业","GYYJ");
        YJ.put("热力","RLYJ");
        YJ.put("不明","PSYJ");
    }
    
    private static Map<String,String> JXJ = new HashMap<>();
    static {
        JXJ.put("热力","RLYJ");
        JXJ.put("排水","JCJ");
        JXJ.put("不明","JCJ");
    }

    private static Map<String,String> RK = new HashMap<>();
    static {
        RK.put("电力","DLYJ");
        RK.put("通讯","DXYJ");
    }

    private static Map<String,String> SK = new HashMap<>();
    static {
        SK.put("电力","DLSK");
        SK.put("通讯","DXSK");
    }

    /*
     * 获取符号名字
     * @param i 1：特征 2：附属物
     * @param s 特征/附属物属性值
     * @param dalei 大类
     * @param xiaolei 小类
     */
    String getMark(int i,String s,String dalei,String xiaolei){
        String mname = "1.dwg";
        switch (i){
            case 1:
                if(tzh_map.get(s)!= null)
                    mname =  tzh_map.get(s)+".dwg";
                break;
            case 2:
                String f = fshw_map.get(s);
                if(f == null || f.isEmpty()) {
                    break;
                }else if(f.endsWith("1")){
                        char n = f.charAt(f.length()-2);
                        switch (n){
                            case 49:
                                if(YJ.get(dalei) != null)
                                    mname = YJ.get(dalei)+".dwg";
                                break;
                            case 50:
                                String st = FMJ.get(dalei);
                                if(st != null && st.endsWith("1")){
                                    mname = FMJ.get(xiaolei)+".dwg";
                                }else {
                                    mname = st+".dwg";
                                }
                                break;
                            case 51:
                                if(JXJ.get(dalei) != null)
                                    mname = JXJ.get(dalei)+".dwg";
                                break;
                            case 52:
                                if(RK.get(dalei) != null)
                                    mname = RK.get(dalei)+".dwg";
                                break;
                            case 53:
                                if(SK.get(dalei) != null)
                                    mname = SK.get(dalei)+".dwg";
                                break;
                        }
                }else {
                    mname = f+".dwg";
                }
                break;
        }
        return mname;
    }

}
