package me.lensevents.lensevents;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Event;
import me.lensevents.model.Group;
import me.lensevents.model.User;

public class RequestForImageTask extends AsyncTask<Object, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(Object... objects) {
        GroupDto group = null;
        User user = null;
        Event event = null;
        Bitmap bitmap = null;
        if (objects[0] instanceof GroupDto) {
            group = (GroupDto) objects[0];
        } else if (objects[0] instanceof User) {
            user = (User) objects[0];
        } else if (objects[0] instanceof Event){
            event = (Event) objects[0];
        }
        String url = null;
        if (group != null) {
            url = group.getPhoto();
        } else if (user != null) {
            url = user.getPhotoUrl();
        } else if (event != null){
            url = event.getPhoto();
        }
        try {
            URLConnection connection = new URL(url).openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception oops) {
            Log.getStackTraceString(oops);
        }


        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.default_image);
        }

        return bitmap;
    }
}
