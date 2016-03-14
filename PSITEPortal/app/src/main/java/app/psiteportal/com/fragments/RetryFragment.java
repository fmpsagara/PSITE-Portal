package app.psiteportal.com.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import app.psiteportal.com.psiteportal.R;

/**
 * Created by fmpdroid on 3/14/2016.
 */
public class RetryFragment extends Fragment implements Button.OnClickListener{

    private Button retry;
    private String pid;
    private String fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.retry, container, false);
        Bundle bundle = this.getArguments();
        pid = bundle.getString("user_pid");
        fragment = bundle.getString("fragment");

        retry = (Button) rootView.findViewById(R.id.btnRetry);
        retry.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment frag = null;
        if(fragment.equals("nomination")){
            frag = nominationFragment();
        }else if(fragment.equals("election")){
            frag = electionFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_container, frag);
        fragmentTransaction.commit();
    }

    Fragment nominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        NominationFragment nominationFragment = new NominationFragment();
        nominationFragment.setArguments(bundle);

        return nominationFragment;
    }
    Fragment electionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        ElectionActivity2 electionFragment = new ElectionActivity2();
        electionFragment.setArguments(bundle);

        return electionFragment;
    }
}
