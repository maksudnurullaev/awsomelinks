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

    private static final String ARG_LINK_ID = "ARG_LINK_ID";
    private String mLinkId = null;

    public LinkFilesFragment(){}

    public static LinkFilesFragment newInstance(String linkId) {
        LinkFilesFragment fragment = new LinkFilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK_ID, linkId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLinkId = getArguments().getString(ARG_LINK_ID);
            Log.d(TAG, "... mLinkId: " + mLinkId);
        }
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
        ListView lv = (ListView) getActivity().findViewById(R.id.list_linkid);
        if( lv != null ) {
            TextView textEmpty = (TextView) getActivity().findViewById(R.id.textViewEmpty);
            if( textEmpty != null){ lv.setEmptyView(textEmpty); }

            //linksDirAdapter = new LinksDirAdapter(getActivity().getApplicationContext(), 0, (Fragment)this, Links.ITEM_TYPE.OUT_BOX);
            //lv.setAdapter(linksDirAdapter);
            lv.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
        }
    }

}
