package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.utils.JSONParser;

public class EditSeminar extends AppCompatActivity {

    int day, month, year, hourOfDay, minute;
    private Calendar calendar;
    TextView seminar_date, time_start, time_end;
    EditText seminar_title;
    EditText attendance_code;
    EditText et_seminar_fee;
    EditText et_discounted_fee;
    EditText seminar_venue;
    EditText et_about;
    EditText bonus_points;
    Button add_seminar;
    Button cancel;
    ImageButton dateToActivate, select_time_start, select_time_end;
    String pass_day, pass_month, pass_year;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String sid, title, date, start_time, end_time, bonus, seminar_fee,
            discounted_fee, points_cost, venue, about, atten_code, is_active, out_activated;
    String TAG_SUCCESS = "success";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seminar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_seminar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sid = extras.getString("sid");
        }

        dateToActivate = (ImageButton) findViewById(R.id.SelectDate);
        select_time_start = (ImageButton) findViewById(R.id.select_start_time);
        select_time_end = (ImageButton) findViewById(R.id.select_end_time);
        add_seminar = (Button) findViewById(R.id.update_seminar);
        cancel = (Button) findViewById(R.id.cancel_btn);
        seminar_title = (EditText) findViewById(R.id.seminar_title);
        seminar_date = (TextView) findViewById(R.id.selectedDate);
        time_start = (TextView) findViewById(R.id.start_time);
        time_end = (TextView) findViewById(R.id.end_time);
        bonus_points = (EditText) findViewById(R.id.bonus_points);
        et_seminar_fee = (EditText) findViewById(R.id.edit_seminar_fee);
        et_discounted_fee = (EditText) findViewById(R.id.discount_fee);
        attendance_code = (EditText) findViewById(R.id.attendance_code);
        seminar_venue = (EditText) findViewById(R.id.seminar_venue);
        et_about = (EditText) findViewById(R.id.edit_about);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getSeminar(sid);
//        new GetSeminarTask().execute();

        add_seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdateSeminarTask().execute();
            }
        });

    }

    public void setDate(View view) {
        showDialog(999);
    }

    public void setStartTime(View view) {
        showDialog(888);
    }

    public void setEndTime(View view) {
        showDialog(777);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == 888) {
            return new TimePickerDialog(this, myTimeListener, hourOfDay, minute, true);
        } else if (id == 777) {
            return new TimePickerDialog(this, myTimeListener2, hourOfDay, minute, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//             TODO Auto-generated method stub
            pass_day = "" + day;
            pass_month = "" + month;
            pass_year = "" + year;
            showDate(year, month + 1, day);
        }
    };


    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeStart(hourOfDay, minute);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeEnd(hourOfDay, minute);
        }
    };

    private void showDate(int year, int month, int day) {
        seminar_date.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }

    private void showTimeStart(int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            start_time = new SimpleDateFormat("HH:mm").format(dateObj) + "";
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        time_start.setText(start_time);
    }

    private void showTimeEnd(int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            end_time = new SimpleDateFormat("HH:mm").format(dateObj) + "";
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        time_end.setText(end_time);
    }


    class UpdateSeminarTask extends AsyncTask<String, String, String> {

        String seminar_title_str = seminar_title.getText().toString();
        String seminar_date_str = seminar_date.getText().toString();
        String seminar_time_start = time_start.getText().toString();
        String seminar_time_end = time_end.getText().toString();
        String seminar_fee_str = et_seminar_fee.getText().toString();
        String seminar_venue_str = seminar_venue.getText().toString();
        String about_str = et_about.getText().toString();
        String discounted_fee_str = et_discounted_fee.getText().toString();
        String bonus_str = bonus_points.getText().toString();
        String attendance_code_str = attendance_code.getText().toString();

        int total_points = Integer.parseInt(seminar_fee_str) - Integer.parseInt(discounted_fee_str);

        Resources res = getResources();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(EditSeminar.this);
            pDialog.setMessage("Updating Seminar . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("seminar_title", seminar_title_str));
                params.add(new BasicNameValuePair("seminar_date", seminar_date_str));
                params.add(new BasicNameValuePair("seminar_start_time", seminar_time_start));
                params.add(new BasicNameValuePair("seminar_end_time", seminar_time_end));
                params.add(new BasicNameValuePair("bonus_points", bonus_str));
                params.add(new BasicNameValuePair("seminar_fee", seminar_fee_str));
                params.add(new BasicNameValuePair("discounted_fee", discounted_fee_str));
                params.add(new BasicNameValuePair("points_cost", total_points+""));
                params.add(new BasicNameValuePair("venue", seminar_venue_str));
                params.add(new BasicNameValuePair("attendance_code", attendance_code_str));
                params.add(new BasicNameValuePair("about", about_str));

                Log.e("to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(res.getString(R.string.update_seminar_url),
                        "POST", params);

                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
                Toast.makeText(EditSeminar.this, "Update successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditSeminar.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

    }

    public void getSeminar(String id) {

        pDialog = new ProgressDialog(this);
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
                            atten_code = jsonObject.getString("attendance_code");
                            about = jsonObject.getString("about");

                            Log.d("seminar_item", jsonObject.toString());

                            seminar_title.setText(title);
                            seminar_date.setText(date);
                            time_start.setText(start_time);
                            time_end.setText(end_time);
                            bonus_points.setText(bonus);
                            et_seminar_fee.setText(seminar_fee);
                            et_discounted_fee.setText(discounted_fee);
                            seminar_venue.setText(venue);
                            attendance_code.setText(atten_code);
                            et_about.setText(about);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

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