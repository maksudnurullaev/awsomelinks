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

import com.awsomelink.base.LinkItemAction;
import com.awsomelink.db.adapters.LinksAdapter;
import com.awsomelink.utils.AWSyncTask;
import com.awsomelink.utils.Links;
import com.awsomelink.utils.MetaFile;

/**
 * Created by m.nurullayev on 03.03.2015.
 */

public class OutboxFragment extends Fragment implements MainActivity.ContentFragment, View.OnClickListener, RefreshableFragment {
    public static final String TAG = "OutboxFragment";

    private LinksAdapter linksDirAdapter = null;
    public static final int LINK_REQUEST_CODE = 1005;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh_list_adapter();
    }

    @Override
    public void refresh_list_adapter(){
        ListView lv = (ListView) getActivity().findViewById(R.id.list_linkid);
        if( lv != null ) {
            TextView textEmpty = (TextView) getActivity().findViewById(R.id.textViewEmpty);
            if( textEmpty != null){ lv.setEmptyView(textEmpty); }

            linksDirAdapter = new LinksAdapter(getActivity().getApplicationContext(), 0, (Fragment)this, Links.LINK_TYPE.OUT);
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
                        MetaFile.addEmptyMeta(context, Links.LINK_TYPE.OUT, linkItemAction.mID);
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
                Intent i = new Intent(getActivity(), LinkActivity.class);
                i.putExtra(Links.LINK_ID_KEY, linkItemAction.mID);
                i.putExtra(Links.LINK_TYPE_KEY, Links.LINK_TYPE.OUT);
                startActivityForResult(i, LINK_REQUEST_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context = getActivity().getApplicationContext();
        switch (requestCode) {
            case LINK_REQUEST_CODE:
                refresh_list_adapter();
                break;
            default:
                Toast.makeText(context, "Unknown REQEST CODE: " + requestCode, Toast.LENGTH_SHORT).show();
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
                String newLinkId = Links.getNewLinkID();
                LinkItemAction linkItemAction = new LinkItemAction(newLinkId, Links.LINK_ACTION.ADD_NEW_LINK, Links.LINK_TYPE.OUT);
                String title = getResources().getString(R.string.Add_new_link) + ": " + newLinkId + " ?";
                yesNoDialog(linkItemAction, title);
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), "Unknown click id: " + id, Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.menu_add_link, popup.getMenu());
        popup.show();
    }

    @Override
    public MainActivity.ContentFragmentType getType() {
        return MainActivity.ContentFragmentType.OUTBOX;
    }
}
