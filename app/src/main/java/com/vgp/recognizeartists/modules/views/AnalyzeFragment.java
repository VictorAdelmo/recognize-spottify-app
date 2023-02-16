package com.vgp.recognizeartists.modules.views;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.modules.AppInterface;
import com.vgp.recognizeartists.modules.presenter.Presenter;

import java.io.File;

public class AnalyzeFragment extends Fragment implements AppInterface.analyzeView {

    public AnalyzeFragment(Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    private final Presenter mPresenter;
    private Button mBackToHomeBtn;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private ImageView background;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analyze_image, container, false);
        mBackToHomeBtn = v.findViewById(R.id.button6);
        mMessageTextView = v.findViewById(R.id.textView12);
        mProgressBar = v.findViewById(R.id.progressBar);
        background = v.findViewById(R.id.imageView4);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.attachAnalyzeView(this);
        mPresenter.setBackgroundImg();
        mPresenter.analyzePicture(getContext());

        mBackToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
    }

    @Override
    public void startResultFrag() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 0, 0);
        fragmentTransaction.replace(R.id.container_frag, new ResultFragment(mPresenter));
        fragmentTransaction.commit();
    }

    @Override
    public void showError(String error) {
        getActivity().runOnUiThread(() -> {
            mMessageTextView.setTextColor(Color.RED);
            mMessageTextView.setText(error);
            mProgressBar.setVisibility(View.GONE);
            mBackToHomeBtn.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void backToHome() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, 0, 0);
        fragmentTransaction.replace(R.id.container_frag, new CameraFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void setBackgroundImg(Object img) {
        Glide.with(getContext())
                .asBitmap()
                .centerCrop()
                .load(img)
                .into(background);
    }
}



