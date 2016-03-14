package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by Lawrence on 10/4/2015.
 */
public class SeminarProfileActivity extends AppCompatActivity {

    LinearLayout seminar_attendance_ll;
    LinearLayout seminarAttendance;
    TextView title_seminar;
    TextView seminar_date;
    TextView seminar_about;
    TextView seminar_time;
    TextView seminar_venue;
    NetworkImageView imageView;
    String sid, title, date, start_time, end_time, bonus, seminar_fee,
            discounted_fee, points_cost, venue, about, bannerUrl, is_active, out_activated, attendance_code;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String seminar_id;
    String seminar_name;
    String user_type;
    int user_id;
    String pid;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    CollapsingToolbarLayout toolBarLayout;
    String message;
    String TAG_ACTIVE = "1";
    private FloatingActionButton btnFab;
    RelativeLayout fabLayout;
    Date current;
    String str_date;
    String network_time_stamp;
    String seminar_time_stamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user_id = extras.getInt("user_pid");
            pid = user_id + "";
            seminar_id = extras.getString("seminar_id");
            seminar_name = extras.getString("seminar_title");
            user_type = extras.getString("usertype");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        imageView = (NetworkImageView) findViewById(R.id.banner_image);
        fabLayout = (RelativeLayout) findViewById(R.id.fab_layout);
        seminarAttendance = (LinearLayout) findViewById(R.id.seminar_attendance_ll);
        title_seminar = (TextView) findViewById(R.id.seminar_title);
        seminar_date = (TextView) findViewById(R.id.seminar_date);
        seminar_time = (TextView) findViewById(R.id.seminar_time);
        seminar_venue = (TextView) findViewById(R.id.seminar_venue);
        seminar_about = (TextView) findViewById(R.id.seminar_about);

        //get the seminar details
        getSeminar(seminar_id);
//        new GetCurrentTime().execute();
//        compareDate();

        if (user_type.equals("officer_member")) {
            //set up the fab button
            setupUI();
        } else {
            fabLayout.setVisibility(View.GONE);
        }


    }

    public void getSeminar(String id) {

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = "http://www.psite7.org/portal/webservices/get_seminar.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", id);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try {
                            sid = jsonObject.getString("sid");
                            title = jsonObject.getString("seminar_title");
                            date = jsonObject.getString("seminar_date");
                            start_time = jsonObject.getString("seminar_start_time");
                            end_time = jsonObject.getString("seminar_end_time");
                            bonus = jsonObject.getString("bonus_points");
                            seminar_fee = jsonObject.getString("seminar_fee");
                            discounted_fee = jsonObject.getString("discounted_fee");
                            points_cost = jsonObject.getString("points_cost");
                            venue = jsonObject.getString("venue_address");
                            about = jsonObject.getString("about");
                            bannerUrl = jsonObject.getString("seminar_banner");
                            is_active = jsonObject.getString("isActive");
                            out_activated = jsonObject.getString("out_activated");
                            attendance_code = jsonObject.getString("attendance_code");

                            Log.d("seminar_item", jsonObject.toString());

                            toolBarLayout.setTitle(title);
                            imageView.setImageUrl(bannerUrl, imageLoader);
                            title_seminar.setText(title);

                            if(out_activated.equals("1")){
                                seminarAttendance.setVisibility(View.VISIBLE);
                            }else{
                                seminarAttendance.setVisibility(View.GONE);
                            }


                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            try {
                                Date d = sdf.parse(date);
                                sdf.applyPattern("MMMM dd, yyyy");
                                String newDateString = sdf.format(d);
                                seminar_date.setText(newDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            String disp_start_time, disp_end_time;
                            try {
                                final SimpleDateFormat sdf2 = new SimpleDateFormat("H:mm");
                                final Date dateObj = sdf2.parse(start_time);
                                final Date dateObj2 = sdf2.parse(end_time);
                                disp_start_time = new SimpleDateFormat("hh:mm a").format(dateObj) + "";
                                disp_end_time = new SimpleDateFormat("hh:mm a").format(dateObj2) + "";

                                seminar_time.setText(disp_start_time + " - " + disp_end_time);

                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }




//                            seminar_time.setText(start_time + " - " + end_time);
                            seminar_venue.setText(venue);
                            seminar_about.setText(about);

                            seminar_time_stamp = date + " " + end_time + ":00";
                            Log.wtf("sem time stamp", seminar_time_stamp);

//                            new GetCurrentTime().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

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

    //class to get the current time from the internet
    class GetCurrentTime extends AsyncTask<String, String, Date> {
        Date current_date;
        SntpClient client;

        @Override
        protected Date doInBackground(String... strings) {
            client = new SntpClient();
            Long current_time;
            if (client.requestTime("0.pool.ntp.org", 30000)) {
                current_time = client.getNtpTime();
                current_date = new Date(current_time);
            }
            return current_date;
        }

        @Override
        protected void onPostExecute(Date date) {
            super.onPostExecute(date);

            current = date;
//            str_date = date.toString();
            Log.wtf("current date", current.toString());
            str_date = current_date.toString().substring(4);
            str_date = str_date.replace("GMT+08:00 ", "");
            Log.wtf("date substring", str_date);
            String year = str_date.substring(16);
            Log.wtf("yeaar", year);
            String month = str_date.substring(0, 3);
            Log.wtf("month", month);
            String day = str_date.substring(4, 6);
            Log.wtf("day", day);
            String parsed_date = year + "/" + month + "/" + day;
            Log.wtf("parsed date", parsed_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd");
            try {
                Date varDate = dateFormat.parse(parsed_date);
                dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Log.wtf("formatted date", dateFormat.format(varDate));
                parsed_date = dateFormat.format(varDate).toString();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            String parsed_time = str_date.substring(7, 15);
            Log.wtf("parsed time", parsed_time);
            network_time_stamp = parsed_date + " " + parsed_time;
            Log.wtf("network time stamp", network_time_stamp);
            Log.wtf("seminar time stamp", seminar_time_stamp);

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = format.parse(network_time_stamp);
                date2 = format.parse(seminar_time_stamp);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (date1.before(date2)) {
                Log.wtf("done", "seminar is not yet finished");
            } else {
                Log.wtf("not yet", "seminar is already finished");
                seminar_attendance_ll.setVisibility(View.VISIBLE);
            }
        }
    }


    public String formatMonth(int month, Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] monthNames = symbols.getMonths();
        return monthNames[month - 1];
    }

    public void seminarAttendance(View v) {
        Intent i = new Intent(this, SeminarAttendance.class);
        i.putExtra("user_id", pid);
        i.putExtra("seminar_id", seminar_id);
        i.putExtra("seminar_title", title);
        i.putExtra("attendance_code", attendance_code);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (user_type.equals("officer_member")) {
            getMenuInflater().inflate(R.menu.seminar_panel_officer_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.seminar_panel_member_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add_photo:
                Intent i = new Intent(this, CaptureImageActivity.class);
                i.putExtra("user_id", pid);
                i.putExtra("seminar_id", seminar_id);
                i.putExtra("seminar_title", seminar_name);
                startActivity(i);
                return true;
            case R.id.action_liquidate:
                Intent e = new Intent(this, LiquidationActivity.class);
                e.putExtra("pid", pid);
                e.putExtra("sid", seminar_id);
                startActivity(e);
                return true;
            case R.id.action_configurations:
                Intent f = new Intent(this, SeminarPanel.class);
                f.putExtra("user_id", pid);
                f.putExtra("seminar_id", seminar_id);
                startActivity(f);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUI() {
        btnFab = (FloatingActionButton) findViewById(R.id.btnFloatingAction);
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(SeminarProfileActivity.this, RegisterSeminarScanner.class);
                i.putExtra("pid", user_id);
                i.putExtra("sid", seminar_id);
                i.putExtra("bonus_points", bonus);
                i.putExtra("seminar_fee", seminar_fee);
                i.putExtra("discount_fee", discounted_fee);
                i.putExtra("points_cost", points_cost);

                startActivity(i);
            }
        });
    }
}