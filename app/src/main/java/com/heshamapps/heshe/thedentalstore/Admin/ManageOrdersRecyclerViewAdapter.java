package com.heshamapps.heshe.thedentalstore.Admin;

import android.content.Context;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.heshamapps.heshe.thedentalstore.util.MyCallback;

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
                updateStock(order);
                sendEmail(order,"Deleted");
                deleteOrder(order, itemPos);

            }
        });

        holder.save_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendEmail(order,holder.status.getSelectedItem().toString());
                saveOrder(order,holder.status.getSelectedItem().toString());
            }
        });

    }

    public void sendEmail(PlacedOrderModel order,String OrderCase) {
        session = new UserSession(context);

        BackgroundMail.newBuilder(context)
                .withUsername("shreen.ods2019@gmail.com")
                .withPassword("$S15#07#1997m$")
                .withMailto(order.getPlaced_user_email())
                .withMailto("shreen.ods2019@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("order "+OrderCase)
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



    private void deleteOrder(PlacedOrderModel orderModel, final int position) {
        firestoreDB.collection("orders").document(orderModel.getOrderid()).delete()
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




    private void saveOrder(PlacedOrderModel orderModel, String status) {

        firestoreDB.collection("orders").document(orderModel.getOrderid()).update( "status", status)
                .addOnCompleteListener(task -> {
                    notifyDataSetChanged();
                    Toast.makeText(context,
                            "order has been Updated",
                            Toast.LENGTH_SHORT).show();
                });
        if(status.equals("Cancelled")){
            updateStock(orderModel);
        }
    }


    private void readData(PlacedOrderModel orderModel, MyCallback myCallback){




        FirebaseFirestore.getInstance().collection("orders").document(orderModel.getOrderid()).collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel productModel = document.toObject(ProductModel.class);
                                myCallback.onCallback(  productModel.getId());
                               // Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });



    }

    private void updateStock(PlacedOrderModel orderModel){





        readData(orderModel,new MyCallback() {

            @Override
            public void onCallback(String productID) {


                // first get current stock then - from it
                final DocumentReference sfDocRef = firestoreDB.collection("products").document(productID);


                firestoreDB.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    int newPopulation = Integer.valueOf(snapshot.get("currentStock").toString());

                    transaction.update(sfDocRef, "currentStock", newPopulation+Integer.parseInt(orderModel.getNo_of_items()));

                    // Success
                    return null;
                }).addOnSuccessListener(aVoid -> Log.d("TAG", "Transaction success!"))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Transaction failure.", e);
                            }
                        });

            }
        });





    }




}
