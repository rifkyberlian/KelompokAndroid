package com.kelompok.android.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelompok.android.R;
import com.kelompok.android.ReplyActivity;
import com.kelompok.android.models.CommentsModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.OriginalViewHolder> {

    private List<CommentsModel> items = new ArrayList<>();
    private Context ctx;



    public CommentsAdapter(Context context, List<CommentsModel> items) {
        this.items = items;
        ctx = context;

    }


    @Override
    public CommentsAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommentsAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
        vh = new CommentsAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.OriginalViewHolder holder, final int position) {

        final CommentsModel obj = items.get(position);


        holder.name.setText(obj.getName());
        holder.comment.setText(obj.getComment());

        Picasso.get().load(obj.getImage()).into(holder.imageView);


        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ctx, ReplyActivity.class);
                intent.putExtra("id",obj.getId());
                ctx.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        private TextView name,comment,reply;
        private View lyt_parent;
        private CircularImageView imageView;


        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            lyt_parent=v.findViewById(R.id.lyt_parent);
            imageView=v.findViewById(R.id.profile_img);
            comment=v.findViewById(R.id.comments);
            reply=v.findViewById(R.id.tv_replay);
        }
    }

}