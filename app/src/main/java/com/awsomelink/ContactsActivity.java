package com.awsomelink;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.awsomelink.base.Contact;
import com.awsomelink.utils.Links;

import java.util.HashMap;

public class ContactsActivity extends ActionBarActivity implements ContactsFragment.OnFragmentInteractionListener {
    private static final String TAG = "ContactsActivity";
    private ContactsFragment mContactsFragment = null;
    public static final String EXTRA_CONTACTS_KEY = "contacts";
    private String mLinkId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_activity);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mContactsFragment = ContactsFragment.newInstance();
            ft.add(R.id.contacts_activity_fragment_place, (Fragment) mContactsFragment);
            ft.commit();
        }
        Intent intent = getIntent();
        if( intent.hasExtra(Links.LINK_ID_KEY) ){
            mLinkId = intent.getStringExtra(Links.LINK_ID_KEY);
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
        if( mContactsFragment == null ){
            Log.e(TAG, "ContactFragment is null!");
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        switch (item.getItemId()){
            case (R.id.contacts_menu_select_all):
                mContactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_ALL);
                return(true);
            case (R.id.contacts_menu_select_none):
                mContactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_NONE);
                return(true);
            case (R.id.contacts_menu_select_reverse):
                mContactsFragment.selection_make(ContactsFragment.SELECTION_ACTS.SELECTION_REVERSE);
                return(true);
            default:
                Toast.makeText(getApplicationContext(),"Unknown options clicked!",Toast.LENGTH_SHORT ).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id){

    }


    public void clickDispatcher(View view) {
        switch (view.getId()) {
            case (R.id.finish_button):
                HashMap<String,Contact> contacts = mContactsFragment.mAdapter.getCheckedContacts();
                if( contacts != null ){
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_CONTACTS_KEY, contacts);
                    if( mLinkId != null ){
                        intent.putExtra(Links.LINK_ID_KEY, mLinkId);
                    }
                    setResult(RESULT_OK,intent);
                    finish();
                } else {
                    Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.Nothing_selected), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this.getApplicationContext(), "Unknown click!", Toast.LENGTH_SHORT).show();
        }
    }
}
