package com.awsomelink.db.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.awsomelink.R;
import com.awsomelink.base.LinkItemAction;
import com.awsomelink.utils.Links;
import com.awsomelink.utils.MetaFile;
import com.awsomelink.utils.MetaItem;

import java.io.File;
import java.util.List;

/**
 * Created by m.nurullayev on 21.04.2015.
 */
public class LinkFilesAdapter extends ArrayAdapter<String> {
    public static final String TAG = "LinkFilesAdapter";

    Context mContext = null;
    Fragment mFragment = null;
    Links.LINK_TYPE mItemsType = null;
    String mLinkId = null;

    public LinkFilesAdapter(Context context, int resourceId, Fragment fragment, Links.LINK_TYPE itemsType, String linkId){
        super(context,resourceId);
        this.mItemsType = itemsType;
        addAll(Links.getLinkFiles(context, itemsType, linkId));
        mContext = context;
        mFragment = fragment;
        mLinkId = linkId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String fileName = getItem(position);
        if( convertView == null ){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.file_row,parent,false);
        }
        // set ID as title text
        TextView tcFileName = (TextView)convertView.findViewById(R.id.textViewFileName);
        tcFileName.setText(fileName);
        // set description text
        TextView tvDesc = (TextView)convertView.findViewById(R.id.textDesc);
        tvDesc.setText(R.string.empty);
        // set buttons
        setUpButtonActions(convertView, fileName, R.id.button_more, Links.LINK_ACTION.MORE, mItemsType);
        setUpButtonActions(convertView, fileName, R.id.button_delete, Links.LINK_ACTION.DELETE, mItemsType);
        return convertView;
    }

    private void setUpButtonActions(View parentItemRow,String id,int viewId, Links.LINK_ACTION linkItemAction, Links.LINK_TYPE linkItemType){
        setUpButtonActions(parentItemRow,id,viewId,linkItemAction,linkItemType,null);
    }

    private void setUpButtonActions(View parentItemRow,String id,int viewId, Links.LINK_ACTION linkItemAction, Links.LINK_TYPE linkItemType, String text){
        Button view = (Button)parentItemRow.findViewById(viewId);
        if( view != null && mFragment != null && mFragment instanceof View.OnClickListener){
            view.setOnClickListener((View.OnClickListener) mFragment);
            view.setTag(new LinkItemAction(id, linkItemAction, linkItemType));
            if( text != null ){
                view.setText(text);
            }
        }
    }
}
