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
        File folder = Links.getFolderLinkFILES(mContext, mLinkItemAction.mItemType, mLinkItemAction.mID);
        Log.d(TAG, "Do AWSync for " + folder.getPath());
        String requestURL = "https://awsome.link/" + mLinkItemAction.mID + "/upload";
        Log.d(TAG, "Do AWSync URI: " + requestURL + " for " + folder.listFiles().length + " file(s)");
        // 1. Upload files...
        for(File f: folder.listFiles()){
            if( !f.exists() || !sendFile2Server(requestURL, f) ){
                mResult = false;
                return null;
            }
        }
        // 2. Upload metafile...
        File metaFile = MetaFile.getMetaFile(mContext, mLinkItemAction.mItemType, mLinkItemAction.mID);
        requestURL = "https://awsome.link/" + mLinkItemAction.mID + "/upload/meta";
        Log.d(TAG, "Do AWSync URI: " + requestURL + " upload meta file for link id: " + mLinkItemAction.mID);
        if( !metaFile.exists() || !sendFile2Server(requestURL, metaFile) ){
            mResult = false;
            return null;
        }

        mResult = true;
        return null;
    }

    private boolean sendFile2Server(String requestURL, File f){
        boolean result = false;
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "AWSomeLink Andriod");
            multipart.addHeaderField("Link-ID", mLinkItemAction.mID);
            Log.d(TAG, "... AWSync for " + f.getPath());
            multipart.addFilePart("files", f);
            List<String> response = multipart.finish();
            for (String line : response) {
                Log.d(TAG, "... AWSync response: " + line);
            }
            result = true;
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            result = false;
        }
        return(result);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if( !mResult ) { return ; }
        // 1. Set awsync property to TRUE
        String metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(mResult));
        MetaFile.setMeta(mContext, Links.LINK_TYPE.OUT, mLinkItemAction.mID, metaString, true);
        // 2. Make a copy of meta file for future compare operations
        File fileSrc = MetaFile.getMetaFile(mContext, mLinkItemAction.mItemType, mLinkItemAction.mID);
        File fileDest = MetaFile.getMetaFileAwsync(mContext, mLinkItemAction.mItemType, mLinkItemAction.mID);
        MediaUtils.copyFile(fileSrc,fileDest);
        // 3. Refresh file list
        mRefreshableFragment.refresh_list_adapter();
    }
}
