package org.androidproject.app;

/**
 * Created by soyoung on 2017-05-07.
 */

public class ListViewItem {

    private String titleStr ;
    private String categoryStr;
    private String openDateStr;
    private String expDateStr;
    private String photoStr;
    private String foodNameStr ;
    private String costStr;
    private String areaStr;
    private String stateStr;
    private String user_keyStr;

    public ListViewItem(){

    }

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setCategory(String category) { categoryStr = category;}
    public void setOpenDate(String openDate){ openDateStr = openDate;}
    public void setExpDate(String expDate){ expDateStr = expDate;}
    public void setPhoto(String photo) { photoStr = photo; }
    public void setFoodName(String foodName) { foodNameStr = foodName; }
    public void setCost(String cost){
        costStr = cost;
    }
    public void setArea(String area){
        areaStr = area;
    }
    public void setState(String state) {stateStr = state;}
    public void setUser_key(String user_key) {
        user_keyStr = user_key;
    }



    public String getTitle() {
        return this.titleStr ;
    }
    public String getCategory() {return this.categoryStr;}
    public String getOpenDate(){return this.openDateStr;}
    public String getExpDate(){return this.expDateStr;}
    public String getPhoto() { return this.photoStr; }
    public String getFoodName() {
        return this.foodNameStr ;
    }
    public String getCost() {
        return this.costStr;
    }
    public String getArea() {
        return this.areaStr;
    }
    public String getState(){ return this.stateStr;  }
    public String getUser_key() {
        return user_keyStr;
    }


}
