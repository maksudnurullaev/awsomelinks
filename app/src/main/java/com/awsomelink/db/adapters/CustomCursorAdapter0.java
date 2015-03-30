package com.awsomelink.db.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.awsomelink.R;

/**
 * Created by m.nurullayev on 30.03.2015.
 */
public class CustomCursorAdapter0 extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public CustomCursorAdapter0(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.contacts_fragment_item,parent,false);
    }
}
