package com.vg.airlinelistapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.vg.airlinelistapplication.airlines.AirlinesListFetcher;
import com.vg.airlinelistapplication.airlines.adapter.AirlineListAdapter;
import com.vg.airlinelistapplication.airlines.dataconverter.AirlineListDataConverter;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.constants.Constants;
import com.vg.airlinelistapplication.helper.HelperActivity;
import com.vg.airlinelistapplication.storage.AirlineTravelPreferenceHelper;

import java.util.ArrayList;

public class TravelHomeActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {

    private ListView lvAirlineList;
    private ProgressBar progressLoading;
    private RadioGroup rgSortList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BroadcastReceiver mAirlineResultReceiver;
    private AirlineListAdapter mAirlineListAdapter;
    private ArrayList<AirlineClass> mAirlineClassArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_home);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReciever();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvShowHelpfulHints:
                showHints(TravelHomeActivity.this);
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        showAirlineDetailActivity(mAirlineClassArrayList.get(position));
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup != null) {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rbtnAll) {
                mAirlineClassArrayList = AirlineListDataConverter.retrieveAirlineArrayList(
                        AirlineTravelPreferenceHelper.getAirlinePreferenceList(TravelHomeActivity.this));
                mAirlineListAdapter = new AirlineListAdapter(TravelHomeActivity.this, R.layout.layout_travel_home_list_item, mAirlineClassArrayList);
                lvAirlineList.setAdapter(mAirlineListAdapter);
                mAirlineListAdapter.notifyDataSetChanged();
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.rbtnFav) {
                mAirlineClassArrayList = AirlineListDataConverter.getFavoriteListFromPreferences(TravelHomeActivity.this);
                mAirlineListAdapter = new AirlineListAdapter(TravelHomeActivity.this, R.layout.layout_travel_home_list_item, mAirlineClassArrayList);
                lvAirlineList.setAdapter(mAirlineListAdapter);
                mAirlineListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Setup the basic views for interaction.
     */
    private void setupViews() {
        rgSortList = (RadioGroup) findViewById(R.id.rgSortList);
        rgSortList.setOnCheckedChangeListener(this);
        TextView tvShowHints = (TextView) findViewById(R.id.tvShowHelpfulHints);
        tvShowHints.setText(Html.fromHtml(getString(R.string.show_helpful_hints)));
        tvShowHints.setOnClickListener(this);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        lvAirlineList = (ListView) findViewById(R.id.lvAirlineList);
        lvAirlineList.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AirlinesListFetcher.getInstance(TravelHomeActivity.this).fetchAirlineList(createAirlineListUrl());
            }
        });
    }

    /**
     * Show the Hints overlay
     * @param context - the calling application context.
     */
    private void showHints(Context context) {
        Intent intentHelperTips = new Intent(context, HelperActivity.class);
        startActivity(intentHelperTips);
        overridePendingTransition(android.R.anim.fade_in, 0);
    }

    private void showHideListView() {
        if (TextUtils.isEmpty(AirlineTravelPreferenceHelper.getAirlinePreferenceList(TravelHomeActivity.this))) {
            progressLoading.setVisibility(View.GONE);
            lvAirlineList.setVisibility(View.GONE);
        } else {
            // TODO invalidate the listview adapter.
            progressLoading.setVisibility(View.GONE);
            lvAirlineList.setVisibility(View.VISIBLE);
            mAirlineClassArrayList = AirlineListDataConverter.retrieveAirlineArrayList(
                    AirlineTravelPreferenceHelper.getAirlinePreferenceList(TravelHomeActivity.this));
            mAirlineListAdapter = new AirlineListAdapter(TravelHomeActivity.this, R.layout.layout_travel_home_list_item, mAirlineClassArrayList);
            lvAirlineList.setAdapter(mAirlineListAdapter);
            if (AirlineTravelPreferenceHelper.isAppFirstTimeLaunched(TravelHomeActivity.this)) {
                showHints(TravelHomeActivity.this);
            }
        }
    }

    /**
     * Initialize Receiver
     */
    private void initializeReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.RESPONSE_AIRLINE_RESULT);
        if (mAirlineResultReceiver == null) {
            mAirlineResultReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getBooleanExtra(Constants.RESPONSE_STATUS, false)) {
                        showHideListView();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        showHideListView();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            };
        }
        registerReceiver(mAirlineResultReceiver, intentFilter);
        if (TextUtils.isEmpty(AirlineTravelPreferenceHelper.getAirlinePreferenceList(TravelHomeActivity.this))) {
            AirlinesListFetcher.getInstance(TravelHomeActivity.this).fetchAirlineList(createAirlineListUrl());
        } else {
            // Display from cache
            showHideListView();
        }
    }

    /**
     * Unregister Reveiver
     */
    private void unregisterReciever() {
        if (mAirlineResultReceiver != null) {
            unregisterReceiver(mAirlineResultReceiver);
        }
    }

    /**
     * Create Airline List URL
     * @return the formatted URL.
     */
    private String createAirlineListUrl() {
        return getString(R.string.kayay_base_url) + getString(R.string.fetch_airlines_url);
    }


    /**
     * Show the airline details activity
     * @param airlineClass - the Airline Class
     */
    private void showAirlineDetailActivity(AirlineClass airlineClass) {
        Intent intent = new Intent(TravelHomeActivity.this, AirlineDetailActivity.class);
        intent.putExtra(Constants.ACTION_VIEW_DETAIL_PAYLOAD, airlineClass);
        startActivity(intent);
    }


}
