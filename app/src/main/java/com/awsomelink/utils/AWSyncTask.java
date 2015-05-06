package com.awsomelink.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.awsomelink.RefreshableFragment;
import com.awsomelink.base.LinkItemAction;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by m.nurullayev on 28.04.2015.
 */
public class AWSyncTask extends AsyncTask<LinkItemAction, Integer, Void> {
    public static final String TAG = "AWSyncTask";

    private Context mContext;
    private LinkItemAction mLinkItemAction;
    private boolean mResult;
    private RefreshableFragment mRefreshableFragment;

    public AWSyncTask(Context context,RefreshableFragment refreshableFragment){
        this.mContext = context;
        this.mRefreshableFragment = refreshableFragment;
    }

    @Override
    protected Void doInBackground(LinkItemAction... params) {
        mLinkItemAction = params[0];
        File folder = Links.getFolderLink(mContext, mLinkItemAction.mItemType, mLinkItemAction.mID);
        Log.d(TAG, "Do AWSync for " + folder.getPath());
        String requestURL = "https://awsome.link/" + mLinkItemAction.mID + "/upload";
        Log.d(TAG, "Do AWSync URI:" + requestURL);
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "AWSomeLink Andriod");
            multipart.addHeaderField("Link-ID", mLinkItemAction.mID);
            for(File f: folder.listFiles()){
                Log.d(TAG, "... AWSync for " + f.getPath());
                multipart.addFilePart("files", f);
            }
            List<String> response = multipart.finish();
            for (String line : response) {
                Log.d(TAG, "... AWSync response: " + line);
            }
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            mResult = false;
        }
        mResult = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        String metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(mResult));
        MetaFile.setMeta(mContext, Links.LINK_TYPE.OUT,mLinkItemAction.mID,metaString,true);
        mRefreshableFragment.refresh_list_adapter();
    }
}
