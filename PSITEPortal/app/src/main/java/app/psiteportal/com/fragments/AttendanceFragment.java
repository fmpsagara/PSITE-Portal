package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.JSONParser;

public class AttendanceFragment extends Fragment implements  View.OnClickListener{

    RatingBar rating;
    TextView rate;
    EditText comment;
    Button mSubmit;
    ProgressDialog pDialog;
    String post, post_rate, post_comm;
    JSONParser jsonParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	public AttendanceFragment(){}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);


        rating = (RatingBar) rootView.findViewById(R.id.ratingBar);
        rate = (TextView) rootView.findViewById(R.id.txtRatingValue);
        comment = (EditText) rootView.findViewById(R.id.comm);

        mSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        mSubmit.setOnClickListener(this);
        addListenerOnRatingBar();



        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.attendance_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_attendance:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addListenerOnRatingBar() {

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                rate.setText(String.valueOf(rating));

            }
        });
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(getActivity(),  String.valueOf(rating.getRating()) + "" +
        comment.getText().toString(), Toast.LENGTH_SHORT).show();

        new PostComment().execute();
    }


    class PostComment extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ATTENDANCE_URL = res.getString(R.string.attendance_url);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            post_rate = String.valueOf(rating.getRating());
            post_comm = comment.getText().toString();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Posting Comment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", "3"));
                params.add(new BasicNameValuePair("pid", "7"));
                params.add(new BasicNameValuePair("rating", post_rate));
                params.add(new BasicNameValuePair("comment", post_comm));

                Log.d("request!", params.toString());

                // Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(ATTENDANCE_URL,
                        "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG)
                        .show();
            }

        }

    }

}
