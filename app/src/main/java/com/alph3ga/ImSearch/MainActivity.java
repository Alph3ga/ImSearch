package com.alph3ga.ImSearch;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.core.content.FileProvider.getUriForFile;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.time.*;

import com.alph3ga.ImSearch.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    private boolean request_granted = true;
    ActivityResultLauncher galleryContract;
    ActivityResultLauncher<Uri> cameraContract;
    Uri savedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View mainView = mainBinding.getRoot();
        setContentView(mainView);

        mainBinding.getFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{CAMERA}, 1);
                }
                if (!request_granted) {
                    request_granted = true;
                    return;
                }
                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (!request_granted) {
                    request_granted = true;
                    return;
                }
                //openCamera(mainBinding.textInput.getText().toString());
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        mainBinding.getFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{READ_MEDIA_IMAGES}, 2);
                }
                if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 2);
                }
                if (!request_granted) {
                    request_granted = true;
                    return;
                }
                openGallery(mainBinding.textInput.getText().toString());
            }
        });

        galleryContract = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) {
                    return;
                }
                processImage(result);
            }
        });

        cameraContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result == false) {
                    System.out.println("\n\n\n\n\ndidnt save");
                    return;
                }
                processImage(savedUri);
            }
        });
    }

    private void openGallery(String searchText) {
        galleryContract.launch(new PickVisualMediaRequest.Builder()
                .setMediaType((ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    private void openCamera(String searchText) {
        getApplicationContext().getCacheDir().delete();
        File mdir= new File(getApplicationContext().getCacheDir(), "images");
        File mfile= new File(mdir.getPath()+File.separator+java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss"))+".jpeg");
        savedUri= FileProvider.getUriForFile(getApplicationContext(),"com.alph3ga.fileprovider", mfile);
        System.out.println("\n\n\n\n\n\n"+savedUri.toString());
        cameraContract.launch(savedUri);
    }

    private void processImage(Uri result) {
        System.out.println("\n\n\n\nGot Uri as");
        System.out.println(result.toString()+"\n\n\n\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    request_granted = false;
                    Toast.makeText(getApplicationContext(), "Cannot open camera", Toast.LENGTH_SHORT).show();
                }
                return;
            case 2:
                if (grantResults.length > 0 &&
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    request_granted = false;
                    Toast.makeText(getApplicationContext(), "Cannot open gallery", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public static class MyFileProvider extends FileProvider {
        public MyFileProvider() {
            super(R.xml.file_paths);
        }
    }
}

