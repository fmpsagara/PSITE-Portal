package app.psiteportal.com.utils;

/**
 * Created by fmpdroid on 3/11/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;

import app.psiteportal.com.psiteportal.TestActivity;

public class MembershipActivationAdapter extends RecyclerView.Adapter<MembershipActivationAdapter.MembershipActivationViewHolder> {

    private final List<Member> mMembers;
    private final Context context;
    private final LayoutInflater inflater;

    public MembershipActivationAdapter(Context context, List<Member> members){
        inflater = LayoutInflater.from(context);
        this.mMembers = new ArrayList<>(members);
        this.context = context;
//        this.mMembers = members;
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    @Override
    public void onBindViewHolder(MembershipActivationViewHolder holder, final int position) {
        final Member m = mMembers.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
//        holder.bind(m);
        holder.name.setText(m.getName());

        holder.email.setText(m.getEmail());
        if(m.getStatus().equals("Active")){
            holder.status.setText(Html.fromHtml("<font color='#00CD00'>" + m.getStatus() + "</font>"));
        }else{
            holder.status.setText(Html.fromHtml("<font color='#ff0000'>" + m.getStatus() + "</font>"));
        }


    }

    @Override
    public MembershipActivationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = inflater.inflate(R.layout.member_activation_content, viewGroup, false);
        return new MembershipActivationViewHolder(itemView);
    }

    public static class MembershipActivationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView name;
        public final TextView email;
        public final TextView status;
        public Switch memberSwitch;
        List<Member> members = new ArrayList<>();
        Context context;

        public MembershipActivationViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            name = (TextView) v.findViewById(R.id.member_name);
            email = (TextView) v.findViewById(R.id.member_email);
            status = (TextView) v.findViewById(R.id.member_status);

//            memberSwitch = (Switch) v.findViewById(R.id.member_switch);
        }
//        private void bind(Member member){
//            name.setText(member.getName());
//            email.setText(member.getEmail());
//            if(member.getStatus().equals("Active")){
//                status.setText(Html.fromHtml("<font color='#00CD00'>" + member.getStatus() + "</font>"));
//            }else{
//                status.setText(Html.fromHtml("<font color='#ff0000'>" + member.getStatus() + "</font>"));
//            }
//        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Member m= this.members.get(position);
            Toast.makeText(view.getContext(), m.getName(), Toast.LENGTH_SHORT).show();
        }
    }
    public List<Member> getMemberList() {
        return mMembers;
    }

    public void animateTo(List<Member> members) {
        applyAndAnimateRemovals(members);
        applyAndAnimateAdditions(members);
        applyAndAnimateMovedItems(members);
    }

    private void applyAndAnimateRemovals(List<Member> newMembers) {
        for (int i = mMembers.size() - 1; i >= 0; i--) {
            final Member member = mMembers.get(i);
            if (!newMembers.contains(member)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Member> newMembers) {
        for (int i = 0, count = newMembers.size(); i < count; i++) {
            final Member member = newMembers.get(i);
            if (!mMembers.contains(member)) {
                addItem(i, member);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Member> newMembers) {
        for (int toPosition = newMembers.size() - 1; toPosition >= 0; toPosition--) {
            final Member member = newMembers.get(toPosition);
            final int fromPosition = mMembers.indexOf(member);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Member removeItem(int position) {
        final Member member = mMembers.remove(position);
        notifyItemRemoved(position);
        return member;
    }

    public void addItem(int position, Member member) {
        mMembers.add(position, member);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Member member = mMembers.remove(fromPosition);
        mMembers.add(toPosition, member);
        notifyItemMoved(fromPosition, toPosition);
    }


}
