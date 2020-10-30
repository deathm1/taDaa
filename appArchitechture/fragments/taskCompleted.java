package com.koshurTech.tadaa.fragments;

public class taskCompleted{
    private String completionTimeStamp;
    private String taskAge;
    private String userTaskCompleted;
    private String timeStamp;


    private taskCompleted(){

    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private taskCompleted(String taskC, String cts, String ta, String timeStamp){
        this.userTaskCompleted = taskC;
        this.completionTimeStamp = cts;
        this.taskAge = ta;

        this.timeStamp = timeStamp;
    }

    public String getCompletionTimeStamp() {
        return completionTimeStamp;
    }

    public void setCompletionTimeStamp(String completionTimeStamp) {
        this.completionTimeStamp = completionTimeStamp;
    }

    public String getTaskAge() {
        return taskAge;
    }

    public void setTaskAge(String taskAge) {
        this.taskAge = taskAge;
    }

    public String getUserTaskCompleted() {
        return userTaskCompleted;
    }

    public void setUserTaskCompleted(String userTaskCompleted) {
        this.userTaskCompleted = userTaskCompleted;
    }
}
