package com.example.electronicsmarket;

import android.widget.ImageView;

public class DataCategory {


    private String categoryText;
    private int categoryImage;
    private String categorySendText;

    public String getCategorySendText() {
        return categorySendText;
    }

    public void setCategorySendText(String categorySendText) {
        this.categorySendText = categorySendText;
    }

    public DataCategory(String categoryText, int categoryImage, String categorySendText) {
        this.categoryText = categoryText;
        this.categoryImage = categoryImage;
        this.categorySendText = categorySendText;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }

    public DataCategory(){

    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }


}
