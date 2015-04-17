package com.awsomelink.base;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by m.nurullayev on 02.04.2015.
 */
public class Contact implements Serializable {
    private String _id;
    private String _lookup_key;
    private String _display_name;
    private Boolean _checked;

    private HashMap<String,Boolean> _phones = new HashMap<>();

    public Contact(){}

    public Contact(String id, String lookup_key, String display_name){
        this._id = id;
        this._lookup_key = lookup_key;
        this._display_name = display_name;
        this._checked = Boolean.FALSE;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_lookup_key() {
        return _lookup_key;
    }

    public void set_lookup_key(String _lookup_key) {
        this._lookup_key = _lookup_key;
    }

    public HashMap<String, Boolean> get_phones() {
        return _phones;
    }

    public void set_phones(HashMap<String, Boolean> _phones) {
        this._phones = _phones;
    }

    public String get_display_name() {
        return _display_name;
    }

    public void set_display_name(String _display_name) {
        this._display_name = _display_name;
    }

    public Boolean get_checked() {
        return _checked;
    }

    public void set_checked(Boolean _checked) {
        this._checked = _checked;
    }


}
