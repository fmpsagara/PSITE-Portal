package app.psiteportal.com.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import app.psiteportal.com.model.Announcement;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.ScrollingElectionFragment;
import app.psiteportal.com.psiteportal.TestActivity;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcements;
    private Context context;
    private String imageUrl;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int counter = 0;
    private int num_positions_needed = AppController.getInstance().getNum_positions_needed();

    public AnnouncementAdapter(Context context, List<Announcement> announcements){
        this.context = context;
        this.announcements = announcements;
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, final int position) {
        Announcement a = announcements.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
        String next = "<font color='#009688'>Details: </font>";

        holder.title.setText(a.getTitle());
        holder.details.setText(Html.fromHtml("<font color='#009688'>Details: </font>" + a.getDetails()));
        holder.date.setText(Html.fromHtml("<font color='#009688'>Date: </font>" + a.getDate()));
        holder.time.setText(Html.fromHtml("<font color='#009688'>Time: </font>" + a.getTime()));
        holder.venue.setText(Html.fromHtml("<font color='#009688'>Venue: </font>" + a.getVenue()));
        holder.poster.setText(Html.fromHtml("<font color='#009688'>Posted by: </font>" + a.getPoster()));


    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.announcement_content, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        return new AnnouncementViewHolder(itemView, context, announcements);
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, poster, details, venue, date, time;

        List<Announcement> announcements = new ArrayList<>();
        Context context;
        public AnnouncementViewHolder(View v, Context context, List<Announcement> announcements) {
            super(v);
            this.announcements = announcements;
            this.context = context;
            v.setOnClickListener(this);
            //pic = (ImageView) v.findViewById(R.id.pic);
            title = (TextView) v.findViewById(R.id.announcement_title);
            poster = (TextView) v.findViewById(R.id.announcement_op);
            details = (TextView) v.findViewById(R.id.announcement_details);
            date = (TextView) v.findViewById(R.id.announcement_date);
            time = (TextView) v.findViewById(R.id.announcement_time);
            venue = (TextView) v.findViewById(R.id.announcement_venue);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Announcement a = this.announcements.get(position);
            Intent intent = new Intent(this.context, TestActivity.class);

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bytes = stream.toByteArray();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//            intent.putExtra("prof_pic", n.getImageUrl());
//            intent.putExtra("name", n.getName());
//            intent.putExtra("institution", n.getInstitution());
//            intent.putExtra("contact", n.getContact());
//            intent.putExtra("email", n.getEmail());
//            intent.putExtra("address", n.getAddress());
//            context.startActivity(intent);


//            Bundle bundle = new Bundle();
//            bundle.putString("prof_pic", n.getImageUrl());
//            bundle.putString("name", n.getName());
//            bundle.putString("institution", n.getInstitution());
//            bundle.putString("contact", n.getContact());
//            bundle.putString("email", n.getEmail());
//            bundle.putString("address", n.getAddress());
//            ScrollingElectionFragment fragment = new ScrollingElectionFragment();
//            FragmentManager fragmentManager = fragment.getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container, fragment);
//            fragmentTransaction.commit();

        }
    }
    public List<Announcement> getAnnouncementsList() {
        return announcements;
    }


}
