package com.awsomelink.utils;

import java.util.ArrayList;

/**
 * Created by m.nurullayev on 18.03.2015.
 */
public class TestUtils {
    public static String[] test_values = new String[] { "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
            "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
            "Android", "iPhone", "WindowsMobile" };
    public static ArrayList<String> getTestList(){
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < test_values.length; ++i) {
            list.add(test_values[i]);
        }
        return(list);
    }
}
