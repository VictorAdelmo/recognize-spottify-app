package com.vgp.recognizeartists.global.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vgp.recognizeartists.R;
import com.vgp.recognizeartists.global.enums.ImageFrom;
import com.vgp.recognizeartists.modules.presenter.Presenter;

import java.util.List;

public class AdapterImages extends RecyclerView.Adapter<AdapterImages.ViewHolder> {

    private Context mContext;
    private List<Integer> mList;
    private Presenter mPresenter;

    public AdapterImages(Context mContext, List<Integer> mList, Presenter presenter) {
        this.mContext = mContext;
        this.mList = mList;
        this.mPresenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_imgs,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int imgItem = mList.get(position);
        ImageView imgView;

        imgView = holder.img;

        Glide.with(mContext)
                .asBitmap()
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .load(imgItem)
                .override(150,150)
                .into(imgView);


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.setImageFrom(ImageFrom.Bitmap);
                mPresenter.setImageId(imgItem);
                mPresenter.elementsClickable();
                mPresenter.startArtistRecogntionFrag();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView3);
        }
    }
}

