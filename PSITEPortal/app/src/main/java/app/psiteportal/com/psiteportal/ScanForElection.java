package app.psiteportal.com.psiteportal;

/**
 * Created by Lawrence on 10/12/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/9/2015.
 */
public class ScanForElection extends AppCompatActivity {
    
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    String pid, fname, lname, insti, usertype;
    TextView fullname, institution, user_type;
    Button scanQR, reg_election;
    ProgressDialog pDialog;
    final static String TAG_SUCCESS = "success";
    final static String TAG_MESSAGE = "message";
    JSONParser jsonParser = new JSONParser();
    int success;
    String message;
    String election_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_election_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            election_id = extras.getString("election_id");
        }

        reg_election = (Button) findViewById(R.id.register_election);
        scanQR = (Button) findViewById(R.id.qr_scan_btn);
        institution = (TextView) findViewById(R.id.institution_holder);
        fullname = (TextView) findViewById(R.id.fname_holder);
        user_type = (TextView) findViewById(R.id.usertype_holder);


        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(ScanForElection.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }

                scanQR.setVisibility(View.GONE);
                reg_election.setVisibility(View.VISIBLE);
            }
        });


        reg_election.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegisterElection().execute();
            }
        });

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
//                Toast toast = Toast.makeText(RegisterSeminarScanner.this, "Content:" + contents + "Format:" + format, Toast.LENGTH_LONG);
//                toast.show();

                String[] resultArr = contents.split(",");

                pid = resultArr[0];
                fname = resultArr[1];
                lname = resultArr[2];
                insti = resultArr[3];
                usertype = resultArr[4];

                fullname.setText(fname +" "+ lname);
                institution.setText(insti);
                user_type.setText(usertype);
            }
        }
    }


    private class RegisterElection extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ELEC_ATTENDANCE_URL = res.getString(R.string.election_attendance_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ScanForElection.this);
            pDialog.setTitle("Getting user details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("election_id", election_id));

            JSONObject json = jsonParser.makeHttpRequest(
                    ELEC_ATTENDANCE_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
                if (success == 1) {


                } else {
//                    Log.d("get seminars failure", json.getString(TAG_MESSAGE));
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
                Toast.makeText(ScanForElection.this, file_url, Toast.LENGTH_SHORT).show();
            }

            if (success == 1) {
                reg_election.setVisibility(View.GONE);
                scanQR.setVisibility(View.VISIBLE);
                Toast.makeText(ScanForElection.this, "User successfully registered", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ScanForElection.this, message , Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
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
