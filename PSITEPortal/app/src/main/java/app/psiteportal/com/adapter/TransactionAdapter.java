package app.psiteportal.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.psiteportal.AddExpenseActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.SeminarProfileActivity;
import app.psiteportal.com.psiteportal.TransactionDetailsActivity;
import app.psiteportal.com.utils.AppController;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    static String usertype;

    public TransactionAdapter(Context context, List<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, final int position) {

        Transaction t =  transactions.get(position);

        holder.trans_img.setImageUrl(t.getTransactionImg(), imageLoader);
        holder.transaction_name.setText(t.getItemName());
        holder.transaction_amount.setText(t.getTransactionAmount());
        holder.transaction_date.setText(t.getTransactionDate());
        holder.transaction_type.setText(t.getTransactionType());
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.transactions_item, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "test", Toast.LENGTH_LONG);
            }
        });
        return new TransactionViewHolder(itemView, context, transactions);
    }


    public static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView transaction_name;
        public TextView transaction_amount;
        public TextView transaction_date;
        public TextView transaction_type;
        public Transaction transaction;
        public NetworkImageView trans_img;

        List<Transaction> transactions = new ArrayList<>();
        Context context;

        public TransactionViewHolder(View v, Context context, List<Transaction> transactions){
            super(v);
            this.transactions = transactions;
            this.context = context;
            v.setOnClickListener(this);

            transaction_name = (TextView) v.findViewById(R.id.transaction_name);
            transaction_amount = (TextView) v.findViewById(R.id.transaction_amount);
            transaction_date = (TextView) v.findViewById(R.id.transaction_date);
            transaction_type = (TextView) v.findViewById(R.id.transaction_type);
            trans_img = (NetworkImageView) v.findViewById(R.id.transaction_photo);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
//            Transaction t = this.transactions.get(position);
//            Intent i = new Intent(context, TransactionDetailsActivity.class)
//                    .putExtra("transaction_id",t.getTransactionId()).putExtra("sid", t.getSid());
//            context.startActivity(i);
        }
    }

    public List<Transaction> getSeminarsList() {
        return transactions;
    }

//    public void getUserType(String userType){
//        usertype = userType;
//    }
}
