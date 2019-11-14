package com.mxpipe.lih.mxpipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/*
 *用户信息类，存储相关信息：区域编号、小组编号、个人编号以及导入偏好
 */
public class User extends AppCompatActivity {

    SharedPreferences sp = null;
    EditText tno,pno,area;
    Spinner slike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tno = findViewById(R.id.et_tno);
        pno = findViewById(R.id.et_pno);
        area = findViewById(R.id.et_area);
        slike = findViewById(R.id.s_like);
        area.setTransformationMethod(new Char2UpperCase());

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        tno.setText(sp.getString("tno", null));
        pno.setText(sp.getString("pno", null));
        area.setText(sp.getString("area", null));
        set(slike,getResources().getStringArray(R.array.like),sp.getString("like",null));
    }

    public void back(View v){
        finish();
    }

    public void Ok(View v){
        Editor editor = sp.edit();
        editor.putString("tno", tno.getText().toString());
        editor.putString("pno", pno.getText().toString());
        editor.putString("area", area.getText().toString().toUpperCase());
        editor.putString("like",slike.getSelectedItem().toString());
        editor.apply();
        Toast.makeText(User.this,"保存完毕！",Toast.LENGTH_SHORT).show();
    }

    private void set(Spinner s, String[] data, String value) {
        for (int x = 0; x < data.length; x++) {
            if (data[x].equals(value)) {
                s.setSelection(x);
            }
        }
    }
}
