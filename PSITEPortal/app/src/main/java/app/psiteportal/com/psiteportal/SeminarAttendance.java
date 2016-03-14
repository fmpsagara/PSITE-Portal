package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 3/6/2016.
 */
public class SeminarAttendance extends AppCompatActivity implements View.OnClickListener{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    String post, post_rate, post_comm;
    Button submit;
    RatingBar rating;
    TextView rate;
    TextView seminar_title_tv;
    EditText comment;
    ProgressDialog pDialog;
    String pid, sid, title, attendance_code;
    RelativeLayout qr_layout;
    LinearLayout rating_layout;
    Button scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Attendance");

        rating = (RatingBar) findViewById(R.id.ratingBar);
        rate = (TextView) findViewById(R.id.txtRatingValue);
        comment = (EditText) findViewById(R.id.comm);
        submit = (Button) findViewById(R.id.btnSubmit);
        qr_layout = (RelativeLayout) findViewById(R.id.qr_layout);
        rating_layout = (LinearLayout) findViewById(R.id.rating_layout);
        scan_btn = (Button) findViewById(R.id.scan_code);
        seminar_title_tv = (TextView) findViewById(R.id.seminar_title_tv);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("user_id");
            sid = extras.getString("seminar_id");
            title = extras.getString("seminar_title");
            attendance_code = extras.getString("attendance_code");
        }

        submit.setOnClickListener(SeminarAttendance.this);
        seminar_title_tv.setText(title);


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(SeminarAttendance.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }
            }
        });

        addListenerOnRatingBar();

    }

    private static AlertDialog showDialog(final FragmentActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return downloadDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if(contents.equals(attendance_code)){
                    qr_layout.setVisibility(View.GONE);
                    rating_layout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this, "Code did not match, try again.", Toast.LENGTH_SHORT).show();
                }
            }
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

            pDialog = new ProgressDialog(SeminarAttendance.this);
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
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("pid", pid));
                params.add(new BasicNameValuePair("rating", post_rate));
                params.add(new BasicNameValuePair("comment", post_comm));

                Log.d("request!", params.toString());

                // Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(ATTENDANCE_URL,
                        "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt("success");
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    return json.getString("message");
                } else {
                    Log.d("Comment Failure!", json.getString("message"));
                    return json.getString("message");

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
                Toast.makeText(SeminarAttendance.this, file_url, Toast.LENGTH_LONG)
                        .show();
                finish();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
