package app.psiteportal.com.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.ScrollingElectionFragment;
import app.psiteportal.com.psiteportal.TestActivity;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class ViewNomineesAdapter extends RecyclerView.Adapter<ViewNomineesAdapter.ViewNomineesViewHolder> {

    private List<Nominee> nominees;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int counter = 0;
    private int num_positions_needed = AppController.getInstance().getNum_positions_needed();

    public ViewNomineesAdapter(Context context, List<Nominee> nominees){
        this.context = context;
        this.nominees = nominees;
    }

    @Override
    public int getItemCount() {
        return nominees.size();
    }

    @Override
    public void onBindViewHolder(ViewNomineesAdapter.ViewNomineesViewHolder holder, final int position) {
        Nominee n = nominees.get(position);

        holder.thumbNail.setImageUrl(n.getImageUrl(), imageLoader);
        holder.name.setText(n.getName());
        holder.institution.setText(n.getInstitution());

    }

    @Override
    public ViewNomineesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.gp_viewnominees, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();
        return new ViewNomineesViewHolder(itemView, context, nominees);
    }

    public static class ViewNomineesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView institution;
        public Nominee nominee;
        public NetworkImageView thumbNail;

        List<Nominee> nominees = new ArrayList<>();
        Context context;
        public ViewNomineesViewHolder(View v, Context context, List<Nominee> nominees) {
            super(v);
            this.nominees = nominees;
            this.context = context;
            v.setOnClickListener(this);

            name = (TextView) v.findViewById(R.id.name);
            institution = (TextView) v.findViewById(R.id.institution);
            thumbNail = (NetworkImageView) v.findViewById(R.id.pic);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Nominee n = this.nominees.get(position);
            Bitmap bitmap = n.getBitmap();
            Toast.makeText(context,n.getName(), Toast.LENGTH_LONG).show();

            Log.d("pota", n.getName());
            Log.d("pota", n.getInstitution());


            Intent intent = new Intent(this.context, TestActivity.class);

            intent.putExtra("prof_pic", n.getImageUrl());
            intent.putExtra("name", n.getName());
            intent.putExtra("institution", n.getInstitution());
            intent.putExtra("contact", n.getContact());
            intent.putExtra("email", n.getEmail());
            intent.putExtra("address", n.getAddress());
            context.startActivity(intent);

        }
    }
    public List<Nominee> getNomineeList() {
        return nominees;
    }


}
