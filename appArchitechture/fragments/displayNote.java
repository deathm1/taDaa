package com.koshurTech.tadaa.fragments;

public class displayNote {
    private String userNote;
    private String timeStamp;

    private boolean isempty=true;

    public boolean isIsempty() {
        return isempty;
    }

    public void setIsempty(boolean isempty) {
        this.isempty = isempty;
    }

    private displayNote(){

    }

    private displayNote(String note, String ts){
        this.userNote = note;
        this.timeStamp = ts;
        isempty = false;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
