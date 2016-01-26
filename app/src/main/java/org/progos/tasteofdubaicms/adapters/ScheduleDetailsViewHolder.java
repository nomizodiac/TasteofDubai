package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.progos.tasteofdubaicms.R;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ScheduleDetailsViewHolder extends RecyclerView.ViewHolder {

    Context context;
    public TextView timeTextView, actTextView;

    public ScheduleDetailsViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        actTextView = (TextView) itemView.findViewById(R.id.actTextView);

        /*if (timeTextView != null)
            timeTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        if (actTextView != null)
            actTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));*/
    }
}
