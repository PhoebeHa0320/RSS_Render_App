package com.projectteam.newsapp.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.helper.RssDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RSSListAdapter extends RecyclerView.Adapter<RSSListAdapter.MyViewHolder> {

    EditText txtname, txtrss;
    AutoCompleteTextView spinner;
    ArrayAdapter<CharSequence> rsstitle;
    Button addbtn;
    Activity activity;
    JSONArray jsonArray;
    RssDbHelper db;
    String[] rss_type;
    ImageView empty_data;
    TextView note01, note02, title_edit;
    String sTranslate;


    public RSSListAdapter (Activity activity, JSONArray jsonArray)
    {
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
    public RSSListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_items, parent, false);

        empty_data = activity.findViewById(R.id.imageView2);
        note01 = activity.findViewById(R.id.Note01);
        note02 = activity.findViewById(R.id.Note02);

        db = new RssDbHelper(view.getContext());
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.txttitle.setText(jsonObject.getString("title"));
            holder.txtname.setText(jsonObject.getString("name"));
            holder.txturl.setText(jsonObject.getString("url"));
            holder.txtdate.setText(jsonObject.getString("date"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(
                        holder.getAbsoluteAdapterPosition()
                );
                String sID = jsonObject.getString("id");
                String sName = jsonObject.getString("name");
                String sURL = jsonObject.getString("url");

                //goi Dialog
                BottomSheetDialog dialog = new BottomSheetDialog(activity);
                dialog.setContentView(R.layout.add_rss);
                dialog.show();
                rss_type = activity.getResources().getStringArray(R.array.title_list);
                spinner = dialog.findViewById(R.id.spinner);
                rsstitle = new ArrayAdapter<>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, rss_type);
                spinner.setAdapter(rsstitle);
                txtname = dialog.findViewById(R.id.txtName);
                txtrss = dialog.findViewById(R.id.spinner_rss);
                title_edit = dialog.findViewById(R.id.title_add_rss);
                addbtn = dialog.findViewById(R.id.btnAdd);

                txtname.setText(sName);
                txtrss.setText(sURL);

                //set button name to update if edit item
                addbtn.setText(R.string.updateBtn);
                title_edit.setText(R.string.UpdateSource);
                /*Now this is how we get default value from autotextcomplete position*/
                spinner.setOnItemClickListener((parent, view, position12, id) -> {
                    switch (position12)
                    {
                        case 0:
                            sTranslate = "11";
                            break;
                        case 1:
                            sTranslate = "12";
                            break;
                        case 2:
                            sTranslate = "13";
                            break;
                        case 3:
                            sTranslate = "14";
                            break;
                        case 4:
                            sTranslate = "15";
                            break;
                        case 5:
                            sTranslate = "16";
                            break;
                        case 6:
                            sTranslate = "17";
                            break;
                        case 7:
                            sTranslate = "18";
                            break;
                        case 8:
                            sTranslate = "19";
                            break;
                        case 9:
                            sTranslate = "20";
                            break;
                        case 10:
                            sTranslate = "21";
                            break;
                        default:
                            break;
                    }
                });
                //set action cho nut update
                addbtn.setOnClickListener(v12 -> {
                    String sTitle1 = spinner.getText().toString().trim();
                    String sName1 = txtname.getText().toString().trim();
                    String sURL1 = txtrss.getText().toString().trim();
                    //kiem tra cac thong tin da nhap day du chua
                    if (TextUtils.isEmpty(txtname.getText().toString()))
                    {
                        Toast.makeText(activity, R.string.NameRequired, Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(txtrss.getText().toString()))
                    {
                        Toast.makeText(activity, R.string.URLRequired, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        db.updateRSSList(sID, sTitle1, sName1, sURL1, sTranslate);
                        //lam moi array
                        updateArray(db.getArray());
                        //notify thay doi
                        notifyItemChanged(holder.getAbsoluteAdapterPosition());
                        Toast.makeText(activity, R.string.update_database_success, Toast.LENGTH_SHORT).show();
                        //refresh fragment
                        //dong dialog
                        dialog.dismiss();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            //lay doi tuong tai vi tri chon
            int position1 = holder.getAbsoluteAdapterPosition();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position1);
                String sID = jsonObject.getString("id");

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog);
                builder.setTitle(R.string.confirm_title);
                String messeage1 = activity.getString(R.string.question_confirm);
                String messeage2 = activity.getString(R.string.messeage_delete2);
                builder.setMessage(messeage1 + "\n" + messeage2);
                builder.setPositiveButton(R.string.yes_btn, (dialog, i) -> {
                    //Xoa data
                    db.deleteRSSList(sID);
                    jsonArray.remove(position1);
                    notifyItemRemoved(position1);
                    notifyItemChanged(position1, jsonArray.length());

                    if (RSSListAdapter.this.getItemCount() == 0)
                    {
                        empty_data.setVisibility(View.VISIBLE);
                        note01.setVisibility(View.VISIBLE);
                        note02.setVisibility(View.VISIBLE);
                    }

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
            //tra ve true
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
    //this is for rss list
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txttitle, txtname, txturl, txtdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txttitle = itemView.findViewById(R.id.rss_title);
            txtname = itemView.findViewById(R.id.rss_name);
            txturl = itemView.findViewById(R.id.rss_url);
        }
    }


}
