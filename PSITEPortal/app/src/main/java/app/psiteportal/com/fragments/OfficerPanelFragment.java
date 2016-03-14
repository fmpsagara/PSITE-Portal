package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.psiteportal.com.psiteportal.AddSeminarActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.SeminarPanel;
import app.psiteportal.com.utils.JSONParser;


public class OfficerPanelFragment extends ListFragment {

    ProgressDialog pDialog;
    final static String TAG_SUCCESS = "success";
    final static String TAG_MESSAGE = "message";
    private static final String TAG_SEMINARS = "seminars";
    private static final String TAG_SID = "sid";
    private static final String TAG_PID = "pid";
    private static final String TAG_TITLE = "seminar_title";
    private static final String TAG_SEMINAR_MONTH = "seminar_month";
    private static final String TAG_SEMINAR_DAY = "seminar_day";
    private static final String TAG_SEMINAR_YEAR = "seminar_year";
    private static final String TAG_TIME_START_HOUR = "seminar_start_time_hour";
    private static final String TAG_TIME_START_MINUTE = "seminar_start_time_minute";
    private static final String TAG_TIME_END_HOUR = "seminar_end_time_hour";
    private static final String TAG_TIME_END_MINUTE = "seminar_end_time_minute";
    private static final String TAG_BONUS_POINTS = "bonus_points";
    private static final String TAG_SEMINAR_FEE = "seminar_fee";
    private static final String TAG_DISCOUNTED_FEE = "discounted_fee";
    private static final String TAG_POINT_COST = "points_cost";
    private static final String TAG_VENUE = "venue";
    private static final String TAG_FACILITATOR = "facilitator_name";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_IS_ACTIVE = "isActive";
    private static final String TAG_OUT_ACTIVATED = "out_activated";

    JSONArray seminars = null;
    ArrayList<HashMap<String, String>> seminarsList;

    public OfficerPanelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.fragment_officer_panel, container, false);


        new GetActiveSeminarsTask().execute();


        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        seminarsList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seminar_id = ((TextView) view.findViewById(R.id.seminar_id)).getText()
                        .toString();
                String seminar_title = ((TextView) view.findViewById(R.id.seminar_title)).getText()
                        .toString();
                String seminar_month = ((TextView) view.findViewById(R.id.seminar_month)).getText()
                        .toString();
                String seminar_day = ((TextView) view.findViewById(R.id.seminar_day)).getText()
                        .toString();
                String seminar_year = ((TextView) view.findViewById(R.id.seminar_year)).getText()
                        .toString();
                String seminar_start_hour = ((TextView) view.findViewById(R.id.seminar_start_hour)).getText()
                        .toString();
                String seminar_start_min = ((TextView) view.findViewById(R.id.seminar_start_min)).getText()
                        .toString();
                String seminar_end_hour = ((TextView) view.findViewById(R.id.seminar_end_hour)).getText()
                        .toString();
                String seminar_end_min = ((TextView) view.findViewById(R.id.seminar_end_min)).getText()
                        .toString();
                String seminar_bonus_points = ((TextView) view.findViewById(R.id.seminar_bonus_points)).getText()
                        .toString();
                String seminar_fee = ((TextView) view.findViewById(R.id.seminar_fee)).getText()
                        .toString();
                String seminar_discounted = ((TextView) view.findViewById(R.id.seminar_discounted)).getText()
                        .toString();
                String seminar_points_cost = ((TextView) view.findViewById(R.id.seminar_points_cost)).getText()
                        .toString();
                String seminar_venue = ((TextView) view.findViewById(R.id.seminar_venue)).getText()
                        .toString();
                String seminar_facilitator = ((TextView) view.findViewById(R.id.seminar_facilitator)).getText()
                        .toString();
                String seminar_about = ((TextView) view.findViewById(R.id.seminar_about)).getText()
                        .toString();
                String seminar_is_active = ((TextView) view.findViewById(R.id.seminar_is_active)).getText()
                        .toString();
                String seminar_out_activated = ((TextView) view.findViewById(R.id.seminar_out_activated)).getText()
                        .toString();

                Log.e("bonus points", seminar_bonus_points);
                Log.e("cost points", seminar_points_cost);
                Log.e("venue", seminar_venue);
                Log.e("title", seminar_title);


                Intent i = new Intent(getActivity(), SeminarPanel.class);
                Bundle bundle = new Bundle();
                i.putExtra("seminar_id", seminar_id);
                i.putExtra("seminar_title", seminar_title);
                i.putExtra("seminar_month", seminar_month);
                i.putExtra("seminar_day", seminar_day);
                i.putExtra("seminar_year", seminar_year);
                i.putExtra("seminar_start_hour", seminar_start_hour);
                i.putExtra("seminar_start_min", seminar_start_min);
                i.putExtra("seminar_end_hour", seminar_end_hour);
                i.putExtra("seminar_end_min", seminar_end_min);
                i.putExtra("seminar_bonus_points", seminar_bonus_points);
                i.putExtra("seminar_fee", seminar_fee);
                i.putExtra("seminar_discounted", seminar_discounted);
                i.putExtra("seminar_points_cost", seminar_points_cost);
                i.putExtra("seminar_venue", seminar_venue);
                i.putExtra("seminar_facilitator", seminar_facilitator);
                i.putExtra("seminar_about", seminar_about);
                i.putExtra("seminar_is_active", seminar_is_active);
                i.putExtra("seminar_out_activated", seminar_out_activated);

                i.putExtras(bundle);

                startActivity(i);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.officer_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_seminar:
                startActivity(new Intent(getActivity(), AddSeminarActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class GetActiveSeminarsTask extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ACTIVE_SEMINARS_URL = res.getString(R.string.active_seminars_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Getting active seminars . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(
                    ACTIVE_SEMINARS_URL, "GET", params);

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get seminars success!", json.toString());
                    seminars = json.getJSONArray(TAG_SEMINARS);

                    for (int i = 0; i < seminars.length(); i++) {
                        JSONObject c = seminars.getJSONObject(i);

                        String id = c.getString(TAG_SID);
                        String title = c.getString(TAG_TITLE);
                        String month = c.getString(TAG_SEMINAR_MONTH);
                        String day = c.getString(TAG_SEMINAR_DAY);
                        String year = c.getString(TAG_SEMINAR_YEAR);
                        String startTime_hour = c.getString(TAG_TIME_START_HOUR);
                        String startTime_mins = c.getString(TAG_TIME_START_MINUTE);
                        String endTime_hour = c.getString(TAG_TIME_END_HOUR);
                        String endTime_mins = c.getString(TAG_TIME_END_MINUTE);
                        String bonus_points = c.getString(TAG_BONUS_POINTS);
                        String seminar_fee = c.getString(TAG_SEMINAR_FEE);
                        String discounted_fee = c.getString(TAG_DISCOUNTED_FEE);
                        String point_cost = c.getString(TAG_POINT_COST);
                        String venue = c.getString(TAG_VENUE);
                        String facilitator = c.getString(TAG_FACILITATOR);
                        String about = c.getString(TAG_ABOUT);
                        String isActive = c.getString(TAG_IS_ACTIVE);
                        String out_activated = c.getString(TAG_OUT_ACTIVATED);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_SID, id);
                        map.put(TAG_TITLE, title);
                        map.put(TAG_SEMINAR_MONTH, month);
                        map.put(TAG_SEMINAR_DAY, day);
                        map.put(TAG_SEMINAR_YEAR, year);
                        map.put(TAG_TIME_START_HOUR, startTime_hour);
                        map.put(TAG_TIME_START_MINUTE, startTime_mins);
                        map.put(TAG_TIME_END_HOUR, endTime_hour);
                        map.put(TAG_TIME_END_MINUTE, endTime_mins);
                        map.put(TAG_BONUS_POINTS, bonus_points);
                        map.put(TAG_SEMINAR_FEE, seminar_fee);
                        map.put(TAG_DISCOUNTED_FEE, discounted_fee);
                        map.put(TAG_POINT_COST, point_cost);
                        map.put(TAG_VENUE, venue);
                        map.put(TAG_FACILITATOR, facilitator);
                        map.put(TAG_ABOUT, about);
                        map.put(TAG_IS_ACTIVE, isActive);
                        map.put(TAG_OUT_ACTIVATED, out_activated);

                        seminarsList.add(map);
                    }

//                    return json.getString(TAG_MESSAGE);
                } else {
//                    Log.d("get seminars failure", json.getString(TAG_MESSAGE));
//
//                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (file_url != null) {
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_SHORT).show();
            }

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), seminarsList,
                            R.layout.active_seminar_item,
                            new String[]{TAG_SID,
                                    TAG_TITLE,
                                    TAG_SEMINAR_MONTH,
                                    TAG_SEMINAR_DAY,
                                    TAG_SEMINAR_YEAR,
                                    TAG_TIME_START_HOUR,
                                    TAG_TIME_START_MINUTE,
                                    TAG_TIME_END_HOUR,
                                    TAG_TIME_END_MINUTE,
                                    TAG_BONUS_POINTS,
                                    TAG_SEMINAR_FEE,
                                    TAG_DISCOUNTED_FEE,
                                    TAG_POINT_COST,
                                    TAG_VENUE,
                                    TAG_FACILITATOR,
                                    TAG_ABOUT,
                                    TAG_IS_ACTIVE,
                                    TAG_OUT_ACTIVATED},
                            new int[]{R.id.seminar_id,
                                    R.id.seminar_title,
                                    R.id.seminar_month,
                                    R.id.seminar_day,
                                    R.id.seminar_year,
                                    R.id.seminar_start_hour,
                                    R.id.seminar_start_min,
                                    R.id.seminar_end_hour,
                                    R.id.seminar_end_min,
                                    R.id.seminar_bonus_points,
                                    R.id.seminar_fee,
                                    R.id.seminar_discounted,
                                    R.id.seminar_points_cost,
                                    R.id.seminar_venue,
                                    R.id.seminar_facilitator,
                                    R.id.seminar_about,
                                    R.id.seminar_is_active,
                                    R.id.seminar_out_activated});
                    // updating listview
                    setListAdapter(adapter);
                }
            });


        }
    }


}
