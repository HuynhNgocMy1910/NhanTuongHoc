package com.my.kltn;

public class HistoryItem {
    private String result;
    private String date;
    private int imageRes;

    public HistoryItem(String result, String date, int imageRes) {
        this.result = result;
        this.date = date;
        this.imageRes = imageRes;
    }

    // Getter methods
    public String getResult() { return result; }
    public String getDate() { return date; }
    public int getImageRes() { return imageRes; }
}