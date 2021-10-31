package com.example.wongwien.model;

public class ModelQuestionAns {
    String uId,uImg,uName,tag,question,descrip,collection,timeStamp;

    public ModelQuestionAns() {
    }

    public ModelQuestionAns(String uId, String uImg, String uName, String tag, String question, String descrip, String collection, String timeStamp) {
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.tag = tag;
        this.question = question;
        this.descrip = descrip;
        this.collection = collection;
        this.timeStamp = timeStamp;
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
}
