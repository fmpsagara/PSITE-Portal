package app.psiteportal.com.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.psiteportal.com.adapter.TransactionAdapter;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.psiteportal.AddExpenseActivity;
import app.psiteportal.com.psiteportal.LiquidationActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;

public class TransactionFragment extends Fragment {

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
    private TransactionAdapter adapter;


    public TransactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_liquidation, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            user_pid = bundle.getString("pid");
            seminar_id = bundle.getString("sid");
        }
        Log.wtf("liquidation activity", user_pid + " / " + seminar_id);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        total_collection = (TextView) rootView.findViewById(R.id.total_collection);
        total_expenses = (TextView) rootView.findViewById(R.id.total_expenses);
        balance = (TextView) rootView.findViewById(R.id.total_balance);
        btnFab = (FloatingActionButton) rootView.findViewById(R.id.btnFloatingAction);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        JsonArrayRequest request = new JsonArrayRequest(transactionsUrl,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        for(int i=0; i<jsonArray.length(); i++){
                            try{
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

                                if(transaction_type.equals("Collection")){
                                    collection = collection + Integer.parseInt(transaction_amount);
                                }else{
                                    expenses = expenses + Integer.parseInt(transaction_amount);
                                }

                            }catch (Exception e){

                            }
                        }

                        total_collection.setText("₱ "+collection+"");
                        total_expenses.setText("₱ "+expenses + "");
                        balance.setText("₱ "+(collection - expenses)+"");

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Activity activity = getActivity();
                if (volleyError instanceof NoConnectionError) {
                    String errormsg = "Check your internet connection";
                    Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request);
        adapter = new TransactionAdapter(getActivity(), transactionList);
        recyclerView.setAdapter(adapter);

        setupUI();


        return rootView;
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

                Intent i = new Intent(getActivity(), AddExpenseActivity.class);
                i.putExtra("sid", seminar_id);
                i.putExtra("pid", user_pid);
                startActivity(i);
            }
        });

    }

}
