package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.ElectionDone;
import app.psiteportal.com.psiteportal.NoElection;
import app.psiteportal.com.psiteportal.NoNomination;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.ElectionAdapter;
import app.psiteportal.com.utils.NominationAdapter;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/14/2016.
 */
public class NominationFragment extends Fragment {

    RecyclerView recyclerView;
    private List<Nominee> nomineeList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private NominationAdapter adapter;
    private static String electionUrl = "http://www.psite7.org/portal/webservices/nominee_check.php";
    private Button btnVote;
    private Nominee n;
    private int count = 0;
    private String positions;
    private String pid;
    private TextView timer;
    private SntpClient client;
    private long result;
    private long current_time;
    private String nominationDate, nominationStartTime, nominationEndTime;
    private static final String FORMAT = "%02d:%02d:%02d:%02d";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gp_nomination, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.nomination_recyclerView);
        btnVote = (Button) rootView.findViewById(R.id.nomination_btn_submit);
        timer = (TextView) rootView.findViewById(R.id.nomination_timer);

        Bundle bundle = this.getArguments();
        pid = bundle.getString("user_pid");
        Log.wtf("PID HERE", pid);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        new TestAsync().execute();

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = AppController.getInstance().get_has_nominated();
                int activated = AppController.getInstance().getActivated();
                if(result==0) {
                    if(activated==1){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Nomination");
                    alertDialog.setMessage("Are you sure of your choices?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    vote();
                                    AppController.getInstance().set_has_nominated(1);
                                    Toast.makeText(getActivity(),"You have successfully nominated!", Toast.LENGTH_SHORT).show();
                                    btnVote.setClickable(false);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();}
                    else{
                        Toast.makeText(getActivity(),"Your account is not yet activated.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "You have already nominated! Wait for further notice for the results.", Toast.LENGTH_LONG).show();
                }
            }
        });
        getData();
        adapter = new NominationAdapter(getActivity(),nomineeList);
        recyclerView.setAdapter(adapter);


        return rootView;
    }

    private void vote(){
        String data = "", username;
        List<Nominee> nom = adapter.getNomineeList();
        List nominate = new ArrayList();
        for (int i = 0; i < nom.size(); i++) {
            n = nom.get(i);
            if (n.isSelected() == true) {
//                insertData(n.getEmail(), pid);
                nominate.add(n.getEmail());
                count++;
            }
        }
        if(count==AppController.getInstance().getNum_positions_needed()) {
            for (int j = 0; j < nominate.size(); j++) {
                insertData(nominate.get(j).toString(), pid);
            }
        }
        else{
            count=0;
            Toast.makeText(getActivity(), "You need to nominate "+AppController.getInstance().getNum_positions_needed()+" person/s", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertData(final String username, final String pid)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.psite7.org/portal/webservices/nominate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("utot", s.substring(0));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                Log.wtf("PID HERE", pid);
                params.put("username", username);
                params.put("pid", pid);
                return params;
            }
        };queue.add(sr);
    }

    private class TestAsync extends AsyncTask<String, String, Void> {
        long time_result;
        Date current,target;
        boolean check;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            // Showing progress dialog before making http request
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            //Sntp Experiment starts here
            client = new SntpClient();
            Long result_time;
            nominationDate = AppController.getInstance().getNominationDate();
            nominationStartTime = AppController.getInstance().getNominationStartTime();
            nominationEndTime = AppController.getInstance().getNominationEndTime();

            if (client.requestTime("0.pool.ntp.org", 30000)){
                check = true;
                current_time = client.getNtpTime();
                Date current_date = new Date(current_time);

                //test
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String test = formatter.format(current_date);
                String formattedTime = sdfTime.format(current_date);
                String formattedDate = sdfDate.format(current_date);

                long curMillis = current_date.getTime();

                try {
                    Log.wtf("BASA DIRI", nominationDate + " " + nominationEndTime);
                    current = formatter.parse(test);
                    target = formatter.parse(nominationDate + " " + nominationEndTime);
                    time_result = target.getTime() - current.getTime();
                    Log.wtf("ANIMAL", String.valueOf(time_result));
                    Date testTime = sdfTime.parse(formattedTime);
                    Date testEndTime = sdfTime.parse("23:59:00");
                    long time = testTime.getTime();
                    long end_time = testEndTime.getTime();
                    //time_result = testEndTime.getTime() - testTime.getTime();

                    long diffSeconds = time_result / 1000 % 60;
                    long diffMinutes = time_result / (60 * 1000) % 60;
                    long diffHours = time_result / (60 * 60 * 1000) % 24;
                    long diffDays = time_result / (24 * 60 * 60 * 1000);

                    Date result_date = new Date(time_result);
                    String formattedTime1 = sdfTime.format(result_date);
                    Log.d("result here", String.valueOf(diffHours)+":" +String.valueOf(diffMinutes) + " " + String.valueOf(diffSeconds)+":" +String.valueOf(diffDays));
                    Log.d("result here", testTime.toString());
                    Log.d("result here", String.valueOf(time_result));
                    Log.d("result here", testEndTime.toString());
                    Log.d("result here", result_date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Log.d("time here", String.valueOf(current_time));
                Log.d("date", test);
                Log.d("date", formattedDate);
                Log.d("time", formattedTime);


            }else{
                Log.d("time here", "failed");
                check = false;

//
//                Fragment frg = null;
//                frg = nominationFragment();
//                final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.detach(frg);
//                ft.attach(frg);
//                ft.commit();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            result= time_result;
            hidePDialog();
            if(check) {
                new CountDownTimer(result, 1000) {
                    @Override
                    public void onTick(long l) {
                        timer.setText("Nomination ends in " + String.format(FORMAT, TimeUnit.MILLISECONDS.toDays(l),
                                TimeUnit.MILLISECONDS.toHours(l) - TimeUnit.DAYS.toHours(
                                        TimeUnit.MILLISECONDS.toDays(l)),
                                TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(l)),
                                TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(l))));

//                    Log.d("tag", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(l)));
//                    Log.d("tag1", String.valueOf(TimeUnit.HOURS.toMinutes(l)));
//                    Log.d("tag2", String.valueOf(TimeUnit.MILLISECONDS.toHours(l)));
//                    Log.d("tag3", String.valueOf(TimeUnit.HOURS.toMinutes(
//                            TimeUnit.MILLISECONDS.toHours(l))));
//                    Log.d("tag4", String.valueOf(TimeUnit.DAYS.toHours(
//                            TimeUnit.MILLISECONDS.toDays(l))));
                    }

                    @Override
                    public void onFinish() {
//                    timer.setText("finished!");
                        AppController.getInstance().setIndicator(2);
//                    startActivity(new Intent(getActivity(), NoNomination.class));
                        Log.wtf("Nomination is done", "it's done");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navigation_container, new NoNomination());
                        fragmentTransaction.commit();
                    }
                }.start();
            }else {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, retryFragment());
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "You need to nominate "+AppController.getInstance().getNum_positions_needed()+" person/s",
                        Snackbar.LENGTH_LONG).show();
//                Toast.makeText(getActivity(),"You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees", Toast.LENGTH_SHORT).show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.election_menu, menu);
    }

    public void getData(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, electionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for(int i = 0; i < jsonArray.length(); i++) {
                                Log.wtf("glendon", "diri");
                                String imageUrl, name, first_name, last_name, institution, contact, email, address;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Nominee nominee;
                                imageUrl = jsonObject.getString("prof_pic");
                                first_name = jsonObject.getString("firstname");
                                last_name = jsonObject.getString("lastname");
                                institution = jsonObject.getString("institution_name");
                                contact = jsonObject.getString("contact");
                                email = jsonObject.getString("email");
                                address = jsonObject.getString("address");
                                name = first_name + " " + last_name;
                                nominee = new Nominee(imageUrl, name, institution, contact, email, address);
                                Log.wtf("glendon", name);
                                nomineeList.add(nominee);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }


                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("sid", String.valueOf(AppController.getInstance().getNomination_sid()));
                return params;
            }
        };queue.add(sr);
    }
    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    Fragment retryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("fragment", "nomination");
        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setArguments(bundle);

        return retryFragment;
    }


}
