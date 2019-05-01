package com.heshamapps.heshe.thedentalstore.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heshamapps.heshe.thedentalstore.usersession.UserSession.KEY_EMAIL;

public class ManageOrdersRecyclerViewAdapter extends
        RecyclerView.Adapter<ManageOrdersRecyclerViewAdapter.ViewHolder> {

    private List<PlacedOrderModel> ordersList;
    private Context context;
    private FirebaseFirestore firestoreDB;
    int SpinnerPos=0;
    UserSession session;

    public ManageOrdersRecyclerViewAdapter(List<PlacedOrderModel> list, Context ctx, FirebaseFirestore firestore) {
        ordersList = list;
        context = ctx;
        firestoreDB = firestore;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public ManageOrdersRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_order_item, parent, false);

        ManageOrdersRecyclerViewAdapter.ViewHolder viewHolder =
                new ManageOrdersRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ManageOrdersRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPos = position;
        final PlacedOrderModel order = ordersList.get(position);

        switch (order.getStatus()){
            case "In Progress":
                SpinnerPos=0;
                    break;
            case "Delivered":
                SpinnerPos=1;
                break;
            case "Cancelled":
                SpinnerPos=2;
                break;
        }
        holder.orderId.setText(order.getOrderid());
        holder.payment.setText(order.getPayment_mode());
        holder.numberOfItems.setText(order.getNo_of_items());
        holder.total_amount.setText("" + order.getTotal_amount());
        holder.delivery_date.setText(order.getDelivery_date());
        holder.status.setSelection(SpinnerPos);


        holder.delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(order,"Deleted");

                deleteOrder(order.getOrderid(), itemPos);
            }
        });

        holder.save_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendEmail(order,holder.status.getSelectedItem().toString());
                saveOrder(order.getOrderid(), itemPos,holder.status.getSelectedItem().toString());
            }
        });

    }

    public void sendEmail(PlacedOrderModel order,String OrderCase) {
        session = new UserSession(context);

        BackgroundMail.newBuilder(context)
                .withUsername("shreen.ods2019@gmail.com")
                .withPassword("$S15#07#1997m$")
                .withMailto(order.getPlaced_user_email())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("order placed")
                .withBody("your order id = " + order.getOrderid() +" is " + OrderCase)
                .withOnSuccessCallback(() -> {
                    //do some magic
                })
                .withOnFailCallback(() -> {
                    //do some magic
                })
                .send();

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
        Spinner status;

        @BindView(R.id.save_order_b)
        Button save_order;

        @BindView(R.id.delete_order_b)
        Button delete_order;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }



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




    private void saveOrder(String docId, final int position,String status) {
        firestoreDB.collection("orders").document(docId).update( "status", status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyDataSetChanged();
                        Toast.makeText(context,
                                "order document has been Updated",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
