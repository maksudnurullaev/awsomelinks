package com.awsomelink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.awsomelink.utils.AWSyncTask;
import com.awsomelink.utils.Links;
import com.awsomelink.utils.MediaUtils;
import com.awsomelink.utils.MetaFile;
import com.awsomelink.utils.MetaItem;
import com.awsomelink.utils.Utils;
import com.awsomelink.utils.VCard;
import java.io.File;
import java.util.HashMap;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class OutboxFragment extends Fragment implements MainActivity.ContentFragment, View.OnClickListener, RefreshableFragment {
    public static final String TAG = "OutboxFragment";
    public static final int LINK_CONTACTS_REQUEST_CODE = 1001;
    public static final int LINK_FILE_REQUEST_CODE = 1002;
    public static final int LINK_IMAGE_FROM_GALLERY_REQUEST_CODE = 1003;
    public static final int LINK_IMAGE_FROM_CAMERA_REQUEST_CODE = 1004;
    public static final int LINK_REQUEST_CODE = 1005;
    private LinksDirAdapter linksDirAdapter = null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ListView lv = (ListView) getActivity().findViewById(android.R.id.list);
        refresh_list_adapter();
    }

    @Override
    public void refresh_list_adapter(){
        ListView lv = (ListView) getActivity().findViewById(R.id.list_linkid);
        if( lv != null ) {
            TextView textEmpty = (TextView) getActivity().findViewById(R.id.textViewEmpty);
            if( textEmpty != null){ lv.setEmptyView(textEmpty); }

            linksDirAdapter = new LinksDirAdapter(getActivity().getApplicationContext(), 0, (Fragment)this, Links.ITEM_TYPE.OUT_BOX);
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
            linkItemAction(v,(LinkItemAction)tag);
        } else {
            clickDispatcher(v);
        }
    }

    private void yesNoDialog(final LinkItemAction linkItemAction, final String title){
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
        final Context context = getActivity().getApplicationContext();
        db.setTitle(title);
        db.setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (linkItemAction.mLinkAction) {
                    case DELETE:
                        Links.deleteLinkItem(context, linkItemAction);
                        refresh_list_adapter();
                        break;
                    case ADD_NEW_LINK:
                        Links.addNewLink(context, linkItemAction.mID);
                        MetaFile.addEmptyMeta(context, Links.ITEM_TYPE.OUT_BOX, linkItemAction.mID);
                        refresh_list_adapter();
                        break;
                    default:
                        Log.e(TAG, "Unknown yesNoDialog action!");
                        Toast.makeText(getActivity().getApplicationContext(), "Unknown yesNoDialog action", Toast.LENGTH_SHORT).show();
                }
            }
        });
        db.setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        db.show();
    }

    private void linkItemAction(View v, LinkItemAction linkItemAction){
        switch(linkItemAction.mLinkAction){
            case DELETE:
                yesNoDialog(linkItemAction,getResources().getString(R.string.Are_you_sure));
                break;
            case MORE:
                //Intent i = new Intent(getActivity(), LinkActivity.class);
                //startActivityForResult(i, LINK_REQUEST_CODE);
                break;
            case SHARE:
                Toast.makeText(getActivity().getApplicationContext(), "Link SHARE action: " + linkItemAction.mID,Toast.LENGTH_SHORT ).show();
                break;
            case AWSYNC:
                v.setEnabled(false);
                AWSyncTask task = new AWSyncTask(getActivity().getApplicationContext(),this);
                task.execute(linkItemAction);
                Toast.makeText(getActivity().getApplicationContext(), "Link AWSYNC action: " + linkItemAction.mID,Toast.LENGTH_SHORT ).show();
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
        Intent intent;
        switch (id) {
            case (R.id.add_link_btn):
                addNewLink();
                //showAddPopup();
                break;
            case (R.id.add_link_addresses):
                Intent i = new Intent(getActivity(),ContactsActivity.class);
                startActivityForResult(i, LINK_CONTACTS_REQUEST_CODE);
                break;
            case (R.id.image_from_gallery):
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, LINK_IMAGE_FROM_GALLERY_REQUEST_CODE);
                break;
            case (R.id.action_file):
                //WildSQLUtils.test_1(getActivity().getApplicationContext());
                //WildSQLUtils.test_2(getActivity().getApplicationContext());
                //WildSQLUtils.test_3(getActivity().getApplicationContext());
                //WildSQLUtils.test_4(getActivity().getApplicationContext());
                //WildSQLUtils.test_5(getActivity().getApplicationContext());
                //WildSQLUtils.test_6(getActivity().getApplicationContext());
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, LINK_FILE_REQUEST_CODE);
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown click id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewLink(){
        String newLinkId = Links.getNewLinkID();
        LinkItemAction linkItemAction = new LinkItemAction(newLinkId, Links.ITEM_ACTION.ADD_NEW_LINK, Links.ITEM_TYPE.OUT_BOX);
        String title = getResources().getString(R.string.Add_new_link) + ": " + newLinkId + " ?";
        yesNoDialog(linkItemAction, title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Context context = getActivity().getApplicationContext();
        switch (requestCode) {
            case LINK_CONTACTS_REQUEST_CODE:
                if( intent == null ){ return; }
                createLinkContacts(intent);
                break;
            case LINK_IMAGE_FROM_GALLERY_REQUEST_CODE:
                if( intent == null ){ return; }
                createLinkFromImage(context,intent);
                break;
            default:
                Toast.makeText(context, "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void createLinkFromImage(Context context, Intent intent){
        File file = Utils.getFile4Image(getActivity().getApplicationContext(), intent);
        if( file == null ){ return ; }

        LinkItemAction linkItemAction = MediaUtils.createLinkFromImage(context,file);
        if( linkItemAction != null ){
            String metaString = MetaItem.makeMetaString(MetaItem.TYPE.PICTURE, linkItemAction.mFileName, "");
            String metaPath = MetaFile.setMeta(context, Links.ITEM_TYPE.OUT_BOX, linkItemAction.mID, metaString);
            metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(false));
            metaPath = MetaFile.setMeta(context, Links.ITEM_TYPE.OUT_BOX,linkItemAction.mID,metaString);
        }
        refresh_list_adapter();
    }

    private void createLinkContacts(Intent data) {
        if( data.hasExtra(ContactsActivity.EXTRA_CONTACTS_KEY) ){
            Context context = getActivity().getApplicationContext();
            HashMap<String,Contact> contacts = (HashMap<String,Contact>) data.getSerializableExtra(ContactsActivity.EXTRA_CONTACTS_KEY);
            LinkItemAction linkItemAction = VCard.toFile(context, contacts);
            if( linkItemAction != null ){ // ... if no errors!
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.CONTACTS, linkItemAction.mFileName, String.valueOf(contacts.size()));
                String metaPath = MetaFile.setMeta(context, Links.ITEM_TYPE.OUT_BOX, linkItemAction.mID, metaString);
                metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(false));
                metaPath = MetaFile.setMeta(context, Links.ITEM_TYPE.OUT_BOX,linkItemAction.mID,metaString);
            }
            refresh_list_adapter();
        }
    }

    public void showAddPopup(){
        View view = getActivity().findViewById(R.id.add_link_btn);
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
