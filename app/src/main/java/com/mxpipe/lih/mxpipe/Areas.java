package com.mxpipe.lih.mxpipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Areas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_help);
    }

    public void Back(View v){
        finish();
    }
}
