package app.psiteportal.com.utils;

/**
 * Created by fmpdroid on 2/19/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;

import app.psiteportal.com.psiteportal.TestActivity;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class NominationAdapter extends RecyclerView.Adapter<NominationAdapter.NominationViewHolder> {

    private List<Nominee> nominees;
    private Context context;
    private String imageUrl;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int num_positions_needed = AppController.getInstance().getNum_positions_needed();
    int lastCheckedPosition = 0;
    private int counter = 0;
    RadioButton lastChecked = null;

    public NominationAdapter(Context context, List<Nominee> nominees){
        this.context = context;
        this.nominees = nominees;
    }

    @Override
    public int getItemCount() {
        return nominees.size();
    }

    @Override
    public void onBindViewHolder(NominationViewHolder holder, final int position) {
        Nominee n = nominees.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
        holder.thumbNail.setImageUrl(n.getImageUrl(), imageLoader);
        holder.name.setText(n.getName());
        holder.institution.setText(n.getInstitution());

//        holder.checkbox.setChecked(nominees.get(position).isSelected());
//        holder.checkbox.setTag(nominees.get(position));
//
//        holder.checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CheckBox ch = (CheckBox) view;
//                Nominee nom = (Nominee) ch.getTag();
//
//                nom.setSelected(ch.isChecked());
//                nominees.get(position).setSelected(ch.isChecked());
//            }
//        });
//        holder.radioButton.setChecked(nominees.get(position).isSelected());
//        holder.radioButton.setTag(new Integer(position));
//        if(position == 0 && nominees.get(0).isSelected() && holder.radioButton.isChecked()){
//            lastCheckedPosition = 0;
//            lastChecked = holder.radioButton;
//        }
//        holder.radioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RadioButton rb = (RadioButton) view;
//                int clickedPos = ((Integer)rb.getTag()).intValue();
//
//                if(rb.isChecked())
//                {
//                    if(lastChecked != null)
//                    {
//                        lastChecked.setChecked(false);
//                        nominees.get(lastCheckedPosition).setSelected(false);
//                    }
//
//                    lastChecked = rb;
//                    lastCheckedPosition = clickedPos;
//                }
//                else
//                    lastChecked = null;
//
//                nominees.get(clickedPos).setSelected(rb.isChecked());
//            }
//        });


        holder.checkbox.setChecked(nominees.get(position).isSelected());
        holder.checkbox.setTag(nominees.get(position));

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                Nominee nom = (Nominee) cb.getTag();
                //nom.setSelected(ch.isChecked());

                if(cb.isChecked()){
                    counter++;
                }else if(!cb.isChecked()){
                    counter--;
                }
                if(counter>num_positions_needed){
                    cb.setChecked(false);
                    counter--;
                    Toast.makeText(context, "You are only allowed to nominate "+num_positions_needed+" nominees", Toast.LENGTH_SHORT).show();
                }else{
                    nominees.get(position).setSelected(cb.isChecked());
                }

            }
        });
    }

    @Override
    public NominationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.gp_nomination_content, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return new NominationViewHolder(itemView, context, nominees);
    }

    public static class NominationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView pic;
        public TextView name;
        public TextView institution;
        public CheckBox checkbox;
        public Nominee nominee;
        public NetworkImageView thumbNail;
        public RadioButton radioButton;

        List<Nominee> nominees = new ArrayList<>();
        Context context;
        public NominationViewHolder(View v, Context context, List<Nominee> nominees) {
            super(v);
            this.nominees = nominees;
            this.context = context;
            v.setOnClickListener(this);
            //pic = (ImageView) v.findViewById(R.id.pic);
            name = (TextView) v.findViewById(R.id.nomination_name);
            institution = (TextView) v.findViewById(R.id.nomination_institution);
            thumbNail = (NetworkImageView) v.findViewById(R.id.nomination_pic);
            checkbox = (CheckBox) v.findViewById(R.id.nominationCheckBox);
            radioButton = (RadioButton) v.findViewById(R.id.radioButton);
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

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bytes = stream.toByteArray();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            intent.putExtra("prof_pic", n.getImageUrl());
            intent.putExtra("name", n.getName());
            intent.putExtra("institution", n.getInstitution());
            intent.putExtra("contact", n.getContact());
            intent.putExtra("email", n.getEmail());
            intent.putExtra("address", n.getAddress());
            context.startActivity(intent);


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
    public List<Nominee> getNomineeList() {
        return nominees;
    }


}
