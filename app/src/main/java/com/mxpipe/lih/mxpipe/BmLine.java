package com.mxpipe.lih.mxpipe;

/*
 * Created by LiHuan on 2018/3/17.
 */
//管线实体类
public class BmLine {

    private String line_code;//线要素编码
    private String start_point;//起点点号
    private String conn_direction;//连接方向
    private float start_depth;//起点埋深
    private float end_depth;//终点埋深
    private String burial_type;//埋设类型
    private String material;//材质
    private String pipe_diameter;//管径
    private String flow_direction;//流向
    private String voltage_pressure;//电压压力
    private String cable_count;//电缆条数
    private String hole_count;//总孔数
    private String allot_holecount;//分配孔数
    private String construction_year;//建设年代
    private String lnumber;//LNUMBER
    private String linetype;//线型
    private String sp_ann_content;//专业注记内容
    private double sp_ann_X;//专业注记X坐标
    private double sp_ann_Y;//专业注记Y坐标
    private double sp_ann_angle;//专业注记角度
    private String com_ann_content;//综合注记内容
    private double com_ann_X;//综合注记X坐标
    private double com_ann_Y;//综合注记Y坐标
    private double com_ann_angle;//综合注记角度
    private String helper_type;//辅助类型
    private String used_holecount;//已用孔数
    private String delete_mark;//删除标记
    private String casing_size;//套管尺寸
    private double start_pipe_topele;//起点管顶高程
    private double end_pipe_topele;//终点管顶高程
    private String pipeline_ower_code;//管线权属代码
    private String beizhu;//备注
    private String operator_library;//操作库
    private String road_name;//道路名称
    private String groove_conncode;//管沟连接码
    private String pipetype;//管线类型

    private String tushangqidian;//图上起点号
    private String tushangzhongdian;//图上终点号

    public String getLine_code() {
        return line_code;
    }

    public void setLine_code(String line_code) {
        this.line_code = line_code;
    }

    public String getStart_point() {
        return start_point;
    }

    public void setStart_point(String start_point) {
        this.start_point = start_point;
    }

    public String getConn_direction() {
        return conn_direction;
    }

    public void setConn_direction(String conn_direction) {
        this.conn_direction = conn_direction;
    }

    public float getStart_depth() {
        return start_depth;
    }

    public void setStart_depth(float start_depth) {
        this.start_depth = start_depth;
    }

    public float getEnd_depth() {
        return end_depth;
    }

    public void setEnd_depth(float end_depth) {
        this.end_depth = end_depth;
    }

    public String getBurial_type() {
        return burial_type;
    }

    public void setBurial_type(String burial_type) {
        this.burial_type = burial_type;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPipe_diameter() {
        return pipe_diameter;
    }

    public void setPipe_diameter(String pipe_diameter) {
        this.pipe_diameter = pipe_diameter;
    }

    public String getFlow_direction() {
        return flow_direction;
    }

    public void setFlow_direction(String flow_direction) {
        this.flow_direction = flow_direction;
    }

    public String getVoltage_pressure() {
        return voltage_pressure;
    }

    public void setVoltage_pressure(String voltage_pressure) {
        this.voltage_pressure = voltage_pressure;
    }

    public String getCable_count() {
        return cable_count;
    }

    public void setCable_count(String cable_count) {
        this.cable_count = cable_count;
    }

    public String getHole_count() {
        return hole_count;
    }

    public void setHole_count(String hole_count) {
        this.hole_count = hole_count;
    }

    public String getAllot_holecount() {
        return allot_holecount;
    }

    public void setAllot_holecount(String allot_holecount) {
        this.allot_holecount = allot_holecount;
    }

    public String getConstruction_year() {
        return construction_year;
    }

    public void setConstruction_year(String construction_year) {
        this.construction_year = construction_year;
    }

    public String getLnumber() {
        return lnumber;
    }

    public void setLnumber(String lnumber) {
        this.lnumber = lnumber;
    }

    public String getLinetype() {
        return linetype;
    }

    public void setLinetype(String linetype) {
        this.linetype = linetype;
    }

    public String getSp_ann_content() {
        return sp_ann_content;
    }

    public void setSp_ann_content(String sp_ann_content) {
        this.sp_ann_content = sp_ann_content;
    }

    public double getSp_ann_X() {
        return sp_ann_X;
    }

    public void setSp_ann_X(double sp_ann_X) {
        this.sp_ann_X = sp_ann_X;
    }

    public double getSp_ann_Y() {
        return sp_ann_Y;
    }

    public void setSp_ann_Y(double sp_ann_Y) {
        this.sp_ann_Y = sp_ann_Y;
    }

    public double getSp_ann_angle() {
        return sp_ann_angle;
    }

    public void setSp_ann_angle(double sp_ann_angle) {
        this.sp_ann_angle = sp_ann_angle;
    }

    public String getCom_ann_content() {
        return com_ann_content;
    }

    public void setCom_ann_content(String com_ann_content) {
        this.com_ann_content = com_ann_content;
    }

    public double getCom_ann_X() {
        return com_ann_X;
    }

    public void setCom_ann_X(double com_ann_X) {
        this.com_ann_X = com_ann_X;
    }

    public double getCom_ann_Y() {
        return com_ann_Y;
    }

    public void setCom_ann_Y(double com_ann_Y) {
        this.com_ann_Y = com_ann_Y;
    }

    public double getCom_ann_angle() {
        return com_ann_angle;
    }

    public void setCom_ann_angle(double com_ann_angle) {
        this.com_ann_angle = com_ann_angle;
    }

    public String getHelper_type() {
        return helper_type;
    }

    public void setHelper_type(String helper_type) {
        this.helper_type = helper_type;
    }

    public String getUsed_holecount() {
        return used_holecount;
    }

    public void setUsed_holecount(String used_holecount) {
        this.used_holecount = used_holecount;
    }

    public String getDelete_mark() {
        return delete_mark;
    }

    public void setDelete_mark(String delete_mark) {
        this.delete_mark = delete_mark;
    }

    public String getCasing_size() {
        return casing_size;
    }

    public void setCasing_size(String casing_size) {
        this.casing_size = casing_size;
    }

    public double getStart_pipe_topele() {
        return start_pipe_topele;
    }

    public double getEnd_pipe_topele() {
        return end_pipe_topele;
    }

    public void setStart_pipe_topele(double start_pipe_topele) {
        this.start_pipe_topele = start_pipe_topele;
    }

    public void setEnd_pipe_topele(double end_pipe_topele) {
        this.end_pipe_topele = end_pipe_topele;
    }

    public String getPipeline_ower_code() {
        return pipeline_ower_code;
    }

    public void setPipeline_ower_code(String pipeline_ower_code) {
        this.pipeline_ower_code = pipeline_ower_code;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getOperator_library() {
        return operator_library;
    }

    public void setOperator_library(String operator_library) {
        this.operator_library = operator_library;
    }

    public String getRoad_name() {
        return road_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

    public String getGroove_conncode() {
        return groove_conncode;
    }

    public void setGroove_conncode(String groove_conncode) {
        this.groove_conncode = groove_conncode;
    }

    public String getPipetype() {
        return pipetype;
    }

    public void setPipetype(String pipetype) {
        this.pipetype = pipetype;
    }

    public String getTushangqidian() {
        return tushangqidian;
    }

    public void setTushangqidian(String tushangqidian) {
        this.tushangqidian = tushangqidian;
    }

    public String getTushangzhongdian() {
        return tushangzhongdian;
    }

    public void setTushangzhongdian(String tushangzhongdian) {
        this.tushangzhongdian = tushangzhongdian;
    }
}
