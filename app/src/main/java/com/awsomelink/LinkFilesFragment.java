package com.awsomelink;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.awsomelink.base.LinkItemAction;
import com.awsomelink.db.adapters.LinkFilesAdapter;
import com.awsomelink.dummy.DummyContent;
import com.awsomelink.utils.AWSyncTask;
import com.awsomelink.utils.Links;

import java.io.File;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class LinkFilesFragment extends Fragment implements RefreshableFragment, View.OnClickListener {
    public static final String TAG = "LinkFilesFragment";

    public static final int LINK_CONTACTS_REQUEST_CODE = 1001;
    public static final int LINK_FILE_REQUEST_CODE = 1002;
    public static final int LINK_IMAGE_FROM_GALLERY_REQUEST_CODE = 1003;
    public static final int LINK_IMAGE_FROM_CAMERA_REQUEST_CODE = 1004;
    public static final int LINK_REQUEST_CODE = 1005;

    public static final String ARG_LINK_ID = "LINK_ID";
    public static final String ARG_LINK_TYPE = "LINK_TYPE";

    private String mLinkId = null;
    private Links.LINK_TYPE mType = null;
    private LinkFilesAdapter mLinkFilesAdapter = null;

    public LinkFilesFragment(){}

    public static LinkFilesFragment newInstance(String linkId, Links.LINK_TYPE itemType) {
        LinkFilesFragment fragment = new LinkFilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK_ID, linkId);
        args.putSerializable(ARG_LINK_TYPE, itemType);
        fragment.setArguments(args);
        return fragment;
    }

    public Links.LINK_TYPE getType() {
        return mType;
    }

    public void setType(Links.LINK_TYPE mType) {
        this.mType = mType;
    }

    public String getLinkId() {
        return mLinkId;
    }

    public void setLinkId(String mLinkId) {
        this.mLinkId = mLinkId;
    }

    public boolean isValidLinkId(){
        if( getLinkId() == null ){
            Log.e(TAG, "Invalid link id!");
            return(false);
        }
        return(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLinkId = getArguments().getString(ARG_LINK_ID);
            mType = (Links.LINK_TYPE)getArguments().getSerializable(ARG_LINK_TYPE);
            Log.d(TAG, "Argument setted up!");
        } else {
            Log.e(TAG, "Fragment initial arguments are invalid!");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Called on view for fragment created!");
        refresh_list_adapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Inflate fragment view!");
        return inflater.inflate(R.layout.link_files_fragment, container, false);
    }

    @Override
    public void refresh_list_adapter(){
        if( !isValidLinkId() ){ return; }
        ListView lv = (ListView) getActivity().findViewById(R.id.list_linkid);
        if( lv != null ) {
            TextView textEmpty = (TextView) getActivity().findViewById(R.id.textViewEmpty);
            if( textEmpty != null){ lv.setEmptyView(textEmpty); }
            mLinkFilesAdapter = new LinkFilesAdapter(getActivity().getApplicationContext(), 0, (Fragment)this, mType, mLinkId);
            lv.setAdapter(mLinkFilesAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof LinkItemAction) {
            linkItemAction(v, (LinkItemAction) tag);
        }
    }

    private void linkItemAction(View v, LinkItemAction linkItemAction){
        switch(linkItemAction.mLinkAction){
            case DELETE:
                File file = Links.getFolderLinkFILESFile(getActivity().getApplicationContext(), linkItemAction.mItemType, linkItemAction.mID, linkItemAction.mFileName);
                if( file.exists() ){
                    file.delete();
                }
                refresh_list_adapter();
                break;
            case MORE:
                /*
                Intent i = new Intent(getActivity(), LinkActivity.class);
                i.putExtra(Links.LINK_ID_KEY, linkItemAction.mID);
                i.putExtra(Links.LINK_TYPE_KEY, Links.LINK_TYPE.OUT);
                startActivityForResult(i, LINK_REQUEST_CODE);
                */
                Toast.makeText(getActivity().getApplicationContext(), "More action not implemented yet!",Toast.LENGTH_SHORT ).show();
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown link item action",Toast.LENGTH_SHORT ).show();
        }
    }
}
