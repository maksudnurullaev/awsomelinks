package com.awsomelinks;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    public enum ContentFragmentType {INBOX,OUTBOX} ;

    private static ContentFragmentType _current_content_type = ContentFragmentType.INBOX ;
    private ContentFragment _current_content;

    private HashMap<ContentFragmentType, Fragment> _hash_contents = new HashMap<ContentFragmentType, Fragment>() ;

    public interface ContentFragment {
        public void clickDispatcher(View view);
        public ContentFragmentType getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initMenu(savedInstanceState);
        initContent(savedInstanceState);
    }

    private void initMenu(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_activity_menu, new MainActivityMenu());
            ft.commit();
        }
    }

    private void initContent(Bundle savedInstanceState) {
        if( isDualMode() ) {
            if (savedInstanceState == null || _current_content == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.main_activity_content, getContent(_current_content_type));
                ft.commit();
            }
        }
    }

    private Fragment getContent(ContentFragmentType content){
        if( !_hash_contents.containsKey(content) ){
            _hash_contents.put(content, newContent(content)) ;
        }
        return _hash_contents.get(content);
    }

    private Fragment newContent(ContentFragmentType content){
        switch (content){
            case INBOX:
                return new InboxFragment();
            case OUTBOX:
                return new OutboxFragment();
            default:
                Log.e(TAG, "Content type not implemented!");
        }
        return null;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if( fragment instanceof ContentFragment){
            _current_content = (ContentFragment) fragment;
            _current_content_type = ((ContentFragment)fragment).getType();
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                new AlertDialog.Builder(this)
                        .setMessage("Settings clicked!")
                        .setPositiveButton("OK", null).show();
                return true;
            case R.id.action_search:
                new AlertDialog.Builder(this)
                        .setMessage("Search clicked!")
                        .setPositiveButton("OK", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickDispatcher(View view) {
        switch (view.getId()) {
            case (R.id.inbox_button):
                activateContent(ContentFragmentType.INBOX);
                break;
            case (R.id.outbox_button):
                activateContent(ContentFragmentType.OUTBOX);
                break;
            default:
                if( _current_content != null ) {
                    _current_content.clickDispatcher(view);
                }
        }
    }

    private void activateContent(ContentFragmentType contentType) {
        if( isDualMode()){
            if(contentType != _current_content_type) {
                activateContentDualMode(contentType);
            }
        } else {
            activateContentSingleMode(contentType);
        }
    }

    private void activateContentSingleMode(ContentFragmentType contentFragmentType){
        Intent intent  = null;
        switch (contentFragmentType){
            case INBOX:
                intent = new Intent (this, InboxActivity.class);
                _current_content_type = ContentFragmentType.INBOX;
                break;
            case OUTBOX:
                intent = new Intent (this, OutboxActivity.class);
                _current_content_type = ContentFragmentType.OUTBOX;
                break;
        }
        startActivity(intent);
    };

    private void activateContentDualMode(ContentFragmentType contentFragmentType) {
        Fragment fragment = null;
        switch (contentFragmentType){
            case INBOX:
                fragment = getContent(ContentFragmentType.INBOX);
                break;
            case OUTBOX:
                fragment = getContent(ContentFragmentType.OUTBOX);
                break;
        }
        if( fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_content, fragment)
                    .commit();
        }
    }

    private boolean isDualMode(){
        return( (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) );
    }
}
