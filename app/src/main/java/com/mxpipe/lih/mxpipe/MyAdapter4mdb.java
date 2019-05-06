package com.mxpipe.lih.mxpipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.HashMap;
import java.util.List;

/*
 *Created by LiHuan at 17:21 on 2019/2/27
 */

//新建管种数据适配
public class MyAdapter4mdb extends BaseAdapter {

    // 填充数据的list
    private        List<String>                 list;
//    // 用来控制CheckBox的选中状况
    static HashMap<Integer,Boolean> isSelected;
    // 上下文
    private        Context                  context;
    // 用来导入布局
    private        LayoutInflater           inflater = null;

    // 构造器
    public MyAdapter4mdb(List<String> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate(){
        for(int i=0; i<list.size();i++) {
            getIsSelected().put(i,false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.listitem, null);
            holder.ct = convertView.findViewById(R.id.tv1);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置list中TextView的显示
        holder.ct.setText(list.get(position));
        // 根据isSelected来设置checkbox的选中状况
        holder.ct.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        MyAdapter4mdb.isSelected = isSelected;
    }

    class ViewHolder{
        CheckedTextView ct;
    }

}
