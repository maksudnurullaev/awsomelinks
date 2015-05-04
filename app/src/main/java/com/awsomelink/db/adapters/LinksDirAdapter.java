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
public class LinksDirAdapter extends ArrayAdapter<String> {
    public static final String TAG = "LinksDirAdapter";

    Context mContext = null;
    Fragment mFragment = null;
    Links.ITEM_TYPE mItemsType = null;

    public LinksDirAdapter(Context context, int resourceId, Fragment fragment, Links.ITEM_TYPE itemsType){
        super(context,resourceId);
        this.mItemsType = itemsType;
        addAll(Links.getLinkIDs(context, itemsType));
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
        // set description text
        TextView tvDesc = (TextView)convertView.findViewById(R.id.textDesc);
        File metaFile =  Links.getFolderLinkFile(mContext, mItemsType, id, MetaFile.FILE_NAME);
        if( metaFile.exists() ) {
            List<MetaItem> metaItems = MetaFile.getMeta(metaFile);
            if( metaItems != null && metaItems.size() > 0 ){
                tvDesc.setText(MetaFile.getMetaDescription(mContext, metaItems));
                if (MetaFile.isAWSynchonized(mContext, mItemsType, id)) {
                    setUpButtonActions(convertView, id, R.id.button_share, Links.ITEM_ACTION.SHARE, mItemsType);
                } else {
                    setUpButtonActions(convertView, id, R.id.button_share, Links.ITEM_ACTION.AWSYNC, mItemsType, mContext.getString(R.string.Synchronize));
                }
                setButtonsVisibility(convertView, View.VISIBLE, R.id.button_more, R.id.button_share);
            } else {
                tvDesc.setText(R.string.empty);
                setButtonsVisibility(convertView, View.VISIBLE, R.id.button_more);
                setButtonsVisibility(convertView, View.INVISIBLE, R.id.button_share);
            }
            setUpButtonActions(convertView, id, R.id.button_more, Links.ITEM_ACTION.MORE, mItemsType);
        } else {
            tvDesc.setText(R.string.Error);
            setButtonsVisibility(convertView, View.INVISIBLE, R.id.button_more, R.id.button_share);
        }
        setUpButtonActions(convertView, id, R.id.button_delete, Links.ITEM_ACTION.DELETE, mItemsType);
        return convertView;
    }

    private void setButtonsVisibility(View parentItemRow, int state, int... ids){
        for( int id : ids){
            Button view = (Button)parentItemRow.findViewById(id);
            view.setVisibility(state);
        }
    }

    private void setUpButtonActions(View parentItemRow,String id,int viewId, Links.ITEM_ACTION linkItemAction, Links.ITEM_TYPE linkItemType){
        setUpButtonActions(parentItemRow,id,viewId,linkItemAction,linkItemType,null);
    }

    private void setUpButtonActions(View parentItemRow,String id,int viewId, Links.ITEM_ACTION linkItemAction, Links.ITEM_TYPE linkItemType, String text){
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
