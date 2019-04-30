package com.heshamapps.heshe.thedentalstore.Doctor;

import android.content.Context;
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
import com.heshamapps.heshe.thedentalstore.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersRecyclerViewAdapter extends
        RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private List<PlacedOrderModel> ordersList;
    private Context context;
    private FirebaseFirestore firestoreDB;

    OrdersRecyclerViewAdapter(List<PlacedOrderModel> list, Context ctx, FirebaseFirestore firestore) {
        ordersList = list;
        context = ctx;
        firestoreDB = firestore;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @NonNull
    @Override
    public OrdersRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_order_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersRecyclerViewAdapter.ViewHolder holder, int position) {
        PlacedOrderModel order = ordersList.get(position);
        holder.setIsRecyclable(false);
        holder.orderId.setText(order.getOrderid());
        holder.payment.setText(order.getPayment_mode());

        holder.numberOfItems.setText(order.getNo_of_items());
        holder.total_amount.setText("" + order.getTotal_amount());
        holder.delivery_date.setText(order.getDelivery_date());
        holder.status.setText(order.getStatus());

        if(order.getStatus().equals("Cancelled")){
            holder.cancel_order.setVisibility(View.INVISIBLE);
        }
        holder.cancel_order.setOnClickListener(view -> {
            cancelOrder(order.getOrderid(), position,holder);            });
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
        Button cancel_order;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    private void cancelOrder(String docId, final int position, ViewHolder holder) {
        firestoreDB.collection("orders").document(docId).update("status", "Cancelled")
                .addOnCompleteListener(task -> {
                    Toast.makeText(context,
                            "order document has been cancelled",
                            Toast.LENGTH_SHORT).show();
                 //   holder.status.setText("Cancelled");
                    holder.cancel_order.setVisibility(View.INVISIBLE);
                    notifyItemChanged(position);                    });
    }
}
