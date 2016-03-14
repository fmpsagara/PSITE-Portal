package app.psiteportal.com.psiteportal;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Edwin on 18/01/2015.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<CardViewItem> mItems;
    final public   int size;
    public CardAdapter(ArrayList<String> title_str, ArrayList<String> about_str) {
        super();

        mItems = new ArrayList<CardViewItem>();



//
//            if (title_str.size() > mItems.size() && about_str.size() > mItems.size()) {

                for (int i = 0; i < title_str.size(); i++) {
                    String title = title_str.get(i);
                    String about = about_str.get(i);
//            System.out.println("Index: " + i + " - Item: " + list.get(i));

                    CardViewItem nature = new CardViewItem();
                    nature.setName(title);
                    nature.setDes(about);

                    switch (i) {
                        case 0:
                            nature.setThumbnail(R.drawable.cloudsherpas);
                            break;
                        case 1:
                            nature.setThumbnail(R.drawable.hackathon);
                            break;
                        case 2:
                            nature.setThumbnail(R.drawable.devcon);
                            break;
                        case 3:
                            nature.setThumbnail(R.drawable.microsoft);
                            break;
                        case 4:
                            nature.setThumbnail(R.drawable.dew);
                            break;
                        case 5:
                            nature.setThumbnail(R.drawable.c);
                            break;

                        case 6:
                            nature.setThumbnail(R.drawable.rewired_banner);
                            break;
                        case 7:
                            nature.setThumbnail(R.drawable.bigdata);
                            break;


                    }

                    Log.e("I I I I I", ""+i);

                    mItems.add(nature);

               }
        size = mItems.size();
        Log.e("LOG LOG LOG LOG", "" + size);

//
//            } else {
//
//
//            }

    }

    public void ClearRecycler() {
        mItems.clear();
    }

    ;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        CardViewItem nature = mItems.get(i);
        viewHolder.tvNature.setText(nature.getName());
        viewHolder.tvDesNature.setText(nature.getDes());
        viewHolder.imgThumbnail.setImageResource(nature.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView tvNature;
        public TextView tvDesNature;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvNature = (TextView) itemView.findViewById(R.id.tv_nature);
            tvDesNature = (TextView) itemView.findViewById(R.id.tv_des_nature);
        }
    }
}


