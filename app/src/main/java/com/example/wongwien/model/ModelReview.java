package com.example.wongwien.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelReview implements Parcelable {
    String rId,r_collection,r_desc0,r_desc1,r_desc2,r_desc3,r_desc4,
            r_image0,r_image1,r_image2,r_image3,r_image4,r_num,r_point,r_tag,r_timeStamp,r_title,
            r_type,uEmail,uId,uImg,uName;
    int countDisplay;

    public ModelReview() {
        countDisplay=0;
    }

    public ModelReview(String rId, String r_collection, String r_desc0, String r_desc1, String r_desc2, String r_desc3, String r_desc4, String r_image0, String r_image1, String r_image2, String r_image3, String r_image4, String r_num, String r_point, String r_tag, String r_timeStamp, String r_title, String r_type, String uEmail, String uId, String uImg, String uName) {
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
        this.r_point = r_point;
        this.r_tag = r_tag;
        this.r_timeStamp = r_timeStamp;
        this.r_title = r_title;
        this.r_type = r_type;
        this.uEmail = uEmail;
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.countDisplay=0;
    }

    public int getCountDisplay() {
        return countDisplay;
    }

    public void setCountDisplay(int countDisplay) {
        this.countDisplay = countDisplay;
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
        r_point = in.readString();
        r_tag = in.readString();
        r_timeStamp = in.readString();
        r_title = in.readString();
        r_type = in.readString();
        uEmail = in.readString();
        uId = in.readString();
        uImg = in.readString();
        uName = in.readString();
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

    public String getR_point() {
        return r_point;
    }

    public void setR_point(String r_point) {
        this.r_point = r_point;
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

    @Override
    public String toString() {
        return "ModelReview{" +
                "rId='" + rId + '\'' +
                ", r_collection='" + r_collection + '\'' +
                ", r_desc0='" + r_desc0 + '\'' +
                ", r_desc1='" + r_desc1 + '\'' +
                ", r_desc2='" + r_desc2 + '\'' +
                ", r_desc3='" + r_desc3 + '\'' +
                ", r_desc4='" + r_desc4 + '\'' +
                ", r_image0='" + r_image0 + '\'' +
                ", r_image1='" + r_image1 + '\'' +
                ", r_image2='" + r_image2 + '\'' +
                ", r_image3='" + r_image3 + '\'' +
                ", r_image4='" + r_image4 + '\'' +
                ", r_num='" + r_num + '\'' +
                ", r_point='" + r_point + '\'' +
                ", r_tag='" + r_tag + '\'' +
                ", r_timeStamp='" + r_timeStamp + '\'' +
                ", r_title='" + r_title + '\'' +
                ", r_type='" + r_type + '\'' +
                ", uEmail='" + uEmail + '\'' +
                ", uId='" + uId + '\'' +
                ", uImg='" + uImg + '\'' +
                ", uName='" + uName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rId);
        dest.writeString(r_collection);
        dest.writeString(r_desc0);
        dest.writeString(r_desc1);
        dest.writeString(r_desc2);
        dest.writeString(r_desc3);
        dest.writeString(r_desc4);
        dest.writeString(r_image0);
        dest.writeString(r_image1);
        dest.writeString(r_image2);
        dest.writeString(r_image3);
        dest.writeString(r_image4);
        dest.writeString(r_num);
        dest.writeString(r_point);
        dest.writeString(r_tag);
        dest.writeString(r_timeStamp);
        dest.writeString(r_title);
        dest.writeString(r_type);
        dest.writeString(uEmail);
        dest.writeString(uId);
        dest.writeString(uImg);
        dest.writeString(uName);
    }
}
