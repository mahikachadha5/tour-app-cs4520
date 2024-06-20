package com.example.project4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToursRecyclerListAdapter extends RecyclerView.Adapter<ToursRecyclerListAdapter.TourViewHolder>{

    Context context;
    List<Tour> tours;
    OnTourClickListener tourClickListener;

    public ToursRecyclerListAdapter(Context context, List<Tour> tours,
                                    OnTourClickListener tourClickListener) {
        this.context = context;
        this.tours = tours;
        this.tourClickListener = tourClickListener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tour_list_layout, parent, false);
        return new TourViewHolder(view, tourClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tours.get(position);
        holder.tourTextView.setText(String.valueOf(tour.getName())); // Convert int to String
        holder.descriptionTextView.setText(String.valueOf(tour.getDescription()));
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }


    public static class TourViewHolder extends RecyclerView.ViewHolder {

        OnTourClickListener tourClickListener;
        TextView tourTextView, descriptionTextView;

        public TourViewHolder(@NonNull View itemView, OnTourClickListener tourClickListener) {
            super(itemView);
            tourTextView = itemView.findViewById(R.id.tour_name_tv);
            descriptionTextView = itemView.findViewById(R.id.tour_description_tv);
            this.tourClickListener = tourClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tourClickListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            tourClickListener.onTourClick(pos);
                        }
                    }
                }
            });
        }

    }
}
