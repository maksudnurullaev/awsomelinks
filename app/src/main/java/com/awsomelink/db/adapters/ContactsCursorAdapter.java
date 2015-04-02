package com.awsomelink.db.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import com.awsomelink.R;
import com.awsomelink.base.Contact;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by m.nurullayev on 30.03.2015.
 */
public class ContactsCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private static final String TAG = "ContactsCursorAdapter";
    private static HashMap<String,Contact> mDataHash = new HashMap<>();
    private Context mContext ;

    public ContactsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CheckedTextView title = (CheckedTextView) view.findViewById(R.id.cb_title);
        String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        if( title != null ) title.setText(displayName);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.list_row_content);
        if( ll != null ){
            ll.removeAllViews();
            String lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Contact contact = getContact(id,lookUpKey,displayName);
            HashMap<String,Boolean> phones = contact.get_phones();
            for(String key: phones.keySet()){
                CheckedTextView phone = (CheckedTextView) mLayoutInflater.inflate(R.layout.contacts_item_row_level2,null);
                phone.setText(key);
                ll.addView(phone);
            }
        }
    }

    public Contact getContact(String id, String lookUpKey, String displayName){
        if( mDataHash.containsKey(displayName) ){
            return(mDataHash.get(displayName));
        }
        Cursor pCur = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] {Data.CONTACT_ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL},
                Data.LOOKUP_KEY + "=?" + " AND "
                        + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
                new String[] {lookUpKey}, null);
        int test = 0;
        //HashMap<String, Boolean> phones = new HashMap<>();
        Contact contact = new Contact(id,lookUpKey,displayName);
        String contactNumber ;
        while (pCur.moveToNext())
        {
            contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if( !contact.get_phones().containsKey(contactNumber) ) {
                contactNumber = PhoneNumberUtils.formatNumber(contactNumber, Locale.getDefault().getCountry());
                contact.get_phones().put(contactNumber, Boolean.FALSE);
            }
        }
        mDataHash.put(displayName,contact);
        return(contact);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.contacts_item_row,parent,false);
    }
}
