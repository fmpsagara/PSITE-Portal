package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
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
import app.psiteportal.com.psiteportal.NoElection;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.ElectionAdapter;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class ElectionActivity2 extends Fragment{

    RecyclerView recyclerView;
    private List<Nominee> nomineeList = new ArrayList<>();
    private ElectionAdapter adapter;
    private Button btnVote;
    private Nominee n;
    private ProgressDialog progressDialog;
    private TextView timer;
    private static String electionUrl = "http://www.psite7.org/portal/webservices/for_election.php";
    private static final String FORMAT = "%02d:%02d:%02d:%02d";
    private long result;
    private long current_time;
    private SntpClient client;
    private String positions;
    private String pid;
    private int count = 0;
    private String electionDate, electionStartTime, electionEndTime;
    private CheckBox checkBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        checkBox = adapter.bindViewHolder(checkBox.setClickable(false),);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.gp_election1, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        btnVote = (Button) rootView.findViewById(R.id.btn_submit);
        timer = (TextView)rootView.findViewById(R.id.election_timer);

        Bundle bundle = this.getArguments();
        positions = bundle.getString("position", null);
        pid = bundle.getString("user_pid", null);
//        Log.d("here",pid);
        Log.d("here","i reached here");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        new TestAsync().execute();

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = AppController.getInstance().get_has_voted();
                int activated = AppController.getInstance().getActivated();
                if(result==0) {
                    if(activated==1){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Election!");
                    alertDialog.setMessage("Are you sure of your choices?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    vote();
                                    AppController.getInstance().set_has_voted(1);
                                    Toast.makeText(getActivity(),"You have successfully voted!", Toast.LENGTH_SHORT).show();
//                                    recyclerView.getChildAt(1).setEnabled(false);
//                                    btnVote.setClickable(false);
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
//                    btnVote.setClickable(false);
//                    recyclerView.getChildAt(1).setEnabled(false);
                    Toast.makeText(getActivity(), "You have already voted! Wait for further notice for the results.", Toast.LENGTH_LONG).show();

                }

                //it will check first if user has already voted someone

            }
        });

        JsonArrayRequest request = new JsonArrayRequest(electionUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        for(int i = 0; i < jsonArray.length(); i++){
                            try {
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

                                nomineeList.add(nominee);

                            }catch (Exception e){

                            }

                        }
                    adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
        adapter = new ElectionAdapter(getActivity(),nomineeList);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


    private class TestAsync extends AsyncTask<String, String, Void>{
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
            electionDate = AppController.getInstance().getElectionDate();
            electionStartTime = AppController.getInstance().getElectionStartTime();
            electionEndTime = AppController.getInstance().getElectionEndTime();

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
                    current = formatter.parse(test);
                    target = formatter.parse(electionDate + " " + electionEndTime);
                    time_result = target.getTime() - current.getTime();

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
                        timer.setText("Election ends in " + String.format(FORMAT, TimeUnit.MILLISECONDS.toDays(l),
                                TimeUnit.MILLISECONDS.toHours(l) - TimeUnit.DAYS.toHours(
                                        TimeUnit.MILLISECONDS.toDays(l)),
                                TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(l)),
                                TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(l))));

                    }

                    @Override
                    public void onFinish() {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navigation_container, new NoElection());
                        fragmentTransaction.commit();
                    }
                }.start();
            }else{
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, retryFragment());
                fragmentTransaction.commit();
            }
        }
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void insertData(final String username, final String pid)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.psite7.org/portal/webservices/elect.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("response", s.substring(0));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("pid", pid);
                return params;
            }
        };queue.add(sr);
    }

    private void vote(){
        String data = "", username;
        List<Nominee> nom = adapter.getNomineeList();
        List elect = new ArrayList();
        for (int i = 0; i < nom.size(); i++) {
            n = nom.get(i);
            if (n.isSelected() == true) {
                elect.add(n.getEmail());
                count++;
            }
        }
        if(count==AppController.getInstance().getNum_positions_needed()) {
            for (int j = 0; j < elect.size(); j++) {
                Log.d("tae", elect.get(j).toString());
                insertData(elect.get(j).toString(), pid);
            }
        }
        else{
            count=0;
            Toast.makeText(getActivity(), "You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.info:
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees",
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

    Fragment retryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("fragment", "election");
        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setArguments(bundle);

        return retryFragment;
    }
}
