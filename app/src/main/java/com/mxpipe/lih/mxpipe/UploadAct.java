package com.mxpipe.lih.mxpipe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mxpipe.lih.mxpipe.DaoruUtil.getPath;
import static com.mxpipe.lih.mxpipe.DaoruUtil.getRealPathFromURI;

public class UploadAct extends AppCompatActivity {

    Button add;
    EditText name,content;
    TextView f1,f2;

    Map<String, File> files = new HashMap<String, File>();
    Map<String, String> params = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        this.add = findViewById(R.id.btn_add);
//        this.cancel = findViewById(R.id.btn_cancel);
//        this.go = findViewById(R.id.btn_go);
        this.f1 = findViewById(R.id.tv_file1);
        this.f2 = findViewById(R.id.tv_file2);
        this.name = findViewById(R.id.et_name);
        this.content = findViewById(R.id.et_content);
    }

    public void Back(View v){
        finish();
    }

    public void Add(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            String path = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(getApplicationContext(), uri);
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri, getApplicationContext());
            }
            if(path != null) {
                String name = path.substring(path.lastIndexOf("/")+1);
                if(name.endsWith("dwg") || name.endsWith("DWG") || name.endsWith("mdb") || name.endsWith("MDB")){
                    File file = new File(path);
                    if(files.size() == 0){
                        f1.setText(name);
                        files.put("file1",file);
                    }else if(files.size() == 1){
                        f2.setText(name);
                        files.put("file2",file);
                        add.setEnabled(false);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"选择的文件格式不正确！",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void Cancel(View v){
        if(!TextUtils.isEmpty(f1.getText())){
            f1.setText("");
        }
        if(!TextUtils.isEmpty(f2.getText())){
            f2.setText("");
        }
        files.clear();
        params.clear();
    }

    public void Go(View v){
        if(files.isEmpty()){
            Toast.makeText(getApplicationContext(),"请先选择要上传的文件！",Toast.LENGTH_SHORT).show();
            return;
        }

        params.put("name",TextUtils.isEmpty(name.getText())?"":name.getText().toString());
        params.put("content",TextUtils.isEmpty(content.getText())?"":content.getText().toString());
        String response=null;
        String url = "";
        try {
            response = UploadUtil.post(url, params, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
