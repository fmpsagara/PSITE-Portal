package app.psiteportal.com.psiteportal;

import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.fragments.CaptureImageFragment;
import app.psiteportal.com.fragments.CountdownFragment;
import app.psiteportal.com.fragments.ElectionActivity2;
import app.psiteportal.com.fragments.NominationFragment;
import app.psiteportal.com.fragments.SeminarsFragment;
import app.psiteportal.com.fragments.SeminarsFragmentOfficer;
import app.psiteportal.com.fragments.UserProfileFragment;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.CircleImageView;
import app.psiteportal.com.utils.SntpClient;

public class OldMainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = OldMainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    public static ArrayList<String> user_type = new ArrayList<String>();

    private static String url = "http://www.psite7.org/portal/webservices/election_check.php";
    private Map<String, String> params = new HashMap<String, String>();
    boolean result;
    private String election_needed;


    String user_pid;
    String user_fname;
    String user_lname;
    String user_gender;
    String user_contact;
    String user_email;
    String user_address;
    String user_institution;
    int user_points;
    String user_qr;

    String user_usertype;

    int has_voted;
    int has_nominated;
    int value;
    Bundle extras;

    private CircleImageView imageView;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Date current, target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyAsyncTask().execute();
        getData();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        imageView = (CircleImageView) findViewById(R.id.user_prof_pic);
        extras = getIntent().getExtras();
        if (extras != null) {
            user_pid = extras.getString("pid");
            user_fname = extras.getString("firstname");
            user_lname = extras.getString("lastname");
            user_gender = extras.getString("gender");
            user_contact = extras.getString("contact");
            user_email = extras.getString("email");
            user_address = extras.getString("address");
            user_institution = extras.getString("institution");
            user_points = extras.getInt("points");
            user_usertype = extras.getString("usertype");
            user_type.add(user_usertype);
            has_nominated = Integer.parseInt(extras.getString("has_nominated"));
            has_voted = Integer.parseInt(extras.getString("has_voted"));


        }
        AppController.getInstance().set_has_nominated(has_nominated);
        AppController.getInstance().set_has_voted(has_voted);
        Log.d("test for singleton", String.valueOf(AppController.getInstance().get_has_nominated()));


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0, user_usertype);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position, user_usertype);
    }

    private void displayView(int position, String user_user_type) {
        Fragment fragment = null;


        String temp_usertype = user_user_type;

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = userDetails();
                title = getString(R.string.title_home);
                break;
            case 1:
                if (user_usertype.equals("member")) {
                    fragment = seminarsFragment();
                    title = "Seminars";
                } else {
                    fragment = seminarsFragmentOfficer();
                    title = "Seminars";
                }
                break;
            case 2:
                fragment = new CaptureImageFragment();
                title = "Photobooth";

                break;
            case 3:
                int s = AppController.getInstance().get_has_nominated();
                Log.d("has_nominated_test", String.valueOf(s));
                boolean check = compareDate(current, target);
                if(check){
                    fragment = countdownFragment();
                }else{
                    fragment = electionFragment();
                }


//                title = "Nomination";
//                Intent intent = new Intent(getApplicationContext(), ElectionActivity2.class);
//                this.startActivity(intent);
//
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private Fragment userDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("user_fname", user_fname);
        bundle.putString("user_lname", user_lname);
        bundle.putString("user_institution", user_institution);
        bundle.putString("user_address", user_address);
        bundle.putString("user_email", user_email);
        bundle.putInt("user_points", user_points);
        bundle.putString("user_qr", user_qr);
        bundle.putString("user_usertype", user_usertype);

        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);

        return userProfileFragment;
    }

    Fragment seminarsFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);

        SeminarsFragment seminarsFragment = new SeminarsFragment();
        seminarsFragment.setArguments(bundle);

        return seminarsFragment;
    }

    Fragment seminarsFragmentOfficer() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putInt("user_points", user_points);
        SeminarsFragmentOfficer seminarsFragmentOfficer = new SeminarsFragmentOfficer();
        seminarsFragmentOfficer.setArguments(bundle);

        return seminarsFragmentOfficer;
    }

    Fragment electionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        ElectionActivity2 electionFragment = new ElectionActivity2();
        electionFragment.setArguments(bundle);

        return electionFragment;
    }

    Fragment nominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        NominationFragment nominationFragment = new NominationFragment();
        nominationFragment.setArguments(bundle);

        return nominationFragment;
    }
    Fragment countdownFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        CountdownFragment countdownFragment = new CountdownFragment();
        countdownFragment.setArguments(bundle);

        return countdownFragment;
    }




    //class to get the current time from the internet
    class MyAsyncTask extends AsyncTask<String, String, Date> {
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
        }
    }

    //method to retrieve data from web server
    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String e_date, e_start, e_end, num_needed, prof_pic;
                int has_voted;
                SimpleDateFormat formatter;
                    try {
                        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        JSONObject object = new JSONObject(s.toString());
                        Log.wtf("testing", object.getString("prof_pic"));
                        e_date = object.getString("election_date");
                        e_start = object.getString("start_time")+":00";
                        e_end = object.getString("end_time")+":00";
                        num_needed = object.getString("num_needed");
                        has_voted = Integer.parseInt(object.getString("has_voted"));
                        prof_pic = object.getString("prof_pic");

                        //set profile image on the navigation drawer
                        imageView.setImageUrl(prof_pic, imageLoader);

                        //formats the strings into an object Date
                        target = formatter.parse(e_date + " " + e_start);

                        //stores the other data retrieved to the global class
                        AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
                        AppController.getInstance().setElectionDate(e_date);
                        AppController.getInstance().setElectionStartTime(e_start);
                        AppController.getInstance().setElectionEndTime(e_end);

                    } catch (Exception e) {
                        Log.wtf("testing", e.getMessage());
                    }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", user_pid);
                return params;
            }
        };queue.add(sr);
    }

    //method to compare the current data to the election start date
    private boolean compareDate(Date current, Date target){
        if(!current.after(target)||current.equals(target)){
            Log.wtf("COUNTDOWN","we're still counting down");
            return true;
        }
        else{
            Log.wtf("ELECTION", "let's go to election now");
            return false;
        }
    }
}