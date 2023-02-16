package com.vgp.recognizeartists.modules.views;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.modules.AppInterface;
import com.vgp.recognizeartists.modules.presenter.Presenter;
import com.vgp.recognizeartists.global.utils.PathUtils;

import java.util.concurrent.Executor;

public class CameraFragment extends Fragment implements AppInterface.cameraView {

    private PreviewView mPreviewView;
    private ImageView mTakePicture, mGallery, mFlipCamera, mTips;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    private Presenter mPresenter;
    private ImageCapture mImageCapture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mPreviewView = v.findViewById(R.id.previewView);
        mTakePicture = v.findViewById(R.id.cameraImage);
        mFlipCamera = v.findViewById(R.id.imageView11);
        mGallery = v.findViewById(R.id.imageView10);
        mTakePicture = v.findViewById(R.id.cameraImage);
        mTips = v.findViewById(R.id.imageView13);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new Presenter();

        mPresenter.attachCameraView(this);
        mPresenter.initializeCamera();

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCamPressed();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonCamUnpressed();
                    }
                },150);
                mPresenter.takePicture();
            }
        });

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGallery();
            }
        });

        mFlipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.switchCamera();
            }
        });

        mTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTipsActivity();
            }
        });
    }

    @Override
    public void startTipsActivity() {
        mTips.setClickable(false);
        mFlipCamera.setClickable(false);
        mTakePicture.setClickable(false);
        mGallery.setClickable(false);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 0, 0);
            fragmentTransaction.add(R.id.container_frag, new ChooseFragment(mPresenter));
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void startArtistRecogntionFrag() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 0, 0);
        fragmentTransaction.replace(R.id.container_frag, new AnalyzeFragment(mPresenter));
        fragmentTransaction.commit();
    }

    @Override
    public void startGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_GALLERY_IMAGE);
    }

    @Override
    public void elementsClickable() {
        mTips.setClickable(true);
        mFlipCamera.setClickable(true);
        mTakePicture.setClickable(true);
        mGallery.setClickable(true);
    }

    @Override
    public void buttonCamPressed() {
        getActivity().runOnUiThread(() -> {
            mTakePicture.setImageResource(R.drawable.ic_baseline_circle_24);
        });
    }

    @Override
    public void buttonCamUnpressed() {
        getActivity().runOnUiThread(() -> {
            mTakePicture.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24);
        });
    }

    @Override
    public void showInPreview(int lens, ProcessCameraProvider cameraProvider) {
        mImageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lens).build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        if (getCameraProvider() != null) {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) getActivity(), cameraSelector, preview, mImageCapture);
        }
    }

    @Override
    public void savePicture(String imgName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imgName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        mImageCapture.takePicture(new ImageCapture.OutputFileOptions.Builder(getActivity().getContentResolver()
                        , MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , contentValues).build(),
                camExecutor(), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        startArtistRecogntionFrag();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("Erro", exception.getMessage());
                        Toast.makeText(getContext(), "Ocorreu um Erro", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public ListenableFuture<ProcessCameraProvider> getCameraProvider() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        return cameraProviderFuture;
    }

    @Override
    public Executor camExecutor() {
        return ContextCompat.getMainExecutor(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String picturePath = PathUtils.getPathFile(getContext(), selectedImageUri);
                if (picturePath != null) {
                    mPresenter.setImageFrom(ImageFrom.Gallery);
                    mPresenter.setImgPath(picturePath);
                    startArtistRecogntionFrag();
                } else {
                    Toast.makeText(getContext(), "Ocorreu um Erro ao Selecionar A Imagem", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Ocorreu um Erro ao Selecionar A Imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

