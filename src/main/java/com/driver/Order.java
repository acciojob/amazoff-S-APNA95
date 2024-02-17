package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        int hour =  ((int)deliveryTime.charAt(0) * 10 + (int)deliveryTime.charAt(1)) * 60 ;
        this.deliveryTime = hour + ((int)deliveryTime.charAt(3)*10 + (int)deliveryTime.charAt(4));

    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
