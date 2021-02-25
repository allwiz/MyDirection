package net.allwiz.mydirection.place;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private static final String TAG = PlaceAdapter.class.getSimpleName();

    private static ItemClickListener    mClickListener;

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public PlaceViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.fragment_place_recycler_view_item_image);
            mName = (TextView) view.findViewById(R.id.fragment_place_recycler_view_item_place_name);
            mAddress = (TextView) view.findViewById(R.id.fragment_place_recycler_view_item_place_address);
            mLastVisitDate = (TextView) view.findViewById(R.id.fragment_place_recycler_view_item_last_visit_date);
            mHitCount = (TextView) view.findViewById(R.id.fragment_place_recycler_view_item_hit_count);

            mTransportModeImage = (ImageView) view.findViewById(R.id.fragment_place_recycler_view_item_transition_mode_image);
            mMoreMenuImage = (ImageView) view.findViewById(R.id.fragment_place_recycler_view_item_more_image);

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
    private TypedArray          mColors;


    public PlaceAdapter(Context context, List<DirectionItem> items) {
        this.mContext = context;
        this.mItems = items;
        this.mColors = context.getResources().obtainTypedArray(R.array.item_colors);
    }


    @NonNull
    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_place_recycler_view_item, parent, false);
        return new PlaceAdapter.PlaceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceViewHolder holder, int position) {

        DirectionItem item = mItems.get(position);
        if (item != null) {
/*
            Drawable background = holder.mImageView.getBackground();
            if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                int index = position % mColors.length();
                int color = mColors.getColor(index, 0);
                gradientDrawable.setColor(color);
            }
            //holder.setImageViewBackground(color);
*/
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
/*
            String itemCount = String.valueOf(item.itemCount);
            if (item.itemCount < 1) {
                itemCount = mContext.getString(R.string.empty_string);
            } else if (item.itemCount > 20) {
                itemCount = mContext.getString(R.string.item_count_over_20);
            }

            holder.getLabelDirectionItemCount().setText(itemCount);
*/
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


    public void setOnItemClickListener(ItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onItemMoreClick(View view, int position);
    }
}

