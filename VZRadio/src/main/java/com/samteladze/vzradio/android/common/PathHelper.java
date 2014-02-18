package com.samteladze.vzradio.android.common;

import java.io.File;

/**
 * Created by nsamteladze on 2/17/14.
 */
public class PathHelper {
    public static String combine(String path1, String path2) {
        return new File(path1, path2).getAbsolutePath();
    }
}
