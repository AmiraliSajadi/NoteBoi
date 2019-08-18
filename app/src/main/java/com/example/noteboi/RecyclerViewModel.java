package com.example.noteboi;


public class RecyclerViewModel {

    String title, memo, id;

    public RecyclerViewModel(String title, String memo, String id) {
        this.title = title;
        this.memo = memo;
        this.id = id;
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
