package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.VenueMap;
import org.progos.tasteofdubaicms.ui.MapDetailsFragment;
import org.progos.tasteofdubaicms.ui.MapsFragment;
import org.progos.tasteofdubaicms.ui.ScheduleDetailsFragment;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.Strings;
import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class MapAdapter extends RecyclerView.Adapter<MapViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private final View header;
    private final ArrayList<VenueMap> venueMaps;

    private Context context;

    public MapAdapter(Context context, View header, ArrayList<VenueMap> venueMaps) {

        this.context = context;
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.venueMaps = venueMaps;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new MapViewHolder(context, header);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_map, parent, false);
        return new MapViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final MapViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        final VenueMap venueMap = venueMaps.get(position - 1);  // Subtract 1 for header
        holder.mapTitle.setText(Html.fromHtml(venueMap.getMapTitle()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FragmentActivity) {
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_MAPS);
                    fm.executePendingTransactions();
                    if (fragment instanceof MapsFragment && fm.findFragmentByTag(Strings.FRAGMENT_MAP_DETAILS) == null) {
                        ft.hide(fragment);
                        Bundle args = new Bundle();
                        args.putSerializable(Strings.MAP_OBJ, venueMap);
                        MapDetailsFragment mapDetailsFragment = new MapDetailsFragment();
                        mapDetailsFragment.setArguments(args);
                        ft.add(R.id.fragmentsContainerLayout, mapDetailsFragment, Strings.FRAGMENT_MAP_DETAILS);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return venueMaps.size() + 1;
    }


}
