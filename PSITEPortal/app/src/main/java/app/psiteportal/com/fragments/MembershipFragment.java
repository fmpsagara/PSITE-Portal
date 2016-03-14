package app.psiteportal.com.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.psiteportal.R;

/**
 * Created by Lawrence on 3/2/2016.
 */
public class MembershipFragment extends Fragment {

    ProgressDialog pDialog;
    EditText search_et;
    Button activate_btn;
    String searched;
    int success;
    String message;
    RelativeLayout alert_layout;
    LinearLayout search_layout;

    public MembershipFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_membership, container, false);

        Bundle extras = getArguments();
        String usertype = extras.getString("usertype");

        alert_layout = (RelativeLayout) rootView.findViewById(R.id.alert);
        search_layout = (LinearLayout) rootView.findViewById(R.id.membership_dashboard);
        search_et = (EditText) rootView.findViewById(R.id.search_et);
        activate_btn = (Button) rootView.findViewById(R.id.activated_btn);


        if(usertype.equals("member_individual") || usertype.equals("member_institutional")){
            //nothing
        }else{
            alert_layout.setVisibility(View.GONE);
            search_layout.setVisibility(View.VISIBLE);
        }


        activate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searched = search_et.getText().toString();
                activateUser();
            }
        });

        return rootView;
    }

    public void activateUser() {
        String url = "http://www.psite7.org/portal/webservices/activate_user.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", searched);
        Log.wtf("username passed", params.toString());

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try {
                            success = jsonObject.getInt("success");
                            message = jsonObject.getString("message");

                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            search_et.setText("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activity activity = getActivity();
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

}


