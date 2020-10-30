package com.koshurTech.tadaa.fragments;

public class myListView{
    private String listName;


    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    private myListView(){

    }



    private myListView(String listName){
        this.listName = listName;
    }


}
