package com.vg.airlinelistapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vg.airlinelistapplication.airlines.AirlinesListFetcher;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.constants.Constants;

/**
 * This is the detail activity containing the details of the Airline.
 */
public class AirlineDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvAirlineWebsite;
    private TextView tvAirlinePhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airline_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        AirlineClass mAirlineClass = (AirlineClass) getIntent().getSerializableExtra(Constants.ACTION_VIEW_DETAIL_PAYLOAD);
        TextView tvAirlineName = (TextView) findViewById(R.id.tvAirlineName);
        tvAirlineName.setText(mAirlineClass.getName());
        tvAirlinePhone = (TextView) findViewById(R.id.tvAirlinePhone);
        if (!TextUtils.isEmpty(mAirlineClass.getPhone()))
            tvAirlinePhone.setText(mAirlineClass.getPhone());
        else {
            tvAirlinePhone.setText(getString(R.string.airline_no_phone));
        }
        tvAirlinePhone.setOnClickListener(this);
        tvAirlineWebsite = (TextView) findViewById(R.id.tvAirlineWebsite);
        if (!TextUtils.isEmpty(mAirlineClass.getSite())) {
            tvAirlineWebsite.setText(mAirlineClass.getSite());
        } else {
            tvAirlineWebsite.setText(getString(R.string.airline_no_website));
        }
        tvAirlineWebsite.setOnClickListener(this);
        NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
        ImageLoader imageLoader = AirlinesListFetcher.getInstance(this).getImageLoader();
        networkImageView.setImageUrl(createLogoUrl(mAirlineClass.getLogoURL()), imageLoader);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvAirlinePhone:
                sendToDialer(tvAirlinePhone.getText().toString());
                break;

            case R.id.tvAirlineWebsite:
                sendToBrowser(tvAirlineWebsite.getText().toString());
                break;

        }
    }

    /**
     * Send it to the phone dialer.
     * @param phone - the number to dial
     */
    private void sendToDialer(String phone) {
        if (!phone.equalsIgnoreCase(getString(R.string.airline_no_phone))) {
            if (ContextCompat.checkSelfPermission(AirlineDetailActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.request_permission_call_title);
                builder.setMessage(R.string.request_permission_call);
                builder.setPositiveButton(R.string.request_permission_call_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendToDialer(tvAirlinePhone.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.request_permission_call_decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } else {
                Intent intentToPhone = new Intent(Intent.ACTION_DIAL);
                if (!phone.startsWith("tel:")) phone = "tel:" + phone;
                intentToPhone.setData(Uri.parse(phone));
                startActivity(intentToPhone);
            }
        }
    }

    /**
     * Send the user to the browser.
     * @param url - the URL to open in browser.
     */
    private void sendToBrowser(String url) {
        if (!url.equalsIgnoreCase(getString(R.string.airline_no_website))) {
            Intent intentToBrowser = new Intent(Intent.ACTION_VIEW);
            intentToBrowser.setData(Uri.parse("http://" + url));
            startActivity(intentToBrowser);
        }
    }

    /**
     * Create the URL using the base URL and logo url
     * @param logoEndUrl - logo end point url
     * @return The complete URL
     */
    private String createLogoUrl(String logoEndUrl) {
        return getString(R.string.kayay_base_url) + logoEndUrl;
    }
}
