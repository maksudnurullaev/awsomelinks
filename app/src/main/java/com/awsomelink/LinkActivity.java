package com.awsomelink;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.awsomelink.utils.Links;


public class LinkActivity extends ActionBarActivity {
    public static final String TAG = "LinkActiviry";
    private String mLinkId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_activity);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.link_files_fragment_place, LinkFilesFragment.newInstance("Test Link ID"));
            ft.commit();
        }
        setUpLinkId();
    }

    private void setUpLinkId() {
        Bundle bundle = getIntent().getExtras();
        if( bundle == null || bundle.getCharSequence(Links.LINK_ID) == null){
            Log.e(TAG, "LinkID not defined due LinkActivity creation!");
        } else {
            mLinkId = bundle.getCharSequence(Links.LINK_ID).toString();
        }
        TextView tvLinkId = (TextView) findViewById(R.id.textViewLinkId);
        if( tvLinkId != null ){
            tvLinkId.setText(mLinkId);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if( fragment instanceof MainActivity.ContentFragment){
            //_fragment_content = (MainActivity.ContentFragment) fragment;
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
