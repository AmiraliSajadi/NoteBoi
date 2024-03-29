package com.example.noteboi;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    List<RecyclerViewModel> data_list;

    class MyViewHolder extends SwipeToAction.ViewHolder<RecyclerViewModel>{

        TextView my_title,my_memo;
        ImageView heart;

        public MyViewHolder(View v) {
            super(v);
            my_title = itemView.findViewById(R.id.tv_title);
            my_memo = itemView.findViewById(R.id.tv_memo);
            heart = itemView.findViewById(R.id.fav);
        }
    }

    public MyAdapter(List<RecyclerViewModel> data_list) {
        this.data_list = data_list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View row_view = inflater.inflate(R.layout.recycler_row,parent,false);

        return new MyViewHolder(row_view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.my_title.setText(data_list.get(position).getTitle());
        holder.my_memo.setText(data_list.get(position).getMemo());
        holder.heart.setImageResource(R.drawable.heat);
        holder.data = data_list.get(position);
        Log.i("current data's title",holder.data.getTitle());
        Log.i("current data's fav stat",String.valueOf(holder.data.isFav()));
        //doesn't recognize any note as fav
        if(holder.data.isFav()) holder.heart.setVisibility(View.VISIBLE);
        else holder.heart.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

}