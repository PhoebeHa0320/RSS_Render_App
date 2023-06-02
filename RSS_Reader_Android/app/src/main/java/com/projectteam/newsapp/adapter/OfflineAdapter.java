package com.projectteam.newsapp.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.projectteam.newsapp.BuildConfig;
import com.projectteam.newsapp.R;

import java.io.File;

public class OfflineAdapter extends RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    Context context;
    File[] filesAndFolders;

    public OfflineAdapter(Context context, File[] filesAndFolders) {
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.offline_items, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OfflineAdapter.ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());
        holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        holder.itemView.setOnClickListener(v -> {
            //not working in android N and later
            /*try {
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String type = "application/pdf";
                intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), "Cannot open the file", Toast.LENGTH_SHORT).show();
            }*/

            //this source helped me so much
            //1. https://stackoverflow.com/questions/56598480/couldnt-find-meta-data-for-provider-with-authority
            //2. https://stackoverflow.com/questions/48035736/opening-a-pdf-file-using-fileprovider-uri-opens-a-blank-screen
            Uri excelPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                excelPath = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", selectedFile);
            } else {
                excelPath = Uri.fromFile(selectedFile);
            }
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(excelPath, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try{
                context.startActivity(pdfIntent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        try
        {
            return filesAndFolders.length;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }
}