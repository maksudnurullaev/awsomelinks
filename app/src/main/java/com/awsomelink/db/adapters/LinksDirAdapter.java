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

import java.util.List;

/**
 * Created by m.nurullayev on 21.04.2015.
 */
public class LinksDirAdapter extends ArrayAdapter<String> {
    Context mContext = null;
    Fragment mFragment = null;

    public LinksDirAdapter(Context context, int resourceId, Fragment fragment){
        super(context,resourceId);
        addAll(Links.getLinkIDs(context, Links.ITEM_TYPE.OUT_BOX));
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String id = getItem(position);
        if( convertView == null ){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.link_item,parent,false);
        }
        // set ID as title text
        TextView tvID = (TextView)convertView.findViewById(R.id.textID);
        tvID.setText(id);
        TextView tvDesc = (TextView)convertView.findViewById(R.id.textDesc);
        tvDesc.setText(MetaFile.getMetaDescription(mContext,Links.ITEM_TYPE.OUT_BOX, id));
        // List<MetaItem> metas = MetaFile.getMeta(mContext, Links.ITEM_TYPE.OUT_BOX, id);
        // setup proper button click action, TODO should be single and more optimal method of initilazing
        setUpButtonActions(convertView, id, R.id.button_delete, Links.ITEM_ACTION.DELETE, Links.ITEM_TYPE.OUT_BOX);
        setUpButtonActions(convertView, id, R.id.button_more, Links.ITEM_ACTION.MORE, Links.ITEM_TYPE.OUT_BOX);
        setUpButtonActions(convertView, id, R.id.button_share, Links.ITEM_ACTION.SHARE, Links.ITEM_TYPE.OUT_BOX);
        return convertView;
    }

    private void setUpButtonActions(View parentItemRow,String id,int viewId, Links.ITEM_ACTION linkItemAction, Links.ITEM_TYPE linkItemType){
        Button view = (Button)parentItemRow.findViewById(viewId);
        if( view != null && mFragment != null && mFragment instanceof View.OnClickListener){
            view.setOnClickListener((View.OnClickListener) mFragment);
            view.setTag(new LinkItemAction(id, linkItemAction, linkItemType));
        }
    }
}
