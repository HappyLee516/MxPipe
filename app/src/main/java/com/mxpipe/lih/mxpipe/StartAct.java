package com.mxpipe.lih.mxpipe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.MxDraw.McDbBlockReference;
import com.MxDraw.McDbEntity;
import com.MxDraw.McDbLayerTable;
import com.MxDraw.McDbLayerTableRecord;
import com.MxDraw.McDbLine;
import com.MxDraw.McDbPoint;
import com.MxDraw.McDbText;
import com.MxDraw.McGeMatrix3d;
import com.MxDraw.McGePoint3d;
import com.MxDraw.McGeVector3d;
import com.MxDraw.MrxDbgSelSet;
import com.MxDraw.MrxDbgUiPrPoint;
import com.MxDraw.MxDrawActivity;
import com.MxDraw.MxDrawDragEntity;
import com.MxDraw.MxDrawWorldDraw;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;
import com.MxDraw.MxResbuf;
import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.ResizeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.objectbox.Box;

import static android.os.Build.VERSION_CODES.M;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.mxpipe.lih.mxpipe.DaochuUtil.Daochu;
import static com.mxpipe.lih.mxpipe.DaochuUtil.addBl2mdb;
import static com.mxpipe.lih.mxpipe.DaochuUtil.addBp2mdb;
import static com.mxpipe.lih.mxpipe.DaochuUtil.addPoint;
import static com.mxpipe.lih.mxpipe.DaochuUtil.lm;
import static com.mxpipe.lih.mxpipe.DaochuUtil.pm;
import static com.mxpipe.lih.mxpipe.DaoruUtil.Mdb2Bls;
import static com.mxpipe.lih.mxpipe.DaoruUtil.Mdb2Bps;
import static com.mxpipe.lih.mxpipe.ReadDatUtil.ReadDat;
import static org.cocos2dx.lib.Cocos2dxHelper.getActivity;

public class StartAct extends MxDrawActivity implements AdapterView.OnItemSelectedListener {

    protected boolean m_isLoadAndroidLayoutUi = false;

    int operate = 0;//操作码：1-管点，2-连线，3-展点
    int ig      = 0, lg = 0; //ig:管点弹窗标识 1-管点，2-查看，3-展点  lg:线弹窗标识 1-添加，2-查看

    boolean enable = false;//是否可编辑

    static long selected = 0;
    String s_code, mark_name = "1.dwg";

    //连线数组：ps[0]为起点id，ps[1]为终点id，ps[2]为连接方式:1-点连点 2-方向线连方向线 3-点连方向线 4-方向线连点
    long[] ps = new long[3];
    static long[] color = new long[3];

    String[] last = new String[]{"", ""};

    View pview, lview;
    Popup pop, dl_pop;

    Box<PipeNo>       pipeNoBox = null;
    Box<CodeNumber>   codeNumberBox;
    SharedPreferences sp        = null;

    //管点弹窗控件
    Spinner type, type_item, tezheng, fushuwu, jgxzh, jgczh, jgzht, jczh, state, data;
    EditText jdmsh, jgchc, jchc, dmgch, beizhu;
    TextView code, p_back, p_ok, np_ok;
    double x, y;
    LinearLayout ll_jing;

    //管线弹窗控件
    TextView tv_type, nl_ok, l_ok, l_back, scode, ecode, tv_gj;
    Spinner s_caizhi, s_mshway, yali, direc;
    EditText smaishen, emaishen, guanjing1, gao, num, allnum, usednum, road;
    LinearLayout ll_yali, ll_guanjing2, ll_direc, ll_ts, ll_ks;

    //方向线弹窗控件
    EditText dl_et_maishen, dl_et_gj, dl_et_ts, dl_et_zksh, dl_et_yyksh, dl_et_gao;
    Spinner dl_czh, dl_mshfsh, dl_yl;
    LinearLayout dl_ll_ts, dl_ll_ks, dl_ll_yl;

    //mdb库
    static Database db, im_db;

    HashMap<Integer, Boolean> checkedMap;

    static BmPoint bmPoint = null;//管点实体类实例-封装属性，传递值
    static BmLine  bmLine  = null;//管线实体类实例-封装属性，传递值

    //若发生崩溃导致图上数据丢失，则导入图纸对应的mdb数据重新生成一次
    List<BmPoint> bps = null;//Mdb库中的所有点数据封装到Bmpoint对象后的集合，即所有点数据封装
    List<BmLine>  bls = null;//Mdb库中的所有线数据封装到Bmline对象后的集合，即所有线数据封装

    static long pid;//选中查看/修改管点时传递的点id

    McGePoint3d startp, endp;//线起点、终点

    //线的起点、终点的X、Y
    double sx = 0.0d;
    double sy = 0.0d;
    double ex = 0.0d;
    double ey = 0.0d;

    private MyHandler mMyHandler = new MyHandler(this);

    /*
     *在handler中实现库中管点数据的修改
     */
    static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        private MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1 || msg.what == 2) {//查看修改管点时修改库
                    //点数据更新--先删后加
                    try {
                        if (db == null) {
                            db = DatabaseBuilder.open(new File(mdbName()));
                        }
                        String ti = bmPoint.getPipetype();
                        if (pm.containsKey(ti)) {
                            Table ta = db.getTable(pm.get(ti));
                            Row row = CursorBuilder.findRow(ta, Collections.singletonMap("物探点号", bmPoint.getExploration_dot()));
                            Log.i("查看-修改-找到的row", row.toString());
                            ta.deleteRow(row);
                            addBp2mdb(pid, ta);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 3) {//移动管点时修改库中坐标
                    try {
                        if (db == null) {
                            db = DatabaseBuilder.open(new File(mdbName()));
                        }
                        String unicode = MxFunction.getxDataString(selected, "unicode");
                        String ti = MxFunction.getxDataString(selected, "type_item");
                        String x = MxFunction.getxDataString(selected, "x");
                        String y = MxFunction.getxDataString(selected, "y");
                        if (pm.containsKey(ti)) {
                            Table ta = db.getTable(pm.get(ti));
                            Row row = CursorBuilder.findRow(ta, Collections.singletonMap("物探点号", unicode));
                            Log.i("移动-找到的row", row.toString());
                            row.put("X", y);
                            row.put("Y", x);
                            ta.updateRow(row);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public StartAct() {
        initWorkDir(Environment.getExternalStorageDirectory() + File.separator + "BY_PIPE");
        setPackageName("com.mxpipe.lih.mxpipe");
    }

    @Override
    public void mcrxEntryPoint(int iCode) {
        super.mcrxEntryPoint(iCode);
        if (iCode == kInitAppMsg) {
            copyShxFile("aaa.shx");
            MxFunction.setShowFileBrowse(true);
            MxFunction.setShowUpToolBar(true);
            MxFunction.setShowDownToolBar(true); //底部功能菜单
            MxFunction.setShowReturnButton(true);
            MxFunction.enableSelect(true);
            MxFunction.enableGridEdit(true); //是否开启夹点编辑
            MxFunction.setShowTip(true);
            MxFunction.setSaveZValue(true);//开启记录Z坐标

            MxFunction.setToolFile("mytool.json"); //底部功能菜单文件
            MxFunction.setMenuFile("mymenu.json");
            //MxFunction.setReadFileContent(ReadContent.kFastRead | ReadContent.kReadObjectsDictionary | ReadContent.kReadXrecord | ReadContent.kReadNamedObjectsDictionary);
            MxFunction.setReadFileContent(ReadContent.kFastRead | ReadContent.kReadObjectsDictionary | ReadContent.kReadxData | ReadContent.kReadNamedObjectsDictionary | ReadContent.kReadXrecord);
            //MxFunction.enableUndo();//回退
            MxFunction.enablePopToolbar(false);
        } else if (iCode == kStartScene) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_isLoadAndroidLayoutUi = true;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mFile = extras.getString("file");
            if (!mFile.isEmpty()) {
                MxFunction.openFile(mFile);
            }
        }
    }

    @Override
    public boolean createInterfaceLayout() {
        if (!m_isLoadAndroidLayoutUi)
            return false;

        setContentView(R.layout.activity_start);

        ResizeLayout mFrameLayout = this.findViewById(R.id.rl);
        Cocos2dxGLSurfaceView mGLSurfaceView = this.findViewById(R.id.cgv_cad);
        initInterfaceLayout(mFrameLayout, null, mGLSurfaceView);

        return true;
    }

    @Override
    public void openComplete(boolean isOpenSucces) {
        Log.i("openComplete", MxFunction.currentFileName());
        //打开完成，设置为默认不可编辑
        enable = false;
        MxFunction.enableSelect(false);
        codeNumberBox = MyApplication.getApplication().getBoxStore().boxFor(CodeNumber.class);
        MxFunction.setSysVarString("CLAYER", "mxcadcomment");

    }

    @Override
    public void initComplete() {
        Log.e("initComplete", "initComplete");
        MxFunction.setViewColor(255, 255, 255); //设置底色为白色
    }

    @Override
    public boolean dynWorldDraw(MxDrawWorldDraw draw, MxDrawDragEntity dragData) {
        if (dragData.GetGuid().equals("mydyndraw")) {
            // 交互画直线的动态绘制。
            // 取到动态绘制数据。
            String sPrv = dragData.GetString("Prv");
            McGePoint3d pt2 = dragData.GetDragCurrentPoint();
            McGePoint3d pt1 = dragData.GetPoint("pt1");
            // 算出，动态距离。
            double dDist = pt1.distanceTo(pt2);
            McGeVector3d vec = pt2.SumVector(pt1);
            double dAng = vec.angleTo(McGeVector3d.kXAxis, McGeVector3d.kNZAxis);
            vec.Mult(0.5);
            pt1.Add(vec);
            String sT;
            sT = sPrv + "=" + String.format("%f", dDist);
            double dH = MxFunction.viewLongToDoc(30);
            // 在两点的中心点，动态绘制一个文本。
            draw.DrawText(pt1.x, pt1.y, sT, dH, dAng, 1, 1);
            // draw.DrawLine(0,0,100,100);
            return true;
        }
        return false;
    }

    //画方向线执行方法
    public void DynDrawLine() {

        McDbBlockReference mdb = (McDbBlockReference) MxFunction.objectIdToObject(selected);
        McGePoint3d pt3 = mdb.position();
        Log.i("pt3...", pt3.x + "::::" + pt3.y);
        // 交互取第一个点.
        MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
        getPoint.setBasePt(pt3);
        getPoint.setUseBasePt(true);
        getPoint.setOffsetInputPostion(true);
        // 初始化动态绘制，在交到过程中，会不停调用 dynWorldDraw函数，实现动态画图。
        MxDrawDragEntity drawdata = getPoint.initUserDraw("mydyndraw");

        // 设置动态绘制数据。
        drawdata.SetString("Prv", "Len");
        drawdata.SetPoint("pt1", pt3);
        getPoint.setOffsetInputPostion(true);
        if (getPoint.go() != MrxDbgUiPrPoint.Status.kOk) {
            return;
        }
        McGePoint3d pt = getPoint.value();

        McDbLayerTable table = MxFunction.getCurrentDatabase().getLayerTable();
        if (!table.has("DIRECTIONLINE")) {
            MxLibDraw.addLayer("DIRECTIONLINE");
        }
        MxLibDraw.setLayerName("DIRECTIONLINE");

        //设置线型为虚线
        MxLibDraw.addLinetype("MyLine", "2,-1", 1);
        MxLibDraw.setLineType("MyLine");

        MxLibDraw.setDrawColor(getColor(MxFunction.getxDataString(selected, "type")));
        Log.i("DynDrawLine", "--" + selected);
        MxLibDraw.setDrawColor(getColor(MxFunction.getxDataString(selected, "type")));
        final long id = MxLibDraw.drawLine(pt3.x, pt3.y, pt.x, pt.y);
        McDbLayerTableRecord tr = new McDbLayerTableRecord("DIRECTIONLINE");
        tr.setIsLocked(true);
        this.runOnUiThread((new Runnable() {
            @Override
            public void run() {
                pop_directionLine(1, id, selected);
            }
        }));
    }

    //画圆
    public void DrawCircle() {
        // 交互取第一个点--圆心
        MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
        getPoint.setOffsetInputPostion(true);
        if (getPoint.go() != MrxDbgUiPrPoint.Status.kOk) {
            return;
        }
        final McGePoint3d pt = getPoint.value();

        // 交互取第二个点
        MrxDbgUiPrPoint getPoint2 = new MrxDbgUiPrPoint();
        getPoint2.setBasePt(pt);
        getPoint2.setUseBasePt(true);
        getPoint2.setOffsetInputPostion(true);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.radius, null);
                final Popup pw = new Popup(v, width, WRAP_CONTENT);
                pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                ColorDrawable cd = new ColorDrawable(0x000000);
                pw.setBackgroundDrawable(cd);
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.4f;
                getWindow().setAttributes(lp);

                pw.setOutsideTouchable(false);
                pw.setFocusable(true);
                pw.setIsdismiss(false);
                pw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

                pw.update();
                pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    //在dismiss中恢复透明度
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);
                    }
                });

                TextView cancel = v.findViewById(R.id.tv_r_cancel);
                TextView sure = v.findViewById(R.id.tv_r_sure);
                final EditText radius = v.findViewById(R.id.et_radius);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.close();
                    }
                });

                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        McDbLayerTable table = MxFunction.getCurrentDatabase().getLayerTable();
                        if (!table.has("ASSIST")) {
                            MxLibDraw.addLayer("ASSIST");
                        }
                        if (!TextUtils.isEmpty(radius.getText())) {
                            pw.close();
                            Log.i("半径.....", "" + radius.getText());
                            Log.i("坐标.....", "" + pt.x + "," + pt.y);
                            MxLibDraw.setLayerName("ASSIST");
                            MxLibDraw.drawCircle(pt.x, pt.y, Double.parseDouble(radius.getText().toString()));
                            McDbLayerTableRecord mc = new McDbLayerTableRecord("ASSIST");
                            mc.setIsLocked(true);
                        }
                    }
                });
            }
        });

    }

    /*
     *@param iType触摸类型
     */
    @Override
    public int touchesEvent(int iType, double dX, double dY) {
        Log.i("touchesEvent", "执行touchesEvent");
        switch (operate) {
            case 0:
                break;
            case 1:
                if (enable) {
                    x = dX;
                    y = dY;
                    final long p = MxFunction.findEntAtPoint(dX, dY, "");
                    if (p != 0 && MxFunction.getTypeName(p).equals("McDbBlockReference")) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "此处已有管点，请重新选择位置！", Toast.LENGTH_SHORT).show();
                                MxFunction.delSelect(p);
                            }
                        });
                        break;
                    } else {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ig = 1;
                                point_pop(ig, x, y, 0);
                                setSpinners();
                            }
                        });
                    }
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "当前已关闭编辑，请先打开编辑！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                operate = 0;
                break;
            case 2:
                if (!enable) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "当前已关闭编辑，请先打开编辑！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
        return 0;
    }

    //管点操作弹出框
    private void op_point(final long id) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.op_point, null);
        pop = new Popup(v, WRAP_CONTENT,
                WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        pop.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setIsdismiss(true);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        pop.update();
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextView chakan = v.findViewById(R.id.tv_chakan);
        TextView yidong = v.findViewById(R.id.tv_yidong);

        TextView del = v.findViewById(R.id.tv_del);

        chakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                ig = 2;
                McDbBlockReference rr = (McDbBlockReference) MxFunction.objectIdToObject(id);
                Log.i("查看点-Z坐标", ":" + rr.position().z);
                point_pop(ig, rr.position().x, rr.position().y, rr.position().z);
                set_Pinfo(id);
            }
        });

        yidong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog ad = builder.setMessage("是否确认进行移动？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MxFunction.doThreadCommand(9);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                ad.show();
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog ad = builder.setMessage("删除管点会同时删除相关管线，是否确认进行删除？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MxFunction.doThreadCommand(10);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();

                ad.show();
            }
        });
    }

    //管线操作弹出框
    private void op_line(final long id) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.op_line, null);
        pop = new Popup(v, WRAP_CONTENT, WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        pop.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setIsdismiss(true);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        pop.update();
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextView chakan = v.findViewById(R.id.tv_chakan);
        TextView del = v.findViewById(R.id.tv_del);

        chakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                ig = 2;
                String type = MxFunction.getxDataString(selected, "type");
                if (type == null) {
                    Toast.makeText(getApplicationContext(), "数据丢失，无法查看!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String ltype = ti_t_Util.getType(type);
                if (ltype == null) {
                    Toast.makeText(getApplicationContext(), "数据丢失，无法查看!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //                Log.i("查询到的大类", ltype);
                line_pop(2, type, ltype);
                lg = 1;
                set_Linfo(selected, ltype);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog ad = builder.setMessage("是否确认删除该管线？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MxFunction.doThreadCommand(20);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                ad.show();
            }
        });
    }

    //方向线操作弹出框
    private void op_directionline(final long id) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.op_line, null);
        pop = new Popup(v, WRAP_CONTENT, WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        pop.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setIsdismiss(true);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        pop.update();
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextView chakan = v.findViewById(R.id.tv_chakan);
        TextView del = v.findViewById(R.id.tv_del);

        chakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                if (MxFunction.getxDataString(id, "point_unicode") == null || "".equals(MxFunction.getxDataString(id, "point_unicode"))) {
                    Toast.makeText(getApplication(), "数据已丢失！", Toast.LENGTH_SHORT).show();
                } else {
                    pop_directionLine(2, id, 0);
                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog ad = builder.setMessage("是否确认删除该方向线？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MxFunction.deleteObject(id);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                ad.show();
            }
        });
    }

    /*
     *管点信息编辑/查看修改弹出框
     * @param flag 弹窗标识：1-管点 2-查看 3-展点编辑
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     */
    private void point_pop(final int flag, double dx, double dy, final double dz) {
        if (flag == 1 || flag == 3) {
            pview = LayoutInflater.from(getActivity()).inflate(R.layout.newpoint, null);
        } else {
            pview = LayoutInflater.from(getActivity()).inflate(R.layout.point_info, null);
        }

        Log.i("z值---", "" + dz);

        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
        int height = (int) (manager.getDefaultDisplay().getHeight() * 0.8);

        final Popup ppw = new Popup(pview, width, height);
        //ppw.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        ppw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ColorDrawable cd = new ColorDrawable(0x000000);
        ppw.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        ppw.setOutsideTouchable(false);
        ppw.setFocusable(true);
        ppw.setIsdismiss(false);
        ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        ppw.update();
        ppw.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        code = pview.findViewById(R.id.et_code);

        type = pview.findViewById(R.id.s_type);
        type_item = pview.findViewById(R.id.s_type_item);
        tezheng = pview.findViewById(R.id.s_tezheng);
        fushuwu = pview.findViewById(R.id.s_fushuwu);
        jgxzh = pview.findViewById(R.id.s_jgxzh);
        jgczh = pview.findViewById(R.id.s_jgczh);
        jgzht = pview.findViewById(R.id.s_jgzht);
        jczh = pview.findViewById(R.id.s_jczh);
        state = pview.findViewById(R.id.np_s_state);
        data = pview.findViewById(R.id.np_s_data);
        jdmsh = pview.findViewById(R.id.np_jdmsh);
        jgchc = pview.findViewById(R.id.np_jgchc);
        jchc = pview.findViewById(R.id.np_jchc);
        dmgch = pview.findViewById(R.id.np_dmgch);
        beizhu = pview.findViewById(R.id.np_beizhu);
        p_back = pview.findViewById(R.id.point_back);
        ll_jing = pview.findViewById(R.id.ll_jing);
        ll_jing.setVisibility(View.GONE);

        ArrayAdapter adapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.type));
        ArrayAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.state));
        ArrayAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.data));
        ArrayAdapter adapter4 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.jinggaixingzhuang));
        ArrayAdapter adapter5 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.jinggaicaizhi));
        ArrayAdapter adapter6 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.jinggaizhuangtai));
        ArrayAdapter adapter7 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.jingcaizhi));

        type.setAdapter(adapter1);
        state.setAdapter(adapter2);
        data.setAdapter(adapter3);
        jgxzh.setAdapter(adapter4);
        jgczh.setAdapter(adapter5);
        jgzht.setAdapter(adapter6);
        jczh.setAdapter(adapter7);

        if (flag == 1) {
            x = dx;
            y = dy;
        } else if (flag == 3) {
            x = dx;
            y = dy;
            dmgch.setText(String.valueOf(dz));
        } else {
            dmgch.setText(MxFunction.getxDataString(selected, "dmgch"));
        }

        type.setOnItemSelectedListener(this);

        if (flag == 1 || flag == 3) {
            np_ok = pview.findViewById(R.id.newpoint_ok);
            np_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //检查库中是否有所选管种。如果没有，则无法添加
                    try {
                        Database db = DatabaseBuilder.open(new File(mdbName()));
                        Set<String> set = db.getTableNames();
                        String tablename = TypeItemUtil.getPre(type_item.getSelectedItem().toString()) + "_POINT";
                        if (!set.contains(tablename)) {
                            Toast.makeText(getApplication(), "库中尚无此管种，请先新建管种！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (tezheng.getSelectedItem().toString().equals("请选择")) {
                        if (fushuwu.getSelectedItem().toString().equals("请选择")) {
                            Toast.makeText(getApplication(), "特征和附属物必须选一个！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    String ti = type_item.getSelectedItem().toString();
                    sp = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
                    String tno = sp.getString("tno", null);
                    String pno = sp.getString("pno", null);
                    String area = sp.getString("area", null);

                    if (pipeNoBox == null) {
                        pipeNoBox = MyApplication.getApplication().getBoxStore().boxFor(PipeNo.class);
                    }
                    String unicode = null;
                    List<PipeNo> pns = pipeNoBox.query().equal(PipeNo_.type, ti).build().find();
                    if (pns.size() == 0) {
                        PipeNo pn = new PipeNo();
                        pn.setNo(1);
                        pn.setType(ti);
                        pipeNoBox.put(pn);
                        String num = String.format("%05d", 1);
                        unicode = TypeItemUtil.getPre(ti) + area + tno + pno + num;
                    } else if (pns.size() == 1) {
                        PipeNo pn = pns.get(0);
                        pn.setNo(pn.getNo() + 1);
                        pipeNoBox.put(pn);
                        String num = String.format("%05d", pn.getNo());
                        unicode = TypeItemUtil.getPre(ti) + area + tno + pno + num;
                    } else {
                        StartAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "数据异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    bmPoint = new BmPoint();
                    bmPoint.setMap_dot(code.getText().toString()); //图上点号
                    bmPoint.setExploration_dot(unicode);//物探点号
                    bmPoint.setFeature(tezheng.getSelectedItem().toString().equals("请选择") ? "" : tezheng.getSelectedItem().toString());
                    bmPoint.setAppendages(fushuwu.getSelectedItem().toString().equals("请选择") ? "" : fushuwu.getSelectedItem().toString());
                    bmPoint.setX(y);//X
                    bmPoint.setY(x);//Y
                    bmPoint.setSign_rotation_angle(0);//符号旋转角
                    bmPoint.setGround_elevation(TextUtils.isEmpty(dmgch.getText()) ? 0.0d : Double.parseDouble(dmgch.getText().toString()));//地面高程
                    bmPoint.setCommap_point_X(0.0D);//综合图点号X坐标
                    bmPoint.setCommap_point_Y(0.0D);//综合图点号Y坐标
                    bmPoint.setSpmap_point_X(0.0D);//专业图点号X坐标
                    bmPoint.setSpmap_point_Y(0.0D);//专业图点号Y坐标
                    bmPoint.setPoint_code("");//点要素编码
                    bmPoint.setRoad_name("");//道路名称
                    bmPoint.setPicture_number("");//图幅号
                    bmPoint.setHelper_type("");//辅助类型
                    bmPoint.setDelete_mark("");//删除标记
                    if (ll_jing.getVisibility() == View.VISIBLE) {
                        bmPoint.setManhole_material(jgczh.getSelectedItem().toString());//井盖材质
                        bmPoint.setManhole_size(TextUtils.isEmpty(jgchc.getText()) ? "" : jgchc.getText().toString());//井盖尺寸
                        bmPoint.setWell_shape(jgxzh.getSelectedItem().toString());//井盖形状
                        bmPoint.setWell_material(jczh.getSelectedItem().toString()); // 井材质
                        bmPoint.setWell_size(TextUtils.isEmpty(jchc.getText()) ? "" : jchc.getText().toString());// 井尺寸
                        bmPoint.setBottom_hole_depth(TextUtils.isEmpty(jdmsh.getText()) ? 0.0f : Float.parseFloat(jdmsh.getText().toString()));
                        bmPoint.setManhole_type(jgzht.getSelectedItem().toString());//井盖类型
                    } else {
                        bmPoint.setManhole_material("");//井盖材质
                        bmPoint.setManhole_size("");//井盖尺寸
                        bmPoint.setWell_shape("");//井盖形状
                        bmPoint.setWell_material(""); // 井材质
                        bmPoint.setWell_size("");// 井尺寸
                        bmPoint.setBottom_hole_depth(0.0f);
                        bmPoint.setManhole_type("");//井盖类型
                    }

                    bmPoint.setUsed_status(state.getSelectedItem().toString());//使用状态
                    bmPoint.setPipeline_type(type.getSelectedItem().toString());//管线类型-大类

                    bmPoint.setEccentric_well_loc("");//偏心井位
                    bmPoint.setEXPNO("");//EXPNO
                    bmPoint.setBeizhu(TextUtils.isEmpty(beizhu.getText()) ? "" : beizhu.getText().toString());//备注
                    bmPoint.setOperator_library("");//操作库

                    bmPoint.setData_source(data.getSelectedItem().toString());//数据来源
                    bmPoint.setPipetype(ti);//管线性质-小类
                    Log.i("类名-表名", ti);
                    Log.i("bmPoint", bmPoint.toString());

                    MxFunction.doCommand(111);

                    ppw.close();

                }
            });
        } else {
            p_ok = pview.findViewById(R.id.point_ok);
            p_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tezheng.getSelectedItem().toString().equals("请选择")) {
                        if (fushuwu.getSelectedItem().toString().equals("请选择")) {
                            Toast.makeText(getApplication(), "特征和附属物必须选一个！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    String code = MxFunction.getxDataString(selected,"code");
                    String unicode = MxFunction.getxDataString(selected, "unicode");
                    x = Double.parseDouble(MxFunction.getxDataString(selected, "x"));
                    y = Double.parseDouble(MxFunction.getxDataString(selected, "y"));
                    bmPoint = new BmPoint();
                    bmPoint.setMap_dot(code); //图上点号
                    bmPoint.setExploration_dot(unicode);//物探点号
                    bmPoint.setFeature(tezheng.getSelectedItem().toString().equals("请选择") ? " " : tezheng.getSelectedItem().toString());
                    bmPoint.setAppendages(fushuwu.getSelectedItem().toString().equals("请选择") ? " " : fushuwu.getSelectedItem().toString());
                    bmPoint.setX(y);//X
                    bmPoint.setY(x);//Y
                    bmPoint.setSign_rotation_angle(0);//符号旋转角
                    bmPoint.setGround_elevation(TextUtils.isEmpty(dmgch.getText()) ? 0.0d : Double.parseDouble(dmgch.getText().toString()));//地面高程
                    bmPoint.setCommap_point_X(0.0D);//综合图点号X坐标
                    bmPoint.setCommap_point_Y(0.0D);//综合图点号Y坐标
                    bmPoint.setSpmap_point_X(0.0D);//专业图点号X坐标
                    bmPoint.setSpmap_point_Y(0.0D);//专业图点号Y坐标
                    bmPoint.setPoint_code(" ");//点要素编码
                    bmPoint.setRoad_name(" ");//道路名称
                    bmPoint.setPicture_number(" ");//图幅号
                    bmPoint.setHelper_type(" ");//辅助类型
                    bmPoint.setDelete_mark(" ");//删除标记
                    if (ll_jing.getVisibility() == View.VISIBLE) {
                        bmPoint.setManhole_material(jgczh.getSelectedItem().toString());//井盖材质
                        bmPoint.setManhole_size(TextUtils.isEmpty(jgchc.getText()) ? " " : jgchc.getText().toString());//井盖尺寸
                        bmPoint.setWell_shape(jgxzh.getSelectedItem().toString());//井盖形状
                        bmPoint.setWell_material(jczh.getSelectedItem().toString()); // 井材质
                        bmPoint.setWell_size(TextUtils.isEmpty(jchc.getText()) ? " " : jchc.getText().toString());// 井尺寸
                        bmPoint.setBottom_hole_depth(TextUtils.isEmpty(jdmsh.getText()) ? 0.0f : Float.parseFloat(jdmsh.getText().toString()));
                        bmPoint.setManhole_type(jgzht.getSelectedItem().toString());//井盖类型
                    } else {
                        bmPoint.setManhole_material(" ");//井盖材质
                        bmPoint.setManhole_size(" ");//井盖尺寸
                        bmPoint.setWell_shape(" ");//井盖形状
                        bmPoint.setWell_material(" "); // 井材质
                        bmPoint.setWell_size(" ");// 井尺寸
                        bmPoint.setBottom_hole_depth(0.0f);
                        bmPoint.setManhole_type(" ");//井盖类型
                    }

                    bmPoint.setUsed_status(state.getSelectedItem().toString());//使用状态
                    bmPoint.setPipeline_type(type.getSelectedItem().toString());//管线类型-大类

                    bmPoint.setEccentric_well_loc(" ");//偏心井位
                    bmPoint.setEXPNO(" ");//EXPNO
                    bmPoint.setBeizhu(TextUtils.isEmpty(beizhu.getText()) ? " " : beizhu.getText().toString());//备注
                    bmPoint.setOperator_library(" ");//操作库

                    bmPoint.setData_source(data.getSelectedItem().toString());//数据来源
                    bmPoint.setPipetype(type_item.getSelectedItem().toString());//管线性质-小类

                    //判断特征/附属物是否变化，变化则重新绘制并修改相关数据
                    String o_type = MxFunction.getxDataString(selected, "tezheng");
                    String o_item = MxFunction.getxDataString(selected, "fushuwu");
                    String n_type = tezheng.getSelectedItem().toString();
                    String n_item = fushuwu.getSelectedItem().toString();

                    if (n_type.equals(o_type) || n_item.equals(o_item)) {
                        Log.i("修改，执行113","113");
                        pid = selected;
                        bmpoint2xdata(bmPoint, selected);
                        MxFunction.doCommand(113);
                        mMyHandler.sendEmptyMessage(1);
                    } else {
                        Log.i("修改，执行112","112");
                        MxFunction.doCommand(112);
                    }

                    ppw.close();

                }
            });
        }

        p_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ppw.close();

                MxFunction.doCommand(113);
            }
        });

    }

    //根据上一个点的类别，自动选择相同的类别
    private void setSpinners() {
        if (!last[0].equals("")) {
            set(type, getResources().getStringArray(R.array.type), last[0]);
            String c_type = last[0];
            if (c_type.equals("给水")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongshui));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.gongshui), last[1]);
            } else if (c_type.equals("排水")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.paishui));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.paishui), last[1]);
            } else if (c_type.equals("燃气")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.ranqi), last[1]);
            } else if (c_type.equals("热力")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reli));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.reli), last[1]);
            } else if (c_type.equals("电力")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.dianli), last[1]);
            } else if (c_type.equals("通讯")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.tongxin));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.tongxin), last[1]);
            } else if (c_type.equals("工业")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.gongye), last[1]);
            } else if (c_type.equals("综合管沟")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.zongheguangou));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.zongheguangou), last[1]);
            } else if (c_type.equals("人防")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.renfang));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.renfang), last[1]);
            } else if (c_type.equals("地铁")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ditie));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.ditie), last[1]);
            } else if (c_type.equals("不明")) {
                ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.buming));
                type_item.setAdapter(ArrayAdapter1);
                set(type_item, getResources().getStringArray(R.array.buming), last[1]);
            }
        } else {
            type.setSelection(0);
            ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongshui));
            type_item.setAdapter(ArrayAdapter1);
        }
    }

    //读取记录的管点属性值,填入弹窗中
    private void set_Pinfo(long id) {
        String s_code = MxFunction.getxDataString(id, "code");
        String s_type = MxFunction.getxDataString(id, "type");
        String s_typeitem = MxFunction.getxDataString(id, "type_item");
        String s_tezheng = MxFunction.getxDataString(id, "tezheng");
        String s_fushuwu = MxFunction.getxDataString(id, "fushuwu");
        String s_jdmsh = MxFunction.getxDataString(id, "jdmsh");
        String s_jgczh = MxFunction.getxDataString(id, "jgczh");
        String s_jgxzh = MxFunction.getxDataString(id, "jgxzh");
        String s_jgchc = MxFunction.getxDataString(id, "jgchc");
        String s_jgzht = MxFunction.getxDataString(id, "jgzht");
        String s_jczh = MxFunction.getxDataString(id, "jczh");
        String s_jchc = MxFunction.getxDataString(id, "jchc");
        String s_shyzht = MxFunction.getxDataString(id, "shyzht");
        String s_shjly = MxFunction.getxDataString(id, "shjly");
        String s_bzh = MxFunction.getxDataString(id, "bzh");

        Log.i("属性值···", s_type + "," + s_typeitem + "," + s_tezheng + "," + s_fushuwu);
        Log.i("unicode···", MxFunction.getxDataString(id, "unicode"));
        set(type, getResources().getStringArray(R.array.type), s_type);
        code.setText(s_code);

        if (s_fushuwu.endsWith("井") || s_fushuwu.endsWith("篦") || s_fushuwu.equals("人孔") || s_fushuwu.equals("手孔")) {
            ll_jing.setVisibility(View.VISIBLE);
            jdmsh.setText(s_jdmsh);
            jgchc.setText(s_jgchc);
            jchc.setText(s_jchc);
            set(jgczh, getResources().getStringArray(R.array.jinggaicaizhi), s_jgczh);
            set(jgxzh, getResources().getStringArray(R.array.jinggaixingzhuang), s_jgxzh);
            set(jgzht, getResources().getStringArray(R.array.jinggaizhuangtai), s_jgzht);
            set(jczh, getResources().getStringArray(R.array.jingcaizhi), s_jczh);
        }

        set(state, getResources().getStringArray(R.array.state), s_shyzht);
        set(data, getResources().getStringArray(R.array.data), s_shjly);
        beizhu.setText(s_bzh);

    }

    /*
      管线弹窗
      @param flag 弹窗标识：1：新建管线 2：查看管线
      @param ltype 管线类别-管点小类
      @param type 管点大类-用作适配页面展示属性及数组资源
     */
    private void line_pop(int flag, final String ltype, final String type) {
        if (flag == 1) {
            lview = LayoutInflater.from(getActivity()).inflate(R.layout.newline, null);
        } else {
            lview = LayoutInflater.from(getActivity()).inflate(R.layout.line_info, null);
        }

        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
        int height = (int) (manager.getDefaultDisplay().getHeight() * 0.8);

        final Popup ppw = new Popup(lview, width, height);
        ppw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ColorDrawable cd = new ColorDrawable(0x000000);
        ppw.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        ppw.setOutsideTouchable(false);
        ppw.setFocusable(true);
        ppw.setIsdismiss(false);
        ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        ppw.update();
        ppw.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                //                MxFunction.delSelect(selected);
            }
        });

        tv_type = lview.findViewById(R.id.tv_type);
        scode = lview.findViewById(R.id.tv_nl_scode);
        ecode = lview.findViewById(R.id.tv_nl_ecode);

        tv_gj = lview.findViewById(R.id.tv_gj);

        l_back = lview.findViewById(R.id.line_back);
        s_caizhi = lview.findViewById(R.id.s_caizhi);
        s_mshway = lview.findViewById(R.id.s_mshway);
        yali = lview.findViewById(R.id.s_yali);

        smaishen = lview.findViewById(R.id.et_smaishen);
        emaishen = lview.findViewById(R.id.et_emaishen);
        guanjing1 = lview.findViewById(R.id.et_guanjing1);
        gao = lview.findViewById(R.id.et_guanjing2);
        num = lview.findViewById(R.id.et_num);
        allnum = lview.findViewById(R.id.et_allkong);
        usednum = lview.findViewById(R.id.et_usedkong);
        road = lview.findViewById(R.id.et_road);
        direc = lview.findViewById(R.id.et_direc);

        ll_yali = lview.findViewById(R.id.ll_yali);
        ll_guanjing2 = lview.findViewById(R.id.ll_guanjing2);
        ll_direc = lview.findViewById(R.id.ll_direc);
        ll_ts = lview.findViewById(R.id.ll_ts);
        ll_ks = lview.findViewById(R.id.ll_ks);

        ll_yali.setVisibility(View.GONE);
        ll_guanjing2.setVisibility(View.GONE);
        ll_direc.setVisibility(View.GONE);
        ll_ts.setVisibility(View.GONE);
        ll_ks.setVisibility(View.GONE);

        if (type == null || "".equals(type)) {
            Toast.makeText(getApplication(), "数据丢失，无法查看该管线数据！", Toast.LENGTH_SHORT).show();
            return;
        }

        String caizhiss[], mshss[], yaliss[] = null;
        if (type.equals("给水")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_js);
            mshss = getResources().getStringArray(R.array.mshway_js);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);
        } else if (type.equals("排水")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_ps);
            mshss = getResources().getStringArray(R.array.mshway_ps);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_direc.setVisibility(View.VISIBLE);
            ArrayAdapter sa = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.direction));
            direc.setAdapter(sa);
        } else if (type.equals("工业")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_gy);
            mshss = getResources().getStringArray(R.array.mshway_gy);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_yali.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye_yali));
            yaliss = getResources().getStringArray(R.array.gongye_yali);
            yali.setAdapter(ArrayAdapter);
        } else if (type.equals("热力")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_rl);
            mshss = getResources().getStringArray(R.array.mshway_rl);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_yali.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reshui_yali));
            yaliss = getResources().getStringArray(R.array.reshui_yali);
            yali.setAdapter(ArrayAdapter);
        } else if (type.equals("电力")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_dl);
            mshss = getResources().getStringArray(R.array.mshway_dl);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_yali.setVisibility(View.VISIBLE);
            ll_ts.setVisibility(View.VISIBLE);
            ll_ks.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli_yali));
            yaliss = getResources().getStringArray(R.array.dianli_yali);
            yali.setAdapter(ArrayAdapter);
        } else if (type.equals("通讯")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_tx);
            mshss = getResources().getStringArray(R.array.mshway_tx);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_ts.setVisibility(View.VISIBLE);
            ll_ks.setVisibility(View.VISIBLE);
        } else if (type.equals("燃气")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_rq);
            mshss = getResources().getStringArray(R.array.mshway_rq);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);

            ll_yali.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi_yali));
            yaliss = getResources().getStringArray(R.array.ranqi_yali);
            yali.setAdapter(ArrayAdapter);
        } else if (type.equals("综合")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_zh);
            mshss = getResources().getStringArray(R.array.mshway_zh);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);
        } else if (type.equals("人防")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_rf);
            mshss = getResources().getStringArray(R.array.mshway_rf);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);
        } else if (type.equals("地铁")) {
            caizhiss = getResources().getStringArray(R.array.caizhi_dt);
            mshss = getResources().getStringArray(R.array.mshway_dt);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);
        } else {
            caizhiss = getResources().getStringArray(R.array.caizhi_bm);
            mshss = getResources().getStringArray(R.array.mshway_bm);
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, mshss);
            s_mshway.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, caizhiss);
            s_caizhi.setAdapter(adapter3);
        }

        tv_type.setText(ltype);

        if (flag == 1) {
            if (ps[2] == 1) {//点连点
                String sscode = MxFunction.getxDataString(ps[0], "code");
                String secode = MxFunction.getxDataString(ps[1], "code");
                scode.setText(sscode);
                ecode.setText(secode);
                Log.i("点连点", sscode + "---" + secode);
                String qdmsh = getDeep(1, sscode, ltype);
                String zhdmsh = getDeep(2, secode, ltype);
                smaishen.setText(qdmsh);
                emaishen.setText(zhdmsh);
            } else if (ps[2] == 2) {//方向线连方向线
                String sscode = MxFunction.getxDataString(ps[0], "point_code");
                String secode = MxFunction.getxDataString(ps[1], "point_code");
                Log.i("方向线连方向线", sscode + "---" + secode);
                String qdmaishen = MxFunction.getxDataString(ps[0], "msh");
                String zhdmaishen = MxFunction.getxDataString(ps[1], "msh");
                String guanjing = MxFunction.getxDataString(ps[0], "gj");
                String guanjing2 = MxFunction.getxDataString(ps[1], "gj");
                String caizhi = MxFunction.getxDataString(ps[0], "czh");
                String msh = MxFunction.getxDataString(ps[0], "mshfsh");
                if (Double.parseDouble(guanjing) != Double.parseDouble(guanjing2)) {
                    Toast.makeText(getActivity(), "管径不一致！不能连接！", Toast.LENGTH_SHORT).show();
                    ppw.close();
                }
                scode.setText(sscode);
                ecode.setText(secode);
                smaishen.setText(qdmaishen);
                emaishen.setText(zhdmaishen);
                guanjing1.setText(guanjing);
                set(s_mshway, mshss, msh);
                set(s_caizhi, caizhiss, " ".equals(caizhi) ? "请选择" : caizhi);

                if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                    gao.setText(MxFunction.getxDataString(ps[0], "gao"));
                }
                if (ll_ts.getVisibility() == View.VISIBLE) {
                    num.setText(MxFunction.getxDataString(ps[0], "tsh"));
                }
                if (ll_ks.getVisibility() == View.VISIBLE) {
                    allnum.setText(MxFunction.getxDataString(ps[0], "zksh"));
                    usednum.setText(MxFunction.getxDataString(ps[0], "yyksh"));
                }
                if (ll_yali.getVisibility() == View.VISIBLE) {
                    set(yali, yaliss, MxFunction.getxDataString(ps[0], "yl"));
                }
            } else if (ps[2] == 3) {//点连方向线
                String sscode = MxFunction.getxDataString(ps[0], "code");
                String secode = MxFunction.getxDataString(ps[1], "point_code");
                Log.i("点连方向线", sscode + "---" + secode);
                String zhdmaishen = MxFunction.getxDataString(ps[1], "msh");
                String guanjing2 = MxFunction.getxDataString(ps[1], "gj");
                String qdmsh = getDeep(1, sscode, ltype);
                String caizhi = MxFunction.getxDataString(ps[1], "czh");
                String msh = MxFunction.getxDataString(ps[1], "mshfsh");
                smaishen.setText(qdmsh);
                emaishen.setText(zhdmaishen);
                scode.setText(sscode);
                ecode.setText(secode);
                guanjing1.setText(guanjing2);
                set(s_mshway, mshss, msh);
                set(s_caizhi, caizhiss, " ".equals(caizhi) ? "请选择" : caizhi);

                if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                    gao.setText(MxFunction.getxDataString(ps[1], "gao"));
                }
                if (ll_ts.getVisibility() == View.VISIBLE) {
                    num.setText(MxFunction.getxDataString(ps[1], "tsh"));
                }
                if (ll_ks.getVisibility() == View.VISIBLE) {
                    allnum.setText(MxFunction.getxDataString(ps[1], "zksh"));
                    usednum.setText(MxFunction.getxDataString(ps[1], "yyksh"));
                }
                if (ll_yali.getVisibility() == View.VISIBLE) {
                    set(yali, yaliss, MxFunction.getxDataString(ps[1], "yl"));
                }
            } else if (ps[2] == 4) {//方向线连点
                String sscode = MxFunction.getxDataString(ps[0], "point_code");
                String secode = MxFunction.getxDataString(ps[1], "code");
                Log.i("方向线连点", sscode + "---" + secode);
                scode.setText(sscode);
                ecode.setText(secode);
                String qdmaishen = MxFunction.getxDataString(ps[0], "msh");
                String zhdmsh = getDeep(2, secode, ltype);
                String guanjing = MxFunction.getxDataString(ps[0], "gj");
                String caizhi = MxFunction.getxDataString(ps[0], "czh");
                String msh = MxFunction.getxDataString(ps[0], "mshfsh");
                smaishen.setText(qdmaishen);
                emaishen.setText(zhdmsh);
                guanjing1.setText(guanjing);
                set(s_mshway, mshss, msh);
                set(s_caizhi, caizhiss, " ".equals(caizhi) ? "请选择" : caizhi);

                if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                    gao.setText(MxFunction.getxDataString(ps[0], "gao"));
                }
                if (ll_ts.getVisibility() == View.VISIBLE) {
                    num.setText(MxFunction.getxDataString(ps[0], "tsh"));
                }
                if (ll_ks.getVisibility() == View.VISIBLE) {
                    allnum.setText(MxFunction.getxDataString(ps[0], "zksh"));
                    usednum.setText(MxFunction.getxDataString(ps[0], "yyksh"));
                }
                if (ll_yali.getVisibility() == View.VISIBLE) {
                    set(yali, yaliss, MxFunction.getxDataString(ps[0], "yl"));
                }
            }

            nl_ok = lview.findViewById(R.id.newline_ok);
            nl_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    McDbBlockReference sc;
                    McDbBlockReference ec;
                    bmLine = new BmLine();
                    if (ps[2] == 1) {
                        sc = new McDbBlockReference(ps[0]);
                        ec = new McDbBlockReference(ps[1]);
                        bmLine.setStart_point(MxFunction.getxDataString(ps[0], "unicode"));
                        bmLine.setConn_direction(MxFunction.getxDataString(ps[1], "unicode"));
                    } else if (ps[2] == 2) {
                        String sid = MxFunction.getxDataString(ps[0], "point_unicode");
                        String ti = MxFunction.getxDataString(ps[0], "directionlinetype");
                        String eid = MxFunction.getxDataString(ps[1], "point_unicode");
                        sc = new McDbBlockReference(unicode2point(sid, ti));
                        ec = new McDbBlockReference(unicode2point(eid, ti));
                        bmLine.setStart_point(MxFunction.getxDataString(ps[0], "point_unicode"));
                        bmLine.setConn_direction(MxFunction.getxDataString(ps[1], "point_unicode"));
                    } else if (ps[2] == 3) {
                        sc = new McDbBlockReference(ps[0]);
                        String ti = MxFunction.getxDataString(ps[1], "directionlinetype");
                        String eid = MxFunction.getxDataString(ps[1], "point_unicode");
                        ec = new McDbBlockReference(unicode2point(eid, ti));
                        bmLine.setStart_point(MxFunction.getxDataString(ps[0], "unicode"));
                        bmLine.setConn_direction(MxFunction.getxDataString(ps[1], "point_unicode"));
                    } else {
                        String sid = MxFunction.getxDataString(ps[0], "point_unicode");
                        String ti = MxFunction.getxDataString(ps[0], "directionlinetype");
                        sc = new McDbBlockReference(unicode2point(sid, ti));
                        ec = new McDbBlockReference(ps[1]);
                        bmLine.setStart_point(MxFunction.getxDataString(ps[0], "point_unicode"));
                        bmLine.setConn_direction(MxFunction.getxDataString(ps[1], "unicode"));
                    }

                    startp = sc.position();
                    endp = ec.position();

                    String types = tv_type.getText().toString();
                    Log.i("线类型、、、、", types);
                    //读字典
                    //                    McDbDictionary mdd1 = new McDbDictionary(MxFunction.getNamedObjectsDictionary());
                    //                    long ld1 = mdd1.getAt("line");
                    //                    if (ld1 == 0) {//字典中没有线记录,添加
                    //                        McDbDictionary myDict = new McDbDictionary(mdd1.addDict("line"));
                    //                        // 向扩展字典中加入一个扩展记录.
                    //                        McDbXrecord xrec = new McDbXrecord(myDict.addRecord(types));
                    //                        // 设置扩展记录数据
                    //                        MxResbuf data = new MxResbuf();
                    //                        data.addString(s_caizhi.getSelectedItem().toString());
                    //                        data.addString(TextUtils.isEmpty(road.getText()) ? " " : road.getText().toString());
                    //                        data.addString(TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString());
                    //                        if (ll_yali.getVisibility() == View.VISIBLE) {
                    //                            data.addString(yali.getSelectedItem().toString());
                    //                        } else {
                    //                            data.addString(" ");
                    //                        }
                    //                        if (ll_ts.getVisibility() == View.VISIBLE) {
                    //                            data.addString(TextUtils.isEmpty(num.getText()) ? " " : num.getText().toString());
                    //                        } else {
                    //                            data.addString(" ");
                    //                        }
                    //                        xrec.setFromRbChain(data);
                    //                    } else {//字典中有线记录
                    //                        McDbDictionary myDict = new McDbDictionary(ld1);
                    //                        long lRecord = myDict.getAt(types);
                    //                        if (lRecord == 0) {//字典中没有该类管线记录，添加
                    //                            McDbXrecord xrec = new McDbXrecord(myDict.addRecord(types));
                    //                            Log.i("读取字典项··", "" + lRecord);
                    //                            MxResbuf data = new MxResbuf();
                    //                            data.addString(s_caizhi.getSelectedItem().toString());
                    //                            data.addString(TextUtils.isEmpty(road.getText()) ? " " : road.getText().toString());
                    //                            data.addString(TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString());
                    //                            if (ll_yali.getVisibility() == View.VISIBLE) {
                    //                                data.addString(yali.getSelectedItem().toString());
                    //                            } else {
                    //                                data.addString(" ");
                    //                            }
                    //                            if (ll_ts.getVisibility() == View.VISIBLE) {
                    //                                data.addString(TextUtils.isEmpty(num.getText()) ? " " : num.getText().toString());
                    //                            } else {
                    //                                data.addString(" ");
                    //                            }
                    //                            xrec.setFromRbChain(data);
                    //                            data.print();
                    //                        } else {//字典中有该类管线记录，清除原有记录，重新记录
                    //                            McDbXrecord xrec = new McDbXrecord(lRecord);
                    //                            Log.i("读取字典项··", "" + lRecord);
                    //                            MxResbuf data = new MxResbuf();
                    //                            data.clear();
                    //                            data.addString(s_caizhi.getSelectedItem().toString());
                    //                            data.addString(TextUtils.isEmpty(road.getText()) ? " " : road.getText().toString());
                    //                            data.addString(TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString());
                    //                            if (ll_yali.getVisibility() == View.VISIBLE) {
                    //                                data.addString(yali.getSelectedItem().toString());
                    //                            } else {
                    //                                data.addString(" ");
                    //                            }
                    //                            if (ll_ts.getVisibility() == View.VISIBLE) {
                    //                                data.addString(TextUtils.isEmpty(num.getText()) ? " " : num.getText().toString());
                    //                            } else {
                    //                                data.addString(" ");
                    //                            }
                    //                            xrec.setFromRbChain(data);
                    //                        }
                    //                    }

                    bmLine.setTushangqidian(scode.getText().toString());
                    bmLine.setTushangzhongdian(ecode.getText().toString());
                    bmLine.setLine_code(" ");//线要素编码
                    bmLine.setStart_depth(TextUtils.isEmpty(smaishen.getText()) ? 0.0f : Float.parseFloat(smaishen.getText().toString()));//起点埋深
                    bmLine.setEnd_depth(TextUtils.isEmpty(emaishen.getText()) ? 0.0f : Float.parseFloat(emaishen.getText().toString()));//终点埋深
                    bmLine.setBurial_type(s_mshway.getSelectedItem().toString());//埋设类型
                    bmLine.setMaterial(s_caizhi.getSelectedItem().toString().equals("请选择") ? " " : s_caizhi.getSelectedItem().toString());//材质
                    if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                        bmLine.setPipe_diameter(TextUtils.isEmpty(gao.getText()) ? (TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString()) : guanjing1.getText() + "*" + gao.getText());
                    } else {
                        bmLine.setPipe_diameter(TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString());
                    }
                    if (ll_direc.getVisibility() == View.VISIBLE) {
                        bmLine.setFlow_direction(direc.getSelectedItem().toString());//流向
                    } else {
                        bmLine.setFlow_direction(" ");//流向
                    }

                    if (ll_yali.getVisibility() == View.VISIBLE) {
                        bmLine.setVoltage_pressure(yali.getSelectedItem().toString());//电压压力
                    } else {
                        bmLine.setVoltage_pressure(" ");//电压压力
                    }
                    if (ll_ts.getVisibility() == View.VISIBLE) {
                        bmLine.setCable_count(TextUtils.isEmpty(num.getText()) ? " " : num.getText().toString());//电缆条数
                    } else {
                        bmLine.setCable_count(" ");
                    }
                    if (ll_ks.getVisibility() == View.VISIBLE) {
                        bmLine.setHole_count(TextUtils.isEmpty(allnum.getText()) ? " " : allnum.getText().toString());//总孔数
                        bmLine.setUsed_holecount(TextUtils.isEmpty(usednum.getText()) ? " " : usednum.getText().toString());//已用孔数
                    } else {
                        bmLine.setHole_count(" ");
                        bmLine.setUsed_holecount(" ");
                    }

                    bmLine.setAllot_holecount(" ");//分配孔数
                    bmLine.setConstruction_year(" ");//建设年代
                    bmLine.setLnumber(" ");//LNUMBER
                    bmLine.setLinetype(" ");//线型
                    bmLine.setSp_ann_content(" ");//专业注记内容
                    bmLine.setSp_ann_X(0.0d);//专业注记X坐标
                    bmLine.setSp_ann_Y(0.0d);//专业注记Y坐标
                    bmLine.setSp_ann_angle(0);//专业注记角度
                    bmLine.setCom_ann_content(" ");//综合注记内容
                    bmLine.setCom_ann_X(0.0d);//综合注记X坐标
                    bmLine.setCom_ann_Y(0.0d);//综合注记Y坐标
                    bmLine.setCom_ann_angle(0);//综合注记角度
                    bmLine.setHelper_type(" ");//辅助类型
                    bmLine.setDelete_mark(" ");//删除标记
                    bmLine.setCasing_size(" ");//套管尺寸
                    bmLine.setStart_pipe_topele(0.0d);//起点管顶高程
                    bmLine.setEnd_pipe_topele(0.0d);//终点管顶高程
                    bmLine.setPipeline_ower_code(" ");//管线权属代码
                    bmLine.setBeizhu(" ");//备注
                    bmLine.setOperator_library(" ");//操作库
                    bmLine.setRoad_name(TextUtils.isEmpty(road.getText()) ? " " : road.getText().toString());//道路名称
                    bmLine.setGroove_conncode(" ");//管沟连接码
                    bmLine.setPipetype(types);//管线类型

                    MxFunction.doCommand(114);

                    ppw.close();
                    ps[0] = 0;
                    ps[1] = 0;
                    ps[2] = 0;
                }
            });
        } else {
            l_ok = lview.findViewById(R.id.line_ok);
            l_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MxFunction.setxDataString(selected, "type", tv_type.getText().toString());
                    MxFunction.setxDataString(selected, "qdmsh", TextUtils.isEmpty(smaishen.getText()) ? " " : smaishen.getText().toString());
                    MxFunction.setxDataString(selected, "zhdmsh", TextUtils.isEmpty(emaishen.getText()) ? " " : emaishen.getText().toString());
                    MxFunction.setxDataString(selected, "mshfsh", s_mshway.getSelectedItem().toString());
                    MxFunction.setxDataString(selected, "gj1", TextUtils.isEmpty(guanjing1.getText()) ? " " : guanjing1.getText().toString());
                    if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(selected, "gj2", TextUtils.isEmpty(gao.getText()) ? " " : gao.getText().toString());
                    } else {
                        MxFunction.setxDataString(selected, "gj2", " ");
                    }
                    MxFunction.setxDataString(selected, "dlmch", TextUtils.isEmpty(road.getText()) ? " " : road.getText().toString());
                    MxFunction.setxDataString(selected, "czh", s_caizhi.getSelectedItem().toString().equals("请选择") ? " " : s_caizhi.getSelectedItem().toString());
                    if (ll_direc.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(selected, "lx", direc.getSelectedItem().toString());
                    } else {
                        MxFunction.setxDataString(selected, "lx", " ");
                    }
                    if (ll_ts.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(selected, "tsh", TextUtils.isEmpty(num.getText()) ? " " : num.getText().toString());
                    } else {
                        MxFunction.setxDataString(selected, "tsh", " ");
                    }
                    if (ll_ks.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(selected, "zksh", TextUtils.isEmpty(allnum.getText()) ? " " : num.getText().toString());
                        MxFunction.setxDataString(selected, "yyksh", TextUtils.isEmpty(usednum.getText()) ? " " : num.getText().toString());
                    } else {
                        MxFunction.setxDataString(selected, "zksh", " ");
                        MxFunction.setxDataString(selected, "yyksh", " ");
                    }
                    if (ll_yali.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(selected, "yl", yali.getSelectedItem().toString().equals("请选择") ? " " : yali.getSelectedItem().toString());
                    } else {
                        MxFunction.setxDataString(selected, "yl", " ");
                    }
                    MxFunction.writeFile(MxFunction.currentFileName());//保存文件-保存数据
                    ppw.close();

                    //修改库中线数据
                    try {
                        Database db = DatabaseBuilder.open(new File(mdbName()));
                        String ti = tv_type.getText().toString();
                        if (lm.containsKey(ti)) {
                            Table ta = db.getTable(lm.get(ti));
                            Map<String, String> map = new HashMap<>();
                            String qddh = MxFunction.getxDataString(selected, "qd_unicode");
                            String zhddh = MxFunction.getxDataString(selected, "zhd_unicode");
                            map.put("起点点号", qddh);
                            map.put("连接方向", zhddh);
                            Row row = CursorBuilder.findRow(ta, map);
                            Log.i("找到的row", row.toString());
                            ta.deleteRow(row);
                            addBl2mdb(selected, ta);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //点击监听
        l_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ppw.close();

                MxFunction.doCommand(115);
            }
        });

        //spinner选中监听
        s_caizhi.setOnItemSelectedListener(this);
        s_mshway.setOnItemSelectedListener(this);
        yali.setOnItemSelectedListener(this);
    }

    //将管线信息展示出来
    private void set_Linfo(long id, String type) {
        String sscode = MxFunction.getxDataString(id, "scode");
        String secode = MxFunction.getxDataString(id, "ecode");
        String sqdmsh = MxFunction.getxDataString(id, "qdmsh");
        String szhdmsh = MxFunction.getxDataString(id, "zhdmsh");
        String sczh = MxFunction.getxDataString(id, "czh");
        String sdlmch = MxFunction.getxDataString(id, "dlmch");
        String slx = MxFunction.getxDataString(id, "lx");
        String syl = MxFunction.getxDataString(id, "yl");
        String stsh = MxFunction.getxDataString(id, "tsh");
        String szksh = MxFunction.getxDataString(id, "zksh");
        String syyksh = MxFunction.getxDataString(id, "yyksh");
        String sgj1 = MxFunction.getxDataString(id, "gj1");
        String sgj2 = MxFunction.getxDataString(id, "gj2");
        String smsh = MxFunction.getxDataString(id, "mshfsh");

        scode.setText(sscode);
        ecode.setText(secode);
        smaishen.setText(sqdmsh);
        emaishen.setText(szhdmsh);
        if (type.equals("给水")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_js), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_js), smsh);
        } else if (type.equals("排水")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_ps), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_ps), smsh);
        } else if (type.equals("工业")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_gy), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_gy), smsh);
        } else if (type.equals("热力")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_rl), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_rl), smsh);
        } else if (type.equals("电力")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_dl), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_dl), smsh);
        } else if (type.equals("通讯")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_tx), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_tx), smsh);
        } else if (type.equals("燃气")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_rq), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_rq), smsh);
        } else if (type.equals("综合")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_zh), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_zh), smsh);
        } else if (type.equals("人防")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_rf), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_rf), smsh);
        } else if (type.equals("地铁")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_dt), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_dt), smsh);
        } else if (type.equals("不明")) {
            set(s_caizhi, getResources().getStringArray(R.array.caizhi_bm), " ".equals(sczh) ? "请选择" : sczh);
            set(s_mshway, getResources().getStringArray(R.array.mshway_bm), smsh);
        }
        road.setText(sdlmch);
        guanjing1.setText(sgj1);
        if (ll_guanjing2.getVisibility() == View.VISIBLE) {
            gao.setText(sgj2);
        }
        if (ll_direc.getVisibility() == View.VISIBLE) {
            set(direc, getResources().getStringArray(R.array.direction), slx);
        }
        if (ll_ts.getVisibility() == View.VISIBLE) {
            num.setText(stsh);
        }
        if (ll_ks.getVisibility() == View.VISIBLE) {
            allnum.setText(szksh);
            usednum.setText(syyksh);
        }
        if (ll_yali.getVisibility() == View.VISIBLE) {
            switch (type) {
                case "燃气":
                    set(yali, getResources().getStringArray(R.array.ranqi_yali), syl);
                    break;
                case "电力":
                    set(yali, getResources().getStringArray(R.array.dianli_yali), syl);
                    break;
                case "工业":
                    set(yali, getResources().getStringArray(R.array.gongye_yali), syl);
                    break;
                case "热力":
                    set(yali, getResources().getStringArray(R.array.reshui_yali), syl);
                    break;
            }
        }
    }

    @Override
    public void commandEvent(int iCommand) {
        switch (iCommand) {
            case 1://点
                operate = 1;
                long[] lidss = MxFunction.getAllLayer();
                for (long l : lidss) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("LINE") || mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(true);
                    }
                }
                ps[0] = 0;
                ps[1] = 0;
                ps[2] = 0;
                break;
            case 111://执行画点操作
                if (bmPoint == null) {
                    break;
                }
                List<CodeNumber> cnlist = codeNumberBox.query().equal(CodeNumber_.filename, MxFunction.currentFileName())
                        .and().equal(CodeNumber_.type, type_item.getSelectedItem().toString()).build().find();
                Log.i("list长度", "" + cnlist.size());
                if (cnlist.size() == 0) {
                    String pre = TypeItemUtil.getPre(type_item.getSelectedItem().toString());
                    int no = 1;
                    CodeNumber cn = new CodeNumber();
                    cn.setNo(no);
                    cn.setPre(pre);
                    cn.setType(type_item.getSelectedItem().toString());
                    cn.setFilename(MxFunction.currentFileName());
                    codeNumberBox.put(cn);
                } else if (cnlist.size() == 1) {
                    CodeNumber cn = cnlist.get(0);
                    int no = cn.getNo() + 1;
                    cn.setNo(no);
                    codeNumberBox.put(cn);
                }

                String param = bmPoint.getPipetype();
                String pl = TypeItemUtil.getPre(param) + "POINT";
                String tl = TypeItemUtil.getPre(param) + "TEXT";
                McDbLayerTable table = MxFunction.getCurrentDatabase().getLayerTable();
                if (!table.has(pl)) {
                    MxLibDraw.addLayer(pl);
                }
                if (!table.has(tl)) {
                    MxLibDraw.addLayer(tl);
                }

                McDbLayerTableRecord mcdtr = new McDbLayerTableRecord(pl);
                McDbLayerTableRecord mcdtr1 = new McDbLayerTableRecord(tl);
                mcdtr.setColor((int) getColor(param)[0], (int) getColor(param)[1], (int) getColor(param)[2]);
                mcdtr1.setColor((int) getColor(param)[0], (int) getColor(param)[1], (int) getColor(param)[2]);

                String co = bmPoint.getMap_dot();
                Log.i("点号为----", co);

                last[0] = type.getSelectedItem().toString();
                last[1] = param;
                Log.i("添加--last[1]", param);

                if (!copyAssetAndWrite(mark_name)) {
                    mark_name = "1.dwg";
                    copyAssetAndWrite(mark_name);
                }
                File block = new File(getCacheDir(), mark_name);
                File af = block.getAbsoluteFile();
                String s1 = af.getPath();

                color = getColor(type.getSelectedItem().toString());
                MxLibDraw.setDrawColor(color);
                MxLibDraw.setLineType("point");
                MxLibDraw.setLayerName(pl);
                MxLibDraw.insertBlock(s1, co);
                long bid = MxLibDraw.drawBlockReference(x, y, co, 0.5, 0);

                //添加图上扩展属性
                bmpoint2xdata(bmPoint, bid);

                McDbBlockReference blkRef = (McDbBlockReference) MxFunction.objectIdToObject(bid);
                blkRef.setLayerName(pl);

                if (ig == 1) {
                    McGePoint3d pos = blkRef.position();
                    pos.z = bmPoint.getGround_elevation();
                    blkRef.setPosition(pos);
                    blkRef.setDrawOrder(9);
                }

                color = getColor(type.getSelectedItem().toString());
                MxLibDraw.setDrawColor(color);
                MxLibDraw.setLayerName(tl);
                MxLibDraw.setLineType("text");
                long tt = MxLibDraw.drawText(x, y + 0.4, 1, co);
                McDbText dt = new McDbText(tt);
                dt.setDrawOrder(1);
                McDbLayerTableRecord tr = new McDbLayerTableRecord(tl);
                tr.setIsLocked(true);

                MxFunction.writeFile(MxFunction.currentFileName());//保存文件-保存数据

                //向库中添加数据
                //创建点实体对象，插入数据
                try {
                    Database db = DatabaseBuilder.open(new File(mdbName()));

                    if (pm.containsKey(param)) {
                        Table ta = db.getTable(pm.get(param));
                        Object[] pa = addPoint(ta, bmPoint);
                        if (pa.length == 32) {
                            Log.i("添加成功", param + "管点");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //解除点、线的锁定
                long[] lids = MxFunction.getAllLayer();
                for (long l : lids) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        if (mltr.isLocked()) {
                            mltr.setIsLocked(false);
                        }
                    }
                    if ((mltr.getName().endsWith("LINE") && !mltr.getName().equals("DIRECTIONLINE")) || mltr.getName().endsWith("POINT")) {
                        if (mltr.isLocked())
                            mltr.setIsLocked(false);
                    }
                    if (mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")) {
                        if (!mltr.isLocked())
                            mltr.setIsLocked(true);
                    }
                }
                break;
            case 112://点特征/附属物变化后重新绘制
                if (bmPoint == null) {
                    break;
                }
                if (!copyAssetAndWrite(mark_name)) {
                    mark_name = "1.dwg";
                }
                File block1 = new File(getCacheDir(), mark_name);
                File af1 = block1.getAbsoluteFile();
                String s2 = af1.getPath();
                Date date = new Date();
                String co1 = bmPoint.getMap_dot();
                MxLibDraw.insertBlock(s2, co1 + date.getTime());
                x = Double.parseDouble(MxFunction.getxDataString(selected, "x"));
                y = Double.parseDouble(MxFunction.getxDataString(selected, "y"));
                Log.i("重绘点-----", x + ":" + y);
                MxFunction.deleteObject(selected);
                MxLibDraw.setLineType("point");
                MxLibDraw.setLayerName(TypeItemUtil.getPre(type_item.getSelectedItem().toString()) + "POINT");
                long bid1 = MxLibDraw.drawBlockReference(x, y, co1 + date.getTime(), 0.5, 0);

                bmpoint2xdata(bmPoint, bid1);

                McDbBlockReference mdrf = new McDbBlockReference(bid1);
                mdrf.setDrawOrder(9);

                pid = bid1;

                //解除点的锁定
                long[] lids23 = MxFunction.getAllLayer();
                for (long l : lids23) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        if (mltr.isLocked())
                            mltr.setIsLocked(false);
                    }
                    if (mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")) {
                        if (!mltr.isLocked())
                            mltr.setIsLocked(true);
                    }
                }

                mMyHandler.sendEmptyMessage(2);

                break;
            case 2://连线:1-点连点 2-方向线连方向线 3-点连方向线 4-方向线连点
                operate = 2;
                long[] lids1 = MxFunction.getAllLayer();
                for (long l : lids1) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().equals("DIRECTIONLINE") || mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(false);
                    } else {
                        mltr.setIsLocked(true);
                    }
                }
                ps[0] = 0;
                ps[1] = 0;
                ps[2] = 0;
                break;
            case 114://执行画线操作
                if (bmLine == null) {
                    break;
                }
                String ltype = bmLine.getPipetype();
                McDbLayerTable table1 = MxFunction.getCurrentDatabase().getLayerTable();
                if (!table1.has(TypeItemUtil.getPre(ltype) + "LINE")) {
                    MxLibDraw.addLayer(TypeItemUtil.getPre(ltype) + "LINE");
                }
                MxLibDraw.setLayerName(TypeItemUtil.getPre(ltype) + "LINE");
                long[] col = getColor(ti_t_Util.getType(ltype));
                MxLibDraw.setDrawColor(col);
                MxLibDraw.addLinetype("line", "1", 1);
                MxLibDraw.setLineType("line");
                long pp = MxLibDraw.drawPoint(x, y);
                MxFunction.deleteObject(pp);
                MxLibDraw.setDrawColor(col);
                final long lid = MxLibDraw.drawLine(startp.x, startp.y, endp.x, endp.y);

                McDbLine dl = new McDbLine(lid);
                dl.setDrawOrder(5);

                Bmline2xdata(bmLine, lid);

                MxFunction.writeFile(MxFunction.currentFileName());//保存文件-保存数据

                //线数据入库
                try {
                    Database db = DatabaseBuilder.open(new File(mdbName()));
                    String tablename = lm.get(ltype);
                    addBl2mdb(lid, db.getTable(tablename));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //解除点、线的锁定
                //锁定展点层
                long[] lidsss = MxFunction.getAllLayer();
                for (long l : lidsss) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if ((mltr.getName().endsWith("LINE") && !mltr.getName().equals("DIRECTIONLINE")) || mltr.getName().endsWith("POINT")) {
                        if (mltr.isLocked())
                            mltr.setIsLocked(false);
                    }
                    if (mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")) {
                        if (!mltr.isLocked())
                            mltr.setIsLocked(true);
                    }
                }
                break;
            case 113://解除点的锁定
                //解除点的锁定
                long[] lids13 = MxFunction.getAllLayer();
                for (long l : lids13) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        if (mltr.isLocked())
                            mltr.setIsLocked(false);
                    }
                    if (mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")) {
                        if (!mltr.isLocked())
                            mltr.setIsLocked(true);
                    }
                }
                break;
            case 115://解除线层锁定
                long[] lids115 = MxFunction.getAllLayer();
                for (long l : lids115) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("LINE") && !mltr.getName().equals("DIRECTIONLINE")) {
                        if (mltr.isLocked())
                            mltr.setIsLocked(false);
                    }
                }
                break;
            case 3://显示全部
                MxFunction.zoomAll();
                break;
            case 4://展点编辑
                long[] lids7 = MxFunction.getAllLayer();
                if (lids7 == null) {
                    break;
                }
                for (long l : lids7) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")) {
                        mltr.setIsLocked(false);
                    } else {
                        mltr.setIsLocked(true);
                    }
                }
                if (operate != 0) {
                    operate = 0;
                }
                break;
            case 5://锁定管点
                long[] lids3 = MxFunction.getAllLayer();
                for (long l : lids3) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(true);
                    }
                    if (mltr.getName().endsWith("LINE")) {
                        mltr.setIsLocked(false);
                    }
                }
                break;
            case 6://锁定管线
                long[] lids11 = MxFunction.getAllLayer();
                for (long l : lids11) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(false);
                    }
                    if (mltr.getName().endsWith("LINE")) {
                        mltr.setIsLocked(true);
                    }
                }
                break;
            case 7://解除锁定
                long[] lids2 = MxFunction.getAllLayer();
                for (long l : lids2) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(false);
                    }
                    if (mltr.getName().endsWith("LINE")) {
                        mltr.setIsLocked(false);
                    }
                }
                break;
            case 8://新建管种
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newPipe4mdb();
                    }
                });
                break;
            case 9://移动点
                MrxDbgUiPrPoint mdpp = new MrxDbgUiPrPoint();
                mdpp.setMessage("点取新位置");
                if (mdpp.go() != MrxDbgUiPrPoint.Status.kOk) {
                    return;
                }
                McGePoint3d pt = mdpp.value();
                McDbBlockReference mdc = (McDbBlockReference) MxFunction.objectIdToObject(selected);
                McGePoint3d ppa = mdc.position();
                pt.z = ppa.z;
                mdc.setPosition(pt);
                MxFunction.setxDataString(selected, "x", "" + pt.x);
                MxFunction.setxDataString(selected, "y", "" + pt.y);
                MrxDbgSelSet mss = new MrxDbgSelSet();
                mss.allSelect();
                for (int i = 0; i < mss.size(); i++) {
                    long lida = mss.at(i);
                    McDbEntity ent = new McDbEntity(lida);

                    //移动点号标注
                    if (ent.layerName().endsWith("TEXT")) {
                        McDbText dt1 = new McDbText(lida);
                        McGePoint3d tp = dt1.position();

                        if (tp.x == ppa.x && (Math.abs(tp.y - ppa.y - 0.4) <= 0.01)) {
                            Log.i("11111", "---" + lida);
                            //dt.setPosition(pt); //无效
                            McGePoint3d tss = new McGePoint3d();
                            tss.x = pt.x;
                            tss.y = pt.y + 0.4;
                            dt1.setPosition(tss);
                            dt1.setAlignmentPoint(tss);
                        }
                    }

                    //移动线
                    if (ent.layerName().endsWith("LINE") && !ent.layerName().equals("DIRECTIONLINE")) {
                        String ss = MxFunction.getxDataString(lida, "scode");
                        String es = MxFunction.getxDataString(lida, "ecode");
                        if (ss.equals(MxFunction.getxDataString(selected, "code"))) {
                            McDbLine dla = new McDbLine(lida);
                            dla.setStartPoint(pt);
                        }
                        if (es.equals(MxFunction.getxDataString(selected, "code"))) {
                            McDbLine dla = new McDbLine(lida);
                            dla.setEndPoint(pt);
                        }
                    }

                    //移动方向线
                    if (ent.layerName().equals("DIRECTIONLINE")) {
                        String unic = MxFunction.getxDataString(selected, "unicode");
                        String dl_uni = MxFunction.getxDataString(lida, "point_unicode");
                        if (unic.equals(dl_uni)) {
                            McGeMatrix3d mat3 = new McGeMatrix3d();
                            mat3.translation(pt.x - ppa.x, pt.y - ppa.y, 0);
                            ent.transformBy(mat3);
                        }
                    }
                }

                //库中修改点坐标
                mMyHandler.sendEmptyMessage(3);
                break;
            case 10://删除点 同时删除标注、相关管线
                if (selected != 0) {
                    String unicd = MxFunction.getxDataString(selected, "unicode");
                    String ti = MxFunction.getxDataString(selected, "type_item");
                    McDbBlockReference blockReference = new McDbBlockReference(selected);
                    McGePoint3d ppt = blockReference.position();

                    //删除标注
                    String type = MxFunction.getxDataString(selected, "type_item");
                    String d = TypeItemUtil.getPre(type) + "TEXT";
                    MrxDbgSelSet selSet = new MrxDbgSelSet();
                    MxResbuf mxResbuf = new MxResbuf();
                    mxResbuf.addString(d, 8);
                    selSet.allSelect(mxResbuf);
                    for (int i = 0; i < selSet.size(); i++) {
                        long id = selSet.at(i);
                        McDbText text = new McDbText(id);
                        McGePoint3d tp = text.position();
                        if (tp.x == ppt.x && (Math.abs(tp.y - ppt.y - 0.4) < 0.02)) {
                            MxFunction.deleteObject(id);
                        }
                    }

                    //删除管线
                    MrxDbgSelSet ss = new MrxDbgSelSet();
                    MxResbuf filter = new MxResbuf();
                    filter.addString(TypeItemUtil.getPre(type) + "LINE", 8);
                    ss.allSelect(filter);
                    for (int i = 0; i < ss.size(); i++) {
                        long ida = ss.at(i);
                        String scd = MxFunction.getxDataString(ida, "qd_unicode");
                        String ecd = MxFunction.getxDataString(ida, "zhd_unicode");
                        if (scd.equals(unicd) || ecd.equals(unicd)) {
                            MxFunction.deleteObject(ida);
                        }
                    }

                    //删除方向线
                    Log.i("删除方向线", "1");
                    McDbLayerTable table1a = MxFunction.getCurrentDatabase().getLayerTable();
                    if (table1a.has("DIRECTIONLINE")) {
                        MrxDbgSelSet ss1 = new MrxDbgSelSet();
                        MxResbuf filter1 = new MxResbuf();
                        filter1.addString("DIRECTIONLINE", 8);
                        ss1.allSelect(filter1);
                        for (int i = 0; i < ss1.size(); i++) {
                            long ida = ss1.at(i);
                            String scd = MxFunction.getxDataString(ida, "point_unicode");
                            if (unicd.equals(scd)) {
                                MxFunction.deleteObject(ida);
                            }
                        }
                    }

                    Log.i("......", "......");
                    //库中删除点、线
                    if (db == null) {
                        try {
                            db = DatabaseBuilder.open(new File(mdbName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Table ptable = db.getTable(pm.get(ti));
                        Row row = CursorBuilder.findRow(ptable, Collections.singletonMap("物探点号", unicd));
                        ptable.deleteRow(row);

                        Table ltable = db.getTable(lm.get(ti));
                        for (Row lr : ltable) {
                            if (lr.get("起点点号").equals(unicd) || lr.get("连接方向").equals(unicd)) {
                                ltable.deleteRow(lr);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                    MxFunction.deleteObject(selected);
                }
                break;
            case 11://打开编辑
                //检查关联mdb库文件
                String name1 = MxFunction.currentFileName();
                if (name1.endsWith(".mwg")) {
                    name1 = name1.replace(".mwg", ".mdb");
                } else if (name1.endsWith(".dwg")) {
                    name1 = name1.replace(".dwg", ".mdb");
                }
                Log.i("name....", ":" + name1);
                String fn1 = name1.substring(name1.lastIndexOf("/") + 1);
                Log.i("fn....", "" + fn1);
                String target = getWorkDir() + File.separator + fn1;
                Log.i("target....", target);
                File nf = new File(target);

                if (!nf.exists()) {
                    MrxDbgSelSet ss = new MrxDbgSelSet();
                    ss.allSelect();
                    if (ss.size() == 0) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "请先新建管种！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                } else {
                    try {
                        db = DatabaseBuilder.open(nf);
                        Set tables = db.getTableNames();
                        if (tables.size() == 0) {
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "请先新建管种！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (enable) {
                    break;//防止连线时再次打开编辑
                }
                enable = true;
                MxFunction.enableSelect(true);

                long[] lids4 = MxFunction.getAllLayer();
                if (lids4 == null) {
                    break;
                }
                for (long l : lids4) {
                    McDbLayerTableRecord mltr = new McDbLayerTableRecord(l);
                    if (mltr.getName().endsWith("POINT")) {
                        mltr.setIsLocked(false);
                    } else if (mltr.getName().endsWith("LINE")) {
                        mltr.setIsLocked(false);
                    } else {
                        mltr.setIsLocked(true);
                    }
                    //if(mltr.getName().equals("ZDH") || mltr.getName().equals("zdh")){
                    //mltr.setIsLocked(false);
                    //}
                }
                break;
            case 12://关闭编辑
                enable = false;
                MxFunction.enableSelect(false);
                break;
            case 14://保存
                String fn = MxFunction.currentFileName();
                MxFunction.writeFile(fn);
                String fnn;
                File newfile;
                try {
                    if (fn.endsWith(".mwg")) {
                        fnn = fn.replace(".mwg", ".dwg");
                        newfile = new File(fnn);
                        newfile.createNewFile();
                    } else {
                        fnn = fn;
                    }
                    Log.i("fn", fnn);
                    if (MxFunction.writeFile(fnn)) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "保存成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "保存失败···", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
                break;
            case 17:
                DrawCircle();
                break;
            case 18://清除辅助圆
                MrxDbgSelSet ss1 = new MrxDbgSelSet();
                MxResbuf filter = new MxResbuf();
                filter.addString("ASSIST", 8);
                ss1.allSelect(filter);
                for (int i = 0; i < ss1.size(); i++) {
                    long id = ss1.at(i);
                    MxFunction.deleteObject(id);
                }
                break;
            case 19://另存为
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SaveAs();
                    }
                });
                break;
            case 21://方向线
                if (selected == 0) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "请先选择要添加方向线的管点！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                } else {
                    McDbEntity mcDbEntity = new McDbEntity(selected);
                    if (!mcDbEntity.layerName().endsWith("POINT")) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "当前选择的目标不正确！请选择管点！！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
                Log.i("方向线-selected", ":" + selected);
                DynDrawLine();
                break;
            case 20://删除管线
                //库中删除
                String scode = MxFunction.getxDataString(selected, "qd_unicode");
                String ecode = MxFunction.getxDataString(selected, "zhd_unicode");
                String ti = MxFunction.getxDataString(selected, "type");
                if (db == null) {
                    try {
                        db = DatabaseBuilder.open(new File(mdbName()));
                        Table lt = db.getTable(lm.get(ti));
                        Map<String, String> map = new HashMap<>();
                        map.put("起点点号", scode);
                        map.put("连接方向", ecode);
                        Row row = CursorBuilder.findRow(lt, map);
                        lt.deleteRow(row);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //图上删除
                if (selected != 0) {
                    MxFunction.deleteObject(selected);
                }
                break;
            case 22://锁定点号标注
                long[] ls = MxFunction.getAllLayer();
                for (long s : ls) {
                    McDbLayerTableRecord ml = new McDbLayerTableRecord(s);
                    String name = ml.getName();
                    if (name.endsWith("TEXT")) {
                        ml.setIsLocked(true);
                    }
                }
                break;
            case 23://清除其它--直线、任意线等
                MrxDbgSelSet mrxDbgSelSet = new MrxDbgSelSet();
                MxResbuf mxResbuf = new MxResbuf();
                //                mxResbuf.addString("OTHER",8);
                mxResbuf.addString("mxcadcomment", 8);
                mrxDbgSelSet.allSelect(mxResbuf);
                for (int i = 0; i < mrxDbgSelSet.size(); i++) {
                    long id = mrxDbgSelSet.at(i);
                    MxFunction.deleteObject(id);
                }
                break;
            case 25://管种管理
                List<String> gzhs = new ArrayList<>();
                long[] pls = MxFunction.getAllLayer();

                for (int i = 0; i < pls.length; i++) {
                    McDbLayerTableRecord tr2 = new McDbLayerTableRecord(pls[i]);
                    String name = tr2.getName();
                    if (name.endsWith("POINT")) {
                        String s = name.replace("POINT", "");
                        gzhs.add(s);
                    }
                }

                final List<String> gzh = gzhs;
                Log.i("管种数", ":" + gzh.size());

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View v = LayoutInflater.from(getApplication()).inflate(R.layout.pipemanage, null);
                        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);

                        final Popup ppw = new Popup(v, width, WRAP_CONTENT);

                        ColorDrawable cd = new ColorDrawable(0x000000);
                        ppw.setBackgroundDrawable(cd);
                        //产生背景变暗效果
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 0.4f;
                        getWindow().setAttributes(lp);

                        ppw.setOutsideTouchable(false);
                        ppw.setFocusable(true);
                        ppw.setIsdismiss(false);
                        ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

                        ppw.update();
                        ppw.setOnDismissListener(new PopupWindow.OnDismissListener() {

                            //在dismiss中恢复透明度
                            public void onDismiss() {
                                WindowManager.LayoutParams lp = getWindow().getAttributes();
                                lp.alpha = 1f;
                                getWindow().setAttributes(lp);
                            }
                        });

                        CheckBox cb = v.findViewById(R.id.cb);
                        final ListView lv = v.findViewById(R.id.mylist);
                        TextView cancel = v.findViewById(R.id.tv_r_cancel);
                        TextView ok = v.findViewById(R.id.tv_r_sure);

                        final MyAdapter la = new MyAdapter(getApplication(), R.layout.child, R.id.ctv, gzh);
                        lv.setAdapter(la);

                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    for (int i = 0; i < gzh.size(); i++) {
                                        CheckBox v = (CheckBox) lv.getChildAt(i);
                                        v.setChecked(true);
                                    }
                                } else {
                                    for (int i = 0; i < gzh.size(); i++) {
                                        CheckBox v = (CheckBox) lv.getChildAt(i);
                                        v.setChecked(false);
                                    }
                                }
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ppw.close();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<String> ll = new ArrayList<>();
                                for (int i = 0; i < gzh.size(); i++) {
                                    CheckBox cb = (CheckBox) lv.getChildAt(i);
                                    if (cb.isChecked()) {
                                        ll.add(cb.getText().toString());
                                    }
                                }
                                long[] ls = MxFunction.getAllLayer();
                                for (long s : ls) {
                                    McDbLayerTableRecord ml = new McDbLayerTableRecord(s);
                                    String name = ml.getName();
                                    boolean b = false;
                                    for (String st : ll) {
                                        if (name.contains(st)) {
                                            b = true;
                                        }
                                    }
                                    if (b)
                                        ml.setIsOff(false);
                                    else
                                        ml.setIsOff(true);
                                }
                                ppw.close();
                            }
                        });
                    }
                });
                break;
            case 55://导出
                //检验读写权限
                if (!hasPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    String[] ps = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(StartAct.this, ps, 1);
                }

                File nf1 = NewDatabase();
                if (Daochu(nf1)) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "导出完毕！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "发生错误，导出终止！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                break;
            case 56://导入mdb
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case 57://导入dat
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent1, 2);
                break;
            case 58://根据bps、bls成图
                for (BmPoint bp : bps) {//遍历bps，生成点
                    Log.i("遍历bps", bps.size() + "");
                    mark_name = "1.dwg";
                    Mark_Util mu = new Mark_Util();
                    String dalei = bp.getPipeline_type();
                    String xiaolei = bp.getPipetype();
                    Log.i("大类：小类",dalei + ":" + xiaolei);
                    if (" ".equals(bp.getFeature()) || "".equals(bp.getFeature()) || bp.getFeature() == null) {
                        if (mu.getMark(2, bp.getAppendages(), dalei, xiaolei) != null)
                            mark_name = mu.getMark(2, bp.getAppendages(), dalei, xiaolei);
                        else
                            mark_name = "1.dwg";
                    } else if (" ".equals(bp.getAppendages()) || "".equals(bp.getAppendages()) || bp.getAppendages() == null) {
                        if (mu.getMark(1, bp.getFeature(), dalei, xiaolei) != null)
                            mark_name = mu.getMark(1, bp.getFeature(), dalei, xiaolei);
                        else
                            mark_name = "1.dwg";
                    } else {
                        mark_name = "1.dwg";
                    }
                    Log.i("符号name",mark_name);
                    copyAssetAndWrite(mark_name);
                    File block2 = new File(getCacheDir(), mark_name);
                    File af2 = block2.getAbsoluteFile();
                    String s3 = af2.getPath();

                    long[] color = StartAct.getColor(bp.getPipeline_type());
                    MxLibDraw.setDrawColor(color);

                    String pre = TypeItemUtil.getPre(bp.getPipeline_type());
                    MxLibDraw.setLayerName(pre + "POINT");
                    McDbLayerTable mcDbLayerTable = MxFunction.getCurrentDatabase().getLayerTable();
                    if (!mcDbLayerTable.has(pre + "POINT")) {
                        MxLibDraw.addLayer(pre + "POINT");
                    }
                    MxLibDraw.setLineType("point");
                    MxLibDraw.insertBlock(s3, bp.getExploration_dot());
                    long bid58 = 0;
                    if(bp.getMap_dot() == null) {
                        bid58 = MxLibDraw.drawBlockReference(bp.getX(), bp.getY(), bp.getExploration_dot(), 0.5, 0);
                    }else {
                        bid58 = MxLibDraw.drawBlockReference(bp.getX(), bp.getY(), bp.getMap_dot(), 0.5, 0);
                    }
                    if (bid58 != 0) {
                        McDbBlockReference blkRef58 = (McDbBlockReference) MxFunction.objectIdToObject(bid58);
                        McGePoint3d pos = blkRef58.position();
                        pos.z = bp.getGround_elevation();
                        blkRef58.setPosition(pos);
                        blkRef58.setDrawOrder(9);
                    }

                    MxLibDraw.setLayerName(pre + "TEXT");
                    MxLibDraw.setLineType("text");
                    long tid = 0;
                    if(bp.getMap_dot() == null) {
                        tid = MxLibDraw.drawText(bp.getX(), bp.getY() + 0.4, 1, bp.getExploration_dot());
                    }else {
                        tid = MxLibDraw.drawText(bp.getX(), bp.getY() + 0.4, 1, bp.getMap_dot());
                    }
                    McDbText text = new McDbText(tid);
                    text.setDrawOrder(1);

                    bmpoint2xdata(bp, bid58);
                }

                for (BmLine bl : bls) {//遍历bls，生成线
                    Log.i("遍历bls", bls.size() + "");
                    String type = bl.getPipetype();
                    String pr = TypeItemUtil.getPre(type);
                    String dalei = ti_t_Util.getType(type);
                    if (null == type)
                        continue;
                    try {
                        Log.i("类别", type);
                        Table ptable = im_db.getTable(pm.get(type));
                        for (Row r : ptable) {
                            if (r.getString("物探点号").equals(bl.getStart_point())) {
                                sx = r.getDouble("Y");
                                sy = r.getDouble("X");
                                Log.i("xy", sx + "," + sy);
                                bl.setTushangqidian(r.getString("图上点号"));
                            }
                            if (r.getString("物探点号").equals(bl.getConn_direction())) {
                                ex = Double.parseDouble(String.valueOf(r.get("Y")));
                                ey = Double.parseDouble(String.valueOf(r.get("X")));
                                Log.i("xy", ex + "," + ey);
                                bl.setTushangzhongdian(r.getString("图上点号"));
                            }
                        }
                        Log.i("sx" + "," + "sy" + "--" + "ex" + "," + "ey", sx + "," + sy + "--" + ex + "," + ey);
                        if (sx != 0.0 && sy != 0.0 && ex != 0.0 && ey != 0.0) {
                            McDbLayerTable mcDbLayerTable = MxFunction.getCurrentDatabase().getLayerTable();
                            if (!mcDbLayerTable.has(pr + "LINE")) {
                                MxLibDraw.addLayer(pr + "LINE");
                            }
                            MxLibDraw.setLayerName(pr + "LINE");
                            MxLibDraw.setDrawColor(StartAct.getColor(dalei));
                            MxLibDraw.setLineType("line");

                            long lid58 = MxLibDraw.drawLine(sx, sy, ex, ey);

                            Bmline2xdata(bl, lid58);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 59://删除丢失数据的点和线，然后删除没有相关点的标注
                MrxDbgSelSet selSet59 = new MrxDbgSelSet();
                selSet59.allSelect();
                Log.i("对象数量", selSet59.size() + "");
                for (int i = 0; i < selSet59.size(); i++) {
                    long id = selSet59.at(i);
                    McDbEntity entity = new McDbEntity(id);
                    String layer = entity.layerName();
                    Log.i("对象图层名", layer);
                    if (layer.endsWith("POINT")) {//删除无效点
                        String code = MxFunction.getxDataString(id, "code");
                        if (code == null || code.isEmpty()) {
                            MxFunction.deleteObject(id);
                        }
                    } else if (layer.equals("DIRECTIONLINE")) {//删除无效方向线
                        String code = MxFunction.getxDataString(id, "point_code");
                        if (code == null || code.isEmpty()) {
                            MxFunction.deleteObject(id);
                        }
                    } else if (layer.endsWith("LINE")) {//删除无效线
                        String code = MxFunction.getxDataString(id, "scode");
                        if (code == null || code.isEmpty()) {
                            MxFunction.deleteObject(id);
                        }
                    } else if (layer.endsWith("TEXT")) {//删除无效标注
                        McDbText text = new McDbText(id);
                        McGePoint3d po = text.position();

                        MrxDbgSelSet ss = new MrxDbgSelSet();
                        McGePoint3d pt1 = new McGePoint3d(po.x, po.y - 0.42, po.z);
                        McGePoint3d pt2 = new McGePoint3d(po.x, po.y - 0.38, po.z);
                        ss.crossingSelect(pt1, pt2);
                        if (ss.size() == 0) {
                            MxFunction.deleteObject(id);
                        } else if (ss.size() == 1) {
                            McDbEntity entity1 = new McDbEntity(ss.at(0));
                            if (!entity1.layerName().endsWith("POINT")) {
                                MxFunction.deleteObject(id);
                            }
                        }
                    }
                }

                break;
            case 99://测试

                break;
        }
    }

    @Override
    public void onKeyReleased(int iKeyCode) {
        if (iKeyCode == 6) {
            MxFunction.sendStringToExecute("Mx_StartPage");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                //                Daoru(data, StartAct.this);
                bps = Mdb2Bps(data, StartAct.this);
                bls = Mdb2Bls(data, StartAct.this);
                if (bls != null && bps != null) {
                    MxFunction.doCommand(58);
                }
            }
            if (requestCode == 2)
                ReadDat(data, StartAct.this);

        }
    }

    @Override
    public void selectModified(long lId) {
        Log.i("selectModified", "执行selectModified");
        if (lId != 0) {
            final long id = lId;
            McDbEntity entity = new McDbEntity(lId);
            Log.i("linetype", operate + "--" + MxLibDraw.lineType() + "--" + MxFunction.getTypeName(lId) + "--" + entity.layerName());
            if (operate == 2) {//连线模式
                if (entity.layerName().equals("DIRECTIONLINE")) {
                    final String sp = MxFunction.getxDataString(lId, "point_code");
                    if (sp == null || "".equals(sp)) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "信息丢失，无法操作！" + sp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    Log.i("当前对象：", "方向线");
                    if (ps[0] == 0) {
                        ps[0] = lId;
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "已选择起点：" + sp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        MxFunction.delSelect(lId);
                    } else {
                        McDbEntity ent0 = new McDbEntity(ps[0]);
                        ps[1] = lId;
                        if (ent0.layerName().equals("DIRECTIONLINE")) {//方向线连方向线
                            ps[2] = 2;
                            final String stype = MxFunction.getxDataString(ps[0], "directionlinetype");
                            final String ltype = MxFunction.getxDataString(ps[0], "type");
                            String etype = MxFunction.getxDataString(lId, "directionlinetype");
                            Log.i("起点与终点···", stype + ":" + etype);
                            if (!stype.equals(etype)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "所选起点与终点类型不一致，无法连接！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            String sp1 = MxFunction.getxDataString(ps[0], "point_unicode");
                            String ep = MxFunction.getxDataString(lId, "point_unicode");
                            if (sp1.equals(ep)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "起点与终点不能是同一个点，请重选终点！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            //判断当前起点和终点是否已连接
                            String ll = TypeItemUtil.getPre(stype) + "LINE";
                            MrxDbgSelSet ss = new MrxDbgSelSet();
                            MxResbuf filter = new MxResbuf();
                            filter.addString(ll, 8);
                            ss.allSelect(filter);
                            Log.i("线层对象数量", ":" + ss.size());
                            for (int i = 0; i < ss.size(); i++) {
                                long lg = ss.at(i);
                                String sc = MxFunction.getxDataString(lg, "qd_unicode");
                                String sc1 = MxFunction.getxDataString(ps[0], "point_unicode");
                                String ec = MxFunction.getxDataString(lg, "zhd_unicode");
                                String ec1 = MxFunction.getxDataString(ps[1], "point_unicode");
                                Log.i("起点与终点连接判断···", sc + ":" + sc1 + "--" + ec + ":" + ec1);
                                if (sc.equals(sc1) && ec.equals(ec1)) {
                                    this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplication(), "当前起点与终点已连接，请重选终点！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            }

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    line_pop(1, stype, ltype);
                                    MxFunction.delSelect(id);
                                    operate = 0;
                                }
                            });
                        } else if (ent0.layerName().endsWith("POINT")) {//点连方向线
                            ps[2] = 3;
                            final String stype = MxFunction.getxDataString(ps[0], "type_item");
                            final String ltype = MxFunction.getxDataString(ps[0], "type");
                            String etype = MxFunction.getxDataString(lId, "directionlinetype");
                            Log.i("起点与终点···", stype + ":" + etype);
                            if (!stype.equals(etype)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "所选起点与终点类型不一致，无法连接！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            String qduni = MxFunction.getxDataString(ps[0], "unicode");
                            String zhduni = MxFunction.getxDataString(lId, "point_unicode");
                            if (qduni.equals(zhduni)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "起点与终点不能是同一个点，请重选终点！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            //判断当前起点和终点是否已连接
                            String ll = TypeItemUtil.getPre(stype) + "LINE";
                            MrxDbgSelSet ss = new MrxDbgSelSet();
                            MxResbuf filter = new MxResbuf();
                            filter.addString(ll, 8);
                            ss.allSelect(filter);
                            Log.i("线层对象数量", ":" + ss.size());

                            for (int i = 0; i < ss.size(); i++) {
                                long lg = ss.at(i);
                                String sc = MxFunction.getxDataString(lg, "qd_unicode");
                                String ec = MxFunction.getxDataString(lg, "zhd_unicode");

                                if (qduni.equals(sc) && zhduni.equals(ec)) {
                                    this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplication(), "当前起点与终点已连接，请重选终点！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            }

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    line_pop(1, stype, ltype);
                                    MxFunction.delSelect(id);
                                    operate = 0;
                                }
                            });
                        }
                    }
                } else if (entity.layerName().endsWith("POINT")) {
                    Log.i("当前对象：", "点");
                    final String sp = MxFunction.getxDataString(lId, "code");
                    if (sp == null || "".equals(sp)) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "信息丢失，无法操作！" + sp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    if (ps[0] == 0) {
                        ps[0] = lId;
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "已选择起点：" + sp, Toast.LENGTH_SHORT).show();
                            }
                        });
                        MxFunction.delSelect(lId);
                    } else {
                        ps[1] = lId;
                        McDbEntity ent0 = new McDbEntity(ps[0]);
                        if (ent0.layerName().endsWith("POINT")) {//点连点
                            ps[2] = 1;
                            final String stype = MxFunction.getxDataString(ps[0], "type_item");
                            final String ltype = MxFunction.getxDataString(ps[0], "type");
                            String etype = MxFunction.getxDataString(lId, "type_item");
                            Log.i("起点与终点···", stype + ":" + etype);
                            if (!stype.equals(etype)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "所选起点与终点类型不一致，无法连接！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            if (ps[0] == lId) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "起点与终点不能是同一个点，请重选终点！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            //判断当前起点和终点是否已连接
                            String ll = TypeItemUtil.getPre(stype) + "LINE";
                            MrxDbgSelSet ss = new MrxDbgSelSet();
                            MxResbuf filter = new MxResbuf();
                            filter.addString(ll, 8);
                            ss.allSelect(filter);
                            Log.i("线层对象数量", ":" + ss.size());
                            for (int i = 0; i < ss.size(); i++) {
                                long lg = ss.at(i);
                                String sc = MxFunction.getxDataString(lg, "qd_unicode");
                                String sc1 = MxFunction.getxDataString(ps[0], "unicode");
                                String ec = MxFunction.getxDataString(lg, "zhd_unicode");
                                String ec1 = MxFunction.getxDataString(lId, "unicode");

                                if (sc.equals(sc1) && ec.equals(ec1)) {
                                    this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplication(), "当前起点与终点已连接，请重选终点！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            }

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    line_pop(1, stype, ltype);
                                    MxFunction.delSelect(id);
                                    operate = 0;
                                }
                            });
                        } else if (ent0.layerName().equals("DIRECTIONLINE")) {//方向线连点
                            ps[2] = 4;
                            final String stype = MxFunction.getxDataString(ps[0], "directionlinetype");
                            final String ltype = MxFunction.getxDataString(ps[0], "type");
                            String etype = MxFunction.getxDataString(lId, "type_item");
                            Log.i("起点与终点···", stype + ":" + etype);
                            if (!stype.equals(etype)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "所选起点与终点类型不一致，无法连接！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            String qduni = MxFunction.getxDataString(ps[0], "point_unicode");
                            String zhduni = MxFunction.getxDataString(lId, "unicode");
                            Log.i("qduni-zhduni", qduni + ":" + zhduni);
                            if (qduni.equals(zhduni)) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication(), "起点与终点不能是同一个点，请重选终点！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            //判断当前起点和终点是否已连接
                            String ll = TypeItemUtil.getPre(stype) + "LINE";
                            MrxDbgSelSet ss = new MrxDbgSelSet();
                            MxResbuf filter = new MxResbuf();
                            filter.addString(ll, 8);
                            ss.allSelect(filter);
                            Log.i("线层对象数量", ":" + ss.size());
                            String sc1 = MxFunction.getxDataString(ps[0], "point_unicode");
                            String ec1 = MxFunction.getxDataString(lId, "unicode");
                            for (int i = 0; i < ss.size(); i++) {
                                long lg = ss.at(i);
                                String sc = MxFunction.getxDataString(lg, "qd_unicode");
                                String ec = MxFunction.getxDataString(lg, "zhd_unicode");

                                if (sc.equals(sc1) && ec.equals(ec1)) {
                                    this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplication(), "当前起点与终点已连接，请重选终点！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            }

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    line_pop(1, stype, ltype);
                                    MxFunction.delSelect(id);
                                    operate = 0;
                                }
                            });
                        }
                    }
                }
            } else {//非连线模式 选中点、线、展点弹出相应弹窗
                McDbEntity mde = new McDbEntity(id);
                String layername = mde.layerName();
                if (layername.endsWith("POINT")) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selected = id;
                            Log.i("选中的点x-y", "x:" + MxFunction.getxDataString(id, "x") + "-y:" + MxFunction.getxDataString(id, "y"));
                            op_point(id);
                            s_code = MxFunction.getxDataString(id, "code");
                            MxFunction.delSelect(id);
                        }
                    });
                } else if (layername.endsWith("LINE") && (!layername.equals("DIRECTIONLINE"))) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selected = id;
                            op_line(id);
                            MxFunction.delSelect(id);
                        }
                    });
                } else if (layername.equals("ZDH") || layername.equals("zdh")) {
                    McDbPoint point = new McDbPoint(id);
                    if (point.position().z == 0) {
                        MxFunction.delSelect(id);
                        return;
                    }
                    long pid = MxFunction.findEntAtPoint(point.position().x, point.position().y, "McDbBlockReference");
                    if (pid != 0) {
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplication(), "当前展点已编辑过，不能重复编辑！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    final McGePoint3d mg = point.position();
                    String sTs;
                    sTs = String.format("Point pos:%f,%f,%f", mg.x, mg.y, mg.z);
                    Log.i("McDbPoint", sTs);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selected = id;
                            ig = 3;
                            Double z = Math.round(mg.z * 1000) / 1000.0;
                            point_pop(3, mg.x, mg.y, z);
                            Log.i("展点坐标信息", mg.x + "," + mg.y + "," + mg.z);
                            setSpinners();
                            MxFunction.delSelect(id);//取消选中状态
                        }
                    });
                } else if (layername.equals("DIRECTIONLINE")) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            op_directionline(id);
                            MxFunction.delSelect(id);
                        }
                    });

                }
            }
        }
    }

    @Override
    public boolean returnStart() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
        return true;
    }

    //验证权限
    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //申请权限
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.s_type:
                Log.i("onItemSelected类型", "R.id.s_type");
                String[] a_ti, a_tzh, a_fshw;
                String c_type = type.getSelectedItem().toString();
                if (c_type.equals("给水")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongshui));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongshui_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongshui_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.gongshui);
                    a_tzh = getResources().getStringArray(R.array.gongshui_tezheng);
                    a_fshw = getResources().getStringArray(R.array.gongshui_fushuwu);
                } else if (c_type.equals("排水")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.paishui));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.paishui_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.paishui_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.paishui);
                    a_tzh = getResources().getStringArray(R.array.paishui_tezheng);
                    a_fshw = getResources().getStringArray(R.array.paishui_fushuwu);
                } else if (c_type.equals("燃气")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.ranqi);
                    a_tzh = getResources().getStringArray(R.array.ranqi_tezheng);
                    a_fshw = getResources().getStringArray(R.array.ranqi_fushuwu);
                } else if (c_type.equals("热力")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reli));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reli_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reli_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.reli);
                    a_tzh = getResources().getStringArray(R.array.reli_tezheng);
                    a_fshw = getResources().getStringArray(R.array.reli_fushuwu);
                } else if (c_type.equals("电力")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.dianli);
                    a_tzh = getResources().getStringArray(R.array.dianli_tezheng);
                    a_fshw = getResources().getStringArray(R.array.dianli_fushuwu);
                } else if (c_type.equals("通讯")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.tongxin));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.tongxin_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.tongxin_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.tongxin);
                    a_tzh = getResources().getStringArray(R.array.tongxin_tezheng);
                    a_fshw = getResources().getStringArray(R.array.tongxin_fushuwu);
                } else if (c_type.equals("工业")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.gongye);
                    a_tzh = getResources().getStringArray(R.array.gongye_tezheng);
                    a_fshw = getResources().getStringArray(R.array.gongye_fushuwu);
                } else if (c_type.equals("综合管沟")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.zongheguangou));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.zongheguangou_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.zongheguangou_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.zongheguangou);
                    a_tzh = getResources().getStringArray(R.array.zongheguangou_tezheng);
                    a_fshw = getResources().getStringArray(R.array.zongheguangou_fushuwu);
                } else if (c_type.equals("人防")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.renfang));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.renfang_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.renfang_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.renfang);
                    a_tzh = getResources().getStringArray(R.array.renfang_tezheng);
                    a_fshw = getResources().getStringArray(R.array.renfang_fushuwu);
                } else if (c_type.equals("地铁")) {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ditie));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ditie_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ditie_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.ditie);
                    a_tzh = getResources().getStringArray(R.array.ditie_tezheng);
                    a_fshw = getResources().getStringArray(R.array.ditie_fushuwu);
                } else {
                    ArrayAdapter ArrayAdapter1 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.buming));
                    type_item.setAdapter(ArrayAdapter1);
                    ArrayAdapter ArrayAdapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.buming_tezheng));
                    tezheng.setAdapter(ArrayAdapter2);
                    ArrayAdapter ArrayAdapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.buming_fushuwu));
                    fushuwu.setAdapter(ArrayAdapter3);
                    a_ti = getResources().getStringArray(R.array.buming);
                    a_tzh = getResources().getStringArray(R.array.buming_tezheng);
                    a_fshw = getResources().getStringArray(R.array.buming_fushuwu);
                }

                if (ig == 2) {
                    set(type_item, a_ti, MxFunction.getxDataString(selected, "type_item"));
                    String s_tezheng = MxFunction.getxDataString(selected, "tezheng");
                    String s_fushuwu = MxFunction.getxDataString(selected, "fushuwu");
                    if (!" ".equals(s_tezheng)) {
                        set(tezheng, a_tzh, s_tezheng);
                    }
                    if (!" ".equals(s_fushuwu)) {
                        set(fushuwu, a_fshw, s_fushuwu);
                    }
                    type.setEnabled(false);
                    type_item.setEnabled(false);
                    //                    ig = 0;
                } else {
                    set(type_item, a_ti, last[1]);
                    fushuwu.setSelection(1);
                }


                type_item.setOnItemSelectedListener(this);
                tezheng.setOnItemSelectedListener(this);
                fushuwu.setOnItemSelectedListener(this);
                break;
            case R.id.s_type_item:
                if (ig == 1 || ig == 3) {
                    //读字典
                    List<CodeNumber> cl = codeNumberBox.query().equal(CodeNumber_.filename, MxFunction.currentFileName())
                            .and().equal(CodeNumber_.type, type_item.getSelectedItem().toString()).build().find();
                    if (cl.size() == 1) {
                        CodeNumber cn = cl.get(0);
                        String pre = cn.getPre();
                        int num = cn.getNo() + 1;
                        code.setText(pre + num);
                        Log.i("自动输入点号", pre + num);
                    } else {
                        String pre = TypeItemUtil.getPre(type_item.getSelectedItem().toString());
                        int no = 1;
                        code.setText(pre + no);
                    }
                }
                //                if (ig == 0) {
                //                    ig = 1;
                //                }
                break;
            case R.id.s_jczh:
                break;
            case R.id.s_jgczh:
                break;
            case R.id.s_jgxzh:
                break;
            case R.id.s_jgzht:
                break;
            case R.id.np_s_state:
                break;
            case R.id.np_s_data:
                break;
            case R.id.s_tezheng:
                String tp = type.getSelectedItem().toString();
                String it = type_item.getSelectedItem().toString();
                Mark_Util mu = new Mark_Util();
                Log.i("选择的特征······", tezheng.getSelectedItem().toString());
                if (tezheng.getSelectedItemPosition() > 0) {
                    fushuwu.setSelection(0);
                    mark_name = mu.getMark(1, tezheng.getSelectedItem().toString(), tp, it);
                }
                break;
            case R.id.s_fushuwu:
                String sfushuwu = parent.getSelectedItem().toString();
                Log.i("选择的附属物······", sfushuwu);
                if (sfushuwu.endsWith("井") || sfushuwu.endsWith("篦") || sfushuwu.equals("人孔") || sfushuwu.equals("手孔")) {
                    ll_jing.setVisibility(View.VISIBLE);
                } else {
                    ll_jing.setVisibility(View.GONE);
                }

                String tp1 = type.getSelectedItem().toString();
                String it1 = type_item.getSelectedItem().toString();
                Mark_Util mu1 = new Mark_Util();
                if (fushuwu.getSelectedItemPosition() > 0) {
                    tezheng.setSelection(0);
                    mark_name = mu1.getMark(2, fushuwu.getSelectedItem().toString(), tp1, it1);
                }
                break;
            case R.id.s_mshway:
                String mshway = parent.getSelectedItem().toString();
                if (mshway.equals("管沟") || mshway.equals("管块") || mshway.equals("管廊")) {
                    tv_gj.setText("宽");
                    ll_guanjing2.setVisibility(View.VISIBLE);
                    if (lg == 1) {
                        gao.setText(MxFunction.getxDataString(selected, "gj2"));
                        lg = 0;
                    }
                    //                    ll_ks.setVisibility(View.VISIBLE);
                } else {
                    tv_gj.setText("管径");
                    ll_guanjing2.setVisibility(View.GONE);
                    //                    ll_ks.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void set(Spinner s, String[] data, String value) {
        for (int x = 0; x < data.length; x++) {
            if (data[x].equals(value)) {
                s.setSelection(x);
            }
        }
    }

    //根据大类获取颜色,参数type:大类
    public static long[] getColor(String type) {
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

    /*
     * 将asset文件写入缓存
     */
    public boolean copyAssetAndWrite(String fileName) {
        try {
            File cacheDir = getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (outFile.exists()) {
                if (outFile.length() > 10) {//表示已经写入一次
                    return true;
                }
            } else {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return false;
                }
            }
            InputStream is = getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //新建管种:根据相应MDB文件中的表名判断是否已有每类管点/线表，没有的则可以选择是否新建
    void newPipe4mdb() {
        String name = MxFunction.currentFileName();
        if (name.endsWith(".mwg")) {
            name = name.replace(".mwg", ".mdb");
        } else if (name.endsWith(".dwg")) {
            name = name.replace(".dwg", ".mdb");
        }
        String fn = name.substring(name.lastIndexOf("/") + 1);
        String target = getWorkDir() + File.separator + fn;
        File nf = new File(target);
        if (!nf.exists()) {
            try {
                if (nf.createNewFile()) {
                    Database.FileFormat ff = Database.FileFormat.V2003;
                    db = DatabaseBuilder.create(ff, nf);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] ss = getResources().getStringArray(R.array.s_type);
        List<String> unadd = Arrays.asList(ss);
        List<String> unadded = new ArrayList<>(unadd);
        try {
            db = DatabaseBuilder.open(nf);
            Set<String> set = db.getTableNames();
            for (String s : ss) {
                for (String sn : set) {
                    if (sn.endsWith("_LINE") && s.substring(s.length() - 3, s.length() - 1).equals(sn.substring(0, 2))) {
                        unadded.remove(s);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("未新建的管种数", unadded.size() + "");
        final List<String> fss = unadded;

        View v = LayoutInflater.from(getApplication()).inflate(R.layout.pipe4mdb, null);
        final WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
        int hight = (int) (manager.getDefaultDisplay().getHeight() * 0.8);
        final Popup ppw = new Popup(v, width, hight);

        ColorDrawable cd = new ColorDrawable(0x000000);
        ppw.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        ppw.setOutsideTouchable(false);
        ppw.setFocusable(true);
        ppw.setIsdismiss(false);
        ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        ppw.update();
        ppw.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        final ListView lv = v.findViewById(R.id.list_left);
        TextView cancel = v.findViewById(R.id.tv_r_cancel);
        TextView ok = v.findViewById(R.id.tv_r_sure);
        checkedMap = new HashMap<>();
        final MyAdapter4mdb mas = new MyAdapter4mdb(fss, getApplication());
        lv.setAdapter(mas);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean flag = MyAdapter4mdb.getIsSelected().get(position);
                lv.setItemChecked(position, !flag);
                MyAdapter4mdb.getIsSelected().put(position, !flag);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ppw.close();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ppw.close();

                List<String> list = new ArrayList<>();
                for (int i = 0; i < fss.size(); i++) {
                    if (MyAdapter4mdb.isSelected.get(i)) {
                        String str = fss.get(i);
                        String st = str.substring(str.length() - 3, str.length() - 1);
                        Log.i("新建管种--选择的管种的缩写", st);
                        list.add(st);
                    }
                }

                try {
                    for (String s : list) {
                        Table ptable = new TableBuilder(s + "_POINT")
                                .addColumn(new ColumnBuilder("ID").setSQLType(Types.BIGINT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("图上点号").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("物探点号").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                                .addColumn(new ColumnBuilder("特征").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("附属物").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("X").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("Y").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("符号旋转角").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("地面高程").setSQLType(Types.DOUBLE).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("综合图点号X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("综合图点号Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("专业图点号X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("专业图点号Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("点要素编码").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                                .addColumn(new ColumnBuilder("道路名称").setSQLType(Types.NVARCHAR).setLength(60).toColumn())
                                .addColumn(new ColumnBuilder("图幅号").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("辅助类型").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("删除标记").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("井盖材质").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("井盖尺寸").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("井盖形状").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("井材质").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("井尺寸").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                                .addColumn(new ColumnBuilder("使用状态").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("管线类型").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("井盖类型").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("偏心井位").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("EXPNO").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("备注").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                                .addColumn(new ColumnBuilder("操作库").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .addColumn(new ColumnBuilder("井底深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("数据来源").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                                .toTable(db);

                        Table ltable = new TableBuilder(s + "_LINE")
                                .addColumn(new ColumnBuilder("ID").setSQLType(Types.BIGINT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("线要素编码").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                                .addColumn(new ColumnBuilder("起点点号").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                                .addColumn(new ColumnBuilder("连接方向").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                                .addColumn(new ColumnBuilder("起点埋深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("终点埋深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("埋设类型").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("材质").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("管径").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("流向").setSQLType(Types.NVARCHAR).setLength(2).toColumn())
                                .addColumn(new ColumnBuilder("电压压力").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("电缆条数").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("总孔数").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("分配孔数").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("建设年代").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("LNUMBER").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .addColumn(new ColumnBuilder("线型").setSQLType(Types.NVARCHAR).setLength(4).toColumn())
                                .addColumn(new ColumnBuilder("专业注记内容").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .addColumn(new ColumnBuilder("专业注记X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("专业注记Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("专业注记角度").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("综合注记内容").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .addColumn(new ColumnBuilder("综合注记X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("综合注记Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("综合注记角度").setSQLType(Types.FLOAT).setLength(10).toColumn())
                                .addColumn(new ColumnBuilder("辅助类型").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                                .addColumn(new ColumnBuilder("已用孔数").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("删除标记").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("套管尺寸").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                                .addColumn(new ColumnBuilder("起点管顶高程").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("终点管顶高程").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                                .addColumn(new ColumnBuilder("管线权属代码").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                                .addColumn(new ColumnBuilder("备注").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                                .addColumn(new ColumnBuilder("操作库").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .addColumn(new ColumnBuilder("道路名称").setSQLType(Types.NVARCHAR).setLength(60).toColumn())
                                .addColumn(new ColumnBuilder("管沟连接码").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                                .toTable(db);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //生成mdb库
    File NewDatabase() {
        List<String> pl = new ArrayList<>();
        List<String> ll = new ArrayList<>();
        long[] pls = MxFunction.getAllLayer();
        for (int i = 0; i < pls.length; i++) {
            McDbLayerTableRecord tr = new McDbLayerTableRecord(pls[i]);
            String name = tr.getName();
            if (name.endsWith("POINT")) {
                MrxDbgSelSet ss = new MrxDbgSelSet();
                MxResbuf filter = new MxResbuf();
                filter.addString(name, 8);
                ss.allSelect(filter);
                for (int j = 0; j < ss.size(); j++) {
                    long id = ss.at(j);
                    String ti = MxFunction.getxDataString(id, "type_item");
                    String pre = TypeItemUtil.getPre(ti);
                    if (!pl.contains(pre)) {
                        pl.add(pre);
                    }
                }
            } else if (name.endsWith("LINE") && (!name.equals("DIRECTIONLINE"))) {
                MrxDbgSelSet ss = new MrxDbgSelSet();
                MxResbuf filter = new MxResbuf();
                filter.addString(name, 8);
                ss.allSelect(filter);
                for (int j = 0; j < ss.size(); j++) {
                    long id = ss.at(j);
                    String ti = MxFunction.getxDataString(id, "type");
                    String lpr = TypeItemUtil.getPre(ti);
                    if (!ll.contains(lpr)) {
                        ll.add(lpr);
                    }
                }
            }
        }
        String name = MxFunction.currentFileName();
        if (name.endsWith(".mwg")) {
            name = name.replace(".mwg", "_导出.mdb");
        } else if (name.endsWith(".dwg")) {
            name = name.replace(".dwg", "_导出.mdb");
        }
        String fn = name.substring(name.lastIndexOf("/") + 1);
        String target = getWorkDir() + File.separator + fn;
        File nf = new File(target);
        try {
            nf.createNewFile();
            Database.FileFormat ff = Database.FileFormat.V2003;
            Database db = DatabaseBuilder.create(ff, nf);
            for (String s : pl) {
                Table table = new TableBuilder(s + "_POINT")
                        .addColumn(new ColumnBuilder("ID").setSQLType(Types.BIGINT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("图上点号").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("物探点号").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                        .addColumn(new ColumnBuilder("特征").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("附属物").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("X").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("Y").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("符号旋转角").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("地面高程").setSQLType(Types.DOUBLE).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("综合图点号X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("综合图点号Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("专业图点号X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("专业图点号Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("点要素编码").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                        .addColumn(new ColumnBuilder("道路名称").setSQLType(Types.NVARCHAR).setLength(60).toColumn())
                        .addColumn(new ColumnBuilder("图幅号").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("辅助类型").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("删除标记").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("井盖材质").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("井盖尺寸").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("井盖形状").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("井材质").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("井尺寸").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                        .addColumn(new ColumnBuilder("使用状态").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("管线类型").setSQLType(Types.NVARCHAR).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("井盖类型").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("偏心井位").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("EXPNO").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("备注").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                        .addColumn(new ColumnBuilder("操作库").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .addColumn(new ColumnBuilder("井底深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("数据来源").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                        .toTable(db);

            }

            for (String s : ll) {
                Table table = new TableBuilder(s + "_LINE")
                        .addColumn(new ColumnBuilder("ID").setSQLType(Types.BIGINT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("线要素编码").setSQLType(Types.NVARCHAR).setLength(16).toColumn())
                        .addColumn(new ColumnBuilder("起点点号").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                        .addColumn(new ColumnBuilder("连接方向").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                        .addColumn(new ColumnBuilder("起点埋深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("终点埋深").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("埋设类型").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("材质").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("管径").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("流向").setSQLType(Types.NVARCHAR).setLength(2).toColumn())
                        .addColumn(new ColumnBuilder("电压压力").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("电缆条数").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("总孔数").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("分配孔数").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("建设年代").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("LNUMBER").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .addColumn(new ColumnBuilder("线型").setSQLType(Types.NVARCHAR).setLength(4).toColumn())
                        .addColumn(new ColumnBuilder("专业注记内容").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .addColumn(new ColumnBuilder("专业注记X坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("专业注记Y坐标").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("专业注记角度").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("综合注记内容").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .addColumn(new ColumnBuilder("综合注记X坐标").setSQLType(Types.BIGINT).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("综合注记Y坐标").setSQLType(Types.BIGINT).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("综合注记角度").setSQLType(Types.FLOAT).setLength(10).toColumn())
                        .addColumn(new ColumnBuilder("辅助类型").setSQLType(Types.NVARCHAR).setLength(30).toColumn())
                        .addColumn(new ColumnBuilder("已用孔数").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("删除标记").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("套管尺寸").setSQLType(Types.NVARCHAR).setLength(20).toColumn())
                        .addColumn(new ColumnBuilder("起点管顶高程").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("终点管顶高程").setSQLType(Types.DOUBLE).setLength(15).toColumn())
                        .addColumn(new ColumnBuilder("管线权属代码").setSQLType(Types.NVARCHAR).setLength(24).toColumn())
                        .addColumn(new ColumnBuilder("备注").setSQLType(Types.NVARCHAR).setLength(40).toColumn())
                        .addColumn(new ColumnBuilder("操作库").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .addColumn(new ColumnBuilder("道路名称").setSQLType(Types.NVARCHAR).setLength(60).toColumn())
                        .addColumn(new ColumnBuilder("管沟连接码").setSQLType(Types.NVARCHAR).setLength(100).toColumn())
                        .toTable(db);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nf;
    }

    /*
     *获得当前打开图纸所对应的mdb文件名
     * mdb文件名与当前打开图纸名一样，后缀不同
     */
    static String mdbName() {
        String name = MxFunction.currentFileName();
        if (name.endsWith(".mwg")) {
            name = name.replace(".mwg", ".mdb");
        } else if (name.endsWith(".dwg")) {
            name = name.replace(".dwg", ".mdb");
        }
        String fn = name.substring(name.lastIndexOf("/") + 1);
        return getWorkDir() + File.separator + fn;
    }

    /*
     *另存为弹窗
     *文件名不能为空。另存路径为工作文件夹
     */
    void SaveAs() {
        View v = LayoutInflater.from(getApplication()).inflate(R.layout.saveas, null);
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);

        final Popup ppw = new Popup(v, width, WRAP_CONTENT);
        ppw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ColorDrawable cd = new ColorDrawable(0x000000);
        ppw.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        ppw.setOutsideTouchable(false);
        ppw.setFocusable(true);
        ppw.setIsdismiss(false);
        ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        ppw.update();
        ppw.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        final EditText et = v.findViewById(R.id.et_sa);
        TextView cancel = v.findViewById(R.id.tv_r_cancel);
        TextView ok = v.findViewById(R.id.tv_r_sure);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ppw.close();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et.getText())) {
                    ppw.close();
                    String name = et.getText().toString();
                    File newfile = new File(getWorkDir() + File.separator + name + ".dwg");

                    File origin = new File(MxFunction.currentFileName());
                    try {
                        if (!newfile.exists())
                            newfile.createNewFile();

                        InputStream fosfrom = new FileInputStream(origin);
                        OutputStream fosto = new FileOutputStream(newfile);
                        byte bt[] = new byte[1024];
                        int c;
                        while ((c = fosfrom.read(bt)) > 0) {
                            Log.i("进行复制。。。", "cc" + c);
                            fosto.write(bt, 0, c);
                        }
                        fosfrom.close();
                        fosto.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    MxFunction.writeFile(getWorkDir() + File.separator + name + ".dwg");
                }
            }
        });

    }

    /*
     *添加方向线弹窗
     * @param id 方向线id
     * @param point_id 方向线所属点id
     */
    private void pop_directionLine(final int flag, final long id, final long point_id) {
        String type;
        if (flag == 1) {
            type = MxFunction.getxDataString(point_id, "type");
        } else {
            type = MxFunction.getxDataString(id, "type");
        }
        final String dalei = type;
        if (dalei == null || "".equals(dalei)) {
            if (flag == 1) {
                Toast.makeText(getApplication(), "相关管点数据丢失，无法继续添加！", Toast.LENGTH_SHORT).show();
                MxFunction.deleteObject(id);
            } else {
                Toast.makeText(getApplication(), "数据丢失，无法查看！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.diretionline4dltx_info, null);
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (manager.getDefaultDisplay().getWidth() * 0.8);
        int hight = (int) (manager.getDefaultDisplay().getHeight() * 0.8);
        dl_pop = new Popup(v, width, hight);
        dl_pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //ColorDrawable cd = new ColorDrawable(0x000000);
        dl_pop.setBackgroundDrawable(null);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        dl_pop.setTouchable(true); // 设置popupwindow可点击
        dl_pop.setOutsideTouchable(false); // 设置popupwindow外部不可点击
        dl_pop.setFocusable(true); // 获取焦点
        dl_pop.setIsdismiss(false);

        dl_pop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        dl_pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                dl_pop = null;
            }
        });

        TextView cancel = v.findViewById(R.id.tv_r_cancel);
        TextView ok = v.findViewById(R.id.tv_r_sure);
        dl_et_maishen = v.findViewById(R.id.et_dl_msh);
        dl_et_gj = v.findViewById(R.id.et_dl_gj);
        dl_et_gao = v.findViewById(R.id.et_guanjing2);
        dl_czh = v.findViewById(R.id.s_caizhi);
        dl_mshfsh = v.findViewById(R.id.s_mshway);
        dl_yl = v.findViewById(R.id.s_yali);
        dl_et_ts = v.findViewById(R.id.et_num);
        dl_et_zksh = v.findViewById(R.id.et_allkong);
        dl_et_yyksh = v.findViewById(R.id.et_usedkong);
        dl_ll_ks = v.findViewById(R.id.dl_ll_ks);
        dl_ll_ts = v.findViewById(R.id.dl_ll_ts);
        dl_ll_yl = v.findViewById(R.id.ll_yali);
        ll_guanjing2 = v.findViewById(R.id.ll_guanjing2);

        tv_gj = v.findViewById(R.id.tv_gj);

        dl_mshfsh.setOnItemSelectedListener(this);

        String[] ss_caizhi, ss_mshway, ss_yl = null;
        if ("给水".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_js));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_js));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_js);
            ss_mshway = getResources().getStringArray(R.array.mshway_js);
        } else if ("排水".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_ps));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_ps));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_ps);
            ss_mshway = getResources().getStringArray(R.array.mshway_ps);
        } else if ("工业".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_gy));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_gy));
            dl_czh.setAdapter(adapter3);

            dl_ll_yl.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.gongye_yali));
            dl_yl.setAdapter(ArrayAdapter);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_gy);
            ss_mshway = getResources().getStringArray(R.array.mshway_gy);
            ss_yl = getResources().getStringArray(R.array.gongye_yali);
        } else if ("热力".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_rl));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_rl));
            dl_czh.setAdapter(adapter3);

            dl_ll_yl.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.reshui_yali));
            dl_yl.setAdapter(ArrayAdapter);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_rl);
            ss_mshway = getResources().getStringArray(R.array.mshway_rl);
            ss_yl = getResources().getStringArray(R.array.reshui_yali);
        } else if ("电力".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_dl));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_dl));
            dl_czh.setAdapter(adapter3);

            dl_ll_yl.setVisibility(View.VISIBLE);
            dl_ll_ts.setVisibility(View.VISIBLE);
            dl_ll_ks.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.dianli_yali));
            dl_yl.setAdapter(ArrayAdapter);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_dl);
            ss_mshway = getResources().getStringArray(R.array.mshway_dl);
            ss_yl = getResources().getStringArray(R.array.dianli_yali);
        } else if ("通讯".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_tx));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_tx));
            dl_czh.setAdapter(adapter3);

            dl_ll_ts.setVisibility(View.VISIBLE);
            dl_ll_ks.setVisibility(View.VISIBLE);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_tx);
            ss_mshway = getResources().getStringArray(R.array.mshway_tx);
        } else if ("燃气".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_rq));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_rq));
            dl_czh.setAdapter(adapter3);

            dl_ll_yl.setVisibility(View.VISIBLE);
            ArrayAdapter ArrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.ranqi_yali));
            dl_yl.setAdapter(ArrayAdapter);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_rq);
            ss_mshway = getResources().getStringArray(R.array.mshway_rq);
            ss_yl = getResources().getStringArray(R.array.ranqi_yali);
        } else if ("综合".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_zh));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_zh));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_zh);
            ss_mshway = getResources().getStringArray(R.array.mshway_zh);
        } else if ("人防".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_rf));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_rf));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_rf);
            ss_mshway = getResources().getStringArray(R.array.mshway_rf);
        } else if ("地铁".equals(dalei)) {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_dt));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_dt));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_dt);
            ss_mshway = getResources().getStringArray(R.array.mshway_dt);
        } else {
            SpinnerAdapter adapter2 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.mshway_bm));
            dl_mshfsh.setAdapter(adapter2);
            SpinnerAdapter adapter3 = new ArrayAdapter<>(this, R.layout.adapter_item, getResources().getStringArray(R.array.caizhi_bm));
            dl_czh.setAdapter(adapter3);
            ss_caizhi = getResources().getStringArray(R.array.caizhi_bm);
            ss_mshway = getResources().getStringArray(R.array.mshway_bm);
        }

        if (flag == 2) {
            dl_et_maishen.setText(MxFunction.getxDataString(id, "msh"));
            dl_et_gj.setText(MxFunction.getxDataString(id, "gj"));
            set(dl_czh, ss_caizhi, " ".equals(MxFunction.getxDataString(id, "czh")) ? "请选择" : MxFunction.getxDataString(id, "czh"));
            set(dl_mshfsh, ss_mshway, MxFunction.getxDataString(id, "mshfsh"));

            if (dl_ll_ts.getVisibility() == View.VISIBLE) {
                dl_et_ts.setText(MxFunction.getxDataString(id, "tsh"));
            }
            if (dl_ll_ks.getVisibility() == View.VISIBLE) {
                dl_et_zksh.setText(MxFunction.getxDataString(id, "zksh"));
                dl_et_yyksh.setText(MxFunction.getxDataString(id, "yyksh"));
            }
            if (dl_ll_yl.getVisibility() == View.VISIBLE && ss_yl != null) {
                set(dl_yl, ss_yl, MxFunction.getxDataString(id, "yl"));
            }
            if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                dl_et_gao.setText(MxFunction.getxDataString(id, "gao"));
            }

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 1) {
                    MxFunction.deleteObject(id);
                }
                dl_pop.close();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(dl_et_maishen.getText()) || TextUtils.isEmpty(dl_et_gj.getText())) {
                    Toast.makeText(getApplication(), "埋深和管径不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (flag == 1) {
                    String msh = dl_et_maishen.getText().toString();
                    String gjg = dl_et_gj.getText().toString();
                    String pc = MxFunction.getxDataString(point_id, "code");
                    String punic = MxFunction.getxDataString(point_id, "unicode");
                    String xiaolei = MxFunction.getxDataString(point_id, "type_item");

                    MxFunction.setxDataString(id, "msh", msh);
                    MxFunction.setxDataString(id, "gj", gjg);
                    MxFunction.setxDataString(id, "mshfsh", dl_mshfsh.getSelectedItem().toString());
                    MxFunction.setxDataString(id, "czh", dl_czh.getSelectedItem().toString().equals("请选择") ? " " : dl_czh.getSelectedItem().toString());
                    //                    MxFunction.setxDataString(id, "point_id", String.valueOf(point_id));
                    MxFunction.setxDataString(id, "point_code", pc);
                    MxFunction.setxDataString(id, "point_unicode", punic);
                    MxFunction.setxDataString(id, "directionlinetype", xiaolei);
                    MxFunction.setxDataString(id, "type", dalei);

                    if (dl_ll_ts.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "tsh", TextUtils.isEmpty(dl_et_ts.getText()) ? " " : dl_et_ts.getText().toString());
                    }
                    if (dl_ll_ks.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "zksh", TextUtils.isEmpty(dl_et_zksh.getText()) ? " " : dl_et_zksh.getText().toString());
                        MxFunction.setxDataString(id, "yyksh", TextUtils.isEmpty(dl_et_yyksh.getText()) ? " " : dl_et_yyksh.getText().toString());
                    }
                    if (dl_ll_yl.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "yl", dl_yl.getSelectedItem().toString());
                    }
                    if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "gao", TextUtils.isEmpty(dl_et_gao.getText()) ? " " : dl_et_gao.getText().toString());
                    }

                } else {
                    String msh = dl_et_maishen.getText().toString();
                    String gjg = dl_et_gj.getText().toString();
                    MxFunction.setxDataString(id, "msh", msh);
                    MxFunction.setxDataString(id, "gj", gjg);
                    MxFunction.setxDataString(id, "mshfsh", dl_mshfsh.getSelectedItem().toString());
                    MxFunction.setxDataString(id, "czh", dl_czh.getSelectedItem().toString().equals("请选择") ? " " : dl_czh.getSelectedItem().toString());

                    if (dl_ll_ts.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "tsh", TextUtils.isEmpty(dl_et_ts.getText()) ? " " : dl_et_ts.getText().toString());
                    }
                    if (dl_ll_ks.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "zksh", TextUtils.isEmpty(dl_et_zksh.getText()) ? " " : dl_et_zksh.getText().toString());
                        MxFunction.setxDataString(id, "yyksh", TextUtils.isEmpty(dl_et_yyksh.getText()) ? " " : dl_et_yyksh.getText().toString());
                    }
                    if (dl_ll_yl.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "yl", dl_yl.getSelectedItem().toString().equals("请选择") ? " " : dl_yl.getSelectedItem().toString());
                    }
                    if (ll_guanjing2.getVisibility() == View.VISIBLE) {
                        MxFunction.setxDataString(id, "gao", TextUtils.isEmpty(dl_et_gao.getText()) ? " " : dl_et_gao.getText().toString());
                    }
                }
                dl_pop.close();
            }
        });

    }

    /*获得管点相关埋深
     *@param falg 标识：1-起点埋深 2-终点埋深
     *@param code 管点点号
     *@param type 管点类型
     */
    String getDeep(int flag, String code, String type) {
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
    long unicode2point(String unicode, String ti) {
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

    /*
     *将点实体封装的属性信息保存至图中
     * @param bp 点实体对象
     * @param bid 图上点的ID
     */
    void bmpoint2xdata(BmPoint bmPoint, long bid) {
        MxFunction.setxDataString(bid, "unicode", bmPoint.getExploration_dot());
        MxFunction.setxDataString(bid, "code", bmPoint.getMap_dot());
        MxFunction.setxDataString(bid, "x", String.valueOf(bmPoint.getY()));
        MxFunction.setxDataString(bid, "y", String.valueOf(bmPoint.getX()));
        Log.i("x--y", x + "---" + y);
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
    void Bmline2xdata(BmLine bl, long lid) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (ps == null) {
            ps = new long[3];
        }
        if (last == null) {
            last = new String[]{"", ""};
        }
        if (color == null) {
            color = new long[3];
        }
        if (codeNumberBox == null) {
            codeNumberBox = MyApplication.getApplication().getBoxStore().boxFor(CodeNumber.class);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MxFunction.writeFile(MxFunction.currentFileName());
    }

}