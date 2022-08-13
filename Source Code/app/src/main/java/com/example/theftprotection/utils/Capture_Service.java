package com.example.theftprotection.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class Capture_Service extends Service {
    protected static final String TAG = "";
    protected static final int CAMERACHOICE = CameraCharacteristics.LENS_FACING_FRONT;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession session;
    String longitude = "0";
    String latitude = "0";
    protected ImageReader imageReader;
    private String email;
    final String path = Environment.getExternalStorageDirectory().toString() + "/theftprotection/images";
    private final File mFile = new File(path, SimpleDateFormat.getDateTimeInstance().format(new Date()) + ".jpg");

    protected final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG, "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            actOnReadyCameraDevice();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.w(TAG, "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice.StateCallback onError " + error);
        }
    };

    protected final CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.i(TAG, "CameraCaptureSession.StateCallback onConfigured");
            Capture_Service.this.session = session;
            try {
                session.setRepeatingRequest(createCaptureRequest(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    protected final ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        Log.i(TAG, "onImageAvailable");
        Image img = reader.acquireLatestImage();
        if (img != null) {
            try {
                processImage(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
            img.close();
        }
        this.onDestroy();
    };


    @SuppressLint("MissingPermission")
    public void readyCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String pickedCamera = getCamera(manager);
            manager.openCamera(pickedCamera, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(320, 240, ImageFormat.JPEG, 1 /* images buffered */);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
            Log.i(TAG, "imageReader created");
            Toast.makeText(
                    getApplicationContext(), "imageReader created", Toast.LENGTH_LONG).show();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Return the Camera Id which matches the field CAMERACHOICE.
     */
    public String getCamera(CameraManager manager) {
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CAMERACHOICE) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand flags " + flags + " startId " + startId);
        readyCamera();
        return super.onStartCommand(intent, flags, startId);
    }

    public void actOnReadyCameraDevice() {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public void onDestroy() {
        try {
            session.abortCaptures();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        session.close();
    }


    private static class ImageSaver implements Runnable {
        private final Image mImage;
        final File mFile;

        ImageSaver(Image image, File file) {
            this.mImage = image;
            this.mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(this.mFile);
                output.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e("Saving Image", this.mFile.getAbsolutePath());
        }
    }

    private void processImage(Image image) throws IOException {
        ImageSaver imageSaver = new ImageSaver(image, mFile);
        imageSaver.run();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (Boolean.parseBoolean(sharedPreferences.getString("email", "false")))
            Log.e("","sending emailxxx");
            sendEmail(mFile.getName());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("failed", "0");
        editor.apply();
    }

    protected CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getProviderData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                this.email = profile.getEmail();
            }
        }
    }

    private String getlocation(Context context) {
        LocationTrackService locationTrackService;
        locationTrackService = new LocationTrackService(context);
        if (locationTrackService.canGetLocation()) {
            this.longitude = String.valueOf(locationTrackService.getLongitude());
            this.latitude = String.valueOf(locationTrackService.getLatitude());
        }
        Log.e("GPS loc", locationTrackService.getLatitude() + " " + locationTrackService.getLongitude());
        return ("Longitude " + locationTrackService.getLongitude() + "\nLatitude: " + locationTrackService.getLatitude());
    }

    private void sendEmail(String file) throws MalformedURLException {
        Log.e("", "Sending Email");
        getProviderData();
        String str="<html>" +
                "<body>" +
                "<h1>Your device "+new Device().getdevicemodel() +
                "has been Accessed by Someone at "+getlocation(this)+"</h1>";

        URL url= new URL("https://www.google.com/maps/search/?api=1&query="+this.latitude+","+this.longitude);
        Log.e("FILE ",mFile.getAbsolutePath());
        str +="<div style='text-align:center'>" +
                "<a style='display: inline-block;\n" +
                "        text-decoration: none;\n"+
                "        background-color: #32CD32;\n" +
                "        border-radius: 10px;\n" +
                "        border: 4px double #cccccc;\n" +
                "        color: #eeeeee;\n" +
                "        text-align: center;\n" +
                "        font-size: 20px;\n" +
                "        padding: 10px;\n" +
                "        width: 150px;\n" +
                "        -webkit-transition: all 0.5s;\n" +
                "        -moz-transition: all 0.5s;\n" +
                "        -o-transition: all 0.5s;\n" +
                "        transition: all 0.5s;\n" +
                "        cursor: pointer;\n" +
                "        margin: 5px;' href="+url+">Track Location</a>" +
                "</div" +
                "</body>" +
                "</html>";


      final GMailSender gMailSender = new GMailSender("aakashdubey91744@gmail.com", "A@kash09072001");
        try {
            gMailSender.sendMail("Theft Protection : Alert",
                    str,
                    "aakashdubey91744@gmail.com",
                    email,file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}