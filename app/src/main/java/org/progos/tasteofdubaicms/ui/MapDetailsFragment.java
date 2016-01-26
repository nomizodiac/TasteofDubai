package org.progos.tasteofdubaicms.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.progos.tasteofdubaicms.AppController;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.VenueMap;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.HashMap;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class MapDetailsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    ImageViewTouch mapImage;
    TextView mapTitle;
    VenueMap venueMap;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;
    ProgressBar mapsListProgress;
    private RelativeLayout mapDetailsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_map_details, container, false);
        uInit(view);
        venueMap = (VenueMap) getArguments().getSerializable(Strings.MAP_OBJ);
        mapTitle.setText(Html.fromHtml(Html.fromHtml(venueMap.getMapTitle()).toString()));

        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMaps(venueMap.getId());
            }
        });
        loadMaps(venueMap.getId());

        return view;
    }

    private void loadMaps(String mapId) {

        if (!Utils.hasConnection(context)) {

            /*ArrayList<VenueMap> mapsArrayList = DataBaseHelper.getInstance(context).getMaps();
            if (mapsArrayList != null && !mapsArrayList.isEmpty()) {
                mapsListProgress.setVisibility(View.GONE);
                mapDetailsLayout.setVisibility(View.VISIBLE);
            } else {
                mapsListProgress.setVisibility(View.GONE);
                mapDetailsLayout.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }*/

            mapsListProgress.setVisibility(View.GONE);
            mapDetailsLayout.setVisibility(View.GONE);
            connectionLostLayout.setVisibility(View.VISIBLE);
        } else {
            connectionLostLayout.setVisibility(View.GONE);
            mapsListProgress.setVisibility(View.VISIBLE);

            //http://elephantationlabs.com/tasteofdubaiapp/wp-json/posts?type[]=maps
            //http://elephantationlabs.com/tasteofdubaiapp/wp-json/posts/67

            String url = "posts/" + venueMap.getId();
            JsonObjectRequest req = new JsonObjectRequest(Urls.base_url + url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    Log.d(TAG, "Response-MapsDetails: " + response.toString());
                    /*Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Activity activity = (Activity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    });
                    thread.start();*/

                    /*parseMapDetailsResponse(response);
                    mapsListProgress.setVisibility(View.GONE);
                    mapDetailsLayout.setVisibility(View.VISIBLE);*/

                    try {
                        String mapId = response.getString("ID");
                        JSONObject featuredImageJsonObj = response.getJSONObject("featured_image");
                        String mapUrl = featuredImageJsonObj.getString("guid");
                        JSONObject postMetaDataJsonObj = response.getJSONObject("postmeta_data");
                        JSONArray legendTitleJsonArray = postMetaDataJsonObj.getJSONArray("legendTitle");
                        JSONArray legendImageUrlsJsonArray = postMetaDataJsonObj.getJSONArray("legendImage");
                        HashMap<String, String> legendTitleUrl = new HashMap<>();
                        for(int i=0; i<legendTitleJsonArray.length(); i++)
                        {
                            legendTitleUrl.put(legendTitleJsonArray.getString(i), legendImageUrlsJsonArray.getString(i));
                        }

                        JSONArray labelsOnMapJsonArray = postMetaDataJsonObj.getJSONArray("labelOnMap");
                        JSONArray restaurantTitlesJsonArray = postMetaDataJsonObj.getJSONArray("restaurantTitle");
                        HashMap<String, String> restTitleLabelOnMap = new HashMap<>();
                        for(int i=0; i<labelsOnMapJsonArray.length(); i++)
                        {
                            restTitleLabelOnMap.put(labelsOnMapJsonArray.getString(i), restaurantTitlesJsonArray.getString(i));
                        }

                        Picasso.with(getContext()).load(mapUrl).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                // cache is now warmed up
                                mapImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
                                mapImage.setImageBitmap(bitmap);
                                mapsListProgress.setVisibility(View.GONE);
                                mapDetailsLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Log.d(TAG, "Error" + "onBitmapFailed");
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Log.d(TAG, "Error" + "onPrepareLoad");
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(req);
        }
    }

    private void parseMapDetailsResponse(JSONObject response) {

        try {
            String mapId = response.getString("ID");
            JSONObject featuredImageJsonObj = response.getJSONObject("featured_image");
            String mapUrl = featuredImageJsonObj.getString("guid");
            JSONObject postMetaDataJsonObj = response.getJSONObject("postmeta_data");
            JSONArray legendTitleJsonArray = postMetaDataJsonObj.getJSONArray("legendTitle");
            JSONArray legendImageUrlsJsonArray = postMetaDataJsonObj.getJSONArray("legendImage");
            HashMap<String, String> legendTitleUrl = new HashMap<>();
            for(int i=0; i<legendTitleJsonArray.length(); i++)
            {
                legendTitleUrl.put(legendTitleJsonArray.getString(i), legendImageUrlsJsonArray.getString(i));
            }

            JSONArray labelsOnMapJsonArray = postMetaDataJsonObj.getJSONArray("labelOnMap");
            JSONArray restaurantTitlesJsonArray = postMetaDataJsonObj.getJSONArray("restaurantTitle");
            HashMap<String, String> restTitleLabelOnMap = new HashMap<>();
            for(int i=0; i<labelsOnMapJsonArray.length(); i++)
            {
                restTitleLabelOnMap.put(labelsOnMapJsonArray.getString(i), restaurantTitlesJsonArray.getString(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void uInit(View view) {

        mapDetailsLayout = (RelativeLayout) view.findViewById(R.id.mapDetailsLayout);
        mapImage = (ImageViewTouch) view.findViewById(R.id.image);
        mapTitle = (TextView) view.findViewById(R.id.mapTitle);
        mapTitle.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_BOLD));
        mapsListProgress = (ProgressBar) view.findViewById(R.id.mapsListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
