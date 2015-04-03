package com.awsomelink.base;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.awsomelink.R;
import com.awsomelink.db.adapters.ContactsCursorAdapter;

/**
 * Created by m.nurullayev on 27.03.2015.
 */
public class ContactsFragmentBase extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, View.OnClickListener {

    private ListView mListView;
    public enum SELECTION_ACTS { SELECTION_ALL, SELECTION_NONE, SELECTION_REVERSE }
    public enum SELECTION_TYPE { TITLE, PHONE }
    public ContactsCursorAdapter mAdapter;
    private String mCurFilter;
    private SearchView mSearchView;
    private static final String TAG = "ContactsFragmentBase";

    // These are the Contacts rows that we will retrieve
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            Contacts._ID,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
            Contacts.LOOKUP_KEY,
    };

    public void selection_make(SELECTION_ACTS selectionAct){
        if( getListView() == null ){ return; }
        switch (selectionAct) {
            case SELECTION_ALL:
                mAdapter.checkAllAs(true);
                break;
            case SELECTION_NONE:
                mAdapter.checkAllAs(false);
                break;
            case SELECTION_REVERSE:
                mAdapter.checkAllReverse();
                break;
        }
        getListView().invalidateViews();
        updateHeader();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ContactsCursorAdapter(getActivity().getApplicationContext(),null,0,(ListFragment)this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
        //mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView = (SearchView)getActivity().findViewById(R.id.searchView);
        if( mSearchView != null ){ mSearchView.setOnQueryTextListener(this); }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "='1') AND ("
                + Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(getActivity(), baseUri,
                CONTACTS_SUMMARY_PROJECTION, select, null,
                Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if( cursor != null && cursor.moveToFirst() ) {
            String id, displayName, lookUpKey;
            do {
                id = cursor.getString(cursor.getColumnIndex(Contacts._ID));
                lookUpKey = cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY));
                displayName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
                mAdapter.getContact(id, lookUpKey, displayName);
            } while (cursor.moveToNext());
            TextView progress = (TextView) getActivity().findViewById(R.id.progress_bar);
            if (progress != null) {
                progress.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.e(TAG, "Invalid cursor!");
        }
        mAdapter.swapCursor(cursor);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        if( v instanceof CheckedTextView && v.getTag() instanceof SELECTION_TYPE ){
            CheckedTextView c = (CheckedTextView)v;
            boolean newSelectionValue = c.isChecked() ? false : true;
            c.setChecked(newSelectionValue);
            SELECTION_TYPE selectionType = (SELECTION_TYPE)v.getTag();
            String displayName;
            switch (selectionType) {
                case TITLE:
                    displayName = c.getText().toString();
                    setTitleChecked(c, displayName, newSelectionValue);
                    break;
                case PHONE:
                    String phoneNumber = c.getText().toString();
                    displayName = (String)c.getTag(R.id.contact_phone_tag_display_name);
                    setPhoneChecked(c, displayName, phoneNumber, newSelectionValue);
                    break;
                default:
                    Log.e(TAG, "Undefined & unhandled SELECTION_TYPE: " + v.getTag());
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            ("Undefined & unhandled SELECTION_TYPE: " + v.getTag()),
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTitleChecked(CheckedTextView c, String titleString, boolean value){
        mAdapter.setTitleChecked(titleString,value,true);
        if(c.getParent() instanceof RelativeLayout){
            RelativeLayout rl = (RelativeLayout)c.getParent();
            if( rl.findViewById(R.id.list_row_content) != null
                    && rl.findViewById(R.id.list_row_content) instanceof LinearLayout){
                LinearLayout ll = (LinearLayout)rl.findViewById(R.id.list_row_content);
                if( ll.getChildCount() > 0 ){
                    for(int i = 0; i < ll.getChildCount(); i++){
                        if( ll.getChildAt(i) instanceof CheckedTextView){
                            ((CheckedTextView)ll.getChildAt(i)).setChecked(value);
                        }
                    }
                }
            };
        }
        updateHeader();
    };

    private void setPhoneChecked(CheckedTextView c, String displayName, String phoneNumber, boolean newSelectionValue){
        mAdapter.setPhoneChecked(displayName, phoneNumber,newSelectionValue);
        LinearLayout ll = (LinearLayout) c.getParent();
        boolean anyCheckedPhone = false;
        for(int i = 0; i < ll.getChildCount(); i++){
            if( ll.getChildAt(i) instanceof CheckedTextView){
                if( ((CheckedTextView)ll.getChildAt(i)).isChecked() ) {
                    anyCheckedPhone = true;
                    break;
                }
            }
        }
        if( ll.getParent() instanceof RelativeLayout){
            RelativeLayout rl = (RelativeLayout) ll.getParent();
            CheckedTextView parentCheck = (CheckedTextView) rl.findViewById(R.id.cb_title);
            parentCheck.setChecked(anyCheckedPhone);
            mAdapter.setTitleChecked(displayName,anyCheckedPhone,false);
        }
        updateHeader();
    }

    private void updateHeader(){
        if( getListView() == null ) { return ; }
        TextView items_count = (TextView) getActivity().findViewById(R.id.selected_items_count_view);
        if( items_count  != null ){ items_count.setText(String.valueOf(mAdapter.getCheckedContactsCount())); }
    }
}