package com.awsomelink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class OutboxFragment extends Fragment implements MainActivity.ContentFragment {

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
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown click id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

    private void addLinkAddresses(){
        //getActivity().getContentResolver()
        //Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //super.startActivityForResult(i, 1001);
        ///Intent intent = new Intent(Intent.ACTION_PICK);
        ///intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        ///startActivityForResult(intent, 1001);
        Intent i = new Intent(getActivity(),ContactsActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:

                if (resultCode == Activity.RESULT_OK) {

                    Cursor s = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            null, null, null);

                    if (s.moveToFirst()) {
                        String phoneNum = s.getString(s.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(getActivity().getBaseContext(), phoneNum, Toast.LENGTH_LONG).show();
                    }
                    s.close();

                }

                break;

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
