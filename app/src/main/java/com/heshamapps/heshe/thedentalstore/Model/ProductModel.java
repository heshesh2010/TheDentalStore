package com.heshamapps.heshe.thedentalstore.Model;



import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProductModel implements Serializable, Parcelable {

    private String title , image , desc ,userEmail ,userMobile ,expireDate ,id;
    private int price  ,no_of_items ,currentStock , initStock;
private boolean isExpired;
    public ProductModel(String id , String title, int price, String desc , String image, int no_of_items, String userEmail , String userMobile, String expireDate, int currentStock , int initStock) {
        this.id=id;
        this.title = title;
        this.price=price;
        this.desc=desc;
        this.image = image;
        this.no_of_items=no_of_items;
        this.userEmail=userEmail;
        this.userMobile=userMobile;
        this.expireDate=expireDate;
        this.currentStock=currentStock;
        this.initStock=initStock;

    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getInitStock() {
        return initStock;
    }

    public void setInitStock(int initStock) {
        this.initStock = initStock;
    }

    public ProductModel() {



    }

    public boolean getExpireStatus(){
        String valid_until = getExpireDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (SafeParcelReader.ParseException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().after(strDate)) {

            isExpired = true;
        }
        else{
            isExpired = false;

        }
        return isExpired;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public int getNo_of_items() {
        return no_of_items;
    }

    public String getDesc() {
        return desc;
    }

    public int getPrice() {
        return price;
    }

    public String getExpireDate() {
        return this.expireDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(desc);
        dest.writeString(userEmail);
        dest.writeString(userMobile);
        dest.writeString(id);
        dest.writeInt(no_of_items);
        dest.writeInt(price);
        dest.writeString(expireDate);
        dest.writeInt(currentStock);
        dest.writeInt(initStock);

    }



    public static final Parcelable.Creator<ProductModel> CREATOR
            = new Parcelable.Creator<ProductModel>() {
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    private ProductModel(Parcel in) {
        title = in.readString();
        image = in.readString();
        desc = in.readString();
        userEmail = in.readString();
        userMobile = in.readString();
        id = in.readString();
        no_of_items = in.readInt();
        price = in.readInt();
        expireDate = in.readString();
        currentStock = in.readInt();
        initStock = in.readInt();

    }









}
