package com.example.fyptest.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.database.faqClass;
import com.example.fyptest.database.productClass;

import java.util.List;

public class FAQ_Adapter extends RecyclerView.Adapter<FAQ_Adapter.ImageViewHolder> {
    Context context;
    List<faqClass> infoList;

    public FAQ_Adapter(Context context, List<faqClass> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.help_centre_listing, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FAQ_Adapter.ImageViewHolder imageViewHolder, int i) {
        faqClass uploadCurrent = infoList.get(i);
        imageViewHolder.textViewAnswer.setText(uploadCurrent.getFaq_Answer());
        imageViewHolder.textViewQuestion.setText(uploadCurrent.getFaq_Question());
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQuestion, textViewAnswer;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = (TextView) itemView.findViewById(R.id.textViewQuestion);
            textViewAnswer = (TextView) itemView.findViewById(R.id.textViewAnswer);
        }
    }
}
