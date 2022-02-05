package com.gzhu.kevin.locktime.View.Viewpager2.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gzhu.kevin.locktime.R;

import java.util.LinkedList;
import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder> {
  List<ItemData> itemDataList;


  public MainRecyclerViewAdapter(List<ItemData> itemDataList){
    this.itemDataList=itemDataList;
  }
  public static class ItemData {
    String textOfTextView="default";
    Drawable drawableOfImageView;

    public ItemData(String textOfTextView, Drawable resOfImageView) {
      this.textOfTextView = textOfTextView;
      this.drawableOfImageView = resOfImageView;
    }


  }
  static class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    TextView textView;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.recycler_view_item_image_view);
      textView = itemView.findViewById(R.id.recycler_view_item_text_view);
    }

  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_layout,parent,false);

    MyViewHolder myViewHolder = new MyViewHolder(view);

    return myViewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    ItemData itemData= itemDataList.get(position);
    holder.textView.setText(itemData.textOfTextView);
    holder.imageView.setImageDrawable(itemData.drawableOfImageView);
  }

  @Override
  public int getItemCount() {
    return itemDataList.size();
  }



}
