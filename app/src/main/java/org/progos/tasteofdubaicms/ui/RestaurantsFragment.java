package org.progos.tasteofdubaicms.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.progos.tasteofdubaicms.AppController;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.adapters.RestaurantsAdapter;
import org.progos.tasteofdubaicms.model.Restaurant;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class RestaurantsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RecyclerView restaurantsList;
    ArrayList<Restaurant> restaurants;
    RestaurantsAdapter adapter;
    ProgressBar restaurantsListProgress;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        uInit(view);

        restaurants = new ArrayList<>();
        restaurantsList.setHasFixedSize(true);
        final GridLayoutManager manager = new GridLayoutManager(context, 3);
        restaurantsList.setLayoutManager(manager);

        View header = LayoutInflater.from(context).inflate(R.layout.header_list_restaurants, restaurantsList, false);
        TextView restaurantsHeadingLbl = (TextView) header.findViewById(R.id.restaurantsHeadingLbl);
        TextView restaurantsDescription = (TextView) header.findViewById(R.id.restaurantsDescription);
        restaurantsHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        restaurantsDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));

        adapter = new RestaurantsAdapter(context, header, restaurants);
        restaurantsList.setAdapter(adapter);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });

        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRestaurants();
            }
        });

        loadRestaurants();
        return view;
    }

    private void loadRestaurants() {

        if (!Utils.hasConnection(context)) {

            // if user has no internet, load data from local database
            // if user has empty database then prompt error message
            ArrayList<Restaurant> restaurantArrayList = DataBaseHelper.getInstance(context).getRestaurants();
            if (restaurantArrayList != null && !restaurantArrayList.isEmpty()) {
                restaurants.addAll(restaurantArrayList);
                adapter.notifyDataSetChanged();
                restaurantsListProgress.setVisibility(View.GONE);
                restaurantsList.setVisibility(View.VISIBLE);
            } else {
                restaurantsListProgress.setVisibility(View.GONE);
                restaurantsList.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }

        } else {
            connectionLostLayout.setVisibility(View.GONE);
            restaurantsListProgress.setVisibility(View.VISIBLE);

            String url = "posts?type[]=restaurants";
            JsonArrayRequest req = new JsonArrayRequest(Urls.base_url + url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(final JSONArray response) {
                    Log.d(TAG, "Response-Restaurants: " + response.toString());
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            parseRestaurantsResponse(response);
                            Activity activity = (Activity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    restaurantsListProgress.setVisibility(View.GONE);
                                    restaurantsList.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                    thread.start();
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

    private void parseRestaurantsResponse(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                String restaurantID = jsonObject.getString("ID");
                JSONObject imageJsonObj = jsonObject.getJSONObject("featured_image");
                String imageUrl = imageJsonObj.getString("guid");
                Restaurant restaurant = new Restaurant(restaurantID, imageUrl, "false");
                restaurants.add(restaurant);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Add restaurants list to local database
        if (restaurants != null && !restaurants.isEmpty()) {
            DataBaseHelper.getInstance(context).addRestaurants(restaurants);
        }
    }

    private void uInit(View view) {

        restaurantsList = (RecyclerView) view.findViewById(R.id.restaurantsList);
        restaurantsListProgress = (ProgressBar) view.findViewById(R.id.restaurantsListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
