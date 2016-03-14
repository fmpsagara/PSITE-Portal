package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
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

import app.psiteportal.com.psiteportal.ElectionActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.JSONParser;

public class ElectionFragment extends ListFragment {
    String user_pid, user_usertype;
    JSONArray elections = null;
    ArrayList<HashMap<String, String>> electionsList;
    ProgressDialog pDialog;
    public final String TAG_SUCCESS = "success";
    public final String TAG_MESSAGE = "message";
    public final String TAG_ELECTION_ID = "election_id";
    public final String TAG_ELECTION_TITLE = "election_title";
    public final String TAG_ELECTIONS = "elections";
    public final String TAG_PID = "pid";

    public ElectionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //start test pable
        Log.d("TEST!", "FRAGMENT CALLED!");
        //end test pable
        Bundle bundle = getArguments();
        user_pid = bundle.getString("user_pid");
        user_usertype = bundle.getString("usertype");

        View rootView = inflater.inflate(R.layout.fragment_election, container, false);

        new GetActiveElectionsTask().execute();


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        electionsList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String election_id = ((TextView) view.findViewById(R.id.election_id)).getText()
                        .toString();
                String election_title = ((TextView) view.findViewById(R.id.election_title)).getText()
                        .toString();

                Intent i = new Intent(getActivity(), ElectionActivity.class);
                Bundle bundle = new Bundle();
                i.putExtra("election_id", election_id);
                i.putExtra("election_title", election_title);
                i.putExtra("user_id", user_pid);
                i.putExtra("user_usertype", user_usertype);

                i.putExtras(bundle);
                startActivity(i);
            }
        });

    }

    private class GetActiveElectionsTask extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ACTIVE_ELECTION_URL = res.getString(R.string.get_election_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Getting active elections . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(
                    ACTIVE_ELECTION_URL, "GET", params);

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get seminars success!", json.toString());
                    elections = json.getJSONArray(TAG_ELECTIONS);

                    for (int i = 0; i < elections.length(); i++) {
                        JSONObject c = elections.getJSONObject(i);

                        String elec_id = c.getString(TAG_ELECTION_ID);
                        String elec_title = c.getString(TAG_ELECTION_TITLE);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_ELECTION_ID, elec_id);
                        map.put(TAG_ELECTION_TITLE, elec_title);
                        map.put(TAG_PID, user_pid);

                        electionsList.add(map);
                    }

//                    return json.getString(TAG_MESSAGE);
                } else {
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
                            getActivity(), electionsList,
                            R.layout.election_item,
                            new String[]{TAG_ELECTION_ID,
                                    TAG_ELECTION_TITLE,
                                    TAG_PID},
                            new int[]{R.id.election_id,
                                    R.id.election_title,
                                    R.id.user_id});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }
    }
}
