package com.androidexample.databaseapp.shoppingonline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidexample.databaseapp.shoppingonline.widget.SampleGLView;
import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;
import com.androidexample.databaseapp.shoppingonline.widget.Filters;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by sudamasayuki2 on 2018/07/02.
 */

public class BaseCameraActivity extends AppCompatActivity {

    private SampleGLView sampleGLView;
    protected CameraRecorder cameraRecorder;
    private String filepath;
    private String filepath2;
    private String vidPath = "";
    private String vidPath1;

    private TextView recordBtn;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 720;
    private AlertDialog filterDialog;
    private  TextView seconds;
    private boolean toggleClick = false;
    private ImageView play, pause;
    private CountDownTimer countDownTimer;
    private long currentMillis=30;

    protected void onCreateActivity() {
//        getSupportActionBar().hide();
        recordBtn = findViewById(R.id.btn_record);
        seconds = findViewById(R.id.seconds);
        play = findViewById(R.id.playbtn);
        pause = findViewById(R.id.pausebtn);



        recordBtn.setOnClickListener(v -> {

            if (recordBtn.getText().equals(getString(R.string.app_record))) {
                seconds.setText(""+currentMillis);
                countDownTimer = new CountDownTimer(currentMillis*1000,1000) {

                    public void onTick(long millisUntilFinished) {
                        currentMillis = millisUntilFinished/1000;
                        seconds.setText("" + millisUntilFinished/1000);
                        filepath = getVideoFilePath();
                        cameraRecorder.start(filepath);
                        recordBtn.setText("Stop");
                        pause.setVisibility(View.VISIBLE);
                        vidPath1 = filepath+";";

                    }

                    public void onFinish() {
//                        seconds.setText("done!");
                        cameraRecorder.stop();
                        recordBtn.setText(getString(R.string.app_record));
                    }
                };
                countDownTimer.start();




            } else {
                cameraRecorder.stop();
                recordBtn.setText(getString(R.string.app_record));
            }

        });
        findViewById(R.id.btn_flash).setOnClickListener(v -> {
            if (cameraRecorder != null && cameraRecorder.isFlashSupport()) {
                cameraRecorder.switchFlashMode();
                cameraRecorder.changeAutoFocus();
            }
        });

        pause.setOnClickListener(v1 -> {
            sampleGLView.onPause();
            pause.setVisibility(View.INVISIBLE);
            play.setVisibility(View.VISIBLE);
            cameraRecorder.stop();
            countDownTimer.cancel();
            recordBtn.setText(getString(R.string.app_record));
        });
        play.setOnClickListener(v1 -> {
            sampleGLView.onResume();
            setUpCamera();
            filepath2 = getVideoFilePath();

            cameraRecorder.start(filepath2);
            vidPath += filepath2+";";
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            seconds.setText(""+currentMillis);
            countDownTimer = new CountDownTimer(currentMillis*1000,1000) {

                public void onTick(long millisUntilFinished) {
                    currentMillis = millisUntilFinished/1000;
                    seconds.setText("" + millisUntilFinished/1000);

                    recordBtn.setText("Stop");
                    pause.setVisibility(View.VISIBLE);

                }

                public void onFinish() {
//                        seconds.setText("done!");
//                    ArrayList<String> myList = new ArrayList<String>();
                    cameraRecorder.stop();
                    recordBtn.setText(getString(R.string.app_record));




                }
            };
            countDownTimer.start();
        });

        findViewById(R.id.btn_switch_camera).setOnClickListener(v -> {
            releaseCamera();
            if (lensFacing == LensFacing.BACK) {
                lensFacing = LensFacing.FRONT;
            } else {
                lensFacing = LensFacing.BACK;
            }
            toggleClick = true;
        });

        findViewById(R.id.btn_filter).setOnClickListener(v -> {
            if (filterDialog == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose a filter");
                builder.setOnDismissListener(dialog -> {
                    filterDialog = null;
                });

                final Filters[] filters = Filters.values();
                CharSequence[] charList = new CharSequence[filters.length];
                for (int i = 0, n = filters.length; i < n; i++) {
                    Toast.makeText(this, "content:"+filters[i].name(), Toast.LENGTH_SHORT).show();
                    charList[i] = filters[i].name();
                }
                builder.setItems(charList, (dialog, item) -> {
                    changeFilter(filters[item]);
                });
                filterDialog = builder.show();
            } else {
                filterDialog.dismiss();
            }

        });

        findViewById(R.id.btn_image_capture).setOnClickListener(v -> {
            captureBitmap(bitmap -> {
                new Handler().post(() -> {
                    String imagePath = getImageFilePath();
                    saveAsPngImage(bitmap, imagePath);
                    exportPngToGallery(getApplicationContext(), imagePath);
                });
            });
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (cameraRecorder != null) {
            cameraRecorder.stop();
            cameraRecorder.release();
            cameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(() -> {
            FrameLayout frameLayout = findViewById(R.id.wrap_view);
            frameLayout.removeAllViews();
            sampleGLView = null;
            sampleGLView = new SampleGLView(getApplicationContext());
            sampleGLView.setTouchListener((event, width, height) -> {
                if (cameraRecorder == null) return;
                cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
            });
            frameLayout.addView(sampleGLView);
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {
                        runOnUiThread(() -> {
                            findViewById(R.id.btn_flash).setEnabled(flashSupport);
                        });
                    }

                    @Override
                    public void onRecordComplete() {

                        exportMp4ToGallery(getApplicationContext(), filepath);
                        exportMp4ToGallery(getApplicationContext(), filepath2);
                    }

                    @Override
                    public void onRecordStart() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("CameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(() -> {
                                setUpCamera();
                            });
                        }
                        toggleClick = false;
                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();


    }

    private void changeFilter(Filters filters) {
        cameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
    }


    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

            runOnUiThread(() -> {
                bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
            });
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.mp4";
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

}
