package com.mxpipe.lih.mxpipe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainAct extends AppCompatActivity {

    SharedPreferences sp = null;

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_main2);
    }

    public void user(View v){
        Intent myIntent = new Intent(getApplicationContext(), User.class);
        startActivity(myIntent);
    }

    public void open(View v) {
        sp = this.getSharedPreferences("userInfo", MODE_PRIVATE);
        String tno = sp.getString("tno", null);
        String pno = sp.getString("pno", null);
        String area = sp.getString("area", null);
        Log.i("tno+pno+area",("".equals(tno)?"1":"2") + ("".equals(pno)?"1":"2")+ ("".equals(area)?"1":"2"));
        if (tno == null || pno == null || area == null || "".equals(tno) || "".equals(pno) || "".equals(area)) {
            Toast.makeText(MainAct.this,"还未保存作业信息，请先保存作业信息！",Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getApplicationContext(), User.class);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(getApplicationContext(), StartAct.class);
            startActivityForResult(myIntent, 0);
        }
    }

    public void Data(View v){
        Intent myIntent = new Intent(getApplicationContext(), Areas.class);
        startActivity(myIntent);
    }

    public void Nums(View v){
        Intent myIntent = new Intent(getApplicationContext(), Numbers.class);
        startActivity(myIntent);
    }

    public void Ocr(View v){
        Intent myIntent = new Intent(getApplicationContext(), OcrActivity.class);
        startActivity(myIntent);
    }

//    public void Upload(View v){
//        Intent intent = new Intent(getApplicationContext(),UploadAct.class);
//        startActivity(intent);
//    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainAct.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        }else {
            System.exit(0);
        }
    }

    public void map(View view) {

    }
}
