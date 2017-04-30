package me.lensevents.lensevents;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import me.lensevents.model.Group;

public class RequestForImageTask extends AsyncTask<Object, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(Object... objects) {
        Group group = null;
        Bitmap bitmap = null;
        if (objects[0] instanceof Group) {
            group = (Group) objects[0];
        }
        if (group != null) {
            try {
                URLConnection connection = new URL(group.getPhoto()).openConnection();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (Exception oops) {
                Log.getStackTraceString(oops);
            }
        }

        if(bitmap==null){
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.default_image);
        }

        return bitmap;
    }
}
