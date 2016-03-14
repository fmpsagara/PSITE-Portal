package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/21/2016.
 */
public class CountdownFragment extends Fragment {

    String date, start_time, user_type, user_id, positions;
    TextView days, hours, minutes, seconds;
    Button btnView;
    ProgressDialog progressDialog;
    int indicator = AppController.getInstance().getIndicator();
    Fragment mFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.countdown_layout, container, false);
        days = (TextView) rootView.findViewById(R.id.txtTimerDay);
        hours = (TextView) rootView.findViewById(R.id.txtTimerHour);
        minutes = (TextView) rootView.findViewById(R.id.txtTimerMinute);
        seconds = (TextView) rootView.findViewById(R.id.txtTimerSecond);
        btnView = (Button) rootView.findViewById(R.id.view_nominees);

        Bundle bundle = getArguments();
        user_id = bundle.getString("user_pid");
        user_type = bundle.getString("usertype");
        positions = bundle.getString("position");
        if(indicator==1) {
            date = AppController.getInstance().getNominationDate();
            start_time = AppController.getInstance().getNominationStartTime();
            mFragment = nominationFragment();
        }else if(indicator==2) {
            date = AppController.getInstance().getElectionDate();
            start_time = AppController.getInstance().getElectionStartTime();
            mFragment = electionFragment();
        }
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new MyAsyncTask().execute();

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewNominees();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private class MyAsyncTask extends AsyncTask<String, Void, Long>{
        SntpClient client;
        long result;

        @Override
        protected Long doInBackground(String... strings) {
            client = new SntpClient();
            if (client.requestTime("0.pool.ntp.org", 30000)) {
                long current_time = client.getNtpTime();
                Date current_date = new Date(current_time);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String formatCurrentDate = formatter.format(current_date);
                try{
                    Date now = formatter.parse(formatCurrentDate);
                    Date start = formatter.parse(date + " " + start_time);
                    result = start.getTime() - now.getTime();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.wtf("error on countdown", e.getMessage());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Long s) {
            super.onPostExecute(s);
            hidePDialog();
            new CountDownTimer(s, 1000){

                @Override
                public void onTick(long l) {
                    days.setText(String.format("%02d", TimeUnit.MILLISECONDS.toDays(l)));
                    hours.setText(String.format("%02d", TimeUnit.MILLISECONDS.toHours(l)- TimeUnit.DAYS.toHours(
                            TimeUnit.MILLISECONDS.toDays(l))));
                    minutes.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(l))));
                    seconds.setText(String.format("%02d",TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(l))));
                }

                @Override
                public void onFinish() {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.navigation_container, mFragment);
                    fragmentTransaction.commit();
                }
            }.start();
        }
    }
    Fragment nominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_id);
        bundle.putString("usertype", user_type);
        bundle.putString("position", positions);
        NominationFragment nominationFragment = new NominationFragment();
        nominationFragment.setArguments(bundle);

        return nominationFragment;
    }

    Fragment electionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_id);
        bundle.putString("usertype", user_type);
        bundle.putString("position", positions);
        ElectionActivity2 electionFragment = new ElectionActivity2();
        electionFragment.setArguments(bundle);

        return electionFragment;
    }

    private void viewNominees(){
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_id);
        bundle.putString("usertype", user_type);
        bundle.putString("position", positions);
        ViewNomineesFragment viewNomineeFragment = new ViewNomineesFragment();
        viewNomineeFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_container, viewNomineeFragment);
        fragmentTransaction.commit();
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
