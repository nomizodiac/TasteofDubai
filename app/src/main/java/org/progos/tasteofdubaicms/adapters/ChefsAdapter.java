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
import org.progos.tasteofdubaicms.model.Chef;
import org.progos.tasteofdubaicms.ui.ChefDetailsFragment;
import org.progos.tasteofdubaicms.ui.ChefsFragment;
import org.progos.tasteofdubaicms.utility.Strings;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ChefsAdapter extends RecyclerView.Adapter<ChefViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private final View header;
    ArrayList<Chef> chefs;
    private Context context;

    public ChefsAdapter(Context context, View header, ArrayList<Chef> chefs) {

        this.context = context;
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.chefs = chefs;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new ChefViewHolder(context, header);
        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chefs, parent, false);
        return new ChefViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final ChefViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        final Chef chef = chefs.get(position - 1);
        holder.chefName.setText(chef.getName());
        Picasso.with(context).load(chef.getImageUrl()).into(holder.chefImg);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof FragmentActivity) {
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_CHEFS);
                    if (fragment instanceof ChefsFragment) {
                        ft.hide(fragment);
                        Bundle args = new Bundle();
                        args.putSerializable(Strings.CHEF_OBJ, chef);
                        ChefDetailsFragment chefDetailsFragment = new ChefDetailsFragment();
                        chefDetailsFragment.setArguments(args);
                        ft.add(R.id.fragmentsContainerLayout, chefDetailsFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });*/

        holder.chefImg.setOnTouchListener(new View.OnTouchListener() {

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
                            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_CHEFS);
                            fm.executePendingTransactions();
                            if (fragment instanceof ChefsFragment && fm.findFragmentByTag(Strings.FRAGMENT_CHEF_DETAILS) == null) {
                                ft.hide(fragment);
                                Bundle args = new Bundle();
                                args.putSerializable(Strings.CHEF_OBJ, chef);
                                ChefDetailsFragment chefDetailsFragment = new ChefDetailsFragment();
                                chefDetailsFragment.setArguments(args);
                                ft.add(R.id.fragmentsContainerLayout, chefDetailsFragment, Strings.FRAGMENT_CHEF_DETAILS);
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
        return chefs.size() + 1;
    }
}
