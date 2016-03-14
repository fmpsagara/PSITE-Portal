package app.psiteportal.com.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.adapter.SeminarAdapter;
import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.psiteportal.AddSeminarActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;

public class SeminarsFragment extends Fragment {

    int user_pid;
    private List<Seminar> seminarList = new ArrayList<>();
    private SeminarAdapter adapter;
    private Seminar s;
    private  String seminarsUrl = "http://www.psite7.org/portal/webservices/active_seminars.php";
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    FrameLayout fabLayout;
    String user_usertype;
    FloatingActionButton btnFab;


    public SeminarsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle != null){
            user_pid = bundle.getInt("user_pid");
            user_usertype = bundle.getString("user_usertype");
        }

        View rootView = inflater.inflate(R.layout.fragment_seminars, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view1);
        fabLayout = (FrameLayout) rootView.findViewById(R.id.layoutInner);
        btnFab = (FloatingActionButton) rootView.findViewById(R.id.btnFloatingAction);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if(user_usertype.equals("officer_member")){
            setupUI(rootView);
        }else{
            fabLayout.setVisibility(View.GONE);
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest request = new JsonArrayRequest(seminarsUrl,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        for(int i=0; i<jsonArray.length(); i++){
                            try{
                                String id,seminarName, bannerUrl;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Seminar seminar;
                                id = jsonObject.getString("sid");
                                seminarName = jsonObject.getString("seminar_title");
                                bannerUrl = jsonObject.getString("seminar_banner");

                                seminar = new Seminar(id, seminarName, bannerUrl);
                                seminarList.add(seminar);

                                Log.d("Active seminar", jsonArray.toString());
                                Log.i("sid", id);
                                Log.i("seminar name", seminarName);
                                Log.i("bannerUrl", bannerUrl);

                            }catch (Exception e){

                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Activity activity = getActivity();
                if(activity != null && isAdded())
                    hidePDialog();
                if (volleyError instanceof NoConnectionError) {
                    String errormsg = "Check your internet connection";
                    Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request);
        adapter = new SeminarAdapter(getActivity(),seminarList);
        adapter.getUserId(user_pid);
        adapter.getUserType(user_usertype);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void setupUI(View rootView) {
        btnFab = (FloatingActionButton) rootView.findViewById(R.id.btnFloatingAction);
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Hello FAB!", Toast.LENGTH_SHORT).show();
                // TODO issue: Rotate animation in pre-lollipop works only once, issue to be resolved!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(500);
                    rotateAnimation.setFillAfter(true);
                    rotateAnimation.setInterpolator(new FastOutSlowInInterpolator());
                    btnFab.startAnimation(rotateAnimation);
                } else {
                    btnFab.clearAnimation();
                    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(btnFab);
                    animatorCompat.setDuration(500);
                    animatorCompat.setInterpolator(new FastOutSlowInInterpolator());
                    animatorCompat.rotation(180);
                    animatorCompat.start();
                }

                Intent i = new Intent(getActivity(),AddSeminarActivity.class);
                startActivity(i);
            }
        });
    }

}
