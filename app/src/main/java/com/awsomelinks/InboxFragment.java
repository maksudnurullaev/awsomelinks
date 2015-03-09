package com.awsomelinks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class InboxFragment extends Fragment implements MainActivity.ContentFragment {

    public InboxFragment(){
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inbox_fragment,container,false);
    }

    @Override
    public void clickDispatcher(View view) {

    }

    @Override
    public MainActivity.ContentFragmentType getType() {
        return MainActivity.ContentFragmentType.INBOX;
    }
}
