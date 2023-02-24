package com.alph3ga.ImSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.alph3ga.ImSearch.databinding.ActivityCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding cameraBinding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ImageCapture imageCapture= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraBinding= ActivityCameraBinding.inflate(getLayoutInflater());
        View cameraView= cameraBinding.getRoot();
        setContentView(cameraView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (Exception e) {
                // Google says this error will never happen so o7
            }
        }, ContextCompat.getMainExecutor(this));

        cameraBinding.captureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*File mdir= new File(getApplicationContext().getExternalCacheDir(), "images");
                File mfile= new File(mdir.getPath()+File.separator+java.time.LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss"))+".jpeg");

                ImageCapture.OutputFileOptions outputFileOptions= new ImageCapture.OutputFileOptions
                        .Builder(mfile).build();*/

                String name= java.time.LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss"));
                ContentValues contentValues= new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ImSearch");

                ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions
                        .Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues)
                        .build();

                imageCapture.takePicture(outputOptions,
                        ContextCompat.getMainExecutor(getApplicationContext()),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                System.out.println("Saved at"+ outputFileResults.getSavedUri());
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                System.err.println("\n\n\n\n\n"+exception.toString());
                            }
                        });
            }
        }
        );
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        PreviewView previewView= cameraBinding.preview;

        imageCapture =
                 new ImageCapture.Builder()
                         .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try{
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);}
        catch (Exception e){
            System.out.println("\n\n\n\n\n"+e.toString());
        }
    }
}