package com.projectteam.newsapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.adapter.RSSListAdapter;
import com.projectteam.newsapp.helper.RssDbHelper;


public class SourceFragment extends Fragment {


    ExtendedFloatingActionButton extendbtn, rssadd, deleteforever;
    RecyclerView recyclerView;
    View view;

    RssDbHelper db ;
    RSSListAdapter rssListAdapter;

    EditText txtname, txtrss;
    MaterialAutoCompleteTextView spinner;
    Button addbtn;
    ArrayAdapter<CharSequence> rsstitle;

    ImageView empty_data;
    TextView note01, note02;

    boolean isALLFloatbtn;
    String sTranslate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.sourcenews, container, false);

        //messeage show no rss source available on database
        empty_data = view.findViewById(R.id.imageView2);
        note01 = view.findViewById(R.id.Note01);
        note02 = view.findViewById(R.id.Note02);

        //Float button
        extendbtn = view.findViewById(R.id.rssbtnopt);
        rssadd = view.findViewById(R.id.addrss);
        deleteforever = view.findViewById(R.id.rssbtndelete);

        //by default, all float button is hidden
        extendbtn.extend();
        rssadd.setVisibility(View.GONE);
        deleteforever.setVisibility(View.GONE);
        isALLFloatbtn = false;

        recyclerView = view.findViewById(R.id.RSSSource);
        db =new RssDbHelper(requireActivity().getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rssListAdapter = new RSSListAdapter(getActivity(),db.getArray());
        if (rssListAdapter.getItemCount() == 0)
        {
            empty_data.setVisibility(View.VISIBLE);
            note01.setVisibility(View.VISIBLE);
            note02.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_data.setVisibility(View.GONE);
            note01.setVisibility(View.GONE);
            note02.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(rssListAdapter);
        showoption();
        addRSS();
        deleteSourceForever();
        return view;
    }

    private void showoption() {
        extendbtn.setOnClickListener(v -> {
            if (!isALLFloatbtn)
            {
                rssadd.show();
                deleteforever.show();
                isALLFloatbtn = true;
            }
            else
            {
                rssadd.hide();
                deleteforever.hide();
                isALLFloatbtn = false;
            }
        });

    }
    private void checkfab()
    {
        if (!isALLFloatbtn)
        {
            rssadd.show();
            deleteforever.show();
            isALLFloatbtn = true;
        }
        else
        {
            rssadd.hide();
            deleteforever.hide();
            isALLFloatbtn = false;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addRSS() {
        rssadd.setOnClickListener(v -> {
            checkfab();
            BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
            dialog.setContentView(R.layout.add_rss);
            dialog.show();
            String[] rss_type = getResources().getStringArray(R.array.title_list);
            txtname = dialog.findViewById(R.id.txtName);
            txtrss = dialog.findViewById(R.id.spinner_rss);
            addbtn = dialog.findViewById(R.id.btnAdd);
            spinner = dialog.findViewById(R.id.spinner);
            rsstitle = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, rss_type);
            spinner.setAdapter(rsstitle);
            /*Now this is how we get default value from autotextcomplete position*/
            spinner.setOnItemClickListener((parent, view, position, id) -> {
                switch (position)
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

            addbtn.setOnClickListener(v1 -> {
                String sTitle = spinner.getText().toString().trim();
                String sName = txtname.getText().toString().trim();
                String sURL = txtrss.getText().toString().trim();

                if (TextUtils.isEmpty(txtname.getText().toString()))
                {
                    Toast.makeText(getActivity(), R.string.NameRequired, Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(txtrss.getText().toString()))
                {
                    Toast.makeText(getActivity(), R.string.URLRequired, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //insert data
                    db.insertRSSList(sTitle, sName, sURL, sTranslate);
                    empty_data.setVisibility(View.GONE);
                    note01.setVisibility(View.GONE);
                    note02.setVisibility(View.GONE);
                    //hide all float button
                    rssadd.hide();
                    deleteforever.hide();
                    isALLFloatbtn = false;
                    //lam moi array
                    rssListAdapter.updateArray(db.getArray());
                    Toast.makeText(getActivity(), "Thêm nguồn thành công", Toast.LENGTH_SHORT).show();
                    rssListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        });
    }

    private void deleteSourceForever() {
        deleteforever.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog);
            builder.setTitle(R.string.confirm_title);
            String mesg1 = getString(R.string.confirm_del_all);
            builder.setMessage(mesg1);
            builder.setPositiveButton(R.string.yes_btn, (dialog, i) -> {
                //Xoa data
                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(requireActivity());
                builder2.setTitle(R.string.confirm_title);
                if (rssListAdapter.getItemCount() == 0)
                {
                    String mesg2 = getString(R.string.No_database);
                    builder2.setMessage(mesg2);
                }
                else
                {
                    String mesg3 = getString(R.string.clear_all_rss_succes);
                    String mesg4 = getString(R.string.messeage_delete2);
                    builder2.setMessage(mesg3 + "\n" + mesg4);
                }
                builder2.setPositiveButton("OK",null);
                builder2.show();
                db.truncate();
                rssListAdapter.updateArray(db.getArray());
                recyclerView.setAdapter(rssListAdapter);
                empty_data.setVisibility(View.VISIBLE);
                note01.setVisibility(View.VISIBLE);
                note02.setVisibility(View.VISIBLE);
                //hide all float button
                rssadd.hide();
                deleteforever.hide();
                extendbtn.shrink();

                isALLFloatbtn = false;

            });
            builder.setNegativeButton(R.string.dont_del_me, (dialog, i) -> {
                //just close dialog
                dialog.dismiss();
            });

            builder.show();

        });
    }

}