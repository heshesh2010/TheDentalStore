package com.heshamapps.heshe.thedentalstore.Doctor.DentalStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDentalStoreRecyclerViewAdapter extends
        RecyclerView.Adapter<MyDentalStoreRecyclerViewAdapter.ViewHolder> {

    private List<PlacedOrderModel> ordersList;
    private FragmentActivity activity;
    private FirebaseFirestore firestoreDB;

    public MyDentalStoreRecyclerViewAdapter(List<PlacedOrderModel> list, FragmentActivity activity, FirebaseFirestore firestore) {
        this.ordersList = list;
        this.activity = activity;
        this.firestoreDB = firestore;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public MyDentalStoreRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_order_item, parent, false);

        MyDentalStoreRecyclerViewAdapter.ViewHolder viewHolder =
                new MyDentalStoreRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyDentalStoreRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPos = position;
        final PlacedOrderModel order = ordersList.get(position);

        holder.orderId.setText(order.getOrderid());
        holder.payment.setText(order.getPayment_mode());

        holder.numberOfItems.setText(order.getNo_of_items());
        holder.total_amount.setText("" + order.getTotal_amount());
        holder.delivery_date.setText(order.getDelivery_date());
        holder.status.setText(order.getStatus());

        holder.view_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOrder(order.getOrderid(), itemPos);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orderId_ed)
        TextView orderId;

        @BindView(R.id.payment_ed)
        TextView payment;

        @BindView(R.id.numberOfItems_ed)
        TextView numberOfItems;

        @BindView(R.id.total_amount_ed)
        TextView total_amount;

        @BindView(R.id.delivery_date_ed)
        TextView delivery_date;

        @BindView(R.id.status_ed)
        TextView status;

        @BindView(R.id.cancel_order_b)
        Button view_order;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    private void viewOrder(String docId, final int position) {

        firestoreDB.collection("orders").document(docId).collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        ArrayList<ProductModel> list = new ArrayList<>();
                                                                        for (DocumentSnapshot  document : task.getResult()) {
                                                                            list.add(  document.toObject(ProductModel.class));
                                                                        }
                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putParcelableArrayList("productsOfOrder", list);

                                                                        ProductsOfOrdersFragment m_ProductsOfOrdersFragment = new ProductsOfOrdersFragment();
                                                                        m_ProductsOfOrdersFragment.setArguments(bundle);

                                                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  m_ProductsOfOrdersFragment).commit();



                                                                    } else {
                                                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                                                    }
                                                                }
                                                            }
        );

    }
}
