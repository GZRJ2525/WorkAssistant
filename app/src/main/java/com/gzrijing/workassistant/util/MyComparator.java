package com.gzrijing.workassistant.util;

import java.util.Comparator;

/**
 * Created by yycq on 2016/5/3.
 */
public class MyComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {

        if(Long.valueOf(s1)<Long.valueOf(s2))
            return 1;
        else if(Long.valueOf(s1)>Long.valueOf(s2))
            return -1;
        else return 1;
    }
}
