package com.vgp.recognizeartists.modules.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.adapters.AdapterImages;
import com.vgp.recognizeartists.modules.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;

public class ChooseFragment extends Fragment {

    private Button mClose;
    private Presenter mPresenter;
    private RecyclerView mRecyclerView;

    public ChooseFragment(Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choose,container,false);
        mClose = v.findViewById(R.id.button);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Integer> drawableIds = new ArrayList<>();
        drawableIds.add(R.drawable.anitta);
        drawableIds.add(R.drawable.michael);
        drawableIds.add(R.drawable.chris);
        drawableIds.add(R.drawable.the_weeknd);
        drawableIds.add(R.drawable.travis);
        drawableIds.add(R.drawable.donald);

        AdapterImages adapter = new AdapterImages(getContext(),drawableIds,mPresenter);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setHasFixedSize(true);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                    mPresenter.elementsClickable();
                }
            }
        });

    }
}
