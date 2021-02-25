/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.recent;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Direction;


import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.PlaceViewHolder> {
    private static final String TAG = RecentAdapter.class.getSimpleName();

    private static RecentAdapter.ItemClickListener mClickListener;

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public PlaceViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.fragment_recent_recycler_view_item_image);
            mName = (TextView) view.findViewById(R.id.fragment_recent_recycler_view_item_place_name);
            mAddress = (TextView) view.findViewById(R.id.fragment_recent_recycler_view_item_place_address);
            mLastVisitDate = (TextView) view.findViewById(R.id.fragment_recent_recycler_view_item_last_visit_date);
            mHitCount = (TextView) view.findViewById(R.id.fragment_recent_recycler_view_item_hit_count);

            mTransportModeImage = (ImageView) view.findViewById(R.id.fragment_recent_recycler_view_item_transition_mode_image);
            mMoreMenuImage = (ImageView) view.findViewById(R.id.fragment_recent_recycler_view_item_more_image);

            mMoreMenuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemMoreClick(v, getAdapterPosition());
                    }
                }
            });

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        private final ImageView     mImageView;
        private final TextView      mName;          // Place Name
        private final TextView      mAddress;       // Address
        private final TextView      mLastVisitDate; // Last Visit Date
        private final TextView      mHitCount; // Visited count

        private final ImageView     mTransportModeImage;
        private final ImageView     mMoreMenuImage;


        public TextView getPlaceName() {
            return mName;
        }

        public TextView getAddress() {
            return mAddress;
        }

        public TextView getLastDate() {
            return mLastVisitDate;
        }

        public void setName(String placeName) {
            mName.setText(placeName);
        }

        public void setAddress(String address) {
            mAddress.setText(address);
        }

        public void setLastAccessDate(String lastAccessDate) {
            mLastVisitDate.setText(lastAccessDate);
        }

        public void setHitCount(String visitedCount) {
            mHitCount.setText(visitedCount);
        }

        public void setImageViewBackground(int color) {
            mImageView.setBackgroundColor(color);
        }

        public void setTransportModeImage(String mode) {
            switch (mode) {
                case Direction.Mode.DRIVING:
                    mTransportModeImage.setImageResource(R.drawable.ic_directions_car_blue);
                    break;
                case Direction.Mode.TRANSIT:
                    mTransportModeImage.setImageResource(R.drawable.ic_directions_transit_blue);
                    break;
                case Direction.Mode.BICYCLING:
                    mTransportModeImage.setImageResource(R.drawable.ic_directions_bike_blue);
                    break;
                case Direction.Mode.WALKING:
                    mTransportModeImage.setImageResource(R.drawable.ic_directions_run_blue);
                    break;
            }
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
    private List<DirectionItem> mItems;


    public RecentAdapter(Context context, List<DirectionItem> items) {
        this.mContext = context;
        this.mItems = items;
    }


    @NonNull
    @Override
    public RecentAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recent_recycler_view_item, parent, false);
        return new RecentAdapter.PlaceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecentAdapter.PlaceViewHolder holder, int position) {

        DirectionItem item = mItems.get(position);
        if (item != null) {
            holder.setName(item.name);
            holder.setAddress(item.address);

            if (item.hitCount > 0) {
                holder.setLastAccessDate(String.format("%s: %s", mContext.getString(R.string.last_visit), item.accessDate));
            } else {
                holder.setLastAccessDate("");
            }

            String visitedCount = "";
            if (item.hitCount > 0 && item.hitCount <= 20) {
                visitedCount = String.format("%d", item.hitCount);
            } else if (item.hitCount > 20) {
                visitedCount = mContext.getString(R.string.item_count_over_20);
            }

            holder.setHitCount(visitedCount);
            holder.setTransportModeImage(item.mode);
        }
    }

    @Override
    public int getItemCount() {
        return (this.mItems == null ? 0 : this.mItems.size());
    }


    public void refresh(List<DirectionItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(RecentAdapter.ItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onItemMoreClick(View view, int position);
    }
}

