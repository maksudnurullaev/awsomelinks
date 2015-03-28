package com.awsomelink;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.awsomelink.base.ContactsFragmentBase0;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends ContactsFragmentBase0 { //} extends ListFragment implements AbsListView.OnItemClickListener {
    //... Used for mass selecting actions
    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    // private SimpleCursorAdapter mAdapter;

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    public ContactsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
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
*/
//*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_item, container, false);
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        //mHeaderView = inflater.inflate(R.layout.select_bar, null);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        // mListView.setOnItemClickListener(this);
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
