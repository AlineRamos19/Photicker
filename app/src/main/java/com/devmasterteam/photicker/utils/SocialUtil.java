package com.devmasterteam.photicker.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SocialUtil {

    private static final String HASHTAG = "photicker.app";



    public static void shareImageOnInsta(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {

        PackageManager pkmanager = mainActivity.getPackageManager();
        try {
            pkmanager.getPackageInfo("com.instagram.android", 0);

            try {
                Bitmap image = ImageUtil.drawBitmap(mRelativePhotoContent);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpeg");

                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(byteArrayOutputStream.toByteArray());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpeg"));
                    intent.setType("image/*");
                    intent.setPackage("com.instagram.android");
                    v.getContext().startActivity(Intent.createChooser(intent, mainActivity.getString(R.string.share_image)));

                } catch (FileNotFoundException e) {
                    Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mainActivity, R.string.instagram_not_installed, Toast.LENGTH_LONG).show();

        }


    }

    public static void shareImageOnTwiter(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
        PackageManager pkManager = mainActivity.getPackageManager();

        try {
            pkManager.getPackageInfo("com.twiter.android", 0);

            try {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);

                Bitmap image = ImageUtil.drawBitmap(mRelativePhotoContent);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpeg");
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(byteArrayOutputStream.toByteArray());
                tweetIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpeg"));
                tweetIntent.setType("image/jpeg");

                PackageManager pm = mainActivity.getPackageManager();
                List<ResolveInfo> resolve = pm.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean resolved = false;
                for (ResolveInfo ri : resolve) {
                    if (ri.activityInfo.name.contains("twiter")) {
                        tweetIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }
                v.getContext().startActivity(resolved ? tweetIntent :
                        Intent.createChooser(tweetIntent, mainActivity.getString(R.string.share_image)));

            } catch (FileNotFoundException e) {
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mainActivity, R.string.twitter_not_installed, Toast.LENGTH_LONG).show();
        }


    }

    public static void shareImageOnWhats(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v) {
        PackageManager pkManager = mainActivity.getPackageManager();
        try {
            pkManager.getPackageInfo("com.whatsapp", 0);

            String fileName = "temp_file" + System.currentTimeMillis() + ".jpg";
            try {
                mRelativePhotoContent.setDrawingCacheEnabled(true);
                mRelativePhotoContent.buildDrawingCache(true);
                File imageFile = new File(Environment.getExternalStorageDirectory(), fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                mRelativePhotoContent.getDrawingCache(true).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
                mRelativePhotoContent.setDrawingCacheEnabled(false);
                mRelativePhotoContent.destroyDrawingCache();

                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + fileName));

                    sendIntent.setType("image/jpeg");
                    sendIntent.setPackage("com.whatsapp");
                    v.getContext().startActivity(Intent.createChooser(sendIntent, mainActivity.getString(R.string.share_image)));
                } catch (Exception e) {
                    Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(mainActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show();

            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mainActivity, R.string.whatsapp_not_installed, Toast.LENGTH_LONG).show();
        }
    }
}
