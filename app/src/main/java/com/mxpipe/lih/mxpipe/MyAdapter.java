package com.mxpipe.lih.mxpipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/*
 * Created by Administrator on 2017/11/2.
 */

public class MyAdapter extends ArrayAdapter {

    private List<String> ss;
    private Context mcontext;

    MyAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
        ss = objects;
        mcontext = context;
    }

    @Override
    public int getCount() {
        return ss.size();
    }

    @Override
    public Object getItem(int position) {
        return ss.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.child,parent,false);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.ctv);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(ss.get(position));
        return convertView;
    }

    class ViewHolder{
        CheckBox tv;
    }

}
