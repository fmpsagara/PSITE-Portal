package app.psiteportal.com.fragments;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.CustomListAdapter;

/**
 * Created by fmpdroid on 1/25/2016.
 */
public class ElectionFragmentV2 extends ListFragment {
    private CustomListAdapter adapter;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
        Log.d("clicked",String.valueOf(position));
    }

    private List<Nominee> nomineeList;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.gp_election, container, false);
        //listView = (ListView) rootView.findViewById(R.id.listView);
        Log.d("here","i reached here");

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "hello", Toast.LENGTH_LONG).show();
            }
        });
        new ElectionTask().execute();
        return rootView;
    }


    private class ElectionTask extends AsyncTask<String, Void, String>{
        String electionUrl = "http://www.psite7.org/portal/webservices/nomineesv2.php";
        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        @Override
        protected String doInBackground(String... params) {
            String jsonResult;
            Nominee n;
            nomineeList = new ArrayList<>();
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
//            JSONObject json = jsonParser.makeHttpRequest(
//                    electionUrl, "POST", params1);
            Log.d("here", "i am here");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(electionUrl);
                HttpResponse response = client.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                jsonResult = EntityUtils.toString(httpEntity);
                JSONArray result = new JSONArray(jsonResult);
                Log.d("here", String.valueOf(result.length()));


                for(int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    Log.d("fuck you", String.valueOf(i));
                    String name, first_name, last_name, institution, contact, email, address;
                    Bitmap bitmap = null;
                    URL url = new URL(jsonObject.getString("prof_pic"));
                    first_name = jsonObject.getString("firstname");
                    last_name = jsonObject.getString("lastname");
                    name = first_name + " " + last_name;
                    institution = jsonObject.getString("institution_name");
                    contact = jsonObject.getString("contact");
                    email = jsonObject.getString("email");
                    address = jsonObject.getString("address");
                    Log.d("here",url + name + institution);

                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();

                        bitmap = BitmapFactory.decodeStream(inputStream);//Convert to bitmap
                        //bitmap.createScaledBitmap(bitmap, 500, 500, true);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
//                    n = new Nominee(bitmap, name, institution,contact, email, address);
//                    nomineeList.add(n);
                }

//                for(int i = 0; i < result.length(); i++) {
//                    Log.d("here", String.valueOf(i));
//                    Log.d("here", "i am here as well "+ String.valueOf(result.length()));
//                    String url, name, institution;
//                    JSONObject jsonObject = result.getJSONObject(i);
//                    //JSONArray jsonArray = result.getJSONArray(i);
//                    url = jsonObject.getString("prof_pic");
//                    name = jsonObject.getString("first_name");
//                    institution = jsonObject.getString("institution");
//                    Log.d("here",url + name + institution);
//                    Nominee n = new Nominee(url, name, institution);
//                    nomineeList.add(n);
//                }
            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for(int i = 0; i < 2; i++) {
                Log.d("fuck you more", String.valueOf(i));
            }
            adapter = new CustomListAdapter(getActivity(), nomineeList);
            setListAdapter(adapter);
        }
    }

}
