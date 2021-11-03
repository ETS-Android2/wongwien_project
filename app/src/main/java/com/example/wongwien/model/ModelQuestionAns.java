package com.example.wongwien.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelQuestionAns implements Parcelable {
    String uId;
    String uImg;
    String uName;
    String uEmail;
    String tag;
    String question;
    String descrip;
    String collection;
    String timeStamp;

    public ModelQuestionAns(String uId, String uImg, String uName, String uEmail, String tag, String question, String descrip, String collection, String timeStamp, String qId) {
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.uEmail = uEmail;
        this.tag = tag;
        this.question = question;
        this.descrip = descrip;
        this.collection = collection;
        this.timeStamp = timeStamp;
        this.qId = qId;
    }

    protected ModelQuestionAns(Parcel in) {
        uId = in.readString();
        uImg = in.readString();
        uName = in.readString();
        uEmail = in.readString();
        tag = in.readString();
        question = in.readString();
        descrip = in.readString();
        collection = in.readString();
        timeStamp = in.readString();
        qId = in.readString();
    }

    public static final Creator<ModelQuestionAns> CREATOR = new Creator<ModelQuestionAns>() {
        @Override
        public ModelQuestionAns createFromParcel(Parcel in) {
            return new ModelQuestionAns(in);
        }

        @Override
        public ModelQuestionAns[] newArray(int size) {
            return new ModelQuestionAns[size];
        }
    };

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    String qId;

    public ModelQuestionAns() {
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "ModelQuestionAns{" +
                "uId='" + uId + '\'' +
                ", uImg='" + uImg + '\'' +
                ", uName='" + uName + '\'' +
                ", uEmail='" + uEmail + '\'' +
                ", tag='" + tag + '\'' +
                ", question='" + question + '\'' +
                ", descrip='" + descrip + '\'' +
                ", collection='" + collection + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", qId='" + qId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uId);
        dest.writeString(uImg);
        dest.writeString(uName);
        dest.writeString(uEmail);
        dest.writeString(tag);
        dest.writeString(question);
        dest.writeString(descrip);
        dest.writeString(collection);
        dest.writeString(timeStamp);
        dest.writeString(qId);
    }
}
