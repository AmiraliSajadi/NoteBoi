package com.example.noteboi;


public class RecyclerViewModel {

    String title, memo, id;
    boolean fav;

    public RecyclerViewModel(String title, String memo, boolean fav, String id) {
        this.title = title;
        this.memo = memo;
        this.fav = false;
        this.id = id;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
