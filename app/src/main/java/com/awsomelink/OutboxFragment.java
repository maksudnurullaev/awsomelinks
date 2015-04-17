package com.awsomelink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.awsomelink.base.Contact;
import com.awsomelink.db.wildsql.WildSQLBase;
import com.awsomelink.db.wildsql.WildSQLUtils;
import com.awsomelink.utils.VCard;

import java.util.HashMap;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class OutboxFragment extends Fragment implements MainActivity.ContentFragment {
    public static final String TAG = "OutboxFragment";
    public static final int CONTACTS_REQUEST_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.outbox_fragment,container,false);
    }

    @Override
    public void clickDispatcher(View view) {
        click4Id(view.getId());
    }

    private void click4Id(int id){
        switch (id) {
            case (R.id.add_link):
                showAddPopup();
                break;
            case (R.id.add_link_addresses):
                //Toast.makeText(getActivity().getApplicationContext(),"Add address clicked!",Toast.LENGTH_SHORT).show();
                addLinkAddresses();
                break;
            case (R.id.action_file):
                //WildSQLUtils.test_1(getActivity().getApplicationContext());
                //WildSQLUtils.test_2(getActivity().getApplicationContext());
                //WildSQLUtils.test_3(getActivity().getApplicationContext());
                //WildSQLUtils.test_4(getActivity().getApplicationContext());
                //WildSQLUtils.test_5(getActivity().getApplicationContext());
                WildSQLUtils.test_6(getActivity().getApplicationContext());
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown click id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

    private void addLinkAddresses(){
        Intent i = new Intent(getActivity(),ContactsActivity.class);
        startActivityForResult(i, CONTACTS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( data == null ){ return; }

        switch (requestCode) {
            case CONTACTS_REQUEST_CODE:
                if( data.hasExtra(ContactsActivity.EXTRA_CONTACTS_KEY) ){
                    Context context = getActivity().getApplicationContext();
                    HashMap<String,Contact> contacts = (HashMap<String,Contact>) data.getSerializableExtra(ContactsActivity.EXTRA_CONTACTS_KEY);
                    //Toast.makeText(context,"Contacts size: " + contacts.size(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Contacts size: " + contacts.size());
                    VCard.toFile(context, contacts);
                }
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    public void showAddPopup(){
        View view = getActivity().findViewById(R.id.add_link);
        PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                click4Id(item.getItemId());
                return true;
            }
        });
        inflater.inflate(R.menu.menu_outbox, popup.getMenu());
        popup.show();
    }

    @Override
    public MainActivity.ContentFragmentType getType() {
        return MainActivity.ContentFragmentType.OUTBOX;
    }
}
