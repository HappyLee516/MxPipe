package com.mxpipe.lih.mxpipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.MxDraw.McDbLayerTable;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.mxpipe.lih.mxpipe.DaoruUtil.getPath;
import static com.mxpipe.lih.mxpipe.DaoruUtil.getRealPathFromURI;

/*
 *Created by LiHuan at 9:20 on 2019/2/21
 * 读取DAT文件数据工具类
 */
class ReadDatUtil {

    static boolean ReadDat(Intent data, Context context) {
        Log.i("调用readdat", "--------");
        Uri uri = data.getData();
        String path;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
            path = getPath(context, uri);
        } else {//4.4以下下系统调用方法
            path = getRealPathFromURI(uri, context);
        }
        File file = new File(path);
        Log.i("path", path);
        McDbLayerTable table = MxFunction.getCurrentDatabase().getLayerTable();
        if (!table.has("ZDH")) {
            MxLibDraw.addLayer("ZDH");
        }
        if (file.getName().endsWith(".dat") || file.getName().endsWith(".DAT")) {
            SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String like = sp.getString("like", null);
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    Log.i("读取到的数据", line);
                    String[] dt = line.split(",");
                    if (dt.length == 5) {
//                        StartAct startAct = (StartAct) context;
//                        startAct.copyAssetAndWrite("sss.dwg");
//                        File block = new File(startAct.getCacheDir(), "sss.dwg");
//                        File af = block.getAbsoluteFile();
//                        String s1 = af.getPath();
                        if ("点号".equals(like)) {
                            MxLibDraw.setLayerName("ZDH");
                            //                            MxLibDraw.insertBlock(s1, dt[0]);
                            //                            long id = MxLibDraw.drawBlockReference(Double.parseDouble(dt[2]),Double.parseDouble(dt[3]), dt[0], 0.5, 0);
                            //                            McDbBlockReference mb = new McDbBlockReference(id);
                            //                            McGePoint3d p3d = mb.position();
                            //                            p3d.z = Double.parseDouble(dt[4]);
                            //                            mb.setPosition(p3d);
                            MxLibDraw.drawPoint(Double.parseDouble(dt[2]), Double.parseDouble(dt[3]), Double.parseDouble(dt[4]));
                            MxLibDraw.drawText(Double.parseDouble(dt[2]) + 0.3, Double.parseDouble(dt[3]), 1, dt[0]);
                        } else {
                            MxLibDraw.setLayerName("ZDH");
                            //                            MxLibDraw.insertBlock(s1, dt[0]);
                            //                            long id = MxLibDraw.drawBlockReference(Double.parseDouble(dt[2]),Double.parseDouble(dt[3]), dt[0], 0.5, 0);
                            //                            McDbBlockReference mb = new McDbBlockReference(id);
                            //                            McGePoint3d p3d = mb.position();
                            //                            p3d.z = Double.parseDouble(dt[4]);
                            //                            mb.setPosition(p3d);
                            MxLibDraw.drawPoint(Double.parseDouble(dt[2]), Double.parseDouble(dt[3]), Double.parseDouble(dt[4]));
                            MxLibDraw.drawText(Double.parseDouble(dt[2]) + 0.3, Double.parseDouble(dt[3]), 1, dt[1]);
                        }
                    } else if (dt.length == 6) {
                        MxLibDraw.drawPoint(Double.parseDouble(dt[3]), Double.parseDouble(dt[4]), Double.parseDouble(dt[5]));

                    } else {
                        return false;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
