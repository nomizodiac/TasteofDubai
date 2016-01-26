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
import org.progos.tasteofdubaicms.adapters.RestaurantDetailsSectionedAdapter;
import org.progos.tasteofdubaicms.adapters.ScheduleDetailsAdapter;
import org.progos.tasteofdubaicms.adapters.ScheduleDetailsSectionedAdapter;
import org.progos.tasteofdubaicms.model.Schedule;
import org.progos.tasteofdubaicms.model.ScheduleItem;
import org.progos.tasteofdubaicms.model.ScheduleSectionedItem;
import org.progos.tasteofdubaicms.sqlite.DataBaseHelper;
import org.progos.tasteofdubaicms.utility.Commons;
import org.progos.tasteofdubaicms.utility.FontFactory;
import org.progos.tasteofdubaicms.utility.Strings;
import org.progos.tasteofdubaicms.utility.Utils;
import org.progos.tasteofdubaicms.webservices.Urls;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NomBhatti on 11/25/2015.
 */
public class ScheduleDetailsFragment extends Fragment {

    Context context;
    RecyclerView scheduleDetailsList;
    ProgressBar scheduleListProgress;
    ScheduleDetailsAdapter adapter;
    ArrayList<ScheduleItem> scheduleItems;
    RelativeLayout connectionLostLayout;
    TextView connectionLostTextView;
    Schedule schedule;

    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        uInit(view);

        schedule = (Schedule) getArguments().getSerializable(Strings.SCHEDULE_OBJ);
        scheduleItems = new ArrayList<>();
        scheduleDetailsList.setHasFixedSize(true);
        scheduleDetailsList.setLayoutManager(new LinearLayoutManager(context));

        View header = LayoutInflater.from(context).inflate(R.layout.header_list_schedule_details, scheduleDetailsList, false);
        TextView dayTextView = (TextView) header.findViewById(R.id.dayTextView);
        TextView dateTextView = (TextView) header.findViewById(R.id.dateTextView);
        dayTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        dateTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_SEMI_BOLD));
        dayTextView.setText(schedule.getDay());
        dateTextView.setText(schedule.getDate());

        adapter = new ScheduleDetailsAdapter(context, header, scheduleItems);
        connectionLostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadScheduleDetails(schedule.getId());
            }
        });

        loadScheduleDetails(schedule.getId());
        return view;
    }

    private void loadScheduleDetails(String scheduleId) {

        if (!Utils.hasConnection(context)) {

            String scheduleItemDetails = DataBaseHelper.getInstance(context).getScheduleItemDetails(scheduleId);
            if (scheduleItemDetails != null && !scheduleItemDetails.isEmpty()) {
                try {
                    parseScheduleDetailsResponse(new JSONObject(scheduleItemDetails));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                scheduleListProgress.setVisibility(View.GONE);
                scheduleDetailsList.setVisibility(View.GONE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            connectionLostLayout.setVisibility(View.GONE);
            scheduleListProgress.setVisibility(View.VISIBLE);

            String url = "http://elephantationlabs.com/tasteofdubaiapp/schedules-api/?parent=" + schedule.getId();
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(final JSONObject response)
                {
                    Log.d(TAG, "Response-ScheduleDetails: " + response.toString());
                    parseScheduleDetailsResponse(response);
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

    private void parseScheduleDetailsResponse(final JSONObject response) {

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final ArrayList<ScheduleSectionedItem> scheduleSectionedItems = new ArrayList<>();
                try
                {
                    String status = response.getString("status");
                    if(status.equalsIgnoreCase("success"))
                    {
                        JSONArray jsonArray = response.getJSONArray("data");
                        if(jsonArray!=null && jsonArray.length()>0)
                        {
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String heading = jsonObject.getString("heading");
                                JSONObject jsonObjectDetail =  jsonObject.getJSONObject("0");
                                String title = jsonObjectDetail.getString("title");
                                String content = jsonObjectDetail.getString("content");
                                JSONArray column1 = jsonObjectDetail.getJSONArray("column1");
                                JSONArray column2 = jsonObjectDetail.getJSONArray("column2");
                                ArrayList<ScheduleItem> scheduleItems = new ArrayList<>();
                                for (int j = 0; j < column1.length(); j++)
                                {
                                    ScheduleItem scheduleItem = new ScheduleItem(column1.getString(j), column2.getString(j));
                                    scheduleItems.add(scheduleItem);
                                }
                                scheduleSectionedItems.add(new ScheduleSectionedItem(schedule.getId(), heading, title, content, scheduleItems));
                            }

                            if (Utils.hasConnection(context) && scheduleSectionedItems != null && !scheduleSectionedItems.isEmpty()) {
                                Log.d(TAG, "ScheduleDetailsItems" + response.toString());
                                DataBaseHelper.getInstance(context).addScheduleItemDetails(schedule.getId(), response.toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(scheduleSectionedItems!=null)
                            showScheduleDetailsList(scheduleSectionedItems);
                    }
                });
            }
        });
        thread.start();
    }

    private void showScheduleDetailsList(ArrayList<ScheduleSectionedItem> scheduleSectionedItems) {

        scheduleItems.clear();
        int sectionedPos = 1;
        List<ScheduleDetailsSectionedAdapter.Section> sections = new ArrayList<>();
        for(int i=0; i<scheduleSectionedItems.size(); i++)
        {
            String heading = scheduleSectionedItems.get(i).getHeading();
            String title = scheduleSectionedItems.get(i).getTitle();
            String content = scheduleSectionedItems.get(i).getContent();
            ArrayList<ScheduleItem> scheduleItemsTemp = scheduleSectionedItems.get(i).getScheduleItems();
            sections.add(new ScheduleDetailsSectionedAdapter.Section(sectionedPos, heading, title, content));
            scheduleItems.addAll(scheduleItemsTemp);
            sectionedPos = scheduleItems.size() + 1;
        }

        ScheduleDetailsSectionedAdapter.Section[] dummy = new ScheduleDetailsSectionedAdapter.Section[sections.size()];
        ScheduleDetailsSectionedAdapter mSectionedAdapter = new
                ScheduleDetailsSectionedAdapter(context, R.layout.item_list_schedule_details_section,
                R.id.venueTextView, R.id.timeTextView, R.id.actTextView, adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        scheduleDetailsList.setAdapter(mSectionedAdapter);
        scheduleListProgress.setVisibility(View.GONE);
        scheduleDetailsList.setVisibility(View.VISIBLE);
    }

    private void uInit(View view) {

        scheduleDetailsList = (RecyclerView) view.findViewById(R.id.scheduleList);
        scheduleListProgress = (ProgressBar) view.findViewById(R.id.scheduleListProgress);
        connectionLostLayout = (RelativeLayout) view.findViewById(R.id.connectionLostLayout);
        connectionLostTextView = (TextView) view.findViewById(R.id.connectionLostTextView);
        connectionLostTextView.setTypeface(FontFactory.getInstance().getFont(context, Commons.FONT_RALEWAY_REGULAR));
    }
}
