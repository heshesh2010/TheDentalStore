package com.heshamapps.heshe.thedentalstore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersRecyclerViewAdapter extends
        RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private List<PlacedOrderModel> ordersList;
    private Context context;
    private FirebaseFirestore firestoreDB;

    public OrdersRecyclerViewAdapter(List<PlacedOrderModel> list, Context ctx, FirebaseFirestore firestore) {
        ordersList = list;
        context = ctx;
        firestoreDB = firestore;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public OrdersRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event_item, parent, false);

        OrdersRecyclerViewAdapter.ViewHolder viewHolder =
                new OrdersRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPos = position;
        final PlacedOrderModel order = ordersList.get(position);

        holder.orderId.setText(order.getOrderid());
        holder.payment.setText(order.getPayment_mode());

        holder.numberOfItems.setText(order.getNo_of_items());
        holder.total_amount.setText("" + order.getTotal_amount());
        holder.delivery_date.setText(order.getDelivery_date());
        holder.status.setText(order.getStatus());

        holder.edit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   editOrderFragment(order);
            }
        });

        holder.delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOrder(order.getOrderid(), itemPos);
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

        @BindView(R.id.edit_order_b)
        Button edit_order;

        @BindView(R.id.delete_order_b)
        Button delete_order;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

  /*  private void editOrderFragment(PlacedOrderModel order) {
        FragmentManager fm = ((OrderActivity) context).getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);

        AddOrderFragment addFragment = new AddEventFragment();
        addFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.events_content, addFragment).commit();
    }*/

    private void deleteOrder(String docId, final int position) {
        firestoreDB.collection("orders").document(docId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ordersList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, ordersList.size());
                        Toast.makeText(context,
                                "order document has been deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
