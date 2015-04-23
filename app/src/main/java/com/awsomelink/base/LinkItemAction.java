package com.awsomelink.base;

import com.awsomelink.utils.Links;

/**
 * Created by m.nurullayev on 21.04.2015.
 */
public class LinkItemAction {
    public String mID;
    public Links.ITEM_ACTION mLinkAction;
    public Links.ITEM_TYPE mItemType;
    public String mFileName;

    public LinkItemAction(String id){
        this.mID = id;
    }

    public LinkItemAction(String id, Links.ITEM_ACTION linkAction, Links.ITEM_TYPE dirType){
        this.mID = id;
        this.mLinkAction = linkAction;
        this.mItemType = dirType;

    }
}
