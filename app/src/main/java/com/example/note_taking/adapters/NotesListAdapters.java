package com.example.note_taking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_taking.NotesClickListenner;
import com.example.note_taking.R;
import com.example.note_taking.models.Notes;

import java.util.List;

public class NotesListAdapters extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    List<Notes> list;
    NotesClickListenner listenner;

    public NotesListAdapters(Context context, List<Notes> list, NotesClickListenner listenner) {
        this.context = context;
        this.list = list;
        this.listenner = listenner;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.title_text.setText(list.get(position).getTitle());
        holder.title_text.setSelected(true);

        holder.note_text.setText(list.get(position).getNote());

        holder.date_text.setText(list.get(position).getDate());
        holder.date_text.setSelected(true);

        holder.notes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenner.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listenner.onLongClick(list.get(holder.getAdapterPosition()), holder.notes_container);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList (List<Notes> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder{

    CardView notes_container;
    TextView title_text, note_text,date_text;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        title_text = itemView.findViewById(R.id.title_text);
        note_text = itemView.findViewById(R.id.note_text);
        date_text = itemView.findViewById(R.id.date_text);
    }
}