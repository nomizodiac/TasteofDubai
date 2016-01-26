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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.progos.tasteofdubaicms.AppController;
import org.progos.tasteofdubaicms.R;
import org.progos.tasteofdubaicms.adapters.ScheduleAdapter;
import org.progos.tasteofdubaicms.model.Schedule;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;

import java.util.ArrayList;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class ScheduleFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context context;
    RecyclerView scheduleList;
    ArrayList<Schedule> schedules;
    ScheduleAdapter adapter;
    ProgressBar scheduleListProgress;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        uInit(view);
        schedules = new ArrayList<>();
        scheduleList.setHasFixedSize(true);
        scheduleList.setLayoutManager(new LinearLayoutManager(context));

        View header = LayoutInflater.from(context).inflate(R.layout.header_list_schedule, scheduleList, false);
        TextView scheduleHeadingLbl = (TextView) header.findViewById(R.id.scheduleHeadingLbl);
        scheduleHeadingLbl.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));

        adapter = new ScheduleAdapter(context, header, schedules);
        scheduleList.setAdapter(adapter);

        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSchedules();
            }
        });
        loadSchedules();

        return view;
    }

    private void loadSchedules() {

        if (!Utils.hasConnection(context)) {

            ArrayList<Schedule> schedulesArrayList = DataBaseHelper.getInstance(context).getSchedules();
            if (schedulesArrayList != null && !schedulesArrayList.isEmpty()) {
                schedules.addAll(schedulesArrayList);
                adapter.notifyDataSetChanged();
                scheduleListProgress.setVisibility(View.GONE);
                scheduleList.setVisibility(View.VISIBLE);
            } else {
                scheduleListProgress.setVisibility(View.GONE);
                scheduleList.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }
        } else {
            connectionLostLayout.setVisibility(View.GONE);
            scheduleListProgress.setVisibility(View.VISIBLE);

            //String url = "http://elephantationlabs.com/tasteofdubaiapp/?schedules=1";
            String url = "http://elephantationlabs.com/tasteofdubaiapp/schedules-api/";
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {
                    Log.d(TAG, "Response-Schedules: " + response.toString());
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            parseSchedulesResponse(response);
                            Activity activity = (Activity) context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    scheduleListProgress.setVisibility(View.GONE);
                                    scheduleList.setVisibility(View.VISIBLE);
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

    private void parseSchedulesResponse(JSONObject response) {

        try {
            String status = response.getString("status");
            if(status.equalsIgnoreCase("success")) {
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String scheduleId = jsonArray.getJSONObject(i).getString("id");
                    String day = jsonArray.getJSONObject(i).getString("title");
                    String date = jsonArray.getJSONObject(i).getString("date");
                    Schedule schedule = new Schedule(scheduleId, day, date);
                    schedules.add(schedule);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (schedules != null && !schedules.isEmpty()) {
            DataBaseHelper.getInstance(context).addSchedules(schedules);
        }
    }

    private void uInit(View view) {

        scheduleList = (RecyclerView) view.findViewById(R.id.scheduleList);
        scheduleListProgress = (ProgressBar) view.findViewById(R.id.scheduleListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
