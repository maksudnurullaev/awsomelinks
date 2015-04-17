package com.awsomelink.db.wildsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.Date;

/**
 * Created by m.nurullayev on 08.04.2015.
 */
public class WildSQLBase extends SQLiteOpenHelper {
    public static final String TAG = "SQLiteOpenHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "awsome.link.db";
    public static final String TABLE_NAME    = "objects";
    public static final String COLUMN_ID     = "id";
    public static final String COLUMN_NAME   = "name";
    public static final String COLUMN_FIELD  = "field";
    public static final String COLUMN_VALUE  = "value";
    public static final String COLUMN__TOTAL = "total";
    public static final String OBJECT_NAME_VALUE = "object_name";
    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_FIELD, COLUMN_VALUE};

    public static final SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

    public static final String DATABASE_CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS objects (name TEXT, id TEXT, field TEXT, value TEXT COLLATE NOCASE);" +
            "CREATE INDEX IF NOT EXISTS i_objects ON objects (name, id, field COLLATE NOCASE);";

    public WildSQLBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SQL);
        Log.d(TAG, "Database " + DATABASE_NAME + " created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to do...
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("PRAGMA synchronous = OFF;");
    }

    /* === UTILITIES === */
    public static String get_new_id(){
        return(dformat.format(new Date()) + ' ' + UUID.randomUUID().toString().substring(0, 8));
    }

    private static HashMap<String,HashMap<String,String>> generate_dbobjects(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0 ){
            Log.w(TAG, "Invalid cursor: Nothing to generate to dbobjects!");
            return(null);
        }
        HashMap<String,HashMap<String,String>> dbobjects = new HashMap<>();
        cursor.moveToFirst();
        do {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            if( !dbobjects.containsKey(id) ){
                HashMap<String,String> dbobject = new HashMap<>();
                String object_name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                dbobject.put(OBJECT_NAME_VALUE, object_name);
                dbobject.put(COLUMN_ID,id);
                dbobjects.put(id, dbobject);
            }
            dbobjects.get(id).put(cursor.getString(cursor.getColumnIndex(COLUMN_FIELD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
        }while(cursor.moveToNext());
        return(dbobjects);
    }

    public static boolean validate2insert(HashMap<String, String> dbobject){
        if(dbobject == null){
            Log.e(TAG,"Invalid dbobject: NULL dbobject!");
            return(false);
        }
        if(!dbobject.containsKey(OBJECT_NAME_VALUE)){
            Log.e(TAG,"Invalid dbobject: Abcense of mandatory '" + OBJECT_NAME_VALUE + "' field!");
            return(false);
        }
        if(dbobject.keySet().size() < 1){
            Log.e(TAG,"Invalid dbobject: No data to insert!");
            return(false);
        }
        return(true);
    }

    public HashMap<String,String> validate2update(HashMap<String, String> dbobject){
        if( !validate2insert(dbobject) ){ return(null); }
        // ... check for existance
        if(!dbobject.containsKey(COLUMN_ID) || TextUtils.isEmpty(dbobject.get(COLUMN_ID))){
            Log.e(TAG,"Invalid dbobject: Abcense of mandatory '" + COLUMN_ID + "' field!");
            return(null);
        }
        if(dbobject.keySet().size() < 2){
            Log.e(TAG,"Invalid dbobject: No data to update!");
            return(null);
        }
        String id = dbobject.get(COLUMN_ID);
        HashMap<String,HashMap<String,String>> dbobjects = get_dbobjects(id);
        if(dbobjects == null && !dbobjects.containsKey(id)){
            Log.e(TAG,"Database object not exists: Nothing to update!");
            return(null);
        }
        return(dbobjects.get(id));
    }

    public static void trip_dbobject(HashMap<String,String> dbobject, String... keys){
        if(dbobject == null){ return; };
        for(String key: keys){ dbobject.remove(key); }
    }

    /* === SQL part === */
    public String insert(HashMap<String,String> dbobject){
        if(!validate2insert(dbobject)){ return(null); }
        String id = get_new_id();
        String object_name = dbobject.get(OBJECT_NAME_VALUE);
        trip_dbobject(dbobject,OBJECT_NAME_VALUE,"id");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values ;
        for(String key:dbobject.keySet()){
            values = new ContentValues();
            values.put(COLUMN_ID,id);
            values.put(COLUMN_NAME,object_name);
            values.put(COLUMN_FIELD, key);
            values.put(COLUMN_VALUE, dbobject.get(key));
            long _id = db.insert(TABLE_NAME, null,values);
            Log.d(TAG,"Record inserted with id: " + _id);
        }
        return(id);
    }

    public String update(HashMap<String,String> dbobject){
        HashMap<String,String> old_dbobject = validate2update(dbobject);
        if( old_dbobject == null ){ return(null); }

        String id = dbobject.get(COLUMN_ID);
        String object_name = dbobject.get(OBJECT_NAME_VALUE);
        trip_dbobject(dbobject,OBJECT_NAME_VALUE,"id");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values ;
        for(String key:dbobject.keySet()){
            if( !old_dbobject.containsKey(key) ) {
                values = new ContentValues();
                values.put(COLUMN_ID, id);
                values.put(COLUMN_NAME, object_name);
                values.put(COLUMN_FIELD, key);
                values.put(COLUMN_VALUE, dbobject.get(key));
                long _id = db.insert(TABLE_NAME, null, values);
                Log.d(TAG, "Record inserted with id: " + _id);
            } else {
                values = new ContentValues();
                values.put(COLUMN_VALUE, dbobject.get(key));
                String whereClause = "id = ? AND field = ?";
                String[] whereArgs = {id,key};
                long _id = db.update(TABLE_NAME, values, whereClause, whereArgs);
                Log.d(TAG, "Record updated with id: " + _id);
            }
        }
        return(id);
    }

    public HashMap<String,HashMap<String,String>> get_dbobjects(String... ids){
        if(ids == null || ids.length == 0){
            Log.e(TAG, "Invalid id for deletion!");
            return(null);
        }
        SQLiteDatabase db = getReadableDatabase();
        String where_part = WildSQLUtils.make_where_in_part(COLUMN_ID,ids.length);
        Cursor cursor = db.query(TABLE_NAME,ALL_COLUMNS,where_part,ids,null,null,null);
        return(generate_dbobjects(cursor));
    }

    public HashMap<String,HashMap<String,String>> get_dbobjects_all(){
        return(get_dbobjects_all(null, null));
    }

    public HashMap<String,HashMap<String,String>> get_dbobjects_all(String selection, String... args){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,ALL_COLUMNS,selection,args,null,null,null);
        return(generate_dbobjects(cursor));
    }

    public HashMap<String,String> get_dbobjects_names(String... names){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null ;
        if( names == null || names.length == 0) {
            cursor = db.rawQuery("SELECT name, COUNT(id) as total FROM (SELECT distinct name, id FROM objects) GROUP BY name", null);
        } else {
            String wherePart = WildSQLUtils.make_where_in_part(COLUMN_NAME,names.length);
            cursor = db.rawQuery("SELECT name, COUNT(id) as total FROM (SELECT distinct name, id FROM objects WHERE " + wherePart +  ") GROUP BY name", names);
        }
        if(cursor == null || cursor.getCount() == 0 ){
            Log.w(TAG, "Invalid cursor: Nothing to generate to dbobjects!");
            return(null);
        }
        HashMap<String,String> dbobjects = new HashMap<>();
        cursor.moveToFirst();
        do {
            String dbobject_name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String dbobject_total = cursor.getString(cursor.getColumnIndex(COLUMN__TOTAL));
            dbobjects.put(dbobject_name,dbobject_total);
        }while(cursor.moveToNext());
        return(dbobjects);
    }

    public void delete_dbobjects(String... ids){
        if(ids == null || ids.length == 0){
            Log.e(TAG, "Invalid id for deletion!");
            return;
        }
        SQLiteDatabase db = getReadableDatabase();
        String where_part = WildSQLUtils.make_where_in_part(COLUMN_ID,ids.length);
        db.delete(TABLE_NAME,where_part,ids);
    }

}
