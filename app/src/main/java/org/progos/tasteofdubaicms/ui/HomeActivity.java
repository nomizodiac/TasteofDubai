package org.progos.tasteofdubaicms.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RelativeLayout bottomBarLayout, fragmentsContainerLayout;
    LinearLayout menuShortcutLayout;
    ImageButton menuBtn, backBtn;
    TextView scheduleBtn, chefsBtn, restaurantsBtn, venueMapBtn;
    boolean isShortCutMenuShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        uInit();
        loadMenuFragment();
    }

    private void loadMenuFragment() {

        if (findViewById(R.id.fragmentsContainerLayout) != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragmentsContainerLayout, new MenuFragment(), Strings.FRAGMENT_MENU);
            ft.commit();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.menuBtn:
                if (isShortCutMenuShown) {
                    menuShortcutLayout.setVisibility(View.GONE);
                    isShortCutMenuShown = false;
                } else {
                    menuShortcutLayout.setVisibility(View.VISIBLE);
                    isShortCutMenuShown = true;
                }
                break;

            case R.id.scheduleBtn:
                loadFragment(Strings.FRAGMENT_SCHEDULE);
                break;

            case R.id.chefsBtn:
                loadFragment(Strings.FRAGMENT_CHEFS);
                break;

            case R.id.restaurantsBtn:
                loadFragment(Strings.FRAGMENT_RESTAURANTS);
                break;

            case R.id.venueMapBtn:
                loadFragment(Strings.FRAGMENT_MAPS);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume" + getClass().getSimpleName());
    }

    private void loadFragment(String fragmentTag) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        Fragment menuFragment = fm.findFragmentByTag(Strings.FRAGMENT_MENU);
        if (menuFragment instanceof MenuFragment) {
            ft.hide(menuFragment);
        }

        if (fragmentTag == Strings.FRAGMENT_RESTAURANTS) {
            ft.add(R.id.fragmentsContainerLayout, new RestaurantsFragment(), Strings.FRAGMENT_RESTAURANTS);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
        } else if (fragmentTag == Strings.FRAGMENT_SCHEDULE) {
            ft.add(R.id.fragmentsContainerLayout, new ScheduleFragment(), Strings.FRAGMENT_SCHEDULE);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
        } else if (fragmentTag == Strings.FRAGMENT_CHEFS) {
            ft.add(R.id.fragmentsContainerLayout, new ChefsFragment(), Strings.FRAGMENT_CHEFS);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
        } else if (fragmentTag == Strings.FRAGMENT_MAPS) {
            ft.add(R.id.fragmentsContainerLayout, new MapDetailsFragment(), Strings.FRAGMENT_MAPS);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
        }

        ft.commit();
        menuShortcutLayout.setVisibility(View.GONE);
    }

    private void uInit() {

        bottomBarLayout = (RelativeLayout) findViewById(R.id.bottomBarLayout);
        fragmentsContainerLayout = (RelativeLayout) findViewById(R.id.fragmentsContainerLayout);
        menuShortcutLayout = (LinearLayout) findViewById(R.id.menuShortcutLayout);
        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        scheduleBtn = (TextView) findViewById(R.id.scheduleBtn);
        chefsBtn = (TextView) findViewById(R.id.chefsBtn);
        restaurantsBtn = (TextView) findViewById(R.id.restaurantsBtn);
        venueMapBtn = (TextView) findViewById(R.id.venueMapBtn);

        menuBtn.setOnClickListener(this);
        scheduleBtn.setOnClickListener(this);
        chefsBtn.setOnClickListener(this);
        restaurantsBtn.setOnClickListener(this);
        venueMapBtn.setOnClickListener(this);

        scheduleBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
        chefsBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
        restaurantsBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
        venueMapBtn.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
