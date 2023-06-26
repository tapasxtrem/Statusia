package com.example.statusia.classes;

public class WhatsappSpinnerItem {
    String ItemName;
    int drawableImage;

    public WhatsappSpinnerItem(String itemName, int drawableImage) {
        ItemName = itemName;
        this.drawableImage = drawableImage;
    }

    public String getItemName() {
        return ItemName;
    }

    public int getDrawableImage() {
        return drawableImage;
    }
}
