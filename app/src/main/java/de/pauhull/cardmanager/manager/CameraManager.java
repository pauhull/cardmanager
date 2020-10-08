package de.pauhull.cardmanager.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class CameraManager {

    private static CameraManager instance;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private CameraManager() {
    }

    public boolean checkCameraPermissions(Activity context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, 201);
            return false;
        }

        return true;
    }

    public void initCamera(Context context) {

        this.barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        this.cameraSource = new CameraSource.Builder(context, CameraManager.getInstance().getBarcodeDetector())
                .setRequestedPreviewSize(1000, 1000)
                .setAutoFocusEnabled(true)
                .setRequestedFps(60)
                .build();
    }

    public static void create() {
        if (instance == null) {
            instance = new CameraManager();
        }
    }

    public static CameraManager getInstance() {
        return instance;
    }

    public CameraSource getCameraSource() {
        return cameraSource;
    }

    public void setCameraSource(CameraSource cameraSource) {
        this.cameraSource = cameraSource;
    }

    public BarcodeDetector getBarcodeDetector() {
        return barcodeDetector;
    }

    public void setBarcodeDetector(BarcodeDetector barcodeDetector) {
        this.barcodeDetector = barcodeDetector;
    }
}
