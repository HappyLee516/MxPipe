/*
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2012 cocos2d-x.org
Copyright (c) 2011      Zynga Inc.
Copyright (c) 2013-2014 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package com.mxpipe.lih.mxpipe;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.MxDraw.McDbArc;
import com.MxDraw.McDbAttribute;
import com.MxDraw.McDbBlockReference;
import com.MxDraw.McDbBlockTableRecord;
import com.MxDraw.McDbCircle;
import com.MxDraw.McDbCurve;
import com.MxDraw.McDbDictionary;
import com.MxDraw.McDbEllipse;
import com.MxDraw.McDbEntity;
import com.MxDraw.McDbLayerTable;
import com.MxDraw.McDbLayerTableRecord;
import com.MxDraw.McDbLine;
import com.MxDraw.McDbMText;
import com.MxDraw.McDbPoint;
import com.MxDraw.McDbPolyline;
import com.MxDraw.McDbSpline;
import com.MxDraw.McDbText;
import com.MxDraw.McDbTextStyleTable;
import com.MxDraw.McDbTextStyleTableRecord;
import com.MxDraw.McDbXrecord;
import com.MxDraw.McGePoint3d;
import com.MxDraw.McGeVector3d;
import com.MxDraw.MrxDbgSelSet;
import com.MxDraw.MrxDbgUiPrPoint;
import com.MxDraw.MrxDbgUtils;
import com.MxDraw.MxDrawActivity;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;
import com.MxDraw.MxResbuf;

import org.cocos2dx.lib.Cocos2dxEditBox;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.ResizeLayout;

import java.util.ArrayList;
import java.util.List;

public class MxCADAppActivity extends MxDrawActivity {




    public MxCADAppActivity() {

        initWorkDir(Environment.getExternalStorageDirectory() + "/"+ "TestMxLib");

    }

    protected  boolean m_isLoadAndroidLayoutUi = false;


    @Override
    public void mcrxEntryPoint(int iCode)
    {
        super.mcrxEntryPoint(iCode);
        if(iCode == kInitAppMsg)
        {
            copyShxFile("aaa.shx");
            MxFunction.setShowFileBrowse(true);
            MxFunction.setShowUpToolBar(true);
            MxFunction.setShowDownToolBar(true);
            MxFunction.setShowReturnButton(true);
            MxFunction.enableSelect(true);
            MxFunction.enableGridEdit(true);
            MxFunction.setToolFile("mxtool.json");
            MxFunction.setMenuFile("mxmenu.json");
            MxFunction.setReadFileContent(ReadContent.kFastRead | ReadContent.kReadObjectsDictionary | ReadContent.kReadXrecord | ReadContent.kReadNamedObjectsDictionary );
            MxFunction.enableUndo();

           //
            //MxFunction.setLanguage(MxFunction.MxSystemLanager.kEn);
//            MxFunction.enablePopToolbar(false);

       }
       else if(iCode == kStartScene )
        {

        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        m_isLoadAndroidLayoutUi = true;

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras  != null)
        {
            String mFile  = extras.getString("file");
            if(!mFile.isEmpty())
            {
                MxFunction.openFile(mFile);
            }
        }
    }

    @Override
    public boolean createInterfaceLayout()
    {
        if(!m_isLoadAndroidLayoutUi)
            return  false;

        setContentView(R.layout.cadglview);

        ResizeLayout  mFrameLayout = (ResizeLayout)this.findViewById(R.id.my_frame);

        Cocos2dxGLSurfaceView mGLSurfaceView = (Cocos2dxGLSurfaceView)this.findViewById(R.id.view_cad);
        Cocos2dxEditBox edittext =  (Cocos2dxEditBox)this.findViewById(R.id.my_edittext);

        initInterfaceLayout(mFrameLayout,edittext,mGLSurfaceView);

        {
            Button btnviewcolor = (Button) findViewById(R.id.zoomE);

            btnviewcolor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    String sPreview = MxFunction.getPreviewFile("sample.dwg");


                    MxFunction.doCommand(6);

                }
            });
        }

        {
            Button btn = (Button) findViewById(R.id.userSelect);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    MxFunction.doThreadCommand(7);

                }
            });
        }

        {
            Button btn = (Button) findViewById(R.id.undo);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                   MxFunction.sendStringToExecute("Mx_Undo");
                   //MxFunction.sendStringToExecute("Mx_Color");

                }
            });
        }

        {
            Button btn = (Button) findViewById(R.id.getAllLayer);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    MxFunction.doCommand(11);

                }
            });
        }

        {
            Button btn = (Button) findViewById(R.id.DrawText);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    MxFunction.doThreadCommand(12);

                }
            });
        }
        {
            Button btn = (Button) findViewById(R.id.Test);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    MxFunction.doThreadCommand(21);

                }
            });
        }





        return true;
    }

    public void TestMxDraw()
    {
        Log.e("currentFileName",MxFunction.currentFileName());
    }

    @Override
    public void openComplete(boolean isOpenSucces) {

        String sT;
        sT = String.format("openComplete:%d",isOpenSucces ? 1 : 0);

        Log.e("openComplete",sT);

        /*
        double ret[] =MxFunction.getMcDbDatabaseBound();
        if(ret != null)
        {
                double dLBx = ret[0];
                double dLBy = ret[1];
                double dRTx = ret[2];
                double dRTy = ret[3];


                sT = String.format("dLB:%f,%f,dRT:%f,%f",dLBx,dLBy,dRTx,dRTy);

                Log.e("getMcDbDatabaseBound",sT);
        }
        else
        {
            Log.e("getMcDbDatabaseBound","error");
        }

        // 得到当前绘图颜色RGB
        int[] ccolor = MxFunction.cecolor();

        sT = String.format("dLB:%d,%d,%d",ccolor[0],ccolor[1],ccolor[2]);

        Log.e("cecolor",sT);
        */

        //MxFunction.doCommand(7);

        //McDbDatabase data = MxFunction.getCurrentDatabase();

       // MxFunction.zoomCenter(10,10);
        //MxFunction.writeFile()
    }

    @Override
    public void initComplete()
    {
        Log.e("initComplete","");
    }


    @Override
    public void selectModified(long lId)
    {
        if(lId != 0)
        {

            String sT;
            sT = String.format(" lId:%d",lId);
            Log.e("selectModified",sT);


            McDbEntity ent = new McDbEntity (lId);

            String sXdata = MxFunction.getxDataString(lId,"PE_URL");


            // 得到对象的层名.
            Log.e("LayerName",ent.layerName());

            String sName =  MxFunction.getTypeName(lId);

            if(sName.equals("McDbLine"))
            {
                McDbLine line = new McDbLine(lId);

                McGePoint3d sPt = line.getStartPoint();
                McGePoint3d ePt = line.getEndPoint();

                String sT1;
                sT1 = String.format("sPt:%f,%f,%f,ePt:%f,%f,%f",sPt.x,sPt.y,sPt.z,ePt.x,ePt.y,ePt.z);

                Log.e("Linedata",sT1);
            }
            else if(sName.equals("McDbSpline"))
            {
                McDbSpline spline = new McDbSpline(lId);
                int[] degree = new int[1];
                double[] fitTolerance = new double[1];
                McGePoint3d[] aryFit = spline.getFitData(degree,fitTolerance);

                if(aryFit != null)
                {
                    for(int i = 0; i < aryFit.length;i++)
                    {
                        String sT1;
                        sT1 = String.format("%f,%f,%f",aryFit[i].x,aryFit[i].y,aryFit[i].z);

                        Log.e("McDbSpline aryFit",sT1);
                    }

                }


                boolean[] rational = new boolean[1];
                boolean[] closed = new boolean[1];
                boolean[] periodic = new boolean[1];
                List<Double> knots = new ArrayList<Double>();
                List<Double> weights = new ArrayList<Double>();
                double[] controlPtTol = new double[1];
                double[] knotTol = new double[1];

                McGePoint3d[] aryControlPt =  spline.getNurbsData(degree,rational,closed,periodic,knots,weights,controlPtTol,knotTol);

                if(aryControlPt != null)
                {
                    for(int i = 0; i < aryControlPt.length;i++)
                    {
                        String sT1;
                        sT1 = String.format("%f,%f,%f",aryControlPt[i].x,aryControlPt[i].y,aryControlPt[i].z);

                        Log.e("McDbSpline aryControlPt",sT1);
                    }

                }

                //spline.setFitData(aryFit,degree[0],fitTolerance[0]);
            }
            if(sName.equals("McDbArc"))
            {
                McDbArc arc = new McDbArc(lId);

                McGePoint3d cen = arc.getCenter();
                double dR = arc.getRadius();
                double dSA = arc.getStartAngle();
                double dEA = arc.getEndAngle();

                String sT1;
                sT1 = String.format("cen:%f,%f,%f,dR:%f,dSA:%f,dEA:%f",cen.x,cen.y,cen.z,
                        dR,dSA,dEA);

                Log.e("McDbArc",sT1);

                // 得到圆弧的开始点，和结束点.
                McGePoint3d sPt = arc.getStartPoint();
                McGePoint3d ePt = arc.getEndPoint();

                String sT2;
                sT2 = String.format("sPt:%f,%f,%f,ePt:%f,%f,%f",sPt.x,sPt.y,sPt.z,ePt.x,ePt.y,ePt.z);

                Log.e("McDbArc points:",sT2);

            }
            else if(sName.equals("McDbBlockReference"))
            {
                McDbBlockReference blkRef = new McDbBlockReference(lId);
                McDbBlockTableRecord blkRec = new McDbBlockTableRecord( blkRef.blockTableRecord());
                Log.e("BlkName:",blkRec.getName());

                long[] allAtt = blkRef.getAllAttribute();
                if(allAtt != null) {
                    for (int i = 0; i < allAtt.length; i++) {
                        McDbAttribute att = new McDbAttribute(allAtt[i]);
                        Log.e("tagConst:", att.tagConst());
                        Log.e("textString:", att.textString());

                    }
                }

            }
            else if(sName.equals("McDbSpline"))
            {
                // 把样条线离线成一堆的点返回
                McDbCurve curve = new McDbCurve(lId);
               McGePoint3d[] ponts =  curve.getSamplePoints(0.1);

                if(ponts != null) {
                    for (int i = 0; i < ponts.length; i++) {
                        McGePoint3d pt = ponts[i];
                        String sT2;
                        sT2 = String.format("pt:%f,%f,%f", pt.x, pt.y, pt.z);

                        Log.e("McDbSpline pt:", sT2);
                    }
                }

            }
            else if(sName.equals("McDbPolyline"))
            {
                McDbPolyline pl = new McDbPolyline(lId);
                double dA = pl.getArea();

                String sA;
                sA = String.format("Area:%f",dA);

                Log.e("McDbPolyline Area:", sA);

                for (int i = 0; i < pl.numVerts(); i++) {
                    McGePoint3d pt = pl.getPointAt(i);
                    double dBulge = pl.getBulgeAt(i);

                    String sT2;
                    sT2 = String.format("pt:%f,%f,%f,dBulge:%f", pt.x, pt.y, pt.z,dBulge);

                    Log.e("McDbPolyline Point:", sT2);

                    if(dBulge > 0.001)
                    {
                        McGePoint3d pt2;

                        if(i ==  pl.numVerts() - 1)
                        {
                            pt2 = pl.getPointAt(0);
                        }
                        else
                        {
                            pt2 = pl.getPointAt(i + 1);
                        }

                        double[] arc = MxFunction.calcArc(pt.x,pt.y,pt2.x,pt2.y,dBulge);
                        if(arc != null)
                        {
                            String sTem = String.format("cen:%f,%f,dR:%f,dS:%f,dE:%f", arc[0],arc[1],arc[2],arc[3],arc[4]);

                            Log.e("Arc:", sTem);
                        }
                    }
                }
            }

        }

    }


    @Override
    public void commandEvent(int iCommand)
    {
        if(iCommand == 1) {
            //MxFunction.zoomAll();
            //MxFunction.sendStringToExecute("MT_TestTip");\




            String sFileName = MxFunction.getWorkDir() + "/TestWirte.dwg";
            MxFunction.writeFile(sFileName);
        }
        else if(iCommand == 2)
        {
            String sFileName = MxFunction.getWorkDir() + "/总图.dwg";
            MxFunction.openFileEx(sFileName,ReadContent.kReadxData);
        }
        else if(iCommand == 3)
        {
            MxFunction.openFile("");
        }
        else if(iCommand == 4)
        {
            MrxDbgSelSet ss = new MrxDbgSelSet();
            ss.allSelect();
            for(int i = 0; i <ss.size();i++)
            {
                long lId = ss.at(i);

                McDbEntity ent = new McDbEntity (lId);

                // 得到对象的层名.
                Log.e("LayerName",ent.layerName());

                String sName =  MxFunction.getTypeName(lId);

                if(sName.equals("McDbLine"))
                {
                    McDbLine line = new McDbLine(ss.at(i));

                    McGePoint3d sPt = line.getStartPoint();
                    McGePoint3d ePt = line.getEndPoint();

                    String sT;
                    sT = String.format("sPt:%f,%f,%f,ePt:%f,%f,%f",sPt.x,sPt.y,sPt.z,ePt.x,ePt.y,ePt.z);

                    Log.e("Linedata",sT);
                }
                else if(sName.equals("McDbCircle"))
                {
                    McDbCircle cir = new McDbCircle(ss.at(i));

                    McGePoint3d cen = cir.getCenter();
                    double fR = cir.getRadius();

                    String sT;
                    sT = String.format("cen:%f,%f,r:%f",cen.x,cen.y,fR);

                    Log.e("Circledata",sT);
                }

                else if(sName.equals("McDbPoint"))
                {
                    McDbPoint point = new McDbPoint(ss.at(i));

                    McGePoint3d pos = point.position();


                    String sT;
                    sT = String.format("Point pos:%f,%f",pos.x,pos.y);

                    Log.e("McDbPoint",sT);
                }

                else if(sName.equals("McDbText"))
                {
                    McDbText txt = new McDbText(ss.at(i));

                    McGePoint3d pos = txt.position();

                    String sTxt = txt.textString();
                    double dH = txt.height();

                    String sT;
                    sT = String.format(" pos:%f,%f,Txt:%s,H:%f",pos.x,pos.y,sTxt,dH);

                    Log.e("McDbText",sT);
                }

                else if(sName.equals("McDbMText"))
                {
                    McDbMText txt = new McDbMText(ss.at(i));

                    McGePoint3d pos = txt.location();

                    String sTxt = txt.contents();
                    double dH = txt.textHeight();

                    String sT;
                    sT = String.format(" pos:%f,%f,Txt:%s,H:%f",pos.x,pos.y,sTxt,dH);

                    Log.e("McDbMText",sT);
                }

                else if(sName.equals("McDbEllipse"))
                {
                    McDbEllipse ellipse = new McDbEllipse(ss.at(i));
                    McGePoint3d cen = ellipse.center();
                    McGeVector3d major = ellipse.majorAxis();
                    double radius = ellipse.radiusRatio();
                    double sang = ellipse.startAngle();
                    double eang = ellipse.endAngle();

                    String sT;
                    sT = String.format(" cen:%f,%f,major:%f,%f,radius:%f,sang:%f,eang:%f",cen.x,cen.y,major.x,major.y,radius,sang,eang);

                    Log.e("McDbEllipse",sT);

                }

                else if(sName.equals("McDbBlockReference"))
                {
                    McDbBlockReference blkRef = new McDbBlockReference(lId);
                    McDbBlockTableRecord blkRec = new McDbBlockTableRecord( blkRef.blockTableRecord());
                    Log.e("BlkName:",blkRec.getName());

                    long[] allAtt = blkRef.getAllAttribute();
                    if(allAtt != null) {
                        for (int j = 0; j < allAtt.length; j++) {
                            McDbAttribute att = new McDbAttribute(allAtt[j]);
                            Log.e("tagConst:", att.tagConst());
                            Log.e("textString:", att.textString());

                            //att.setTextString("zzzzzzzzzzzz");
                        }
                    }
                   // blkRef.assertWriteEnabled();

                }
            }
        }
        else if(iCommand == 5)
        {
            //long lImageId = MxFunction.drawImage("start.png",100,100,1);

            MxFunction.drawImageMarkEx("tag.png",100,100,0.5,MxFunction.ImageAttachment.kBottomCenter);
            MxLibDraw.drawLine(100,100,200,300);


            MxFunction.zoomAll();
        }
        else if(iCommand == 6)
        {

            MxFunction.zoomAll();
        }
        else if(iCommand == 7)
        {

            MrxDbgSelSet ss = new MrxDbgSelSet();

            // 与用户交到在图上选择对象。
            ss.userSelect();
            String sT;
            sT = String.format("size:%d",ss.size());



            // 提示选择的对象.
            Log.e("userSelect",sT);
        }
        else  if(iCommand == 8)
        {
            MxFunction.openFile("");

            // 设置画图颜色.
            long[] rgb = new long[3];
            rgb[0] = 255;
            rgb[1] = 0;
            rgb[2] = 0;

            MxLibDraw.setDrawColor(rgb);

            MxLibDraw.setLineWidth(10);

            MxLibDraw.drawLine(10,10,200,300);

            MxLibDraw.addLinetype("MyLine","20,-10",1);

            MxLibDraw.setLineType("MyLine");
            MxLibDraw.setLineWidth(5);

            long lId = MxLibDraw.drawLine(10,300,200,10);

            // 测试求最近点函数。
            McDbCurve curve = new McDbCurve(lId);
            McGePoint3d pt = new McGePoint3d(100,100,0);
            McGePoint3d onPt =  curve.getClosestPointTo(pt);
            if(onPt != null)
            {
                // 得到最近点.
                String sT;
                sT = String.format("onPt:%f,%f,%f",onPt.x,onPt.y,onPt.z);

                Log.e("onPt",sT);
            }

            MxFunction.zoomAll();
        }
        else if(iCommand == 9)
        {
            // 交互绘直线.
            MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
            if(getPoint.go() != MrxDbgUiPrPoint.Status.kOk)
            {
                return;
            }

            McGePoint3d pt = getPoint.value();

            String sT;
            sT = String.format("pt:%f,%f,%f",pt.x,pt.y,pt.z);

            MrxDbgUiPrPoint getPoint2 = new MrxDbgUiPrPoint();
            getPoint2.setBasePt(pt);
            getPoint2.setUseBasePt(true);
            if(getPoint2.go() != MrxDbgUiPrPoint.Status.kOk)
            {
                return;
            }

            McGePoint3d pt2 = getPoint2.value();


            MxLibDraw.drawLine(pt.x,pt.y,pt2.x,pt2.y);

            Log.e("getPoint",sT);
        }

        else if(iCommand == 10)
        {
            long lId = MrxDbgUtils.selectEnt("点击选择对象:");
            if(lId != 0)
            {
                String sT;
                sT = String.format("selectEnt lId:%d",lId);
                Log.e("selectEnt",sT);

                McDbEntity ent = new McDbEntity (lId);

                // 得到对象的层名.
                String sLayer = ent.layerName();
                Log.e("LayerName",sLayer);

               MxResbuf xdata = ent.xData("");
               if(xdata != null) {

                   long lCount = xdata.getCount();

                   xdata.print();
               }

               long lDictId = ent.extensionDictionary();
                printDictionary(lDictId);




               // final long lEraseId = lId;
              //  this.runOnGLThread(new Runnable() {
              //          @Override
                //        public void run() {
               //             MxFunction.deleteObject(lEraseId);
              //           }
               //     });


            }

        }
        else if(iCommand == 11)
        {
            long[] ids = MxFunction.getAllLayer();
            if(ids ==null)
                return;

            for(int i = 0; i < ids.length;i++)
            {
                McDbLayerTableRecord layer = new McDbLayerTableRecord(ids[i]);
                String sName = layer.getName();
                Log.e("LayerName:",sName);

                //layer.setIsOff(true);
            }
        }

        else if(iCommand == 12)
        {
            MrxDbgUiPrPoint getPoint = new MrxDbgUiPrPoint();
            getPoint.setMessage("点取文字插入点");
            if(getPoint.go() != MrxDbgUiPrPoint.Status.kOk)
            {
                return;
            }

            McGePoint3d pt = getPoint.value();


            MxLibDraw.drawText(pt.x,pt.y,500,"测试Test");


        }

        else if(iCommand == 13)
        {
            MxFunction.openFile("");

            MxLibDraw.drawCircle(10,10,100);

            MxLibDraw.drawArc(10,10,200,0,45 * 3.14159265/ 180.0);
            MxFunction.zoomAll();
        }
        else if(iCommand == 14)
        {
            MxFunction.openFile("");
            MxLibDraw.pathMoveTo(10,10);
            MxLibDraw.pathLineTo(10,20);
            //MxLibDraw.pathLineToEx(20,20,2,1,0.1);
            MxLibDraw.pathLineTo(20,20);
            MxLibDraw.pathLineTo(20,10);

            MxLibDraw.drawPathToPolyline();
            MxFunction.zoomAll();
        }
        else if(iCommand == 15)
        {
            MxFunction.openFile("");
            MxLibDraw.pathMoveTo(10,10);
            MxLibDraw.pathLineTo(10,20);
            //MxLibDraw.pathLineToEx(20,20,2,1,0.1);
            MxLibDraw.pathLineTo(20,20);
            MxLibDraw.pathLineTo(20,10);

            MxLibDraw.drawPathToSpline();
            MxFunction.zoomAll();
        }
        else if(iCommand == 16)
        {
            printDictionary(MxFunction.getNamedObjectsDictionary());
        }
        else if(iCommand == 17)
        {
            MxFunction.newFile();

            Log.e("isModifyed",MxFunction.isModifyed() ? "Y" : "N");

            //String sFileName = MxFunction.getWorkDir() + "/tree.dwg";
            String sFileName = MxFunction.getWorkDir() + "/tk.dwg";
            String sBlkName = "tree";
            MxLibDraw.insertBlock(sFileName,sBlkName);

            // drawBlockReference(double dPosX, double dPosY, String pszBlkName, double dScale, double dAng);
            MxLibDraw.drawBlockReference(0,0,"ZZCSTK_A3标准图框",1,0);

            MxFunction.zoomAll();

            Log.e("isModifyed",MxFunction.isModifyed() ? "Y" : "N");
        }
        else if(iCommand == 18)
        {
            // long  drawEllipse(double dCenterX, double dCenterY, double dMajorAxisX, double dMajorAxisY, double dRadiusRatio);
            MxFunction.newFile();
            MxLibDraw.drawEllipse(0,0,100,100,0.5);

            MxLibDraw.drawEllipseArc(200,0,100,100,0.5,15 * 3.14159265 / 180,90 * 3.14159265 / 180);


            MxFunction.zoomAll();
        }

        else if(iCommand == 19)
        {
            // 得到所有图层名称
            McDbLayerTable layerTable =  MxFunction.getCurrentDatabase().getLayerTable();
            long[] allLayerId = layerTable.getAll();
            for(int i = 0; i < allLayerId.length;i++)
            {
                McDbLayerTableRecord laryRec = new McDbLayerTableRecord(allLayerId[i]);

                Log.e("LayerName:",laryRec.getName());


            }
        }
        else if(iCommand == 20)
        {
            // 得到所有文字样式名称
            McDbTextStyleTable txtstyleTable =  MxFunction.getCurrentDatabase().getTextstyle();
            long[] allId = txtstyleTable.getAll();
            for(int i = 0; i < allId.length;i++)
            {
                McDbTextStyleTableRecord textStyleRecord = new McDbTextStyleTableRecord(allId[i]);

                Log.e("textStyleRecord:",textStyleRecord.getName());

                Log.e("fileName:",textStyleRecord.fileName());
                Log.e("bigFontFileName:",textStyleRecord.bigFontFileName());

            }
        }
        else if(iCommand == 55)
        {
            Log.e("custom toolbar:","55");
        }
        else if(iCommand == 65)
        {
            Log.e("custom toolbar:","65");
        }
        else if(iCommand == 21)
        {
            TestMxDraw();
        }
    }

    public void printDictionary(long lId)
    {
        if(lId==0)
            return;
        McDbDictionary dict = new McDbDictionary(lId);

        long[] all = dict.getAll();
        if(all == null)
            return;

        for(int i = 0; i <all.length;i++)
        {
            String sType = MxFunction.getTypeName(all[i]);
            String sName = dict.getName(all[i]);
            Log.e("Name",sName);

            if(sType.equals("McDbDictionary"))
            {
                printDictionary(all[i]);
            }
            else if(sType.equals("McDbXrecord"))
            {
                McDbXrecord xRec = new McDbXrecord(all[i]);
                MxResbuf data =  xRec.rbChain();
                if(data != null)
                    data.print();

            }
        }


    }
    @Override
    public  boolean returnStart()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 finish();
            }
        });

        return true;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        char aChar2;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                aChar2 = theString.charAt(x++);
                if (aChar == 'U'
                        && aChar2 == '+'
                        )
                {
                    // Read the xxxx



                        int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                }

                else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    @Override
    public  int touchesEvent(int iType,double dX,double dY)
    {

        double[] ret = MxFunction.docToView(dX,dY);
        String sK;
        sK = String.format("docToView:%f,%f",ret[0],ret[1]);
        Log.e("docToView",sK);

        // 在点击事件上，得到点的对象。

       // if(iType == EventType.kLongPressed)
        if(false)
        {


            String sT;
            sT = String.format("touchesEvent:%f,%f",dX,dY);
            Log.e("touchesEvent",sT);

            long lIdImage  = MxFunction.findEntAtPoint(dX,dY,"IMAGE");
            if(lIdImage != 0)
            {
                String sGetVal = MxFunction.getxDataString(lIdImage,"MyData");

                Log.v("Find MxImage",decodeUnicode(sGetVal));

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        new AlertDialog.Builder(MxCADAppActivity.this).setTitle("系统提示")//设置对话框标题

                                .setMessage("找到一个标记")//设置显示的内容

                                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮



                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                        // TODO Auto-generated method stub



                                    }

                                }).show();//在按键响应事件中显示此对话框


                    }
                }
                );
            }
            else
            {
               // long lId = MxFunction.findEntAtPoint(dX,dY,"TEXT,MTEXT");

                //if(lId != 0)
                {


                    long lImageId = MxFunction.drawImage("start.png",dX,dY,30);

                    //long lImageId = MxFunction.drawImage2("start.png",dX,dY,dX + 1000,dY + 3000);

                    MxFunction.setxDataString(lImageId,"MyData","TestVal中文测试111");

                    String sGetVal = MxFunction.getxDataString(lImageId,"MyData");


                    this.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {


                                               new AlertDialog.Builder(MxCADAppActivity.this).setTitle("系统提示")//设置对话框标题

                                                       .setMessage("在图上绘了一个标记")//设置显示的内容

                                                       .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮



                                                           @Override

                                                           public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                                               // TODO Auto-generated method stub



                                                           }

                                                       }).show();//在按键响应事件中显示此对话框


                                           }
                                       }
                    );
                }
            }


        }
        return 0;
    }

}
