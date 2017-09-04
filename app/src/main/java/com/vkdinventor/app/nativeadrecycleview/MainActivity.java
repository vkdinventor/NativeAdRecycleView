package com.vkdinventor.app.nativeadrecycleview;

import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.vkdinventor.app.nativeadrecycleview.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ITEMS_PER_AD = 3;
    List<Object> mRecyclerViewItems;
    RecyclerView mRecyclerView;
    AdsRecycleViewAdapter mListAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String testID = "ca-app-pub-3940256099942544~3347511713";
        MobileAds.initialize(this, testID);
        mRecyclerView = (RecyclerView) findViewById(R.id.nativeadList);

        mContext = this;
        setupRecycleView();
        addNativeExpressAds(3);

    }

    void setupRecycleView() {
        mRecyclerViewItems = new ArrayList<>();
        for (int i=0; i< 20; i++){
            mRecyclerViewItems.add(new VideoItem("Video item no : "+i));
        }
        mListAdapter = new AdsRecycleViewAdapter(mContext, mRecyclerViewItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setOnItemClickedListener(new AdsRecycleViewAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(VideoItem videoItem) {
                Toast.makeText(mContext, "Playing "+videoItem.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        mListAdapter.setLoadMoreItemListener(new AdsRecycleViewAdapter.LoadMoreItemListener() {
            @Override
            public void onLoadMore() {
                Toast.makeText(mContext, "Loading...", Toast.LENGTH_LONG).show();
                Log.d("TAG", "Loading more video");
            }
        });
    }

    private void addNativeExpressAds(int initialPosition) {
        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = initialPosition; i <= mRecyclerViewItems.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(mContext);
            Log.v(" Ad", "adding ads at position " + i);
            mRecyclerViewItems.add(i, adView);
        }
        setUpAndLoadNativeExpressAds(initialPosition);
    }

    private void setUpAndLoadNativeExpressAds(final int offset) {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = mContext.getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                for (int i = offset; i < mRecyclerViewItems.size(); i++) {
                    if (!(mRecyclerViewItems.get(i) instanceof NativeExpressAdView)) {
                        continue;
                    }
                    final NativeExpressAdView adView =
                            (NativeExpressAdView) mRecyclerViewItems.get(i);
                    AdSize adSize = new AdSize(320,138);
                    adView.setAdSize(adSize);
                    //ad id for small ad "ca-app-pub-3940256099942544/2793859312"
                    adView.setAdUnitId("ca-app-pub-3940256099942544/2793859312");
                    //adView.setAdUnitId("ca-app-pub-3940256099942544/2177258514");

                    Log.v(" Ad", "Loading Native ads at position " + i);
                }

                // Load the first Native Express ad in the items list.
                loadNativeExpressAd(offset);
            }
        });
    }

    /**
     * Loads the Native Express ads in the items list.
     */
    private void loadNativeExpressAd(final int index) {

        if (index >= mRecyclerViewItems.size()) {
            return;
        }

        Object item = mRecyclerViewItems.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            return;
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous ad failed to load. Attempting to"
                        + " load the next  ad in the items list at position " + index);
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        // Load the Native Express ad.
        try {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
