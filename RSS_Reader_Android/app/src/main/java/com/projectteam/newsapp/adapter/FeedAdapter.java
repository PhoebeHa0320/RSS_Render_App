package com.projectteam.newsapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.RSSFeed.ItemClickListener;
import com.projectteam.newsapp.RSSFeed.RSSObject;
import com.projectteam.newsapp.activity.NewsDetailActivity;
import com.projectteam.newsapp.helper.RssDbHelper;
import com.squareup.picasso.Picasso;

class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
{

    public TextView txtTitle,txtPubDate,txtsource;
    public ImageView imageView;
    public RssDbHelper db;
    public Button favbtn;
    public Activity activity;

    private ItemClickListener itemClickListener;

    public FeedViewHolder(View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtPubDate = itemView.findViewById(R.id.txtPubDate);
        txtsource = itemView.findViewById(R.id.txtSource);
        imageView = itemView.findViewById(R.id.imgNews);
        favbtn = itemView.findViewById(R.id.fav_item);
        db = new RssDbHelper(itemView.getContext());
        //Set Event
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),false);
    }
    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAbsoluteAdapterPosition(),true);
        return true;
    }
}

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private final RSSObject rssObject;
    private final Context mContext;
    private final LayoutInflater inflater;
    private int count;

    public FeedAdapter(RSSObject rssObject, Context mContext) {
        this.rssObject = rssObject;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.new_items,parent,false);
        return new FeedViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {

        holder.txtTitle.setText(rssObject.getItems().get(position).getTitle());
        holder.txtPubDate.setText(rssObject.getItems().get(position).getPubDate());
        holder.txtsource.setText(rssObject.getItems().get(position).getLink());
        try
        {
            String path = rssObject.getItems().get(position).getThumbnail();
            Picasso.get().load(path).into(holder.imageView);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            int notavailable = R.drawable.not_available;
            holder.imageView.setImageResource(notavailable);
        }
        
        holder.itemView.setOnClickListener(view -> {
                //getLink (link to enter website)
                Intent intent = new Intent(mContext , NewsDetailActivity.class);
                intent.putExtra("url",rssObject.getItems().get(position).getLink() );
                intent.putExtra("title",rssObject.getItems().get(position).getTitle() );
                intent.putExtra("img",rssObject.getItems().get(position).getThumbnail() );
                intent.putExtra("source",rssObject.getItems().get(position).getLink() );
                intent.putExtra("pubdate",rssObject.getItems().get(position).getPubDate() );
                mContext.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            int position2 = holder.getAbsoluteAdapterPosition();
            PopupMenu popupMenu = new PopupMenu(inflater.getContext(), v ,com.google.android.material.R.style.Widget_Material3_PopupMenu);
            popupMenu.setGravity(Gravity.RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                popupMenu.setForceShowIcon(true);
            }
            popupMenu.inflate(R.menu.menu_news_feed);

            String getTitle = rssObject.getItems().get(position2).getTitle();
            String getSource = rssObject.getItems().get(position2).getLink();
            String getPubDate = rssObject.getItems().get(position2).getPubDate();
            String getImage = rssObject.getItems().get(position2).getThumbnail();
            String favstatus = "1";
            //get count to check holder item is avail at favourite table or not
            Cursor result = holder.db.checkfavourite(getTitle);
            if (result.moveToFirst())
            {
                count  = result.getCount();
            }
            result.close();

            popupMenu.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.addfavbtn)
                {
                    if (count < 1)
                    {
                        holder.db.insertFav(getTitle, getSource, getImage, getPubDate, favstatus);
                        Toast.makeText(mContext, R.string.favourite_add_success, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.e("Data count status", "Available, skip it");
                        Toast.makeText(mContext, R.string.data_available, Toast.LENGTH_SHORT).show();
                    }
                }
                if(item.getItemId() == R.id.delfavbtn)
                {
                    if (count >= 1)
                    {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(inflater.getContext(), R.style.MaterialAlertDialog);
                        builder.setTitle(R.string.confirm_title);
                        builder.setMessage(R.string.Delete_favourite);
                        builder.setPositiveButton(R.string.yes_btn, (dialog, i) -> {
                            //Xoa data
                            holder.db.remove_fav_main(getTitle);
                            Toast.makeText(mContext, R.string.del_favourite_success, Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton(R.string.dont_del_me, (dialog, i) -> {
                            //just close dialog
                            dialog.dismiss();
                        });
                        builder.show();
                    }
                    else
                    {
                        Log.e("Data count status", "Not Available, skip it");
                        Toast.makeText(mContext, R.string.Data_notavailable, Toast.LENGTH_SHORT).show();
                    }
                }
                if(item.getItemId() == R.id.offlinebtn)
                {
                    MaterialAlertDialogBuilder info = new MaterialAlertDialogBuilder(inflater.getContext(), R.style.MaterialAlertDialog);
                    info.setTitle(R.string.offline_save_title);
                    String info1 = mContext.getString(R.string.info1_offline) + "\n";
                    String info2 = mContext.getString(R.string.info2_offline) + "\n";
                    String info3 = mContext.getString(R.string.info3_offline) + "\n";
                    String path_offline = mContext.getString(R.string.offline_path) + "\n";
                    String info4 = mContext.getString(R.string.info4_offline) + "\n";
                    String info5 = mContext.getString(R.string.info5_offline) + "\n";
                    info.setMessage(info1 + info2 + info3 + path_offline + info4 + info5);
                    info.setNegativeButton("OK", (dialog, i) -> {
                        //Xoa data
                        dialog.dismiss();
                    });
                    info.show();
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        try
        {
            return rssObject.items.size();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return 0;
        }

    }
}
