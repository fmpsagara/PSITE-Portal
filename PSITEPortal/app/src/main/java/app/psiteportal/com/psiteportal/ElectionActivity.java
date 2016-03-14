package app.psiteportal.com.psiteportal;

/**
 * Created by Lawrence on 10/12/2015.
 */

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/9/2015.
 */

public class ElectionActivity extends ListActivity {


    ProgressDialog pDialog;
    RadioButton nominate;
    ArrayList<HashMap<String, String>> nomineesList;
    JSONArray nominees = null;
    String pid, usertype, election_title, election_id;
    public final String TAG_MESSAGE = "message";
    public final String TAG_SUCCESS = "success";
    public final String TAG_NOMINEES = "nominees";
    public final String TAG_PID = "pid";
    private static final String TAG_FNAME = "firstname";
    private static final String TAG_LNAME = "lastname";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_INSTITUTION = "institution";
    private static final String TAG_POINTS = "points";
    private static final String TAG_USERTYPE = "usertype";
    int nominated_position;
    ListAdapter adapter;
    String vote_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        //start test pable
        Log.d("TEST!", "ACTIVITY CALLED!");
        //end test pable
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("user_id");
            usertype = extras.getString("user_usertype");
            election_id = extras.getString("election_id");
            election_title = extras.getString("election_title");
        }

//        nominate = (RadioButton) findViewById(R.id.nominate_radio);

        new GetNomineesTask().execute();


        nomineesList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                vote_user_id = ((TextView) view.findViewById(R.id.elec_pid)).getText()
                        .toString();
                String firstname = ((TextView) view.findViewById(R.id.elec_fname)).getText()
                        .toString();
                String lastname = ((TextView) view.findViewById(R.id.elec_lname)).getText()
                        .toString();

                confirmationDialog(firstname, lastname);

            }
        });

    }

    public void confirmationDialog(String firstname, String lastname){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to cast your vote to "+ firstname +" "+ lastname);

        alertDialogBuilder.setPositiveButton("Cast", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(ElectionActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                new VoteUser().execute();
            }
        });

        alertDialogBuilder.setNegativeButton("Decline",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }




    private class GetNomineesTask extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String NOMINEES_URL = res.getString(R.string.nominees_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ElectionActivity.this);
            pDialog.setTitle("Getting qualified users . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("election_id", election_id));
            Log.e("election id", election_id);
            JSONObject json = jsonParser.makeHttpRequest(
                    NOMINEES_URL, "POST", params);


            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("get users success!", json.toString());
                    nominees = json.getJSONArray(TAG_NOMINEES);

                    for (int i = 0; i < nominees.length(); i++) {
                        JSONObject c = nominees.getJSONObject(i);

                        String pid = c.getString(TAG_PID);
                        String fname = c.getString(TAG_FNAME);
                        String lname = c.getString(TAG_LNAME);
                        String gender = c.getString(TAG_GENDER);
                        String contact = c.getString(TAG_CONTACT);
                        String email = c.getString(TAG_EMAIL);
                        String address = c.getString(TAG_ADDRESS);
                        String institution = c.getString(TAG_INSTITUTION);
                        String points = c.getString(TAG_POINTS);
                        String usertype = c.getString(TAG_USERTYPE);


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_PID, pid);
                        map.put(TAG_FNAME, fname);
                        map.put(TAG_LNAME, lname);
                        map.put(TAG_GENDER, gender);
                        map.put(TAG_CONTACT, contact);
                        map.put(TAG_EMAIL, email);
                        map.put(TAG_ADDRESS, address);
                        map.put(TAG_INSTITUTION, institution);
                        map.put(TAG_POINTS, points);
                        map.put(TAG_USERTYPE, usertype);

                        nomineesList.add(map);
                    }

//                    return json.getString(TAG_MESSAGE);
                } else {
//                    Log.d("get seminars failure", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
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
                Toast.makeText(ElectionActivity.this, file_url, Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(new Runnable() {
                public void run() {

                    adapter = new SimpleAdapter(
                            ElectionActivity.this, nomineesList,
                            R.layout.nominees_item, new String[]{TAG_PID, TAG_FNAME, TAG_LNAME, TAG_GENDER,
                            TAG_CONTACT, TAG_EMAIL, TAG_ADDRESS, TAG_INSTITUTION, TAG_POINTS, TAG_USERTYPE},
                            new int[]{R.id.elec_pid, R.id.elec_fname, R.id.elec_lname, R.id.elec_gender,
                                    R.id.elec_contact, R.id.elec_email, R.id.elec_address, R.id.elec_institution,
                                    R.id.elec_points, R.id.elec_usertype});

                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }


    private class VoteUser extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String VOTE_URL = res.getString(R.string.vote_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ElectionActivity.this);
            pDialog.setTitle("Processing your vote . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("election_id", election_id));
            params.add(new BasicNameValuePair("pid",vote_user_id));
            params.add(new BasicNameValuePair("voter", pid));

            JSONObject json = jsonParser.makeHttpRequest(
                    VOTE_URL, "POST", params);


            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("vote success!", json.toString());
                    nominees = json.getJSONArray(TAG_NOMINEES);

//                    return json.getString(TAG_MESSAGE);
                } else {
//                    Log.d("get seminars failure", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
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
                Toast.makeText(ElectionActivity.this, file_url, Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (usertype.equals("member")) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            getMenuInflater().inflate(R.menu.election_scanner, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_scan:
                Intent i = new Intent(this, ScanForElection.class);
                i.putExtra("election_id", election_id);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
