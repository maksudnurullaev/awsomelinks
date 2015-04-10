package com.awsomelink.db.wildsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.HashMap;

/**
 * Created by m.nurullayev on 10.04.2015.
 */
public class WildSQLUtils {
    public static final String TAG = "WildSQLUtils";

    /* ===  REAL TESTS ==== */
    // ... simple add
    public static void test_1(Context context) {
        // clear test dbobjects from database
        clear_test_dbobjects(context);
        // add some tet objects
        test_add_dbobjects(context, "test objects 1", 4, 5);
        // get all objects from database
        WildSQLBase dbase = new WildSQLBase(context);
        HashMap<String, HashMap<String, String>> dbobjects = dbase.get_all_dbobjects();
        // log all dbobjects
        // test_log_objects(dbobjects);
        my_assert(dbobjects.size() == 4, "Test #1, create some dbobjects!");
    }

    // ... simple add + delete
    public static void test_2(Context context) {
        // clear test dbobjects from database
        clear_test_dbobjects(context);
        // add some tet objects
        test_add_dbobjects(context, "test objects 1", 4, 5);
        // get all objects from database
        WildSQLBase dbase = new WildSQLBase(context);
        HashMap<String, HashMap<String, String>> dbobjects = dbase.get_all_dbobjects();
        // log all dbobjects
        my_assert(dbobjects.size() == 4, "Test #2, create some dbobjects!");
        // delete all test objects
        for(String id:dbobjects.keySet()){
            dbase.delete_dbobjects(id);
        }
        // check again
        dbobjects = dbase.get_all_dbobjects();
        my_assert(dbobjects == null || dbobjects.size() == 0, "Test #2, delete all test dbobjects");
    }

    // ... simple add + scope delete
    public static void test_3(Context context) {
        // clear test dbobjects from database
        clear_test_dbobjects(context);
        // add some tet objects
        test_add_dbobjects(context, "test objects 1", 14, 5);
        // get all objects from database
        WildSQLBase dbase = new WildSQLBase(context);
        HashMap<String, HashMap<String, String>> dbobjects = dbase.get_all_dbobjects();
        // log all dbobjects
        my_assert(dbobjects.size() == 14, "Test #3, create some dbobjects!");
        // delete all test objects
        dbase.delete_dbobjects(dbobjects.keySet().toArray(new String[dbobjects.keySet().size()]));
        // check again
        dbobjects = dbase.get_all_dbobjects();
        my_assert(dbobjects == null || dbobjects.size() == 0, "Test #3, scope deletion of dbobjects!");
    }

    // ... simple add + test id & field
    public static void test_4(Context context) {
        // clear test dbobjects from database
        clear_test_dbobjects(context);
        // add some tet objects
        test_add_dbobjects(context, "test objects 1", 14, 5);
        // get all objects from database
        WildSQLBase dbase = new WildSQLBase(context);
        HashMap<String, HashMap<String, String>> dbobjects = dbase.get_all_dbobjects();
        // log all dbobjects
        my_assert(dbobjects.size() == 14, "Test #4, create some dbobjects!");
        String[] ids = dbobjects.keySet().toArray(new String[dbobjects.keySet().size()]);
        String firstId = ids[0], lastId = ids[ids.length-1];
        dbobjects = dbase.get_dbobjects(firstId,lastId);
        my_assert(dbobjects.containsKey(firstId) && dbobjects.containsKey(lastId),"Test #4, test existance of dbobjects!");
    }

    /* ===  UTILITIES ==== */
    public static void test_add_dbobjects(Context context, String object_name, int dbobject_count, int dbobjects_fields_count) {
        WildSQLBase dbase = new WildSQLBase(context);

        HashMap<String, String> dbobject = new HashMap<>();
        for (int i = 1; i <= dbobject_count; i++) {
            dbobject.clear();
            dbobject.put(WildSQLBase.OBJECT_NAME_VALUE, object_name);
            for (int j = 1; j < 10; j++) {
                dbobject.put("test field " + j, "test value " + j);
            }
            String dbobject_id = dbase.insert(dbobject);
        }
    }

    public static void test_log_objects(HashMap<String, HashMap<String, String>> dbobjects) {
        if (dbobjects == null || dbobjects.size() == 0) {
            Log.w(TAG, "No data!");
        } else {
            Log.d(TAG, "Total dbobjects count: " + dbobjects.keySet().size());
            for (String key1 : dbobjects.keySet()) {
                HashMap<String, String> dbobject = dbobjects.get(key1);
                Log.d(TAG, "DBOBJECT ID: " + key1);
                for (String key2 : dbobject.keySet()) {
                    Log.d(TAG, " .... " + key2 + " --> " + dbobject.get(key2));
                }
            }
        }
    }

    public static void my_assert(boolean result, String message){
        if(result){
            Log.d(TAG, "[TEST...OK]: " + message);
        }else{
            Log.e(TAG, "[TEST...ERROR]: " + message);
        }
    }

    public static void clear_test_dbobjects(Context context) {
        WildSQLBase dbase = new WildSQLBase(context);
        SQLiteDatabase db = dbase.getWritableDatabase();
        db.delete(WildSQLBase.TABLE_NAME, " name like 'test%'", null);
    }


    public static String make_where_in_part(String field_name, int field_count){
        String where_part = field_name + " IN (";
        for(int i=0; i<field_count; i++){
            if( i != 0 ) where_part += ',';
            where_part += '?';
        }
        where_part += ')';
        return(where_part);
    }
}
