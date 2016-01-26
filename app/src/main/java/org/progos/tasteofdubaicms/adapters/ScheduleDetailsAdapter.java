package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.ScheduleItem;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ScheduleDetailsAdapter extends RecyclerView.Adapter<ScheduleDetailsViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private View header;
    ArrayList<ScheduleItem> scheduleItems;

    private Context context;

    public ScheduleDetailsAdapter(Context context, View header, ArrayList<ScheduleItem> scheduleItems) {

        this.context = context;
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.scheduleItems = scheduleItems;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public ScheduleDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new ScheduleDetailsViewHolder(context, header);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_schedule_details, parent, false);
        return new ScheduleDetailsViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final ScheduleDetailsViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        final ScheduleItem scheduleItem = scheduleItems.get(position - 1);
        holder.timeTextView.setText(scheduleItem.getTime());
        holder.actTextView.setText(scheduleItem.getAct());
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size() + 1;
    }


}
