package app.psiteportal.com.psiteportal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.adapter.TransactionAdapter;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.NominationAdapter;


public class LiquidationActivity extends AppCompatActivity {

    private FloatingActionButton btnFab;
    TextView total_collection;
    TextView total_expenses;
    TextView balance;
    int collection, expenses;
    int pid;
    String sid;
    ListView list;
    private List<Transaction> transactionList = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String user_pid, seminar_id;
    private String transactionsUrl = "http://www.psite7.org/portal/webservices/transactions.php";
    private TransactionAdapter transactionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user_pid = bundle.getString("pid");
            seminar_id = bundle.getString("sid");
        }
        Log.wtf("liquidation activity", user_pid + " / " + seminar_id);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        total_collection = (TextView) findViewById(R.id.total_collection);
        total_expenses = (TextView) findViewById(R.id.total_expenses);
        balance = (TextView) findViewById(R.id.total_balance);
        btnFab = (FloatingActionButton) findViewById(R.id.btnFloatingAction);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, transactionsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Transaction transaction;
                                id = jsonObject.getString("transaction_id");
                                sid = jsonObject.getString("sid");
                                pid = jsonObject.getString("pid");
                                item_name = jsonObject.getString("item_name");
                                transaction_amount = jsonObject.getString("transaction_amount");
                                check_number = jsonObject.getString("check_number");
                                transaction_photo = jsonObject.getString("transaction_photo");
                                transaction_type = jsonObject.getString("transaction_type");
                                transaction_date = jsonObject.getString("transaction_date");

                                transaction = new Transaction(id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date);
                                transactionList.add(transaction);

                                if (transaction_type.equals("Collection")) {
                                    collection = collection + Integer.parseInt(transaction_amount);
                                } else {
                                    expenses = expenses + Integer.parseInt(transaction_amount);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        total_collection.setText("₱ " + collection + "");
                        total_expenses.setText("₱ " + expenses + "");
                        balance.setText("₱ " + (collection - expenses) + "");

                        transactionAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", seminar_id);
                return params;
            }
        };
        queue.add(sr);

        transactionAdapter = new TransactionAdapter(this,transactionList);
        recyclerView.setAdapter(transactionAdapter);


//        JsonArrayRequest request = new JsonArrayRequest(transactionsUrl,
//                new Response.Listener<JSONArray>(){
//                    @Override
//                    public void onResponse(JSONArray jsonArray) {
//                        hidePDialog();
//                        for(int i=0; i<jsonArray.length(); i++){
//                            try{
//                                String id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date;
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                Transaction transaction;
//                                id = jsonObject.getString("transaction_id");
//                                sid = jsonObject.getString("sid");
//                                pid = jsonObject.getString("pid");
//                                item_name = jsonObject.getString("item_name");
//                                transaction_amount = jsonObject.getString("transaction_amount");
//                                check_number = jsonObject.getString("check_number");
//                                transaction_photo = jsonObject.getString("transaction_photo");
//                                transaction_type = jsonObject.getString("transaction_type");
//                                transaction_date = jsonObject.getString("transaction_date");
//
//                                transaction = new Transaction(id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date);
//                                transactionList.add(transaction);
//
//                                if(transaction_type.equals("Collection")){
//                                    collection = collection + Integer.parseInt(transaction_amount);
//                                }else{
//                                    expenses = expenses + Integer.parseInt(transaction_amount);
//                                }
//
//                            }catch (Exception e){
//
//                            }
//                        }
//
//                        total_collection.setText("₱ "+collection+"");
//                        total_expenses.setText("₱ "+expenses + "");
//                        balance.setText("₱ "+(collection - expenses)+"");
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Activity activity = LiquidationActivity.this;
//                if (volleyError instanceof NoConnectionError) {
//                    String errormsg = "Check your internet connection";
//                    Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(request);
//        adapter = new TransactionAdapter(this, transactionList);
//        recyclerView.setAdapter(adapter);

        setupUI();

    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void setupUI() {
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(LiquidationActivity.this, AddExpenseActivity.class);
                i.putExtra("sid", seminar_id);
                i.putExtra("pid", user_pid);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
