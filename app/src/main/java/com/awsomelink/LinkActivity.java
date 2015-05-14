package com.awsomelink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;


public class LinkActivity extends ActionBarActivity {
    public static final String TAG = "LinkActivity";
    private String mLinkId = null;
    private Links.LINK_TYPE mType = null;
    private RefreshableFragment mFilesFragment = null;

    public static final int LINK_CONTACTS_REQUEST_CODE = 1001;
    public static final int LINK_FILE_REQUEST_CODE = 1002;
    public static final int LINK_IMAGE_FROM_GALLERY_REQUEST_CODE = 1003;
    public static final int LINK_IMAGE_FROM_CAMERA_BIG_REQUEST_CODE = 1004;
    public static final int LINK_IMAGE_FROM_CAMERA_SMALL_REQUEST_CODE = 1005;

    private String mTempFilePath = null;

    private String mTempOldValue = null;
    private MenuItem mAddMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_activity);
        setUpLinkArgs();
        if (savedInstanceState == null) {
            setUpFilesFragment();
        }
        List<MetaItem> metaItems = MetaFile.getMeta(getApplication(), mType, mLinkId);
        setUpMetaEditText(metaItems, R.id.editTextDesciption, MetaItem.TYPE.DESCRIPTION);
        setUpMetaEditText(metaItems, R.id.editTextPassword, MetaItem.TYPE.PASSWORD);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean result1 = checkUpMetaEditTextUpdate(R.id.editTextDesciption, MetaItem.TYPE.DESCRIPTION);
        boolean result2 = checkUpMetaEditTextUpdate(R.id.editTextPassword, MetaItem.TYPE.PASSWORD);
        if( result1 || result2 ){ MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false); }
    }

    private void setUpMetaEditText(List<MetaItem> metaItems, int id, MetaItem.TYPE type){
        String content = MetaFile.getMetaContent(metaItems, type);
        if( !TextUtils.isEmpty(content) ) {
            EditText et = (EditText) findViewById(id);
            et.setText(content);
            et.setTag(content);
        }
    }

    private boolean checkUpMetaEditTextUpdate( int id, MetaItem.TYPE metaType){
        EditText et = (EditText) findViewById(id);
        String oldValue = (String)et.getTag();
        String newValue = et.getText().toString();
        if (TextUtils.isEmpty(oldValue)) {
            if (!TextUtils.isEmpty(newValue)) {
                updateMeta(metaType, newValue);
                return true;
            }
        } else {
            if (!oldValue.equals(newValue)) {
                updateMeta(metaType, newValue);
                return true;
            }
        }
        return false;
    }

    private void updateMeta(MetaItem.TYPE mMetaType, String newValue){
        Log.d(TAG, "Update " + mMetaType.toString() + " to " + newValue);
        String metaString = MetaItem.makeMetaString(mMetaType, newValue);
        String metaPath = MetaFile.setMeta(getApplicationContext(), mType, mLinkId, metaString, true);
    }

    private void setUpFilesFragment() {
        if( mLinkId == null || mType == null){
            Log.e(TAG, "Undefined link id!");
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.link_files_fragment_place, (Fragment)LinkFilesFragment.newInstance(mLinkId, mType));
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
        mAddMenu = menu.findItem(R.id.menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_add_link_addresses:
                intent = new Intent(this,ContactsActivity.class);
                startActivityForResult(intent, LINK_CONTACTS_REQUEST_CODE);
                break;
            case R.id.action_add_link_image_from_camera_big:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //File file = MediaUtils.createNextLinkJpegfile(getApplicationContext(), mLinkId, true);
                    File file = MediaUtils.createNextLinkJpegBigTemp();
                    if( file != null ){
                        mTempFilePath = file.getAbsolutePath();
                        Log.d(TAG, "Try to add bit image: " + mTempFilePath);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(takePictureIntent, LINK_IMAGE_FROM_CAMERA_BIG_REQUEST_CODE);
                    } else {
                        Log.e(TAG, "Error due creating bit image: " + mTempFilePath);
                        mTempFilePath = null;
                    }
                }
                break;
            case R.id.action_add_link_image_from_camera_small:
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, LINK_IMAGE_FROM_CAMERA_SMALL_REQUEST_CODE);
                }
                break;
            case R.id.action_add_link_image_from_gallery:
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
            case R.id.action_add_link_file:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, LINK_FILE_REQUEST_CODE);
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
                createLinkFileFromContacts(intent);
                break;
            case LINK_IMAGE_FROM_GALLERY_REQUEST_CODE:
                if ( intent == null ){ return; }
                createLinkFileFromImage(intent);
                break;
            case LINK_IMAGE_FROM_CAMERA_SMALL_REQUEST_CODE:
                if (intent == null ){ return; }
                createLinkFileFromCameraImageSmall(intent);
                break;
            case LINK_IMAGE_FROM_CAMERA_BIG_REQUEST_CODE:
                createLinkFileFromCameraImageBig();
                break;
            case LINK_FILE_REQUEST_CODE:
                if( intent == null ){ return; }
                createLinkeFileFromFile(intent);
                break;
            default:
                Toast.makeText(context, "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void createLinkFileFromCameraImageSmall(Intent data){
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        LinkItemAction linkItemAction = MediaUtils.createLinkFileFromCameraImage(getApplicationContext(), mLinkId, imageBitmap);
        if( linkItemAction != null ){
            String metaString = MetaItem.makeMetaString(MetaItem.TYPE.PICTURE, linkItemAction.mFileName, "");
            String metaPath = MetaFile.setMeta(getApplicationContext(), mType, linkItemAction.mID, metaString, false);
            MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false);
        }
        refresh_files_fragment();
    }

    private void createLinkFileFromCameraImageBig(){
        if( mTempFilePath == null ) return;
        File file = new File(mTempFilePath);
        LinkItemAction linkItemAction = null;
        if( file.exists() ){
            linkItemAction = MediaUtils.createLinkFileFromFile(getApplicationContext(), mLinkId, file);
            if( linkItemAction != null ){
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.PICTURE, linkItemAction.mFileName, "");
                String metaPath = MetaFile.setMeta(getApplicationContext(), mType, linkItemAction.mID, metaString, false);
                MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false);
                refresh_files_fragment();
            }
        }
    }

    private void createLinkeFileFromFile(Intent intent){
        try {
            URI uriFile = new URI(intent.toUri(0));
            File file = new File(uriFile.getPath());
            LinkItemAction linkItemAction = null;
            if( file.exists() ){
                linkItemAction = MediaUtils.createLinkFileFromFile(getApplicationContext(), mLinkId, file);
                Log.d(TAG, "Create new file link for: " + uriFile.getPath());
            } else {
                Log.e(TAG, "File " + uriFile.getPath() + " does not exits!");
            }
            if( linkItemAction != null ){
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.FILE, linkItemAction.mFileName, "");
                String metaPath = MetaFile.setMeta(getApplicationContext(), mType, linkItemAction.mID, metaString, false);
                MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false);
            }
            refresh_files_fragment();
        } catch (URISyntaxException e){
            Log.e(TAG, e.toString());
        }
    }

    private void createLinkFileFromContacts(Intent intent) {
        if( intent.hasExtra(ContactsActivity.EXTRA_CONTACTS_KEY) ){
            Context context = getApplicationContext();
            HashMap<String,Contact> contacts = (HashMap<String,Contact>) intent.getSerializableExtra(ContactsActivity.EXTRA_CONTACTS_KEY);
            LinkItemAction linkItemAction = VCard.createLinkFileFromContacts(context, contacts, mLinkId);
            if( linkItemAction != null ){
                String metaString = MetaItem.makeMetaString(MetaItem.TYPE.CONTACTS, linkItemAction.mFileName, String.valueOf(contacts.size()));
                String metaPath = MetaFile.setMeta(context, Links.LINK_TYPE.OUT, linkItemAction.mID, metaString, false);
                MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false);
            }
            refresh_files_fragment();
        }
    }

    private void createLinkFileFromImage(Intent intent){
        File file = Utils.getFile4Image(getApplicationContext(), intent);
        if( file == null ){ return ; }

        LinkItemAction linkItemAction = MediaUtils.createLinkFileFromFile(getApplicationContext(), mLinkId, file);
        if( linkItemAction != null ){
            String metaString = MetaItem.makeMetaString(MetaItem.TYPE.PICTURE, linkItemAction.mFileName, "");
            String metaPath = MetaFile.setMeta(getApplicationContext(), mType, linkItemAction.mID, metaString, false);
            MetaFile.setMetaAwsync(getApplicationContext(), mLinkId, false);
        }
        refresh_files_fragment();
    }

    private void refresh_files_fragment(){
        if( mFilesFragment  != null ) mFilesFragment.refresh_list_adapter();
    }

    public void clickAdd(View v){
        openOptionsMenu();
    }
}
