package org.progos.tasteofdubaicms.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.progos.tasteofdubaicms.AppController;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.adapters.RestaurantDetailsAdapter;
import org.progos.tasteofdubaicms.adapters.RestaurantDetailsSectionedAdapter;
import org.progos.tasteofdubaicms.model.Restaurant;
import org.progos.tasteofdubaicms.model.RestaurantItem;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class RestaurantDetailsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RecyclerView restaurantDetailsList;
    ProgressBar restaurantsListProgress;
    ArrayList<RestaurantItem> restaurantItems;
    RestaurantDetailsAdapter adapter;
    Restaurant restaurant;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        uInit(view);

        restaurant = (Restaurant) getArguments().getSerializable(Strings.RESTAURANT_OBJ);
        restaurantItems = new ArrayList<>();
        restaurantDetailsList.setHasFixedSize(true);
        restaurantDetailsList.setLayoutManager(new LinearLayoutManager(context));

        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRestaurantDetails(restaurant.getId());
            }
        });
        loadRestaurantDetails(restaurant.getId());

        return view;
    }

    private void loadRestaurantDetails(String restaurantId) {

        if (!Utils.hasConnection(context)) {

            restaurantsListProgress.setVisibility(View.GONE);
            restaurantDetailsList.setVisibility(View.GONE);
            connectionLostLayout.setVisibility(View.VISIBLE);

            ArrayList<RestaurantItem> restaurantItemArrayList = DataBaseHelper.getInstance(context).getRestaurantItems(restaurant);
            if (restaurantItemArrayList != null && !restaurantItemArrayList.isEmpty()) {
                View header = LayoutInflater.from(context).inflate(R.layout.header_list_restaurant_details, restaurantDetailsList, false);
                ImageView restaurantImg = (ImageView) header.findViewById(R.id.restaurantImg);
                TextView greyBarTxt = (TextView) header.findViewById(R.id.greyBarTxt);
                greyBarTxt.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_BOLD));

                Picasso.with(context).load(restaurant.getImgUrl()).into(restaurantImg);

                if (restaurant.getHasCat().equalsIgnoreCase("false")) {
                    adapter = new RestaurantDetailsAdapter(context, header, restaurantItemArrayList);
                    restaurantDetailsList.setAdapter(adapter);
                } else {
                    adapter = new RestaurantDetailsAdapter(context, header, restaurantItemArrayList);

                    //This is the code to provide a sectioned list
                    List<RestaurantDetailsSectionedAdapter.Section> sections = new ArrayList<>();
                    //Sections
                    for (int i = 0; i < restaurantItemArrayList.size(); i++) {
                        String itemCat = restaurantItemArrayList.get(i).getCategory();
                        if (!itemCat.isEmpty()) {
                            if (i == 0)
                                i = 1;
                            sections.add(new RestaurantDetailsSectionedAdapter.Section(i, itemCat));
                        }
                    }

                    //Add your adapter to the sectionAdapter
                    RestaurantDetailsSectionedAdapter.Section[] dummy = new RestaurantDetailsSectionedAdapter.Section[sections.size()];
                    RestaurantDetailsSectionedAdapter mSectionedAdapter = new
                            RestaurantDetailsSectionedAdapter(context, R.layout.item_list_restaurant_details_section, R.id.section_text, adapter);
                    mSectionedAdapter.setSections(sections.toArray(dummy));

                    restaurantDetailsList.setAdapter(mSectionedAdapter);
                }

                restaurantDetailsList.setVisibility(View.VISIBLE);
                connectionLostLayout.setVisibility(View.GONE);
            }
        } else {
            connectionLostLayout.setVisibility(View.GONE);
            restaurantsListProgress.setVisibility(View.VISIBLE);

            Log.d(TAG, Strings.RESTAURANT_ID + " :" + restaurantId);
            String url = Urls.base_url + "posts/" + restaurantId;
            JsonObjectRequest req = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Response-RestaurantDetails: " + response.toString());
                    parseRestaurantDetailsResponse(response);
                    restaurantsListProgress.setVisibility(View.GONE);
                    restaurantDetailsList.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(req);
        }
    }

    private void parseRestaurantDetailsResponse(JSONObject response) {

        try {
            JSONObject jsonObject = response.getJSONObject("postmeta_data");
            JSONArray itemTitlesJsonArray = jsonObject.getJSONArray("item_title");
            JSONArray itemDescriptionsJsonArray = jsonObject.getJSONArray("item_description");
            JSONArray itemPricesJsonArray = jsonObject.getJSONArray("item_price");
            JSONArray itemCatsJsonArray = jsonObject.getJSONArray("item_cat");
            String hasCat = jsonObject.getString("has_cat");

            View header = LayoutInflater.from(context).inflate(R.layout.header_list_restaurant_details, restaurantDetailsList, false);
            ImageView restaurantImg = (ImageView) header.findViewById(R.id.restaurantImg);
            TextView greyBarTxt = (TextView) header.findViewById(R.id.greyBarTxt);
            greyBarTxt.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_BOLD));
            Picasso.with(context).load(restaurant.getImgUrl()).into(restaurantImg);

            ArrayList<RestaurantItem> restaurantItems = new ArrayList<>();

            if (hasCat.equalsIgnoreCase("false")) {
                // means there are no categories
                for (int i = 0; i < itemTitlesJsonArray.length(); i++) {
                    String title = itemTitlesJsonArray.getString(i);
                    String category = itemCatsJsonArray.getString(i);
                    String description = itemDescriptionsJsonArray.getString(i);
                    String price = itemPricesJsonArray.getString(i);

                    RestaurantItem restaurantItem = new RestaurantItem(title, category, description, price, restaurant.getId());
                    restaurantItems.add(restaurantItem);
                }
                adapter = new RestaurantDetailsAdapter(context, header, restaurantItems);
                restaurantDetailsList.setAdapter(adapter);

                DataBaseHelper.getInstance(context).addRestaurantItems(restaurantItems);
                restaurant.setHasCat("false");
                DataBaseHelper.getInstance(context).updateRestaurant(restaurant);
            } else {
                for (int i = 0; i < itemTitlesJsonArray.length(); i++) {
                    String title = itemTitlesJsonArray.getString(i);
                    String category = itemCatsJsonArray.getString(i);
                    String description = itemDescriptionsJsonArray.getString(i);
                    String price = itemPricesJsonArray.getString(i);

                    RestaurantItem restaurantItem = new RestaurantItem(title, category, description, price, restaurant.getId());
                    restaurantItems.add(restaurantItem);
                }
                adapter = new RestaurantDetailsAdapter(context, header, restaurantItems);

                //This is the code to provide a sectioned list
                List<RestaurantDetailsSectionedAdapter.Section> sections = new ArrayList<>();
                //Sections
                for (int i = 0; i < itemCatsJsonArray.length(); i++) {
                    String itemCat = itemCatsJsonArray.getString(i);
                    if (!itemCat.isEmpty()) {
                        if (i == 0)
                            i = 1;
                        sections.add(new RestaurantDetailsSectionedAdapter.Section(i, itemCat));
                    }
                }

                //Add your adapter to the sectionAdapter
                RestaurantDetailsSectionedAdapter.Section[] dummy = new RestaurantDetailsSectionedAdapter.Section[sections.size()];
                RestaurantDetailsSectionedAdapter mSectionedAdapter = new
                        RestaurantDetailsSectionedAdapter(context, R.layout.item_list_restaurant_details_section, R.id.section_text, adapter);
                mSectionedAdapter.setSections(sections.toArray(dummy));
                restaurantDetailsList.setAdapter(mSectionedAdapter);


                DataBaseHelper.getInstance(context).addRestaurantItems(restaurantItems);
                restaurant.setHasCat("true");
                DataBaseHelper.getInstance(context).updateRestaurant(restaurant);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void uInit(View view) {

        restaurantDetailsList = (RecyclerView) view.findViewById(R.id.restaurantsList);
        restaurantsListProgress = (ProgressBar) view.findViewById(R.id.restaurantsListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
