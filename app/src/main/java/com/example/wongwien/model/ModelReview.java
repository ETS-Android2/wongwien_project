package com.example.wongwien.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelReview implements Parcelable {
    String rId, r_collection, r_desc0, r_desc1, r_desc2, r_desc3, r_desc4,
            r_image0, r_image1, r_image2, r_image3, r_image4, r_num, r_tag, r_timeStamp, r_title,
            r_type, uEmail, uId, uImg, uName;
    int r_point;

    public ModelReview() {
    }

    protected ModelReview(Parcel in) {
        rId = in.readString();
        r_collection = in.readString();
        r_desc0 = in.readString();
        r_desc1 = in.readString();
        r_desc2 = in.readString();
        r_desc3 = in.readString();
        r_desc4 = in.readString();
        r_image0 = in.readString();
        r_image1 = in.readString();
        r_image2 = in.readString();
        r_image3 = in.readString();
        r_image4 = in.readString();
        r_num = in.readString();
        r_tag = in.readString();
        r_timeStamp = in.readString();
        r_title = in.readString();
        r_type = in.readString();
        uEmail = in.readString();
        uId = in.readString();
        uImg = in.readString();
        uName = in.readString();
        r_point = in.readInt();
    }

    public static final Creator<ModelReview> CREATOR = new Creator<ModelReview>() {
        @Override
        public ModelReview createFromParcel(Parcel in) {
            return new ModelReview(in);
        }

        @Override
        public ModelReview[] newArray(int size) {
            return new ModelReview[size];
        }
    };

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getR_collection() {
        return r_collection;
    }

    public void setR_collection(String r_collection) {
        this.r_collection = r_collection;
    }

    public String getR_desc0() {
        return r_desc0;
    }

    public void setR_desc0(String r_desc0) {
        this.r_desc0 = r_desc0;
    }

    public String getR_desc1() {
        return r_desc1;
    }

    public void setR_desc1(String r_desc1) {
        this.r_desc1 = r_desc1;
    }

    public String getR_desc2() {
        return r_desc2;
    }

    public void setR_desc2(String r_desc2) {
        this.r_desc2 = r_desc2;
    }

    public String getR_desc3() {
        return r_desc3;
    }

    public void setR_desc3(String r_desc3) {
        this.r_desc3 = r_desc3;
    }

    public String getR_desc4() {
        return r_desc4;
    }

    public void setR_desc4(String r_desc4) {
        this.r_desc4 = r_desc4;
    }

    public String getR_image0() {
        return r_image0;
    }

    public void setR_image0(String r_image0) {
        this.r_image0 = r_image0;
    }

    public String getR_image1() {
        return r_image1;
    }

    public void setR_image1(String r_image1) {
        this.r_image1 = r_image1;
    }

    public String getR_image2() {
        return r_image2;
    }

    public void setR_image2(String r_image2) {
        this.r_image2 = r_image2;
    }

    public String getR_image3() {
        return r_image3;
    }

    public void setR_image3(String r_image3) {
        this.r_image3 = r_image3;
    }

    public String getR_image4() {
        return r_image4;
    }

    public void setR_image4(String r_image4) {
        this.r_image4 = r_image4;
    }

    public String getR_num() {
        return r_num;
    }

    public void setR_num(String r_num) {
        this.r_num = r_num;
    }

    public String getR_tag() {
        return r_tag;
    }

    public void setR_tag(String r_tag) {
        this.r_tag = r_tag;
    }

    public String getR_timeStamp() {
        return r_timeStamp;
    }

    public void setR_timeStamp(String r_timeStamp) {
        this.r_timeStamp = r_timeStamp;
    }

    public String getR_title() {
        return r_title;
    }

    public void setR_title(String r_title) {
        this.r_title = r_title;
    }

    public String getR_type() {
        return r_type;
    }

    public void setR_type(String r_type) {
        this.r_type = r_type;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuImg() {
        return uImg;
    }

    public void setuImg(String uImg) {
        this.uImg = uImg;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public int getR_point() {
        return r_point;
    }

    public void setR_point(int r_point) {
        this.r_point = r_point;
    }

    public ModelReview(String rId, String r_collection, String r_desc0, String r_desc1, String r_desc2, String r_desc3, String r_desc4, String r_image0, String r_image1, String r_image2, String r_image3, String r_image4, String r_num, String r_tag, String r_timeStamp, String r_title, String r_type, String uEmail, String uId, String uImg, String uName, int r_point) {
        this.rId = rId;
        this.r_collection = r_collection;
        this.r_desc0 = r_desc0;
        this.r_desc1 = r_desc1;
        this.r_desc2 = r_desc2;
        this.r_desc3 = r_desc3;
        this.r_desc4 = r_desc4;
        this.r_image0 = r_image0;
        this.r_image1 = r_image1;
        this.r_image2 = r_image2;
        this.r_image3 = r_image3;
        this.r_image4 = r_image4;
        this.r_num = r_num;
        this.r_tag = r_tag;
        this.r_timeStamp = r_timeStamp;
        this.r_title = r_title;
        this.r_type = r_type;
        this.uEmail = uEmail;
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.r_point = r_point;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rId);
        parcel.writeString(r_collection);
        parcel.writeString(r_desc0);
        parcel.writeString(r_desc1);
        parcel.writeString(r_desc2);
        parcel.writeString(r_desc3);
        parcel.writeString(r_desc4);
        parcel.writeString(r_image0);
        parcel.writeString(r_image1);
        parcel.writeString(r_image2);
        parcel.writeString(r_image3);
        parcel.writeString(r_image4);
        parcel.writeString(r_num);
        parcel.writeString(r_tag);
        parcel.writeString(r_timeStamp);
        parcel.writeString(r_title);
        parcel.writeString(r_type);
        parcel.writeString(uEmail);
        parcel.writeString(uId);
        parcel.writeString(uImg);
        parcel.writeString(uName);
        parcel.writeInt(r_point);
    }
}

