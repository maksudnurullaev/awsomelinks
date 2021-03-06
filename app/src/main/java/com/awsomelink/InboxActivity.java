package com.awsomelink;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class InboxActivity extends ActionBarActivity {
    private MainActivity.ContentFragment _fragment_content ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_activity);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.inbox_activity_fragment_place, new InboxFragment());
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
