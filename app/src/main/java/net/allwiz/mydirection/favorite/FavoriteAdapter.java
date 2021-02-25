/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.favorite;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.database.LabelDescItem;
import net.allwiz.mydirection.define.Category;


import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.LabelViewHolder> {
    private static final String TAG = FavoriteAdapter.class.getSimpleName();

    private static FavoriteAdapter.ItemClickListener mClickListener;


    public static class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public LabelViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.fragment_favorite_recycler_view_item_image);
            mLabelName = (TextView) view.findViewById(R.id.fragment_favorite_recycler_view_item_label_name);
            mDirectionNames = (TextView) view.findViewById(R.id.fragment_favorite_recycler_view_item_direction_names);
            mDirectionCount = (TextView) view.findViewById(R.id.fragment_favorite_recycler_view_item_direction_count);


            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        private final ImageView        mImageView;
        private final TextView         mLabelName;
        private final TextView         mDirectionNames;   // direction names
        private final TextView         mDirectionCount;   // direction count


        public ImageView getLabelImage() {
            return mImageView;
        }


        public TextView getLabelName() {
            return mLabelName;
        }

        public TextView getLabelDirectionItemNames() {
            return mDirectionNames;
        }

        public TextView getLabelDirectionItemCount() {
            return mDirectionCount;
        }




        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(v, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(v, getAdapterPosition());
            return false;
        }

    }


    private Context mContext;
    private List<LabelDescItem> mItems;
    private int                     mCategory = Category.FAVORITE;


    public FavoriteAdapter(Context context, List<LabelDescItem> items) {
        this.mContext = context;
        this.mItems = items;
        this.mCategory = Category.FAVORITE;
    }

    public FavoriteAdapter(Context context, List<LabelDescItem> items, int category) {
        this.mContext = context;
        this.mItems = items;
        this.mCategory = category;
    }


    @NonNull
    @Override
    public FavoriteAdapter.LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite_recycler_view_item, parent, false);
        return new FavoriteAdapter.LabelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.LabelViewHolder holder, int position) {

        LabelDescItem item = mItems.get(position);
        if (item != null) {

            holder.getLabelName().setText(item.name);
            holder.getLabelDirectionItemNames().setText(item.itemNames);

            String itemCount = String.valueOf(item.itemCount);
            if (item.itemCount < 1) {
                itemCount = mContext.getString(R.string.item_count_0);
            } else if (item.itemCount > 20) {
                itemCount = mContext.getString(R.string.item_count_over_20);
                // If you want to change color, you can use below the codes.
                //holder.getLabelDirectionItemCount().setBackgroundResource(R.drawable.round_corner_orange_text);
                //holder.getLabelDirectionItemCount().setTextColor(Color.WHITE);
            }

            holder.getLabelDirectionItemCount().setText(itemCount);
        }
    }

    @Override
    public int getItemCount() {
        return (this.mItems == null ? 0 : this.mItems.size());
    }


    public void refresh(List<LabelDescItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(FavoriteAdapter.ItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
