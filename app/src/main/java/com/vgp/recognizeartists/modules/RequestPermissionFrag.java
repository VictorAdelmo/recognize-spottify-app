package com.vgp.recognizeartists.modules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.modules.views.CameraFragment;
import com.vgp.recognizeartists.global.utils.PermissionUtils;

public class RequestPermissionFrag extends Fragment {

    private Button mAcceptPermissionsBtn;
    private String[] PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_check_permission,container,false);
        mAcceptPermissionsBtn = v.findViewById(R.id.button5);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAcceptPermissionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionUtils.permissionsGranted(getContext(),PERMISSIONS)){
                    startCameraFragment();
                }else {
                    ActivityCompat.requestPermissions(getActivity(),PERMISSIONS,CODE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionUtils.permissionsGranted(getContext(),PERMISSIONS)){
            startCameraFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startCameraFragment() {
        Fragment fr = new CameraFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container_frag, fr);
        fragmentTransaction.commit();
    }

}
