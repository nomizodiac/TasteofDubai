package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    Context context;
    public TextView dayTextView, dateTextView;

    public ScheduleViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
        dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);

        if (dayTextView != null)
            dayTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        if (dateTextView != null)
            dateTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
    }
}
