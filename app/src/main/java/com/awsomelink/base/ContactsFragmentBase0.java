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
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.awsomelink.R;
import com.awsomelink.db.adapters.CustomCursorAdapter0;

/**
 * Created by m.nurullayev on 27.03.2015.
 */
public class ContactsFragmentBase0 extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    public enum SELECTION_ACTS { SELECTION_ALL, SELECTION_NONE, SELECTION_REVERSE }
    // This is the Adapter being used to display the list's data
    //public SimpleCursorAdapter mAdapter;
    public CustomCursorAdapter0 mAdapter;
    // If non-null, this is th
    private String mCurFilter;
    private SearchView mSearchView;



    // These are the Contacts rows that we will retrieve
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            Contacts._ID,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
            Contacts.LOOKUP_KEY,
    };

    public void selection_make(SELECTION_ACTS selectionAct){
        if( getListView() == null ){ return; }
        int len = getListView().getCount();
        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        if( getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE ) {
            for (int i = 0; i < len; i++) {
                switch (selectionAct){
                    case SELECTION_ALL:
                        getListView().setItemChecked(i, true);
                        break;
                    case SELECTION_NONE:
                        getListView().setItemChecked(i, false);
                        break;
                    case SELECTION_REVERSE:
                        getListView().setItemChecked(i, !checked.get(i));
                        break;
                }
            }
        }
        updateHeader();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new CustomCursorAdapter0(getActivity().getApplicationContext(),null,0);
        getLoaderManager().initLoader(0, null, this);
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
        cursor.moveToFirst();
        String displayName, lookUpKey;
        do {
            displayName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
            lookUpKey = cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY));
            mAdapter.getHashData4(displayName,lookUpKey);
        }while (cursor.moveToNext());

        TextView progress = (TextView) getActivity().findViewById(R.id.progress_bar);
        if( progress != null ){ progress.setVisibility(View.INVISIBLE); }
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        updateHeader();
    }

    private void updateHeader(){
        if( getListView() == null ) { return ; }
        TextView items_count = (TextView) getActivity().findViewById(R.id.selected_items_count_view);
        if( items_count  != null ){ items_count.setText(String.valueOf(getMyCheckedItems())); }
    }

    //... get count of checked items in list
    private int getMyCheckedItems(){
        if( getListView() == null ){ return(0); }
        int len = getListView().getCount();
        int result = 0 ;
        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        for (int i = 0; i < len; i++) { if (checked.get(i)) { result++; } }
        return(result);
    }

}