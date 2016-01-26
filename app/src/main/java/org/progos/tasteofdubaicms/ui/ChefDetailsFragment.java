package org.progos.tasteofdubaicms.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.model.Chef;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class ChefDetailsFragment extends Fragment {

    Context context;
    TextView chefName, chefDescription, chefsHeadingLbl, chefsDescription;
    ImageView chefImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_chef_details, container, false);
        uInit(view);

        Chef chef = (Chef) getArguments().getSerializable(Strings.CHEF_OBJ);
        chefName.setText(Html.fromHtml(chef.getName()).toString());
        chefDescription.setText(Html.fromHtml(Html.fromHtml(chef.getDescription()).toString()));
        chefDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_MEDIUM));

        if (chef.getName().contains("&"))
            chefImg.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(context).load(chef.getImageUrl()).into(chefImg);

        return view;
    }

    private void uInit(View view) {

        chefsHeadingLbl = (TextView) view.findViewById(R.id.chefsHeadingLbl);
        chefsDescription = (TextView) view.findViewById(R.id.chefsDescription);
        chefName = (TextView) view.findViewById(R.id.chefName);
        chefDescription = (TextView) view.findViewById(R.id.chefDescription);
        chefImg = (ImageView) view.findViewById(R.id.chefImg);

        chefsHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        chefsDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_MEDIUM));
        chefName.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        chefDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_MEDIUM));
    }
}
