package org.progos.tasteofdubaicms.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.progos.tasteofdubaicms.AppController;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.adapters.ChefsAdapter;
import org.progos.tasteofdubaicms.adapters.ChefsSectionedAdapter;
import org.progos.tasteofdubaicms.model.Chef;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class ChefsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RecyclerView chefsList;
    ChefsAdapter adapter;
    ArrayList<Chef> chefs;
    ProgressBar chefsListProgress;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;
    List<ChefsSectionedAdapter.Section> sections;
    ChefsSectionedAdapter mSectionedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_chefs, container, false);
        uInit(view);

        chefs = new ArrayList<>();
        sections = new ArrayList<>();
        chefsList.setHasFixedSize(true);
        final GridLayoutManager manager = new GridLayoutManager(context, 2);
        chefsList.setLayoutManager(manager);
        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChefs();
            }
        });


        loadChefs();


        return view;
    }

    private void loadChefs() {

        if (!Utils.hasConnection(context)) {
            // if user has no internet, load data from local database
            // if user has empty database then prompt error message
            ArrayList<Chef> chefsArrayList = DataBaseHelper.getInstance(context).getChefs();
            if (chefsArrayList != null && !chefsArrayList.isEmpty()) {
                sections.clear();
                for (int i = 0; i < chefsArrayList.size(); i++) {
                    Chef chef = chefsArrayList.get(i);
                    if (chef.getName().contains("&")) {
                        int tempInt = i + 1;
                        sections.add(new ChefsSectionedAdapter.Section(tempInt, chef));
                    } else
                        chefs.add(chefsArrayList.get(i));
                }

                View header = LayoutInflater.from(context).inflate(R.layout.header_list_chefs, chefsList, false);
                TextView chefsHeadingLbl = (TextView) header.findViewById(R.id.chefsHeadingLbl);
                TextView chefsDescription = (TextView) header.findViewById(R.id.chefsDescription);
                chefsHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
                chefsDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_MEDIUM));

                adapter = new ChefsAdapter(context, header, chefs);

                //Add your adapter to the sectionAdapter
                ChefsSectionedAdapter.Section[] dummy = new ChefsSectionedAdapter.Section[sections.size()];
                mSectionedAdapter = new
                        ChefsSectionedAdapter(context, R.layout.item_list_two_chefs, R.id.chefName, R.id.chefImg, chefsList, adapter);
                mSectionedAdapter.setSections(sections.toArray(dummy));
                chefsList.setAdapter(mSectionedAdapter);

                //mSectionedAdapter.notifyDataSetChanged();
                chefsListProgress.setVisibility(View.GONE);
                chefsList.setVisibility(View.VISIBLE);
            } else {
                chefsListProgress.setVisibility(View.GONE);
                chefsList.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }
        } else {
            connectionLostLayout.setVisibility(View.GONE);
            chefsListProgress.setVisibility(View.VISIBLE);
            String url = "posts?type[]=chefs";
            JsonArrayRequest req = new JsonArrayRequest(Urls.base_url + url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(final JSONArray response) {
                    Log.d(TAG, "Response-Chefs: " + response.toString());
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            parseChefsResponse(response);
                            Activity activity = (Activity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<Chef> chefsArrayList = new ArrayList<>();
                                    chefsArrayList.addAll(chefs);
                                    chefs.clear();
                                    if (chefsArrayList != null && !chefsArrayList.isEmpty()) {
                                        sections.clear();
                                        for (int i = 0; i < chefsArrayList.size(); i++) {
                                            if (chefsArrayList.get(i).getName().contains("&")) {
                                                int tempInt = i + 1;
                                                sections.add(new ChefsSectionedAdapter.Section(tempInt, chefsArrayList.get(i)));
                                            } else
                                                chefs.add(chefsArrayList.get(i));
                                        }

                                        View header = LayoutInflater.from(context).inflate(R.layout.header_list_chefs, chefsList, false);
                                        TextView chefsHeadingLbl = (TextView) header.findViewById(R.id.chefsHeadingLbl);
                                        TextView chefsDescription = (TextView) header.findViewById(R.id.chefsDescription);
                                        chefsHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
                                        chefsDescription.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_MEDIUM));
                                        adapter = new ChefsAdapter(context, header, chefs);

                                        //Add your adapter to the sectionAdapter
                                        ChefsSectionedAdapter.Section[] dummy = new ChefsSectionedAdapter.Section[sections.size()];
                                        mSectionedAdapter = new
                                                ChefsSectionedAdapter(context, R.layout.item_list_two_chefs, R.id.chefName, R.id.chefImg, chefsList, adapter);
                                        mSectionedAdapter.setSections(sections.toArray(dummy));
                                        chefsList.setAdapter(mSectionedAdapter);

                                        //mSectionedAdapter.notifyDataSetChanged();
                                        chefsListProgress.setVisibility(View.GONE);
                                        chefsList.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    });
                    thread.start();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            AppController.getInstance().addToRequestQueue(req);
        }
    }

    private void parseChefsResponse(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                String chefId = jsonObject.getString("ID");
                String chefName = jsonObject.getString("title");
                JSONObject imageJsonObj = jsonObject.getJSONObject("featured_image");
                String imageUrl = imageJsonObj.getString("guid");
                String description = jsonObject.getString("content");

                Chef chef = new Chef(chefId, chefName, imageUrl, description);
                chefs.add(chef);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DataBaseHelper.getInstance(context).addChefs(chefs);
    }


    private void uInit(View view) {

        chefsList = (RecyclerView) view.findViewById(R.id.chefsList);
        chefsListProgress = (ProgressBar) view.findViewById(R.id.chefsListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));

    }
}
