package com.vgp.recognizeartists.global.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class PathUtils {

    public PathUtils() {}

    public static String getPathFile(Context context, Uri uri){
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null );
        if(cursor != null){
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        } else {
            return null;
        }

        if(result == null) {
            return null;
        }
        return result;
    }
}
