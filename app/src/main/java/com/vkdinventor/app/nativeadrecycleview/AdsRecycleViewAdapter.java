package com.vkdinventor.app.nativeadrecycleview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;
import com.vkdinventor.app.nativeadrecycleview.model.VideoItem;

import java.util.List;

/**
 * Created by vkdinventor on 04-09-2017
 * Adapter for showing video List
 */
public class AdsRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIDEO_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;
    private static final int LOAD_BUTTON_VIEW = 2;
    private Context mContext = null;
    private List<Object> mVideoListItems = null;
    private LoadMoreItemListener loadMoreItemListener;
    private OnItemClickedListener onItemClickedListener;

    public AdsRecycleViewAdapter(@NonNull Context context, @NonNull List<Object> mVideoListItems) {
        this.mContext = context;
        this.mVideoListItems = mVideoListItems;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setLoadMoreItemListener(LoadMoreItemListener loadMoreItemListener) {
        this.loadMoreItemListener = loadMoreItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case LOAD_BUTTON_VIEW:
                View loadButton = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);
                return new LoadButtonViewHolder(loadButton);

            case VIDEO_ITEM_VIEW_TYPE:
                View videoItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.video_cell, parent, false);
                return new VideoItemViewHolder(videoItemLayoutView);
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                //fall through
            default:
                View nativeExpressLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.native_express_ad_container,
                        parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // TODOBind your data to the views here
        int viewType = getItemViewType(position);
        switch (viewType) {
            case LOAD_BUTTON_VIEW:
                break;
            case VIDEO_ITEM_VIEW_TYPE:

                final VideoItem item = (VideoItem) mVideoListItems.get(position);
                VideoItemViewHolder vh = (VideoItemViewHolder) holder;
                vh.titleTextView.setText(item.getTitle());
                break;

            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                //fall through
            default:
                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) mVideoListItems.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mVideoListItems.size())
            return LOAD_BUTTON_VIEW;
        else
            return (mVideoListItems.get(position) instanceof VideoItem) ? VIDEO_ITEM_VIEW_TYPE : NATIVE_EXPRESS_AD_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return mVideoListItems != null ? mVideoListItems.size() + 1 : 0;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        mVideoListItems.clear();
        addVideoItems(videoItems);
    }

    public void addVideoItems(List<VideoItem> videoItems) {
        mVideoListItems.addAll(videoItems);
        notifyDataSetChanged();
    }

    public interface LoadMoreItemListener {
        void onLoadMore();
    }

    public interface OnItemClickedListener {
        void onItemClicked(VideoItem videoItem);
    }

    private class VideoItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnailImageView;
        private TextView titleTextView;

        private VideoItemViewHolder(View rootView) {
            super(rootView);
            thumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail_image_view);
            titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        }
    }

    private class LoadButtonViewHolder extends RecyclerView.ViewHolder {
        private Button button;

        LoadButtonViewHolder(View view) {
            super(view);
            button = (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loadMoreItemListener != null) {
                        loadMoreItemListener.onLoadMore();
                    }
                }
            });
        }
    }

    private class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }
}
