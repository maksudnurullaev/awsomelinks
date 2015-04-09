package com.awsomelink;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ContactsActivity extends ActionBarActivity implements ContactsFragment.OnFragmentInteractionListener {
    private static final String TAG = "ContactsActivity";
    private ContactsFragment contactsFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_activity);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            contactsFragment = ContactsFragment.newInstance();
            ft.add(R.id.contacts_activity_fragment_place, (Fragment)contactsFragment);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( contactsFragment == null ){
            Log.e(TAG, "ContactFragment is null!");
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        switch (item.getItemId()){
            case (R.id.contacts_menu_select_all):
                contactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_ALL);
                return(true);
            case (R.id.contacts_menu_select_none):
                contactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_NONE);
                return(true);
            case (R.id.contacts_menu_select_reverse):
                contactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_REVERSE);
                return(true);
            default:
                Toast.makeText(getApplicationContext(),"Unknown options clicked!",Toast.LENGTH_SHORT ).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id){

    }
}
