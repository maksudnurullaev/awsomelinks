package com.awsomelink;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends ListFragment implements AbsListView.OnItemClickListener {
    //... Used for mass selecting actions
    public enum SELECTION_ACTS { SELECTION_ALL, SELECTION_NONE, SELECTION_REVERSE }
    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private SimpleCursorAdapter mAdapter;

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    public ContactsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String selection = ContactsContract.Contacts.DISPLAY_NAME + " <> \"\" ";
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME},
                selection,
                null,
                 "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                cursor,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                new int[] { android.R.id.text1 }, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        //mHeaderView = inflater.inflate(R.layout.contacts_fragment_header, null);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Click ListItem Number " + position, Toast.LENGTH_SHORT)
                    .show();
        }
        updateHeader();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        updateHeader();
    }

    private void updateHeader(){
        if( mListView == null ) { return ; }
        TextView items_count = (TextView) getActivity().findViewById(R.id.selected_items_count_view);
        if( items_count  != null ){ items_count.setText(String.valueOf(getMyCheckedItems())); }
    }

    public void selection_make(SELECTION_ACTS selectionAct){
        if( mListView == null ){ return; }
        int len = mListView.getCount();
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        if( mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE ) {
            for (int i = 0; i < len; i++) {
                switch (selectionAct){
                    case SELECTION_ALL:
                        mListView.setItemChecked(i, true);
                        break;
                    case SELECTION_NONE:
                        mListView.setItemChecked(i, false);
                        break;
                    case SELECTION_REVERSE:
                        mListView.setItemChecked(i, !checked.get(i));
                        break;
                }
            }
        }
        updateHeader();
    }

    //... get count of checked items in list
    private int getMyCheckedItems(){
        if( mListView == null ){ return(0); }
        int len = mListView.getCount();
        int result = 0 ;
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        for (int i = 0; i < len; i++) { if (checked.get(i)) { result++; } }
        return(result);
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

}
