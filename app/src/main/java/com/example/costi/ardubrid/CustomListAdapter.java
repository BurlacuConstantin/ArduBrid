package com.example.costi.ardubrid;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<PaternModel> implements View.OnClickListener
{
    Context context;
    private ArrayList<PaternModel> asset;

    private int lastPosition = -1;

    private static class ViewHolder
    {
        TextView txtTitle;
        TextView txtSubtitle1;
        TextView txtSubtitle2;
        ImageView ico;
    }


    public CustomListAdapter(Context context, ArrayList<PaternModel> data)
    {
        super(context, R.layout.custom_listview, data);

        this.asset = data;
        this.context = context;
    }

    @Override
    public void onClick(View v)
    {
        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        PaternModel model = (PaternModel) obj;

        switch (v.getId())
        {
            case R.id.icon1:
            {
                break;
            }
            case R.id.title:
            {
                break;
            }
            case R.id.subtitle1:
            {
                break;
            }
            case R.id.subtitle2:
            {
                break;
            }
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PaternModel model = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.txtSubtitle1 = (TextView) convertView.findViewById(R.id.subtitle1);
            viewHolder.txtSubtitle2 = (TextView) convertView.findViewById(R.id.subtitle2);
            viewHolder.ico = (ImageView) convertView.findViewById(R.id.icon1);

            result = convertView;

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.txtTitle.setText(model.getTitle());
        viewHolder.txtSubtitle1.setText(model.getFirstSubtitle());
        viewHolder.txtSubtitle2.setText(model.getSecondSubtitle());
        viewHolder.ico.setImageResource(model.getIconID());

        return convertView;
    }

}
