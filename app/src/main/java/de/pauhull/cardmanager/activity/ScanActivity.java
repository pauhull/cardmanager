package de.pauhull.cardmanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;

import de.pauhull.cardmanager.R;
import de.pauhull.cardmanager.manager.BarcodeManager;
import de.pauhull.cardmanager.manager.CameraManager;
import de.pauhull.cardmanager.manager.CardManager;
import de.pauhull.cardmanager.util.VibratorUtil;

public class ScanActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private ProgressBar scanningProgress;
    private boolean codeFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        this.surfaceView = this.findViewById(R.id.surfaceView);
        this.scanningProgress = this.findViewById(R.id.scanningProgress);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        this.cameraSetup();
    }

    private void cameraSetup() {

        scanningProgress.animate().alpha(1).setStartDelay(3000).setDuration(500);
        surfaceView.setAlpha(0);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            CameraManager.getInstance().getCameraSource().start(surfaceView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                surfaceView.animate().alpha(1).setStartDelay(1000).setDuration(0);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CameraManager.getInstance().getCameraSource().stop();
                    }
                });
            }
        });

        CameraManager.getInstance().getBarcodeDetector().setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                if (codeFound) {
                    return;
                }

                SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    Barcode barcode = barcodes.valueAt(0);

                    if (!BarcodeManager.getInstance().isIso_8859_1(barcode.rawValue)) {
                        return;
                    }

                    codeFound = true;
                    VibratorUtil.vibratePulse(ScanActivity.this);

                    BarcodeManager.getInstance().setLastBarcodeFormat(barcode.format);
                    BarcodeManager.getInstance().setLastBarcodeValue(barcode.rawValue);
                    CardManager.getInstance().setCardToEdit(null);

                    startActivity(new Intent(ScanActivity.this, EditCardActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.cameraSetup();
    }

    @Override
    protected void onPause() {
        super.onPause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CameraManager.getInstance().getCameraSource().release();
            }
        }).start();
    }
}