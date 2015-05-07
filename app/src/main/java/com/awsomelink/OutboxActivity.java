package com.awsomelink;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class OutboxActivity extends ActionBarActivity {
    private MainActivity.ContentFragment _fragment_content ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outbox_activity);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.outbox_links_list_fragment_place, new OutboxFragment());
            ft.commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if( fragment instanceof MainActivity.ContentFragment){
            _fragment_content = (MainActivity.ContentFragment) fragment;
        }
        super.onAttachFragment(fragment);
    }

    public void clickDispatcher(View view) {
        if( _fragment_content != null ){
            _fragment_content.clickDispatcher(view);
        }
    }
}
