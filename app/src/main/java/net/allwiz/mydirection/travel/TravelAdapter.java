/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.travel;

import android.content.Context;
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

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.LabelViewHolder> {
    private static final String TAG = TravelAdapter.class.getSimpleName();

    private static TravelAdapter.ItemClickListener mClickListener;


    public static class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public LabelViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.fragment_travel_recycler_view_item_image);
            mLabelName = (TextView) view.findViewById(R.id.fragment_travel_recycler_view_item_label_name);
            mDirectionNames = (TextView) view.findViewById(R.id.fragment_travel_recycler_view_item_direction_names);
            mDirectionCount = (TextView) view.findViewById(R.id.fragment_travel_recycler_view_item_direction_count);


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
    private int                     mCategory = Category.TRAVEL;


    public TravelAdapter(Context context, List<LabelDescItem> items) {
        this.mContext = context;
        this.mItems = items;
        this.mCategory = Category.TRAVEL;
    }


    @NonNull
    @Override
    public TravelAdapter.LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_travel_recycler_view_item, parent, false);
        return new TravelAdapter.LabelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TravelAdapter.LabelViewHolder holder, int position) {

        LabelDescItem item = mItems.get(position);
        if (item != null) {

            holder.getLabelName().setText(item.name);
            holder.getLabelDirectionItemNames().setText(item.itemNames);

            String itemCount = String.valueOf(item.itemCount);
            if (item.itemCount < 1) {
                itemCount = mContext.getString(R.string.item_count_0);
            } else if (item.itemCount > 20) {
                itemCount = mContext.getString(R.string.item_count_over_20);
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


    public void setOnItemClickListener(TravelAdapter.ItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
