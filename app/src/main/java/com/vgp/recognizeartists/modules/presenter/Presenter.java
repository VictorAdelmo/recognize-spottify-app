package com.vgp.recognizeartists.modules.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.common.util.concurrent.ListenableFuture;
import com.vgp.recognizeartists.global.Constants;
import com.vgp.recognizeartists.global.callbacks.ApiCallBack;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.global.enums.ServerError;
import com.vgp.recognizeartists.modules.model.ArtistModel;
import com.vgp.recognizeartists.modules.AppInterface;
import com.vgp.recognizeartists.modules.model.ApiService;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Presenter implements AppInterface.presenter {

    private AppInterface.cameraView mCameraView;
    private AppInterface.analyzeView mAnalyzeView;
    private AppInterface.resultView mResultView;

    private String imgPath;
    private int imageId;
    private ArtistModel artistModel;
    private boolean IsFrontCamera;
    private ImageFrom imageFrom;

    public Presenter() {}

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public ArtistModel getArtistModel() {
        return artistModel;
    }

    public void setArtistModel(ArtistModel artistModel) {
        this.artistModel = artistModel;
    }

    @Override
    public void attachCameraView(AppInterface.cameraView cameraView) {
        this.mCameraView = cameraView;
    }

    @Override
    public void attachAnalyzeView(AppInterface.analyzeView analyzeView) {
        this.mAnalyzeView = analyzeView;
    }

    @Override
    public void attachResultView(AppInterface.resultView resultView) {
        this.mResultView = resultView;
    }

    @Override
    public void elementsClickable() {
        mCameraView.elementsClickable();
    }

    @Override
    public void startArtistRecogntionFrag() {
        mCameraView.startArtistRecogntionFrag();
    }

    @Override
    public void initializeCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = mCameraView.getCameraProvider();
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    if (IsFrontCamera) {
                        mCameraView.showInPreview(CameraSelector.LENS_FACING_FRONT, cameraProvider);
                    }else {
                        mCameraView.showInPreview(CameraSelector.LENS_FACING_BACK, cameraProvider);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, mCameraView.camExecutor());
    }

    @Override
    public void takePicture() {
        Date date = new Date();
        String imgName = String.valueOf(date.getTime());
        setImageFrom(ImageFrom.Camera);
        setImgPath(Constants.IMG_DIR + File.separator + imgName + ".jpg");
        mCameraView.savePicture(imgName);
    }

    @Override
    public void switchCamera() {
        IsFrontCamera =! IsFrontCamera;
        initializeCamera();
    }

    @Override
    public void startAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF
                , 0.5f
                , Animation.RELATIVE_TO_SELF
                , 0.5f);
        rotateAnimation.setDuration(30000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        mResultView.setAnimation(rotateAnimation);
    }

    @Override
    public void setImageProfile() {
        if (getImageFrom() == ImageFrom.Bitmap) {
            mResultView.setImageProfile(getImageId());
        }else {
            mResultView.setImageProfile(getImgPath());
        }
    }

    @Override
    public void setBackgroundImg() {
        if (getImageFrom() == ImageFrom.Bitmap) {
            mAnalyzeView.setBackgroundImg(getImageId());
        }else {
            mAnalyzeView.setBackgroundImg(getImgPath());
        }
    }

    @Override
    public void analyzePicture(Context context) {
        ApiService apiService = null;
        if (getImageFrom() == ImageFrom.Bitmap){
            apiService = new ApiService(context,getImageFrom(),getImageId());
        }else {
            apiService = new ApiService(context,getImageFrom(),getImgPath());
        }

        apiService.sendImgToApi(new ApiCallBack() {
            @Override
            public void success(ArtistModel artistModel) {
                setArtistModel(artistModel);
                mAnalyzeView.startResultFrag();
            }

            @Override
            public void error(ServerError error) {
                String errorMsg = ServerError.convertServerErrorToMsg(error);
                mAnalyzeView.showError(errorMsg);
            }
        });
    }


}




