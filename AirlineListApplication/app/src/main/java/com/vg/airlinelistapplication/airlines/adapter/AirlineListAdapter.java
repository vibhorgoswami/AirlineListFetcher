package com.vg.airlinelistapplication.airlines.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.vg.airlinelistapplication.R;
import com.vg.airlinelistapplication.airlines.AirlinesListFetcher;
import com.vg.airlinelistapplication.airlines.model.AirlineClass;
import com.vg.airlinelistapplication.storage.AirlineTravelPreferenceHelper;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by vgoswami on 9/30/15.
 */
public class AirlineListAdapter extends ArrayAdapter<AirlineClass> {

    private Context mContext;
    private int layout;
    private List<AirlineClass> airlineClassList;
    private ImageLoader mImageLoader;

    public static class ViewHolderItem {
        TextView textViewAirlineName;
        ImageView imageViewAirlineIcon;
        CheckBox checkBoxAirlineFavorite;
    }

    public AirlineListAdapter(Context context, int resource, List<AirlineClass> objects) {
        super(context, resource, objects);
        mContext = context;
        layout = resource;
        airlineClassList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (airlineClassList != null && airlineClassList.size() > 0) {
            final ViewHolderItem viewHolderItem = (convertView == null) ? new ViewHolderItem() : (ViewHolderItem) convertView.getTag();
            mImageLoader = AirlinesListFetcher.getInstance(mContext).getImageLoader();
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
                viewHolderItem.textViewAirlineName = (TextView) convertView.findViewById(R.id.tvAirlineName);
                viewHolderItem.imageViewAirlineIcon = (ImageView) convertView.findViewById(R.id.ivAirlineLogo);
                viewHolderItem.checkBoxAirlineFavorite = (CheckBox) convertView.findViewById(R.id.cbxAirlineFavorite);
                convertView.setTag(viewHolderItem);
            }
            final AirlineClass airlineClass = airlineClassList.get(position);
            String[] favAirlineCodes = TextUtils.split(AirlineTravelPreferenceHelper.getAirlineFavItems(mContext), Pattern.quote("|"));
            List<String> stringList = Arrays.asList(favAirlineCodes);
            viewHolderItem.textViewAirlineName.setText(airlineClass.getName());
            ImageRequest imageRequest = new ImageRequest(createLogoUrl(airlineClass.getLogoURL()),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            viewHolderItem.imageViewAirlineIcon.setImageBitmap(response);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            viewHolderItem.imageViewAirlineIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                            Log.e("Error", error.getMessage());
                        }
                    });
            AirlinesListFetcher.getInstance(mContext).addToRequestQueue(imageRequest);
            if (stringList.contains(airlineClass.getCode())) {
                viewHolderItem.checkBoxAirlineFavorite.setChecked(true);
            }
            viewHolderItem.checkBoxAirlineFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        AirlineTravelPreferenceHelper.addAirlineFavItem(mContext, airlineClass.getCode());
                    } else {
                        AirlineTravelPreferenceHelper.removeAirlineFavItem(mContext, airlineClass.getCode());
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
            }
            TextView textViewEmpty = (TextView) convertView.findViewById(R.id.tvAirlineName);
            textViewEmpty.setText(mContext.getString(R.string.airline_list_empty));
        }
        return convertView;
    }

    private String createLogoUrl(String logoEndUrl) {
        return mContext.getString(R.string.kayay_base_url) + logoEndUrl;
    }

}
