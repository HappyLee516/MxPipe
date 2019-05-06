package com.mxpipe.lih.mxpipe;

/*
 * Created by LiHuan on 2018/11/9.
 */

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.MxDraw.McDbBlockReference;
import com.MxDraw.McDbLayerTable;
import com.MxDraw.McDbText;
import com.MxDraw.McGePoint3d;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.mxpipe.lih.mxpipe.DaochuUtil.lm;
import static com.mxpipe.lih.mxpipe.DaochuUtil.pm;

 class DaoruUtil {
    
    static boolean Daoru(Intent data, Context context) {
        Uri uri = data.getData();
        String path;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
            path = getPath(context, uri);
            //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        } else {//4.4以下下系统调用方法
            path = getRealPathFromURI(uri,context);
        }
        File file = new File(path);
        if (file.getName().endsWith("mdb") || file.getName().endsWith("MDB")) {
            try {
                Database db = DatabaseBuilder.open(file);
                for (String s : db.getTableNames()) {
                    System.out.println(s);
                    Table table = db.getTable(s);

                    String qianzhui = s.substring(0,s.indexOf("_"));
                    McDbLayerTable layerTable = MxFunction.getCurrentDatabase().getLayerTable();
                    if(!layerTable.has(qianzhui + "POINT")){
                        MxLibDraw.addLayer(qianzhui + "POINT");
                    }
                    if(!layerTable.has(qianzhui + "TEXT")){
                        MxLibDraw.addLayer(qianzhui + "TEXT");
                    }
                    if(!layerTable.has(qianzhui + "LINE")){
                        MxLibDraw.addLayer(qianzhui + "LINE");
                    }

                    //判断是点表还是线表
                    if (s.endsWith("POINT")) {
                        for (Row row : table) {
                            String x = String.valueOf(row.get("Y"));
                            String y = String.valueOf(row.get("X"));
                            String type_item = "";

                            String code = row.getString("物探点号");
                            if(code == null){
                                continue;
                            }
                            //根据表名获取管线小类
                            for (Map.Entry entry : pm.entrySet()) {
                                if (s.equals(entry.getValue())) {
                                    type_item = entry.getKey().toString();
                                }
                            }
                            String type = ti_t_Util.getType(type_item);
                            MxLibDraw.setDrawColor(StartAct.getColor(type));
                            String tzh = row.getString("特征");
                            String fshw = row.getString("附属物");
                            String jdmsh = String.valueOf(row.getFloat("井底深"));
                            String jgxzh = row.getString("井盖形状");
                            String jgchc = row.getString("井盖尺寸");
                            String jgczh = row.getString("井盖材质");
                            String jgzht = row.getString("井盖类型");
                            String jczh = row.getString("井材质");
                            String jchc = row.getString("井尺寸");
                            String dmgch = String.valueOf(row.getDouble("地面高程"));
                            String shyzht = row.getString("使用状态");
                            String shjly = row.getString("数据来源");
                            String bzh = row.getString("备注");

                            String mark_name;
                            Mark_Util mu = new Mark_Util();
                            mu.p_item = type_item;
                            mu.p_type = ti_t_Util.getType(type_item);
                            StartAct startAct = (StartAct) context;
                            if(" ".equals(tzh) || "".equals(tzh) || tzh == null) {
                                if(mu.getMark(2, fshw) != null)
                                    mark_name = mu.getMark(2, fshw);
                                else
                                    mark_name = "1.dwg";
                            } else if(" ".equals(fshw) || "".equals(fshw) || fshw == null) {
                                if(mu.getMark(1, tzh) != null)
                                    mark_name = mu.getMark(1, tzh);
                                else
                                    mark_name = "1.dwg";
                            } else {
                                mark_name = "1.dwg";
                            }
                            Log.i("导入--mark_name",mark_name);
                            Log.i("导入--特征--附属物",tzh + "--" + fshw);
                            startAct.copyAssetAndWrite(mark_name);
                            File block = new File(startAct.getCacheDir(), mark_name);
                            File af = block.getAbsoluteFile();
                            String s1 = af.getPath();

                            long[] color = StartAct.getColor(type);
                            MxLibDraw.setDrawColor(color);

                            String pre = TypeItemUtil.getPre(type);
                            MxLibDraw.setLayerName(pre + "POINT");
                            MxLibDraw.setLineType("point");
                            MxLibDraw.insertBlock(s1, code);

                            long bid = MxLibDraw.drawBlockReference(Double.parseDouble(x),Double.parseDouble(y), code, 0.5, 0);
                            if(bid != 0) {
                                McDbBlockReference blkRef = (McDbBlockReference) MxFunction.objectIdToObject(bid);
                                McGePoint3d pos = blkRef.position();
                                pos.z = Double.parseDouble(dmgch);
                                blkRef.setPosition(pos);
                                blkRef.setDrawOrder(9);
                            }
                            Log.i("写入点···","" + bid);

                            MxLibDraw.setLayerName(pre + "TEXT");
                            MxLibDraw.setLineType("text");
                            long tid = MxLibDraw.drawText(Double.parseDouble(x),Double.parseDouble(y) + 0.4,1,code);
                            McDbText text = new McDbText(tid);
                            text.setDrawOrder(1);

                            MxFunction.setxDataString(bid, "code", code);
                            MxFunction.setxDataString(bid, "x", x);
                            MxFunction.setxDataString(bid, "y", y);
                            MxFunction.setxDataString(bid, "type", type);
                            MxFunction.setxDataString(bid, "type_item", type_item);
                            MxFunction.setxDataString(bid, "tezheng", tzh);
                            MxFunction.setxDataString(bid, "fushuwu", fshw);
                            MxFunction.setxDataString(bid, "jdmsh", jdmsh);
                            MxFunction.setxDataString(bid, "jgxzh", jgxzh);
                            MxFunction.setxDataString(bid, "jgchc", jgchc);
                            MxFunction.setxDataString(bid, "jgczh", jgczh);
                            MxFunction.setxDataString(bid, "jgzht", jgzht);
                            MxFunction.setxDataString(bid, "jczh", jczh);
                            MxFunction.setxDataString(bid, "jchc", jchc);
                            MxFunction.setxDataString(bid, "shyzht", shyzht);
                            MxFunction.setxDataString(bid, "dmgch", dmgch);
                            MxFunction.setxDataString(bid, "shjly", shjly);
                            MxFunction.setxDataString(bid, "bzh", bzh);

                            MxFunction.writeFile(MxFunction.currentFileName());//保存文件-保存数据

                        }
                    } else if (s.endsWith("LINE")) {
                        for (Row row : table) {
                            String scode = row.getString("起点点号");
                            String ecode = row.getString("连接方向");
                            if(scode == null || ecode == null){
                                continue;
                            }
                            String type  = null;
                            for (Map.Entry entry : lm.entrySet()) {
                                if (s.equals(entry.getValue())) {
                                   type = entry.getKey().toString();
                                   Log.i("导入-线-类型",type);
                                }
                            }
                            String lt = ti_t_Util.getType(type);//根据管线类别获取大类名称；
                            String pr = TypeItemUtil.getPre(type);
                            String mshfsh = row.getString("埋设类型");
                            String qdmsh = String.valueOf(row.getFloat("起点埋深"));
                            String zhdmsh = String.valueOf(row.getFloat("终点埋深"));
                            String lx = row.getString("流向");
                            String gj = row.getString("管径");
                            String gj1,gj2;
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
                            String czh = row.getString("材质");
                            String tsh = row.getString("电缆条数");
                            String zksh = row.getString("总孔数");
                            String yyksh = row.getString("已用孔数");
                            String yl = row.getString("电压压力");
                            String dlmch = row.getString("道路名称");
                            if(type != null){
                                Table ptable = db.getTable(pm.get(type));
                                double sx = 0.0;
                                double sy = 0.0;
                                double ex = 0.0;
                                double ey = 0.0;
                                for(Row r : ptable){
                                    if(r.getString("物探点号").equals(scode)) {
                                        sx = Double.parseDouble(String.valueOf(r.get("Y")));
                                        sy = Double.parseDouble(String.valueOf(r.get("X")));
                                    }
                                    if(r.getString("物探点号").equals(ecode)){
                                        ex = Double.parseDouble(String.valueOf(r.get("Y")));
                                        ey = Double.parseDouble(String.valueOf(r.get("X")));
                                    }
                                }
                                if(sx != 0.0 && sy != 0.0 && ex != 0.0 && ey!=0.0 ){
                                    MxLibDraw.setLayerName(pr + "LINE");
                                    MxLibDraw.setDrawColor(StartAct.getColor(lt));
                                    MxLibDraw.setLineType("line");
                                    long lid = MxLibDraw.drawLine(sx, sy, ex, ey);
                                    Log.i("写入线···","" + lid);
                                    MxFunction.setxDataString(lid, "scode", scode);
                                    MxFunction.setxDataString(lid, "ecode", ecode);
                                    MxFunction.setxDataString(lid, "type", type);
                                    MxFunction.setxDataString(lid, "qdmsh", qdmsh);
                                    MxFunction.setxDataString(lid, "zhdmsh",zhdmsh);
                                    MxFunction.setxDataString(lid, "mshfsh", mshfsh);
                                    MxFunction.setxDataString(lid, "gj1", gj1);
                                    MxFunction.setxDataString(lid, "gj2", gj2);
                                    MxFunction.setxDataString(lid, "dlmch", dlmch);
                                    MxFunction.setxDataString(lid, "czh", czh);
                                    MxFunction.setxDataString(lid, "lx", lx);
                                    MxFunction.setxDataString(lid, "tsh", tsh);
                                    MxFunction.setxDataString(lid, "zksh", zksh);
                                    MxFunction.setxDataString(lid, "yyksh", yyksh);
                                    MxFunction.setxDataString(lid, "yl", yl);

                                    MxFunction.writeFile(MxFunction.currentFileName());//保存文件-保存数据
                                }
                            }
                        }
                    }
                }
                Toast.makeText(context, "导入完毕！", Toast.LENGTH_SHORT).show();
             } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "发生错误，导入终止！", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            Toast.makeText(context, "选择的文件格式不正确！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    static String getRealPathFromURI(Uri contentUri,Context context) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /*
    * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
    */
    @SuppressLint("NewApi")
    static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /*
          * @param uri The Uri to check.
           * @return Whether the Uri authority is ExternalStorageProvider.
         */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}
