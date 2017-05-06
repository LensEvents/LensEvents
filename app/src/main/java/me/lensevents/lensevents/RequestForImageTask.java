package me.lensevents.lensevents;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Event;
import me.lensevents.model.User;

public class RequestForImageTask extends AsyncTask<Object, Void, Bitmap> {


    public void execute(Object object, final ImageView mImage) {
        GroupDto group = null;
        User user = null;
        Event event = null;
        String ref = null;
        if (object instanceof GroupDto) {
            group = (GroupDto) object;
        } else if (object instanceof User) {
            user = (User) object;
        } else if (object instanceof Event) {
            event = (Event) object;
        } else if (object instanceof String) {
            ref = (String) object;
        }
        String url = null;
        if (group != null) {
            url = group.getPhoto();
        } else if (user != null) {
            url = user.getPhotoUrl();
        } else if (event != null) {
            url = event.getPhoto();
        } else if (ref != null) {
            url = ref;
        }

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        FirebaseStorage.getInstance().getReference(url).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap image = BitmapFactory.decodeFile(String.valueOf(finalLocalFile));
                mImage.setImageBitmap(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

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
        } else if (objects[0] instanceof Event) {
            event = (Event) objects[0];
        }
        String url = null;
        if (group != null) {
            url = group.getPhoto();
        } else if (user != null) {
            url = user.getPhotoUrl();
        } else if (event != null) {
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
