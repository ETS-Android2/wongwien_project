package com.example.wongwien.model;

public class ModelComment {
    String cId,cName,cImage,cUid,comment,timeStamp;

    public ModelComment() {
    }

    public ModelComment(String cId, String cName, String cImage, String cUid, String comment, String timeStamp) {
        this.cId = cId;
        this.cName = cName;
        this.cImage = cImage;
        this.cUid = cUid;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcImage() {
        return cImage;
    }

    public void setcImage(String cImage) {
        this.cImage = cImage;
    }

    public String getcUid() {
        return cUid;
    }

    public void setcUid(String cUid) {
        this.cUid = cUid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
