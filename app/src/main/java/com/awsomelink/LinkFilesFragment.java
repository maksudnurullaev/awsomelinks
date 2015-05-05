package com.awsomelink;

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

import com.awsomelink.db.adapters.LinksDirAdapter;
import com.awsomelink.dummy.DummyContent;
import com.awsomelink.utils.Links;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class LinkFilesFragment extends Fragment implements RefreshableFragment {
    public static final String TAG = "LinkFilesFragment";
    public static final int LINK_CONTACTS_REQUEST_CODE = 1001;
    public static final int LINK_FILE_REQUEST_CODE = 1002;
    public static final int LINK_IMAGE_FROM_GALLERY_REQUEST_CODE = 1003;
    public static final int LINK_IMAGE_FROM_CAMERA_REQUEST_CODE = 1004;
    public static final int LINK_REQUEST_CODE = 1005;
    private LinksDirAdapter linksDirAdapter = null;

    private String mLinkId = null;
    private Links.LINK_TYPE mType = null;

    public LinkFilesFragment(){}

    public static LinkFilesFragment newInstance(String linkId, Links.LINK_TYPE itemType) {
        LinkFilesFragment fragment = new LinkFilesFragment();
        fragment.setLinkId(linkId);
        fragment.setType(itemType);
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
        Log.d(TAG, "Argument setted up!");
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

            //linksDirAdapter = new LinksDirAdapter(getActivity().getApplicationContext(), 0, (Fragment)this, Links.LINK_TYPE.OUT);
            //lv.setAdapter(linksDirAdapter);
            lv.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
        }
    }

}
