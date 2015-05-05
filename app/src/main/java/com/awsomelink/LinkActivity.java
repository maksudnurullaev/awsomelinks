package com.awsomelink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.awsomelink.base.Contact;
import com.awsomelink.base.LinkItemAction;
import com.awsomelink.utils.Links;
import com.awsomelink.utils.MediaUtils;
import com.awsomelink.utils.MetaFile;
import com.awsomelink.utils.MetaItem;
import com.awsomelink.utils.Utils;
import com.awsomelink.utils.VCard;

import java.io.File;
import java.util.HashMap;


public class LinkActivity extends ActionBarActivity {
    public static final String TAG = "LinkActiviry";
    private String mLinkId = null;
    private Links.LINK_TYPE mType = null;
    private RefreshableFragment mFilesFragment = null;

    public static final int LINK_CONTACTS_REQUEST_CODE = 1001;
    public static final int LINK_FILE_REQUEST_CODE = 1002;
    public static final int LINK_IMAGE_FROM_GALLERY_REQUEST_CODE = 1003;
    public static final int LINK_IMAGE_FROM_CAMERA_REQUEST_CODE = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_activity);
        setUpLinkArgs();
        if (savedInstanceState == null) {
            setUpFilesFragment();
        }
    }

    private void setUpFilesFragment() {
        if( mLinkId == null || mType == null){
            Log.e(TAG, "Undefined link id!");
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.link_files_fragment_place, LinkFilesFragment.newInstance(mLinkId, mType));
        ft.commit();
    }

    private void setUpLinkArgs() {
        Bundle bundle = getIntent().getExtras();
        if( bundle == null
                || bundle.getCharSequence(Links.LINK_ID_KEY) == null
                || bundle.getSerializable(Links.LINK_TYPE_KEY) == null){
            Log.e(TAG, "LinkID not defined due LinkActivity creation!");
        } else {
            mLinkId = bundle.getCharSequence(Links.LINK_ID_KEY).toString();
            mType = (Links.LINK_TYPE)bundle.getSerializable(Links.LINK_TYPE_KEY);
            TextView tvLinkId = (TextView) findViewById(R.id.textViewLinkId);
            if( tvLinkId != null ){
                tvLinkId.setText(mLinkId);
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if( fragment instanceof RefreshableFragment){
            mFilesFragment = (RefreshableFragment) fragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (item.getItemId()){
            case R.id.action_add_link_addresses:
                intent = new Intent(this,ContactsActivity.class);
                startActivityForResult(intent, LINK_CONTACTS_REQUEST_CODE);
                break;
            case R.id.action_add_link_image_from_camera:
                Toast.makeText(getApplicationContext(), "Non-handled add  image from camera action", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.action_add_link_image_from_gallery):
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, LINK_IMAGE_FROM_GALLERY_REQUEST_CODE);
                break;
            case R.id.action_add_link_video_from_camera:
                Toast.makeText(getApplicationContext(), "Non-handled add  video from camera action", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_add_link_video_from_gallery:
                Toast.makeText(getApplicationContext(), "Non-handled add  video from gallery action", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Context context = getApplicationContext();
        switch (requestCode) {
            case LINK_CONTACTS_REQUEST_CODE:
                if( intent == null ){ return; }
                createLinkContacts(intent);
                break;
            case LINK_IMAGE_FROM_GALLERY_REQUEST_CODE:
                if( intent == null ){ return; }
                createLinkFromImage(context, intent);
                break;
            default:
                Toast.makeText(context, "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void createLinkContacts(Intent data) {
        if( data.hasExtra(ContactsActivity.EXTRA_CONTACTS_KEY) ){
            Context context = getApplicationContext();
            HashMap<String,Contact> contacts = (HashMap<String,Contact>) data.getSerializableExtra(ContactsActivity.EXTRA_CONTACTS_KEY);
            LinkItemAction linkItemAction = VCard.toFile(context, contacts, mLinkId);
            if( linkItemAction != null ){ // ... if no errors!
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.CONTACTS, linkItemAction.mFileName, String.valueOf(contacts.size()));
                String metaPath = MetaFile.setMeta(context, Links.LINK_TYPE.OUT, linkItemAction.mID, metaString);
                metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(false));
                metaPath = MetaFile.setMeta(context, Links.LINK_TYPE.OUT,linkItemAction.mID,metaString);
            }
            //refresh_list_adapter();
        }
    }

    private void createLinkFromImage(Context context, Intent intent){
        File file = Utils.getFile4Image(getApplicationContext(), intent);
        if( file == null ){ return ; }

        LinkItemAction linkItemAction = MediaUtils.createLinkFromImage(context, file, mLinkId);
        if( linkItemAction != null ){
            String metaString = MetaItem.makeMetaString(MetaItem.TYPE.PICTURE, linkItemAction.mFileName, "");
            String metaPath = MetaFile.setMeta(context, Links.LINK_TYPE.OUT, linkItemAction.mID, metaString);
            metaString = MetaItem.makeMetaString(MetaItem.TYPE.AWSYNCHRONIZED, String.valueOf(false));
            metaPath = MetaFile.setMeta(context, Links.LINK_TYPE.OUT,linkItemAction.mID,metaString);
        }
        //refresh_list_adapter();
    }

}
