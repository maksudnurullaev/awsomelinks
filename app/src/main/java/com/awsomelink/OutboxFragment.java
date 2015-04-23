package com.awsomelink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.awsomelink.base.Contact;
import com.awsomelink.base.LinkItemAction;
import com.awsomelink.db.adapters.LinksDirAdapter;
import com.awsomelink.utils.Links;
import com.awsomelink.utils.MetaFile;
import com.awsomelink.utils.MetaItem;
import com.awsomelink.utils.VCard;
import java.util.HashMap;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class OutboxFragment extends Fragment implements MainActivity.ContentFragment, View.OnClickListener {
    public static final String TAG = "OutboxFragment";
    public static final int SAVE_CONTACTS_REQUEST_CODE = 1001;
    private LinksDirAdapter linksDirAdapter = null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ListView lv = (ListView) getActivity().findViewById(android.R.id.list);
        refresh_list_adapter();
    }

    public void refresh_list_adapter(){
        ListView lv = (ListView) getActivity().findViewById(R.id.list_linkid);
        if( lv != null ) {
            TextView textEmpty = (TextView) getActivity().findViewById(R.id.textViewEmpty);
            if( textEmpty != null){ lv.setEmptyView(textEmpty); }

            linksDirAdapter = new LinksDirAdapter(getActivity().getApplicationContext(), 0, (Fragment)this);
            lv.setAdapter(linksDirAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.outbox_fragment,container,false);
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof LinkItemAction) {
            linkItemAction((LinkItemAction)tag);
        } else {
            clickDispatcher(v);
        }
    }

    private void linkItemAction(LinkItemAction linkItemAction){
        switch(linkItemAction.mLinkAction){
            case DELETE:
                Links.deleteLinkItem(getActivity().getApplicationContext(),linkItemAction);
                refresh_list_adapter();
                Toast.makeText(getActivity().getApplicationContext(), "Link DELETE action: " + linkItemAction.mID,Toast.LENGTH_SHORT ).show();
                break;
            case MORE:
                Toast.makeText(getActivity().getApplicationContext(), "Link MORE action: " + linkItemAction.mID,Toast.LENGTH_SHORT ).show();
                break;
            case SHARE:
                Toast.makeText(getActivity().getApplicationContext(), "Link SHARE action: " + linkItemAction.mID,Toast.LENGTH_SHORT ).show();
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown link item action",Toast.LENGTH_SHORT ).show();
        }
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
                addLinkAddresses();
                break;
            case (R.id.action_file):
                //WildSQLUtils.test_1(getActivity().getApplicationContext());
                //WildSQLUtils.test_2(getActivity().getApplicationContext());
                //WildSQLUtils.test_3(getActivity().getApplicationContext());
                //WildSQLUtils.test_4(getActivity().getApplicationContext());
                //WildSQLUtils.test_5(getActivity().getApplicationContext());
                //WildSQLUtils.test_6(getActivity().getApplicationContext());
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown click id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

    private void addLinkAddresses(){
        Intent i = new Intent(getActivity(),ContactsActivity.class);
        startActivityForResult(i, SAVE_CONTACTS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SAVE_CONTACTS_REQUEST_CODE:
                if( data == null ){ return; }
                saveContacts(data);
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveContacts(Intent data) {
        if( data.hasExtra(ContactsActivity.EXTRA_CONTACTS_KEY) ){
            Context context = getActivity().getApplicationContext();
            HashMap<String,Contact> contacts = (HashMap<String,Contact>) data.getSerializableExtra(ContactsActivity.EXTRA_CONTACTS_KEY);
            LinkItemAction linkItemAction = VCard.toFile(context, contacts);
            if( linkItemAction != null ){ // ... if no errors!
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.CONTACTS, linkItemAction.mFileName, String.valueOf(contacts.size()));
                String metaPath = MetaFile.setMeta(context, Links.ITEM_TYPE.OUT_BOX,linkItemAction.mID,metaString);
                Log.d(TAG, "Meta string saved on: " + metaPath);
                Log.d(TAG, "Meta string : " + metaString);
                metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(false));
                Log.d(TAG, "Meta string : " + metaString);
            }
            refresh_list_adapter();
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
