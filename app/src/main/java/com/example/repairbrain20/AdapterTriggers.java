package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterTriggers extends BaseAdapter {
    Activity act;
    Map<String,String> triggers = new HashMap<>();
    static Map<String,String> triggers_copy = new HashMap<>();
    List<String> keys = new ArrayList<>();
    boolean delete = false;
    View view;
    ListView list;

    AdapterTriggers(Activity activity,View view, Map<String,String> map)
    {
        this.act = activity;
        this.view = view;

        list = view.findViewById(R.id.list);
        ImageView loading = view.findViewById(R.id.loading);

        if(map==null || map.size()==0)
        {
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            Glide.with(activity).load(R.drawable.noresultfound).into(loading);
        }
        else
        {
            list.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            triggers.putAll(map);
            keys.addAll(map.keySet());
        }

        triggers_copy.clear();
        triggers_copy.putAll(triggers);

    }

    AdapterTriggers(Activity activity,View view,boolean delete)
    {
        this(activity,view,triggers_copy);
        this.delete = delete;
    }

    @Override
    public int getCount() {
        return triggers.size();
    }

    @Override
    public Object getItem(int i) {
        return triggers.get(keys.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view =  act.getLayoutInflater().inflate(R.layout.custom_triggers_list,null);

        RelativeLayout main = view.findViewById(R.id.main);
        TextView trigger_name = view.findViewById(R.id.trigger_name);
        TextView date_added = view.findViewById(R.id.date_added);
        ImageView delete = view.findViewById(R.id.delete);

        if(this.delete)
        {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = trigger_name.getText().toString();

                    Snackbar snack = Snackbar.make(AdapterTriggers.this.view,"Removing", BaseTransientBottomBar.LENGTH_INDEFINITE);
                    snack.show();

                    DatabaseReference reference = User.getRepairReference();
                    reference
                            .child("triggers")
                            .child(key)
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    snack.dismiss();
                                    Toast.makeText(act,"Trigger Removed",Toast.LENGTH_SHORT).show();

                                    AdapterTriggers.this.triggers.remove(key);
                                    AdapterTriggers.triggers_copy.remove(key);

                                    list.setAdapter(new AdapterTriggers(act,AdapterTriggers.this.view,true));
                                }
                            });
                }
            });
        }

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String trigger = keys.get(i);
        String added_on = triggers.get(trigger);

        trigger_name.setText(trigger);
        date_added.setText(added_on);

        return view;
    }
}