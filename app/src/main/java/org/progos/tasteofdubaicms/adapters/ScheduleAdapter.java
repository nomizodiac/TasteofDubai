package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.Schedule;
import org.progos.tasteofdubaicms.ui.ScheduleDetailsFragment;
import org.progos.tasteofdubaicms.ui.ScheduleFragment;
import org.progos.tasteofdubaicms.utility.Strings;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private final View header;
    private final ArrayList<Schedule> schedules;

    private Context context;

    public ScheduleAdapter(Context context, View header, ArrayList<Schedule> schedules) {

        this.context = context;
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.schedules = schedules;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new ScheduleViewHolder(context, header);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_schedule, parent, false);
        return new ScheduleViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final ScheduleViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        final Schedule schedule = schedules.get(position - 1);  // Subtract 1 for header
        holder.dayTextView.setText(schedule.getDay());
        holder.dateTextView.setText(schedule.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FragmentActivity) {
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_SCHEDULE);
                    fm.executePendingTransactions();
                    if (fragment instanceof ScheduleFragment && fm.findFragmentByTag(Strings.FRAGMENT_SCHEDULE_DETAILS) == null) {
                        ft.hide(fragment);
                        Bundle args = new Bundle();
                        args.putSerializable(Strings.SCHEDULE_OBJ, schedule);
                        ScheduleDetailsFragment scheduleDetailsFragment = new ScheduleDetailsFragment();
                        scheduleDetailsFragment.setArguments(args);
                        ft.add(R.id.fragmentsContainerLayout, scheduleDetailsFragment, Strings.FRAGMENT_SCHEDULE_DETAILS);
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
        return schedules.size() + 1;
    }


}
