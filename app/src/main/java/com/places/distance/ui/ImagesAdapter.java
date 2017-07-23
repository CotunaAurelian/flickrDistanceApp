package com.places.distance.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.places.distance.R;
import com.places.distance.domain.model.ImageData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Adapter for the image loading mechanism
 * Created by Aurelian Cotuna
 */

public class ImagesAdapter extends RecyclerView.Adapter {

    private ArrayList<ImageData> mImageDataList;
    private Context mContext;

    public ImagesAdapter(Context context) {
        this.mImageDataList = new ArrayList<>();
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_layout, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = ((ViewHolder) holder).mImageView;

        Glide.with(mContext)
                .load(mImageDataList.get(position).getUrl())
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mImageDataList.size();
    }

    public void setData(ArrayList<ImageData> datas) {
        mImageDataList = datas;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
