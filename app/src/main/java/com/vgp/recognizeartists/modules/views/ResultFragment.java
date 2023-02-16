package com.vgp.recognizeartists.modules.views;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;
import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.modules.AppInterface;
import com.vgp.recognizeartists.modules.model.ArtistModel;
import com.vgp.recognizeartists.modules.presenter.Presenter;

import java.io.File;
import java.text.NumberFormat;

public class ResultFragment extends Fragment implements AppInterface.resultView {

    private Presenter mPresenter;
    private static final String CLIENT_ID = "";
    private static final String REDIRECT_URI = "";
    private SpotifyAppRemote mSpotifyAppRemote;
    private ImageView mPlayBtn, mPreviousBtn, mNextBtn, mAlbumImg, mProfileImg, mBackBtn;
    private TextView mTrackNameTxt, mAgeTxt, mWorthTxt, mHeightTxt, mArtistTopName, mBiographyTxt;
    private ArtistModel mArtistModel;
    private boolean isPlaying = true;

    public ResultFragment(Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);
        mProfileImg = v.findViewById(R.id.imageView2);
        mArtistTopName = v.findViewById(R.id.textView);
        mTrackNameTxt = v.findViewById(R.id.textView5);
        mWorthTxt = v.findViewById(R.id.textView20);
        mHeightTxt = v.findViewById(R.id.textView16);
        mAgeTxt = v.findViewById(R.id.textView14);
        mPlayBtn = v.findViewById(R.id.imageView16);
        mPreviousBtn = v.findViewById(R.id.imageView12);
        mNextBtn = v.findViewById(R.id.imageView14);
        mAlbumImg = v.findViewById(R.id.imageView6);
        mBiographyTxt = v.findViewById(R.id.textView2);
        mBackBtn = v.findViewById(R.id.imageView18);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.attachResultView(this);
        mPresenter.setImageProfile();

        mArtistModel = mPresenter.getArtistModel();
        mBiographyTxt.setText(mArtistModel.getBiography() + "");
        mAgeTxt.setText(mArtistModel.getAge() + "");
        mHeightTxt.setText(mArtistModel.getHeight() + "");
        mArtistTopName.setText(mArtistModel.getName() + "");

        String currency =  NumberFormat.getCurrencyInstance().format(mArtistModel.getWorth()) + " USD";
        mWorthTxt.setText(currency);

        connect(true);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });

        mPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCameraFragment();
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    mSpotifyAppRemote.getPlayerApi().resume();
                }else {
                    mSpotifyAppRemote.getPlayerApi().pause();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().pause();
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    @Override
    public void setImageProfile(Object img){
        Glide.with(getContext())
                .asBitmap()
                .centerCrop()
                .load(img)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .override(120, 120)
                .into(mProfileImg);
    }

    @Override
    public void goToCameraFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 0, 0);
        fragmentTransaction.replace(R.id.container_frag, new CameraFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void setAnimation(RotateAnimation rotateAnimation) {
        mAlbumImg.setAnimation(rotateAnimation);
    }

    @Override
    public void pauseAnimation() {
        mAlbumImg.clearAnimation();
    }

    private void connect(boolean showAuthView) {
        SpotifyAppRemote.connect(
                getContext(),
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(showAuthView)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        mSpotifyAppRemote.getPlayerApi().play("spotify:artist:" + mArtistModel.getPlayListKey());
                        registryPlayer();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e("MyActivity", error.getMessage(), error);
                        mTrackNameTxt.setText("An Error Ocurred, please check if you are logged in spotify");
                        mTrackNameTxt.setTextColor(Color.RED);
                    }
                });
    }

    private void registryPlayer(){
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    isPlaying = playerState.isPaused;
                    if (playerState.isPaused) {
                        pauseAnimation();
                        mPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }else {
                        mPresenter.startAnimation();
                        mPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
                    }

                    if (track != null) {
                        mTrackNameTxt.setText(track.name);
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(track.imageUri, Image.Dimension.SMALL)
                                .setResultCallback(
                                        bitmap -> {
                                            Glide.with(getContext())
                                                    .asBitmap()
                                                    .circleCrop()
                                                    .load(bitmap)
                                                    .override(180,180)
                                                    .into(mAlbumImg);
                                        });

                    }
                });
    }
}
