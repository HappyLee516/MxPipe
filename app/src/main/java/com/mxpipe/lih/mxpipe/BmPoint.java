package com.mxpipe.lih.mxpipe;

/*
 * Created by LiHuan on 2018/3/17.
 */
//管点实体类
 class BmPoint {

    private String map_dot;//图上点号
    private String exploration_dot;//物探点号
    private String feature;//特征
    private String appendages;//附属物
    private double x;
    private double y;
    private int sign_rotation_angle;//符号旋转角
    private double ground_elevation;//地面高程
    private double commap_point_X;//综合图点号X坐标
    private double commap_point_Y;//综合图点号Y坐标
    private double spmap_point_X;//专业图点号X坐标
    private double spmap_point_Y;//专业图点号Y坐标
    private String point_code;//点要素编码
    private String road_name;//道路名称
    private String picture_number;//图幅号
    private String helper_type;//辅助类型
    private String delete_mark;//删除标记
    private String manhole_material;//井盖材质
    private String manhole_size;//井盖尺寸
    private String well_shape;//井盖形状
    private String well_material;//井材质
    private String well_size;//井尺寸
    private String used_status;//使用状态
    private String pipeline_type;//管线类型-大类
    private String manhole_type;//井盖类型
    private String eccentric_well_loc;//偏心井位
    private String EXPNO;//
    private String beizhu;//备注
    private String operator_library;//操作库
    private float bottom_hole_depth;//井底埋深
    private String data_source;//数据来源
    private String pipetype;//管线性质-小类

    String getMap_dot() {
        return map_dot;
    }

     void setMap_dot(String map_dot) {
        this.map_dot = map_dot;
    }

     String getExploration_dot() {
        return exploration_dot;
    }

     void setExploration_dot(String exploration_dot) {
        this.exploration_dot = exploration_dot;
    }

    String getFeature() {
        return feature;
    }

    void setFeature(String feature) {
        this.feature = feature;
    }

    String getAppendages() {
        return appendages;
    }

    void setAppendages(String appendages) {
        this.appendages = appendages;
    }

     double getX() {
        return x;
    }

     void setX(double x) {
        this.x = x;
    }

     double getY() {
        return y;
    }

     void setY(double y) {
        this.y = y;
    }

     double getGround_elevation() {
        return ground_elevation;
    }

     void setGround_elevation(double ground_elevation) {
        this.ground_elevation = ground_elevation;
    }

     int getSign_rotation_angle() {
        return sign_rotation_angle;
    }

     void setSign_rotation_angle(int sign_rotation_angle) {
        this.sign_rotation_angle = sign_rotation_angle;
    }

     double getCommap_point_X() {
        return commap_point_X;
    }

     void setCommap_point_X(double commap_point_X) {
        this.commap_point_X = commap_point_X;
    }

     double getCommap_point_Y() {
        return commap_point_Y;
    }

     void setCommap_point_Y(double commap_point_Y) {
        this.commap_point_Y = commap_point_Y;
    }

     double getSpmap_point_X() {
        return spmap_point_X;
    }

     void setSpmap_point_X(double spmap_point_X) {
        this.spmap_point_X = spmap_point_X;
    }

     double getSpmap_point_Y() {
        return spmap_point_Y;
    }

     void setSpmap_point_Y(double spmap_point_Y) {
        this.spmap_point_Y = spmap_point_Y;
    }

     void setBottom_hole_depth(float bottom_hole_depth) {
        this.bottom_hole_depth = bottom_hole_depth;
    }

     String getPoint_code() {
        return point_code;
    }

     void setPoint_code(String point_code) {
        this.point_code = point_code;
    }

     String getRoad_name() {
        return road_name;
    }

     void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

     String getPicture_number() {
        return picture_number;
    }

     void setPicture_number(String picture_number) {
        this.picture_number = picture_number;
    }

     String getHelper_type() {
        return helper_type;
    }

     void setHelper_type(String helper_type) {
        this.helper_type = helper_type;
    }

     String getDelete_mark() {
        return delete_mark;
    }

     void setDelete_mark(String delete_mark) {
        this.delete_mark = delete_mark;
    }

     String getManhole_material() {
        return manhole_material;
    }

     void setManhole_material(String manhole_material) {
        this.manhole_material = manhole_material;
    }

     String getManhole_size() {
        return manhole_size;
    }

     void setManhole_size(String manhole_size) {
        this.manhole_size = manhole_size;
    }

     String getWell_shape() {
        return well_shape;
    }

     void setWell_shape(String well_shape) {
        this.well_shape = well_shape;
    }

     String getWell_material() {
        return well_material;
    }

     void setWell_material(String well_material) {
        this.well_material = well_material;
    }

     String getWell_size() {
        return well_size;
    }

     void setWell_size(String well_size) {
        this.well_size = well_size;
    }

     String getUsed_status() {
        return used_status;
    }

     void setUsed_status(String used_status) {
        this.used_status = used_status;
    }

     String getPipeline_type() {
        return pipeline_type;
    }

     void setPipeline_type(String pipeline_type) {
        this.pipeline_type = pipeline_type;
    }

     String getManhole_type() {
        return manhole_type;
    }

     void setManhole_type(String manhole_type) {
        this.manhole_type = manhole_type;
    }

     String getEccentric_well_loc() {
        return eccentric_well_loc;
    }

     void setEccentric_well_loc(String eccentric_well_loc) {
        this.eccentric_well_loc = eccentric_well_loc;
    }

     String getEXPNO() {
        return EXPNO;
    }

     void setEXPNO(String EXPNO) {
        this.EXPNO = EXPNO;
    }

     String getBeizhu() {
        return beizhu;
    }

     void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

     String getOperator_library() {
        return operator_library;
    }

     void setOperator_library(String operator_library) {
        this.operator_library = operator_library;
    }

     float getBottom_hole_depth() {
        return bottom_hole_depth;
    }

     String getData_source() {
        return data_source;
    }

     void setData_source(String data_source) {
        this.data_source = data_source;
    }

     String getPipetype() {
        return pipetype;
    }

     void setPipetype(String pipetype) {
        this.pipetype = pipetype;
    }

    @Override
    public String toString() {
        return "BmPoint{" +
                "map_dot='" + map_dot + '\'' +
                ", exploration_dot='" + exploration_dot + '\'' +
                ", feature='" + feature + '\'' +
                ", appendages='" + appendages + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", sign_rotation_angle=" + sign_rotation_angle +
                ", ground_elevation=" + ground_elevation +
                ", commap_point_X=" + commap_point_X +
                ", commap_point_Y=" + commap_point_Y +
                ", spmap_point_X=" + spmap_point_X +
                ", spmap_point_Y=" + spmap_point_Y +
                ", point_code='" + point_code + '\'' +
                ", road_name='" + road_name + '\'' +
                ", picture_number='" + picture_number + '\'' +
                ", helper_type='" + helper_type + '\'' +
                ", delete_mark='" + delete_mark + '\'' +
                ", manhole_material='" + manhole_material + '\'' +
                ", manhole_size='" + manhole_size + '\'' +
                ", well_shape='" + well_shape + '\'' +
                ", well_material='" + well_material + '\'' +
                ", well_size='" + well_size + '\'' +
                ", used_status='" + used_status + '\'' +
                ", pipeline_type='" + pipeline_type + '\'' +
                ", manhole_type='" + manhole_type + '\'' +
                ", eccentric_well_loc='" + eccentric_well_loc + '\'' +
                ", EXPNO='" + EXPNO + '\'' +
                ", beizhu='" + beizhu + '\'' +
                ", operator_library='" + operator_library + '\'' +
                ", bottom_hole_depth=" + bottom_hole_depth +
                ", data_source='" + data_source + '\'' +
                ", pipetype='" + pipetype + '\'' +
                '}';
    }


}
