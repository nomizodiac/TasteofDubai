package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.Restaurant;
import org.progos.tasteofdubaicms.ui.RestaurantDetailsFragment;
import org.progos.tasteofdubaicms.ui.RestaurantsFragment;
import org.progos.tasteofdubaicms.utility.Strings;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private final View header;
    ArrayList<Restaurant> restaurants;

    private Context context;

    public RestaurantsAdapter(Context context, View header, ArrayList<Restaurant> restaurants) {

        this.context = context;
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.restaurants = restaurants;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new RestaurantViewHolder(header);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_restaurants, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RestaurantViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }

        final Restaurant restaurant = restaurants.get(position - 1); // Subtract 1 for header
        final String imgUrl = restaurant.getImgUrl();
        Picasso.with(context).load(imgUrl).into(holder.restaurantImg);

        /*holder.restaurantImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof FragmentActivity) {
                    Bundle args = new Bundle();
                    args.putSerializable(Strings.RESTAURANT_OBJ, restaurant);
                    RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();
                    restaurantDetailsFragment.setArguments(args);
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_RESTAURANTS);
                    if (fragment instanceof RestaurantsFragment) {
                        ft.hide(fragment);
                        ft.add(R.id.fragmentsContainerLayout, restaurantDetailsFragment, Strings.FRAGMENT_RESTAURANT_DETAILS);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });*/

        holder.restaurantImg.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        Drawable drawable = view.getDrawable();
                        if(drawable!=null) {
                            drawable.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        if (context instanceof FragmentActivity) {
                            Bundle args = new Bundle();
                            args.putSerializable(Strings.RESTAURANT_OBJ, restaurant);
                            RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();
                            restaurantDetailsFragment.setArguments(args);
                            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_RESTAURANTS);
                            if (fragment instanceof RestaurantsFragment) {
                                ft.hide(fragment);
                                ft.add(R.id.fragmentsContainerLayout, restaurantDetailsFragment, Strings.FRAGMENT_RESTAURANT_DETAILS);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        Drawable drawable = view.getDrawable();
                        if(drawable!=null) {
                            drawable.clearColorFilter();
                            view.invalidate();
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return restaurants.size() + 1;
    }


}
