package org.progos.tasteofdubaicms.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    Context context;
    TextView restaurantsBtn, venueMapBtn, scheduleBtn, chefsBtn, logoLbl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        uInit(view);

        return view;
    }

    @Override
    public void onClick(View v) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(Strings.FRAGMENT_MENU);
        if (fragment instanceof MenuFragment) {
            ft.hide(fragment);
        }
        fm.executePendingTransactions();

        switch (v.getId()) {

            case R.id.restaurantsBtn:
                if(fm.findFragmentByTag(Strings.FRAGMENT_RESTAURANTS) == null) {
                    ft.add(R.id.fragmentsContainerLayout, new RestaurantsFragment(), Strings.FRAGMENT_RESTAURANTS);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    Commons.STACK_IDENTIFIER_RESTAURANT_FRAGMENT = ft.commit();
                }
                break;

            case R.id.venueMapBtn:
                if(fm.findFragmentByTag(Strings.FRAGMENT_MAPS) == null) {
                    //ft.add(R.id.fragmentsContainerLayout, new MapDetailsFragment(), Strings.FRAGMENT_VENUE_MAP);
                    ft.add(R.id.fragmentsContainerLayout, new MapsFragment(), Strings.FRAGMENT_MAPS);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    Commons.STACK_IDENTIFIER_VENUE_MAP_FRAGMENT = ft.commit();
                }
                break;

            case R.id.scheduleBtn:
                if(fm.findFragmentByTag(Strings.FRAGMENT_SCHEDULE) == null) {
                    ft.add(R.id.fragmentsContainerLayout, new ScheduleFragment(), Strings.FRAGMENT_SCHEDULE);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    Commons.STACK_IDENTIFIER_SCHEDULE_FRAGMENT = ft.commit();
                }
                break;

            case R.id.chefsBtn:
                if(fm.findFragmentByTag(Strings.FRAGMENT_CHEFS) == null) {
                    ft.add(R.id.fragmentsContainerLayout, new ChefsFragment(), Strings.FRAGMENT_CHEFS);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    Commons.STACK_IDENTIFIER_CHEF_FRAGMENT = ft.commit();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume" + getClass().getSimpleName());
    }

    private void uInit(View view) {

        restaurantsBtn = (TextView) view.findViewById(R.id.restaurantsBtn);
        venueMapBtn = (TextView) view.findViewById(R.id.venueMapBtn);
        scheduleBtn = (TextView) view.findViewById(R.id.scheduleBtn);
        chefsBtn = (TextView) view.findViewById(R.id.chefsBtn);
        logoLbl = (TextView) view.findViewById(R.id.logoLbl);

        restaurantsBtn.setOnClickListener(this);
        venueMapBtn.setOnClickListener(this);
        scheduleBtn.setOnClickListener(this);
        chefsBtn.setOnClickListener(this);

        logoLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_EXTRA_BOLD));
        restaurantsBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        venueMapBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        scheduleBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        chefsBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
    }
}
