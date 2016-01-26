package org.progos.tasteofdubaicms.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import org.progos.tasteofdubaicms.adapters.MapAdapter;
import org.progos.tasteofdubaicms.adapters.ScheduleAdapter;
import org.progos.tasteofdubaicms.model.Schedule;
import org.progos.tasteofdubaicms.model.VenueMap;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class MapsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RecyclerView mapsList;
    ArrayList<VenueMap> venueMaps;
    MapAdapter adapter;
    ProgressBar mapsListProgress;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        uInit(view);
        venueMaps = new ArrayList<>();

        mapsList.setHasFixedSize(true);
        mapsList.setLayoutManager(new LinearLayoutManager(context));

        View header = LayoutInflater.from(context).inflate(R.layout.header_list_maps, mapsList, false);
        TextView scheduleHeadingLbl = (TextView) header.findViewById(R.id.mapHeadingLbl);
        scheduleHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));

        adapter = new MapAdapter(context, header, venueMaps);
        mapsList.setAdapter(adapter);

        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMaps();
            }
        });
        loadMaps();

        return view;
    }

    private void loadMaps() {

        if (!Utils.hasConnection(context)) {

            ArrayList<VenueMap> mapsArrayList = DataBaseHelper.getInstance(context).getMaps();
            if (mapsArrayList != null && !mapsArrayList.isEmpty()) {
                venueMaps.addAll(mapsArrayList);
                adapter.notifyDataSetChanged();
                mapsListProgress.setVisibility(View.GONE);
                mapsList.setVisibility(View.VISIBLE);
            } else {
                mapsListProgress.setVisibility(View.GONE);
                mapsList.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }
        } else {
            connectionLostLayout.setVisibility(View.GONE);
            mapsListProgress.setVisibility(View.VISIBLE);

            //http://elephantationlabs.com/tasteofdubaiapp/wp-json/posts?type[]=maps
            //http://elephantationlabs.com/tasteofdubaiapp/wp-json/posts/67

            String url = "posts?type[]=maps";
            JsonArrayRequest req = new JsonArrayRequest(Urls.base_url + url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(final JSONArray response) {
                    Log.d(TAG, "Response-Maps: " + response.toString());
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            parseMapsResponse(response);
                            Activity activity = (Activity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    mapsListProgress.setVisibility(View.GONE);
                                    mapsList.setVisibility(View.VISIBLE);
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

    private void parseMapsResponse(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                String mapId = jsonObject.getString("ID");
                String mapTitle = jsonObject.getString("title");
                VenueMap venueMap = new VenueMap(mapId, mapTitle);
                venueMaps.add(venueMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (venueMaps != null && !venueMaps.isEmpty()) {
            DataBaseHelper.getInstance(context).addMaps(venueMaps);
        }
    }

    private void uInit(View view) {

        mapsList = (RecyclerView) view.findViewById(R.id.mapsList);
        mapsListProgress = (ProgressBar) view.findViewById(R.id.mapsListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
