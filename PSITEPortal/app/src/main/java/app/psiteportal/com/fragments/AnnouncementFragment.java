package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.psiteportal.com.model.Announcement;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AnnouncementAdapter;
import app.psiteportal.com.utils.AppController;

/**
 * Created by fmpdroid on 2/29/2016.
 */
public class AnnouncementFragment extends Fragment{
    RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementLIst = new ArrayList<>();
    private Announcement a;
    private static String announcementUrl = "http://www.psite7.org/portal/webservices/get_announcements.php";
    private ProgressDialog progressDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.announcement, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.announcement_recycler_view);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

    progressDialog = new ProgressDialog(getActivity());
    // Showing progress dialog before making http request
    progressDialog.setMessage("Loading...");
    progressDialog.show();

    JsonArrayRequest request = new JsonArrayRequest(announcementUrl,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    hidePDialog();
                    for(int i = 0; i < jsonArray.length(); i++){
                        try {
                            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String pid, title, details, venue, name, date, time, formattedTime, formattedDate;
                            Date formatDate;
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Announcement announcement;
                            pid = jsonObject.getString("pid");
                            title = jsonObject.getString("announcement_title");
                            details = jsonObject.getString("announcement_details");
                            date = jsonObject.getString("date").replace("-", "/");
                            formatDate = formatter.parse(date);
                            formattedDate = dateFormatter.format(formatDate);
                            formattedTime = timeFormatter.format(formatDate);
                            venue = jsonObject.getString("announcement_venue");
                            name = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");
                            announcement = new Announcement(pid, title, details, formattedDate, formattedTime, venue, name);

                            announcementLIst.add(announcement);

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.w("wewewe", e.getMessage());
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
    adapter = new AnnouncementAdapter(getActivity(),announcementLIst);
    recyclerView.setAdapter(adapter);


        return rootView;
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
