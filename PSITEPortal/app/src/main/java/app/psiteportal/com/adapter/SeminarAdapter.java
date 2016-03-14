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

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.SeminarProfileActivity;
import app.psiteportal.com.utils.AppController;


public class SeminarAdapter extends RecyclerView.Adapter<SeminarAdapter.SeminarViewHolder> {

    private List<Seminar> seminars;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    static String usertype;
    static int pid;

    public SeminarAdapter(Context context, List<Seminar> seminars){
        this.context = context;
        this.seminars = seminars;
    }

    @Override
    public int getItemCount() {
        return seminars.size();
    }

    @Override
    public void onBindViewHolder(SeminarViewHolder holder, final int position) {

        Seminar s =  seminars.get(position);
        holder.banner.setImageUrl(s.getBannerUrl(), imageLoader);
        holder.seminarName.setText(s.getSeminarName());
    }

    @Override
    public SeminarViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.seminar_item, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "SHIT", Toast.LENGTH_LONG);
            }
        });
        return new SeminarViewHolder(itemView, context, seminars);
    }


    public static class SeminarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        public TextView seminarName;
        public Seminar seminar;
        public NetworkImageView banner;

        List<Seminar> seminars = new ArrayList<>();
        Context context;

        public SeminarViewHolder(View v, Context context, List<Seminar> seminars){
            super(v);
            this.seminars = seminars;
            this.context = context;
            v.setOnClickListener(this);

            seminarName = (TextView) v.findViewById(R.id.seminar_name);
            banner = (NetworkImageView) v.findViewById(R.id.banner);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Seminar s = this.seminars.get(position);
            Bitmap bitmap = s.getBanner();
            Intent i = new Intent(context, SeminarProfileActivity.class)
                    .putExtra("seminar_id",s.getId()).putExtra("seminar_title",s.getSeminarName()).putExtra("user_pid",pid).putExtra("usertype",usertype);
            context.startActivity(i);
        }
    }

    public List<Seminar> getSeminarsList() {
        return seminars;
    }


    public void getUserId(int userId){
        pid = userId;
    }
    public void getUserType(String userType){
        usertype = userType;
    }
}


