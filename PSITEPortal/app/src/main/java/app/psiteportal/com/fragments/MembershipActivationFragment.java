package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.MembershipActivationAdapter;

/**
 * Created by fmpdroid on 3/11/2016.
 */
public class MembershipActivationFragment extends Fragment implements SearchView.OnQueryTextListener{

    private static String url = "http://www.psite7.org/portal/webservices/get_members.php";
    private List<Member> memberList;
    private RecyclerView recyclerView;
    private MembershipActivationAdapter adapter;
    private String pid;
    private ProgressDialog progressDialog;

    private static final String[] MOVIES = new String[]{
            "The Woman in Black: Angel of Death",
            "20 Once Again",
            "Taken 3",
            "Tevar",
            "I",
            "Blackhat",
            "Spare Parts",
            "The Wedding Ringer",
            "Ex Machina",
            "Mortdecai",
            "Strange Magic",
            "The Boy Next Door",
            "The SpongeBob Movie: Sponge Out of Water",
            "Kingsman: The Secret Service",
            "Boonie Bears: Mystical Winter",
            "Project Almanac",
            "Running Man",
            "Wild Card",
            "It Follows",
            "C'est si bon",
            "Yennai Arindhaal",
            "Shaun the Sheep Movie",
            "Jupiter Ascending",
            "Old Fashioned",
            "Somewhere Only We Know",
            "Fifty Shades of Grey",
            "Dragon Blade",
            "Zhong Kui: Snow Girl and the Dark Crystal",
            "Badlapur",
            "Hot Tub Time Machine 2",
            "McFarland, USA",
            "The Duff",
            "The Second Best Exotic Marigold Hotel",
            "A la mala",
            "Focus",
            "The Lazarus Effect",
            "Chappie",
            "Faults",
            "Road Hard",
            "Unfinished Business",
            "Cinderella",
            "NH10",
            "Run All Night",
            "X+Y",
            "Furious 7",
            "Danny Collins",
            "Do You Believe?",
            "Jalaibee",
            "The Divergent Series: Insurgent",
            "The Gunman",
            "Get Hard",
            "Home"
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_activation, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.members_recycler_view);

        setHasOptionsMenu(true);
        memberList = new ArrayList<>();

        Bundle bundle = this.getArguments();
        pid = bundle.getString("user_pid");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        populate();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        for(String movie : MOVIES){
//            memberList.add(new Member(movie));
//        }
//
//        adapter = new MembershipActivationAdapter(getActivity(), memberList);
//        recyclerView.setAdapter(adapter);
    }

    public void populate(){
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        String  name, email, status;
                        Member member;
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                name = object.getString("firstname") + " " + object.getString("lastname");
                                email = object.getString("email");
                                if(Integer.parseInt(object.getString("activated"))==1){
                                    status = "Active";
                                }else{
                                    status = "Not Active";
                                }
                                member = new Member(name, email, status);

                                memberList.add(member);
                            } catch (Exception e) {

                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(request);
        adapter = new MembershipActivationAdapter(getActivity(), memberList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Member> filteredMemberList = filter(memberList, query);
        adapter.animateTo(filteredMemberList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Member> filter(List<Member> members, String query) {
        query = query.toLowerCase();

        final List<Member> filteredModelList = new ArrayList<>();
        for (Member member : members) {
            final String text = member.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(member);
            }
        }
        return filteredModelList;
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
