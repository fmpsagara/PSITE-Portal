package app.psiteportal.com.psiteportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.CircleImageView;

/**
 * Created by fmpdroid on 2/23/2016.
 */
public class UserProfileActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private CircleImageView imageView;
    private TextView txtName;
    private TextView txtInstitution;
    private TextView txtContact;
    private TextView txtEmail;
    private TextView txtAddress;
    private TextView txtPoints;
    private Bitmap bitmap;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private String user_info_url = "http://www.psite7.org/portal/webservices/get_user.php";
    private String user_pid;
    private String username;

    private RecyclerView recyclerView;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);


        bindActivity();
        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        Intent intent = getIntent();
        user_pid = intent.getStringExtra("user_pid");
        Log.wtf("HERYE", user_pid);
        getData();

//        byte[] bytes = intent.getByteArrayExtra("prof_pic");
//        bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        imageUrl = intent.getStringExtra("prof_pic");
//        name = intent.getStringExtra("name");
//        institution = intent.getStringExtra("institution");
//        contact = intent.getStringExtra("contact");
//        email = intent.getStringExtra("email");
//        address = intent.getStringExtra("address");


        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);

        txtName = (TextView) findViewById(R.id.profile_name);
        imageView = (CircleImageView) findViewById(R.id.avatar_pic);
        txtInstitution = (TextView)findViewById(R.id.info_institution);
        txtContact = (TextView)findViewById(R.id.info_contact);
        txtEmail = (TextView)findViewById(R.id.info_email);
        txtAddress = (TextView)findViewById(R.id.info_location);
        txtPoints = (TextView) findViewById(R.id.info_points);
//        recyclerView = (RecyclerView) findViewById(R.id.sem_attended_recyclerview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_image:
                Log.wtf("username test", username);
                Intent intent = new Intent(getApplicationContext(), UserProfileChangePicture.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, user_info_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String e_username, e_name, e_email, e_institution, e_contact, e_address, e_points, prof_pic;
                int has_voted;
                SimpleDateFormat formatter;
                try {
                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    JSONObject object = new JSONObject(s.toString());
                    Log.wtf("testing", object.getString("prof_pic"));
                    username = object.getString("username");
                    e_name = object.getString("firstname") + " " + object.getString("lastname");
                    e_email = object.getString("email");
                    e_institution = object.getString("institution");
                    e_contact = object.getString("contact");
                    e_address = object.getString("address");
                    e_points = object.getString("points");
                    prof_pic = object.getString("prof_pic");

                    //set profile image on the navigation drawer
                    Log.wtf("tae", e_name);
                    txtName.setText(e_name);
                    mTitle.setText(e_name);
                    imageView.setImageUrl(prof_pic, imageLoader);
                    txtInstitution.setText(e_institution);
                    txtContact.setText(e_contact);
                    txtEmail.setText(e_email);
                    txtAddress.setText(e_address);
                    txtPoints.setText(e_points);

                } catch (Exception e) {
                    Log.wtf("testing", e.getMessage());
                    Log.wtf("HEREEE", s.substring(0));
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
                params.put("pid", user_pid);
                return params;
            }
        };queue.add(sr);
    }

//    private void getSeminars() {
//
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest sr = new StringRequest(Request.Method.POST, seminar_list_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                String seminars[] = null;
//                try {
//                    JSONArray jsonArray = new JSONArray(s.toString());
//                    for(int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        seminars[i] = jsonObject.getString("seminar_title");
//                    }
//                } catch (Exception e) {
//                    Log.wtf("testing", e.getMessage());
//                    Log.wtf("HEREEE", s.substring(0));
//                }
//                adapter = new SimpleAdapter(getApplicationContext(),seminars,android.R.layout.simple_list_item_1);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("pid", user_pid);
//                return params;
//            }
//        };queue.add(sr);
//    }
}
