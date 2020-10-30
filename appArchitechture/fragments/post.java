package com.koshurTech.tadaa.fragments;

public class post implements Comparable<post> {
    private String userTask;
    private String timeStamp;

    private boolean isempty=true;

    public boolean isIsempty() {
        return isempty;
    }

    public void setIsempty(boolean isempty) {
        this.isempty = isempty;
    }

    private post(){

    }

    private post(String task, String ts){
        this.userTask = task;
        this.timeStamp = ts;
        isempty = false;
    }


    public String getUserTask() {
        return userTask;
    }

    public void setUserTask(String userTask) {
        this.userTask = userTask;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(post post) {
        return 0;
    }
}
