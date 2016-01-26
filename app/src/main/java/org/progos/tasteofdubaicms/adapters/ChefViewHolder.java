package org.progos.tasteofdubaicms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.SquareImageView;

/**
 * Created by NomBhatti on 11/26/2015.
 */
public class ChefViewHolder extends RecyclerView.ViewHolder {

    Context context;
    public SquareImageView chefImg;
    public TextView chefName;

    public ChefViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        chefImg = (SquareImageView) itemView.findViewById(R.id.chefImg);
        chefName = (TextView) itemView.findViewById(R.id.chefName);
        if (chefName != null)
            chefName.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
    }
}
