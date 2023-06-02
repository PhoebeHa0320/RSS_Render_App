package com.projectteam.newsapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.activity.NewsDetailActivity;
import com.projectteam.newsapp.helper.RssDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FavouriteNewsAdpater extends RecyclerView.Adapter<FavouriteNewsAdpater.MyViewFavourite> {

    public TextView txtTitle,txtPubDate,txtsource;
    private final Context mContext;
    public ImageView imageView;
    Activity activity;
    JSONArray jsonArray;
    RssDbHelper db;

    public FavouriteNewsAdpater(Context mContext, Activity activity, JSONArray jsonArray)
    {
        this.mContext = mContext;
        this.activity = activity;
        this.jsonArray = jsonArray;
    }

    //constructor update Array
    public void updateArray(JSONArray jsonArray)
    {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public FavouriteNewsAdpater.MyViewFavourite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_items, parent, false);

        txtTitle = activity.findViewById(R.id.txtTitle);
        txtPubDate = activity.findViewById(R.id.txtPubDate);
        txtsource = activity.findViewById(R.id.txtSource);
        imageView = activity.findViewById(R.id.imgNews);
        db = new RssDbHelper(view.getContext());
        return new MyViewFavourite(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewFavourite holder, int position) {
        try {
            JSONObject jsonFavourite = jsonArray.getJSONObject(position);
            holder.txttitle.setText(jsonFavourite.getString("title"));
            holder.txturl.setText(jsonFavourite.getString("url"));
            holder.txtpubdate.setText(jsonFavourite.getString("pubdate"));
            try
            {
                String path = jsonFavourite.getString("img");
                Picasso.get().load(path).into(holder.thumbnail);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
                int notavailable = R.drawable.not_available;
                holder.thumbnail.setImageResource(notavailable);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(view -> {
            //getLink (link to enter website)
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(
                        holder.getAbsoluteAdapterPosition());
                String sTitle = jsonObject.getString("title");
                String sPubDate = jsonObject.getString("pubdate");
                String sURL = jsonObject.getString("url");
                String sImg = jsonObject.getString("img");
                Intent intent = new Intent(mContext , NewsDetailActivity.class);
                intent.putExtra("url",sURL );
                intent.putExtra("title",sTitle );
                intent.putExtra("img",sImg);
                intent.putExtra("source",sURL );
                intent.putExtra("pubdate",sPubDate );
                mContext.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            int position1 = holder.getAbsoluteAdapterPosition();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position1);
                String sID = jsonObject.getString("id");

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                builder.setTitle(R.string.confirm_title);
                builder.setMessage(R.string.Delete_favourite);
                builder.setPositiveButton(R.string.yes_btn, (dialog, i) -> {
                    //Xoa data
                    db.remove_fav(sID);
                    jsonArray.remove(position1);
                    notifyItemRemoved(position1);
                    notifyItemChanged(position1, jsonArray.length());

                    Toast.makeText(activity, R.string.Delete_success, Toast.LENGTH_SHORT).show();

                });
                builder.setNegativeButton(R.string.dont_del_me, (dialog, i) -> {
                    //just close dialog
                    dialog.dismiss();
                });
                builder.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    //this is for favourite list, we will use it to view in favourite fragment
    public static class MyViewFavourite extends RecyclerView.ViewHolder {
        TextView txttitle, txturl, txtpubdate ;
        ImageView thumbnail;
        Button favbtn;
        public MyViewFavourite(@NonNull View itemView) {
            super(itemView);
            txttitle = itemView.findViewById(R.id.txtTitle);
            txturl = itemView.findViewById(R.id.txtSource);
            txtpubdate = itemView.findViewById(R.id.txtPubDate);
            thumbnail = itemView.findViewById(R.id.imgNews);
            favbtn = itemView.findViewById(R.id.fav_item);
        }
    }
}