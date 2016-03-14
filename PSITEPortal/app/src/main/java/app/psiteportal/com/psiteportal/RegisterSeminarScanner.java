package app.psiteportal.com.psiteportal;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 9/27/2015.
 */
public class RegisterSeminarScanner extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    Button scanQR;
    Button pay, discount_pay;
    Button registerUserBtn;
    RadioGroup pay_type;
    RadioButton payMethod;
    RadioButton discounted_pay;
    TextView name, address, email, institution, points, status;
    ProgressDialog pDialog;
    String pid;
    final static String TAG_SUCCESS = "success";
    final static String TAG_MESSAGE = "message";
    private static final String TAG_PAY = "normal";
    private static final String TAG_PAY_DISCOUNT = "discounted";
    JSONParser jsonParser = new JSONParser();
    JSONArray users = null;
    String sid;
    String bonus_points;
    String seminar_fee;
    String discount_fee;
    int points_cost;
    LinearLayout dashholder;
    LinearLayout dashboard;
    String currentDate;
    String res_registration_id;
    String res_paid;
    String res_pay_type;
    String res_attended;
    String res_id;
    String res_fname;
    String res_lname;
    String res_gender;
    String res_address;
    String res_email;
    String res_institution;
    String res_points;
    String res_usertype;
    String res_activated;
    String result_message;
    int success;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Scanner");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sid = extras.getString("sid");
            bonus_points = extras.getString("bonus_points");
            seminar_fee = extras.getString("seminar_fee");
            discount_fee = extras.getString("discount_fee");
            points_cost = extras.getInt("points_cost");
        }

        Log.wtf("seminar payment", seminar_fee +" ----- "+ discount_fee);

        scanQR = (Button) findViewById(R.id.qr_scan_btn);
        pay = (Button) findViewById(R.id.pay);
        registerUserBtn = (Button) findViewById(R.id.register_user);
        name = (TextView) findViewById(R.id.fname_holder);
        address = (TextView) findViewById(R.id.address_holder);
        email = (TextView) findViewById(R.id.email_holder);
        institution = (TextView) findViewById(R.id.institution_holder);
        points = (TextView) findViewById(R.id.points_holder);
        status = (TextView) findViewById(R.id.status_holder);
        dashholder = (LinearLayout) findViewById(R.id.dash_holder);
        dashboard = (LinearLayout) findViewById(R.id.dashboard);
        pay_type = (RadioGroup) findViewById(R.id.pay_type);
        discounted_pay = (RadioButton) findViewById(R.id.discounted_pay);

        currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        Log.wtf("current Date", currentDate);

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(RegisterSeminarScanner.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }
            }
        });


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = pay_type.getCheckedRadioButtonId();
                payMethod = (RadioButton) findViewById(selectedId);

//                Toast.makeText(RegisterSeminarScanner.this, payMethod.getText(), Toast.LENGTH_SHORT).show();

                if (payMethod.getText().equals("Normal Fee")) {
                    new AlertDialog.Builder(RegisterSeminarScanner.this)
                            .setTitle("Seminar Registration")
                            .setMessage("Confirm payment of ₱" + seminar_fee+ " \nby Mr./Mrs. "+ res_fname +" "+ res_lname +"?")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new PayRegistration().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(RegisterSeminarScanner.this)
                            .setTitle("Seminar Registration")
                            .setMessage("Confirm payment of ₱" + discount_fee + " \nby Mr./Mrs. "+ res_fname +" "+ res_lname +"?")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new PayDiscountedRegistration().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(RegisterSeminarScanner.this)
                        .setTitle("Seminar Registration")
                        .setMessage("Register " + res_fname + " " + res_lname + " to this seminar?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                updateRegistration();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
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
                Log.e("user ID", resultArr[0]);

//                new GetUserTask().execute();
                getUserTask();
            }
        }
    }


    public void getUserTask() {
        String url = "http://www.psite7.org/portal/webservices/get_registered_user.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", pid);
        params.put("sid", sid);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try {
                            res_registration_id = jsonObject.getString("registration_id");
                            res_paid = jsonObject.getString("paid");
                            res_pay_type = jsonObject.getString("pay_type");
                            res_attended = jsonObject.getString("attended");

                            res_id = jsonObject.getString("pid");
                            res_fname = jsonObject.getString("firstname");
                            res_lname = jsonObject.getString("lastname");
                            res_institution = jsonObject.getString("institution");
                            res_address = jsonObject.getString("address");
                            res_email = jsonObject.getString("email");
                            res_points = jsonObject.getString("points");
                            res_usertype = jsonObject.getString("usertype");
                            res_activated = jsonObject.getString("activated");

                            Log.wtf("register seminar", jsonObject.toString());

                            name.setText(res_fname + " " + res_lname);
                            address.setText(res_address);
                            email.setText(res_email);
                            institution.setText(res_institution);
                            points.setText(String.valueOf(res_points));

                            if (res_activated.equals("1")) {
                                status.setText("Member");
                            } else {
                                status.setText("Non member");
                            }

                            dashholder.setVisibility(View.VISIBLE);

                            if (res_paid.equals("1")) {
                                dashboard.setVisibility(View.GONE);
                                if (res_attended.equals("1")) {
                                    //e-register ang user
                                    registerUserBtn.setVisibility(View.GONE);
                                } else {
                                    dashholder.setEnabled(false);
                                }
                            } else {
                                //show dashboard kung wa pa kabayad
                                dashboard.setVisibility(View.VISIBLE);
                                if (res_activated.equals("1")) {

                                } else {
                                    discounted_pay.setEnabled(false);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activity activity = RegisterSeminarScanner.this;
                        if (volleyError instanceof NoConnectionError) {
                            String errormsg = "Check your internet connection";
                            Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        queue.add(jsonObjectRequest);

    }

    public void updateRegistration() {
        String url = "http://www.psite7.org/portal/webservices/update_sem_registration.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", pid);
        params.put("sid", sid);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try {
                            success = jsonObject.getInt("success");
                            message = jsonObject.getString("message");

                            Log.wtf("seminar registration", jsonObject.toString());

                            if (success == 1) {
                                name.setText("");
                                address.setText("");
                                email.setText("");
                                institution.setText("");
                                points.setText("");
                                status.setText("");
                                dashholder.setVisibility(View.GONE);
                                Toast.makeText(RegisterSeminarScanner.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterSeminarScanner.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activity activity = RegisterSeminarScanner.this;
                        if (volleyError instanceof NoConnectionError) {
                            String errormsg = "Check your internet connection";
                            Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    private class PayRegistration extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String PAYMENT_URL = res.getString(R.string.payment_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterSeminarScanner.this);
            pDialog.setTitle("Processing transaction details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid));
            params.add(new BasicNameValuePair("sid", sid));
            params.add(new BasicNameValuePair("pay_type", TAG_PAY));
            params.add(new BasicNameValuePair("date", currentDate));
            params.add(new BasicNameValuePair("amount", seminar_fee));

            JSONObject json = jsonParser.makeHttpRequest(
                    PAYMENT_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                } else {
                    result_message = json.getString(TAG_MESSAGE);
                    Log.e("Failed to register", json.getString(TAG_MESSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (success == 1) {

                Toast.makeText(RegisterSeminarScanner.this, "The payment of " + res_fname + " " + res_lname + " is successfully recorded.", Toast.LENGTH_SHORT).show();
                dashboard.setVisibility(View.GONE);
            } else {
                Toast.makeText(RegisterSeminarScanner.this, result_message, Toast.LENGTH_SHORT).show();
            }

        }

    }


    private class PayDiscountedRegistration extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String PAYMENT_URL = res.getString(R.string.payment_url);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterSeminarScanner.this);
            pDialog.setTitle("Processing transaction details . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", pid + ""));
            params.add(new BasicNameValuePair("sid", sid));
            params.add(new BasicNameValuePair("pay_type", TAG_PAY_DISCOUNT));
            params.add(new BasicNameValuePair("date", currentDate));
            params.add(new BasicNameValuePair("amount", discount_fee));

            JSONObject json = jsonParser.makeHttpRequest(
                    PAYMENT_URL, "POST", params);

            try {
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                } else {
                    result_message = json.getString(TAG_MESSAGE);
                    Log.e("Failed to register", result_message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (success == 1) {
                Toast.makeText(RegisterSeminarScanner.this, "The payment of " + res_fname + " " + res_lname + " is successfully recorded.", Toast.LENGTH_SHORT).show();
                dashboard.setVisibility(View.GONE);
            } else {
                Toast.makeText(RegisterSeminarScanner.this, result_message, Toast.LENGTH_SHORT).show();
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

