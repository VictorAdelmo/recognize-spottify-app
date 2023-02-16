package com.vgp.recognizeartists.modules;

import android.content.ContentValues;
import android.content.Context;
import android.view.animation.RotateAnimation;

import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

public interface AppInterface {

    interface presenter {
        void attachCameraView(AppInterface.cameraView cameraView);
        void attachAnalyzeView(AppInterface.analyzeView analyzeView);
        void attachResultView(AppInterface.resultView resultView);
        void elementsClickable();
        void startArtistRecogntionFrag();
        void initializeCamera();
        void analyzePicture(Context context);
        void takePicture();
        void switchCamera();
        void startAnimation();
        void setImageProfile();
        void setBackgroundImg();
    }

    interface cameraView {
        void startTipsActivity();
        void startArtistRecogntionFrag();
        void startGallery();
        void elementsClickable();
        void buttonCamPressed();
        void buttonCamUnpressed();
        void showInPreview(int lens, ProcessCameraProvider cameraProvider);
        void savePicture(String img);
        ListenableFuture<ProcessCameraProvider> getCameraProvider();
        Executor camExecutor();
    }

    interface analyzeView{
        void startResultFrag();
        void showError(String errorMsg);
        void backToHome();
        void setBackgroundImg(Object img);
    }

    interface resultView{
        void setAnimation(RotateAnimation rotateAnimation);
        void pauseAnimation();
        void setImageProfile(Object img);
        void goToCameraFragment();
    }





}
