package com.heshamapps.heshe.thedentalstore.view;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.heshamapps.heshe.thedentalstore.inteface.ItemClickListener;

public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;

    public ProductHolder(View itemView) {
        super(itemView);

      //  img= (ImageView) itemView.findViewById(R.id.movieImage);
    //    nameTxt= (TextView) itemView.findViewById(R.id.nameTxt);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(getLayoutPosition());
    }

}
