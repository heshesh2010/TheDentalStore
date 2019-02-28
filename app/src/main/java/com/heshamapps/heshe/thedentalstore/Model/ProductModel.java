package com.heshamapps.heshe.thedentalstore.Model;



import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class ProductModel implements Serializable, Parcelable {

    private String title , image , desc ,userEmail,userMobile;
    private int id ,price  ,no_of_items;

    public ProductModel(int id ,String title, int price, String desc ,String image,int no_of_items, String userEmail , String userMobile) {
        this.id=id;
        this.title = title;
        this.price=price;
        this.desc=desc;
        this.image = image;
        this.no_of_items=no_of_items;
        this.userEmail=userEmail;
        this.userMobile=userMobile;

    }
    public ProductModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setImage(String image) {
        this.image = image;
    }

    public int getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(int no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

   /* public boolean isLike_status() {
        return like_status;
    }*/

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
        dest.writeInt(id);
        dest.writeInt(no_of_items);
        dest.writeInt(id);
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
        id = in.readInt();
        no_of_items = in.readInt();
        id = in.readInt();
    }

}
