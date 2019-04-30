package com.heshamapps.heshe.thedentalstore.Doctor.DentalStore;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.util.Receiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class OrdersDetailsRecyclerViewAdapter extends
        RecyclerView.Adapter<OrdersDetailsRecyclerViewAdapter.ViewHolder> {

    private List<ProductModel> productsOfOrder;
    private Context context;
    private final Calendar currentDate = Calendar.getInstance();

    OrdersDetailsRecyclerViewAdapter(List<ProductModel> productsOfOrder, Context ctx) {
        this.productsOfOrder = productsOfOrder;
        this.context = ctx;
    }

    @Override
    public int getItemCount() {
        return  this.productsOfOrder.size();
    }

    @NonNull
    @Override
    public OrdersDetailsRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout. products_of_order_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrdersDetailsRecyclerViewAdapter.ViewHolder productHolder, int position) {
         ProductModel productModel = productsOfOrder.get(position);
        productHolder.setIsRecyclable(false);

        productHolder.productExpire.setText(String.valueOf(productModel.getExpireDate()));
        productHolder.textTitle.setText(productModel.getTitle());
        Glide.with(context)
                .load(productModel.getImage())
                .into(productHolder.imageView);


        productHolder.datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDateTimePicker(productHolder.datePicker);
            }
        });



      /*  holder.cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder(order.getOrderid(), itemPos);            }
        });*/
    }



    Calendar date;
    public void showDateTimePicker(EditText datePicker) {

        date = Calendar.getInstance();
        new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                       Log.v("TAG", "The choosen one " + date.getTime());
                        updateLabel( datePicker, date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void updateLabel(EditText datePicker, Date time) {


        String myFormat = "MM dd, yyyy hh:mm:ss a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        datePicker.setText(sdf.format(time));


        AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter("ALARM_ACTION");
        context.registerReceiver(receiver, filter);

        Intent intent = new Intent("ALARM_ACTION");
        intent.putExtra("param", "My scheduled action");
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, 0);
        // I choose 3s after the launch of my application
        alarms.set(AlarmManager.RTC_WAKEUP, time.getTime(), operation) ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.productExpire)
        TextView productExpire;
        @BindView(R.id.thumbnail)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textTitle;

        @BindView(R.id.datePicker)
        EditText datePicker;



        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }





  /*  private void cancelOrder(String docId, final int position) {
        firestoreDB.collection("orders").document(docId).update("status", "Cancelled")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        productsOfOrder.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, productsOfOrder.size());
                        Toast.makeText(context,
                                "order document has been deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
}
