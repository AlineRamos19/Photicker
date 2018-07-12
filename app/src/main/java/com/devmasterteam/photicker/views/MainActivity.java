package com.devmasterteam.photicker.views;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.utils.ImageUtil;
import com.devmasterteam.photicker.utils.LongEventType;
import com.devmasterteam.photicker.utils.PermissionUtil;
import com.devmasterteam.photicker.utils.SocialUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private final ViewHolder mViewHolder = new ViewHolder();
    private ImageView mImageSelected;
    private boolean mAutoIncrement;
    private LongEventType mLongEventType;
    private Handler mRepeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImagesList();

        final LinearLayout content = findViewById(R.id.linear_horizontal_scroll_content);
        mViewHolder.mRelativePhotoContent = findViewById(R.id.relative_photo_content_draw);
        for (Integer imageiD : mListImages) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageiD, 70, 70));
            image.setPadding(20, 10, 20, 10);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageiD, dimensions);

            final int widht = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(mViewHolder.mRelativePhotoContent, imageiD, widht, height));
            content.addView(image);
        }

        mViewHolder.mPanelControl = findViewById(R.id.linear_control_panel);
        mViewHolder.mPanelShare = findViewById(R.id.linear_share_panel);
        mViewHolder.mImageFacebook = findViewById(R.id.image_facebook);
        mViewHolder.mImageFacebook = findViewById(R.id.image_twiter);
        mViewHolder.mImageFacebook = findViewById(R.id.image_whatsapp);
        mViewHolder.mImageFacebook = findViewById(R.id.image_instagram);
        mViewHolder.mButtonFinish = findViewById(R.id.image_finish);
        mViewHolder.mButtonRemove = findViewById(R.id.image_remove);
        mViewHolder.mButtonRotateLeft = findViewById(R.id.image_rotate_left);
        mViewHolder.mButtonRotateRight = findViewById(R.id.image_rotate_right);
        mViewHolder.mButtonZoomIn = findViewById(R.id.image_zoom_in);
        mViewHolder.mButtonZoomOut = findViewById(R.id.image_zoom_out);
        mViewHolder.mImagePhoto = findViewById(R.id.image_photo);

        setListeners();

    }

    private void setListeners() {
        findViewById(R.id.image_facebook).setOnClickListener(this);
        findViewById(R.id.image_twiter).setOnClickListener(this);
        findViewById(R.id.image_whatsapp).setOnClickListener(this);
        findViewById(R.id.image_instagram).setOnClickListener(this);
        findViewById(R.id.image_zoom_in).setOnClickListener(this);
        findViewById(R.id.image_zoom_out).setOnClickListener(this);
        findViewById(R.id.image_rotate_right).setOnClickListener(this);
        findViewById(R.id.image_rotate_left).setOnClickListener(this);
        findViewById(R.id.image_finish).setOnClickListener(this);
        findViewById(R.id.image_remove).setOnClickListener(this);
        findViewById(R.id.image_take_photo).setOnClickListener(this);

        findViewById(R.id.image_zoom_in).setOnLongClickListener(this);
        findViewById(R.id.image_zoom_out).setOnLongClickListener(this);
        findViewById(R.id.image_rotate_right).setOnLongClickListener(this);
        findViewById(R.id.image_rotate_left).setOnLongClickListener(this);

        findViewById(R.id.image_zoom_in).setOnTouchListener(this);
        findViewById(R.id.image_zoom_out).setOnTouchListener(this);
        findViewById(R.id.image_rotate_right).setOnTouchListener(this);
        findViewById(R.id.image_rotate_left).setOnTouchListener(this);


    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageiD, int widht, int height) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView image = new ImageView(MainActivity.this);
                image.setBackgroundResource(imageiD);
                relativeLayout.addView(image);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                mImageSelected = image;
                toogleControlPanel(true);

                image.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        float x, y;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mImageSelected = image;
                                toogleControlPanel(true);
                                break;

                            case MotionEvent.ACTION_MOVE:
                                int coords[] = {0, 0};
                                relativeLayout.getLocationOnScreen(coords);

                                x = (event.getRawX() - (image.getWidth() / 2));
                                y = (event.getRawY() - (coords[1] + 100) + (image.getHeight() / 2));
                                image.setX(x);
                                image.setY(y);
                                break;

                            case MotionEvent.ACTION_UP:
                                break;
                        }

                        return true;
                    }
                });
            }
        };
    }

    private void toogleControlPanel(boolean showControl) {
        if (showControl) {
            mViewHolder.mPanelShare.setVisibility(View.GONE);
            mViewHolder.mPanelControl.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mPanelShare.setVisibility(View.VISIBLE);
            mViewHolder.mPanelControl.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            setPhotoAsBackground();
        }

    }

    private void setPhotoAsBackground() {
        int targetW = mViewHolder.mImagePhoto.getWidth();
        int targetH = mViewHolder.mImagePhoto.getHeight();

        BitmapFactory.Options mOptions = new BitmapFactory.Options();
        mOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mViewHolder.mUriPhotoPath.getPath(), mOptions);
        int photoW  = mOptions.outWidth;
        int photoH  = mOptions.outHeight;

        int scaleFactory = Math.min(photoW / targetW, photoH / targetH);
        mOptions.inJustDecodeBounds = false;
        mOptions.inSampleSize = scaleFactory;

        Bitmap bitmap = BitmapFactory.decodeFile(mViewHolder.mUriPhotoPath.getPath(), mOptions);

        Bitmap bitmapRotated = ImageUtil.rotateImageIfRequered(bitmap, mViewHolder.mUriPhotoPath);
        mViewHolder.mImagePhoto.setImageBitmap(bitmapRotated);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_take_photo:
                if (PermissionUtil.hasCameraPermission(this)) {
                    PermissionUtil.asksCameraPermission(this);
                }
                break;

            case R.id.image_zoom_in:
                ImageUtil.handleZoomIn(this.mImageSelected);
                break;

            case R.id.image_zoom_out:
                ImageUtil.handleZoomOut(this.mImageSelected);
                break;

            case R.id.image_rotate_left:
                ImageUtil.handleRotateLeft(this.mImageSelected);
                break;

            case R.id.image_rotate_right:
                ImageUtil.handleRotateRight(this.mImageSelected);
                break;

            case R.id.image_finish:
                toogleControlPanel(false);
                break;

            case R.id.image_remove:
                mViewHolder.mRelativePhotoContent.removeView(this.mImageSelected);
                break;

            case R.id.image_facebook:

                break;

            case R.id.image_instagram:
                SocialUtil.shareImageOnInsta(this, mViewHolder.mRelativePhotoContent, v);
                break;

            case R.id.image_twiter:
                SocialUtil.shareImageOnTwiter(this, mViewHolder.mRelativePhotoContent, v);
                break;

            case R.id.image_whatsapp:
                SocialUtil.shareImageOnWhats(this, mViewHolder.mRelativePhotoContent, v);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = ImageUtil.createImageFile(this);
                mViewHolder.mUriPhotoPath = Uri.fromFile(photo);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Não foi possível iniciar a câmera!",
                        Toast.LENGTH_LONG).show();
            }

            if (photo != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.image_zoom_in) mLongEventType = LongEventType.ZoomIn;
        if (v.getId() == R.id.image_zoom_out) mLongEventType = LongEventType.ZoomOut;
        if (v.getId() == R.id.image_rotate_left) mLongEventType = LongEventType.RotateLeft;
        if (v.getId() == R.id.image_rotate_right) mLongEventType = LongEventType.RotateRight;
        mAutoIncrement = true;

        new RptUpdater().run();
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.image_zoom_in || id == R.id.image_zoom_out
                || id == R.id.image_rotate_left || id == R.id.image_rotate_right) {
            if (event.getAction() == MotionEvent.ACTION_UP && mAutoIncrement) {
                mAutoIncrement = false;
                this.mLongEventType = null;
            }
        }

        return false;
    }

    private static class ViewHolder {
        LinearLayout mPanelShare;
        LinearLayout mPanelControl;
        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mButtonRemove;
        ImageView mButtonFinish;
        RelativeLayout mRelativePhotoContent;
        Uri mUriPhotoPath;
        ImageView mImagePhoto;
        ImageView mImageInstagram;
        ImageView mImageTwiter;
        ImageView mImageFacebook;
        ImageView mImageWhatsapp;
    }

    private class RptUpdater implements Runnable {
        @Override
        public void run() {
            if (mAutoIncrement)
                mRepeatUpdateHandler.postDelayed(new RptUpdater(), 50);

            if (mLongEventType != null) {
                switch (mLongEventType) {
                    case ZoomIn:
                        ImageUtil.handleZoomIn(mImageSelected);
                        break;

                    case ZoomOut:
                        ImageUtil.handleZoomOut(mImageSelected);
                        break;

                    case RotateLeft:
                        ImageUtil.handleRotateLeft(mImageSelected);
                        break;

                    case RotateRight:
                        ImageUtil.handleRotateRight(mImageSelected);
                        break;
                }
            }

        }
    }

}