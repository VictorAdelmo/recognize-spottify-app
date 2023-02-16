package com.vgp.recognizeartists.modules;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;

import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.utils.PermissionUtils;
import com.vgp.recognizeartists.modules.views.CameraFragment;
import com.vgp.recognizeartists.modules.views.ResultFragment;

public class MainActivity extends AppCompatActivity {

    private String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PermissionUtils.permissionsGranted(this,PERMISSIONS)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_frag,
                    new CameraFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_frag,
                    new RequestPermissionFrag()).commit();
        }

    }
}