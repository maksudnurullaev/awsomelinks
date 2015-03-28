package com.awsomelink.base;

import android.app.ActionBar.LayoutParams;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.provider.ContactsContract.Contacts;

import com.awsomelink.R;

/**
 * Created by m.nurullayev on 27.03.2015.
 */
public abstract class ContactsFragmentBase0 extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    // This is the Adapter being used to display the list's data
    public SimpleCursorAdapter mAdapter;
    // If non-null, this is th
    private String mCurFilter;
    private SearchView mSearchView;



    // These are the Contacts rows that we will retrieve
    // static final String[] PROJECTION = new String[] {ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME};
    // static final String[] PROJECTION = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_PRESENCE,
            Contacts.PHOTO_ID,
            Contacts.LOOKUP_KEY,
    };

    // This is the select criteria
/*
    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";
*/
    static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME + " <> \"\" " ;
    static final String ORDER     = "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC" ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create a progress bar to display while the list loads
/*
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
*/
        // Must add the progress bar to the root of the layout
//        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
//        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
/*
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                fromColumns, toViews, 0);
*/
/*
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " <> \"\" ";
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME},
                selection,
                null,
                "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");
*/
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                null,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                new int[] { android.R.id.text1 }, 0);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
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
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
/*
        return new CursorLoader(getActivity(),
                //ContactsContract.Data.CONTENT_URI,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                null,
                ORDER);
*/
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + Contacts.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(getActivity(), baseUri,
                CONTACTS_SUMMARY_PROJECTION, select, null,
                Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        View progress = (View) getActivity().findViewById(R.id.progress_bar);
        if( progress != null ){ progress.setVisibility(View.INVISIBLE); }
        mAdapter.swapCursor(data);
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
        // Do something when a list item is clicked
    }
}