package com.driver;

import io.swagger.models.auth.In;

import java.util.*;

@org.springframework.stereotype.Repository
public class OrderRepository {

    Map<String,Order> orderMap = new HashMap<>();
    Map<String, List<String>> partnerMap = new HashMap<>();

    public void addOrder(Order order) {
        orderMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId) {
        partnerMap.put(partnerId,new ArrayList<>());
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        partnerMap.get(partnerId).add(orderId);
    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        int numberOfOrders = partnerMap.get(partnerId).size();
        deliveryPartner.setNumberOfOrders(numberOfOrders);
        return deliveryPartner;
    }

    public Integer getOrderCount(String partnerId) {
        int numberOfOrders= partnerMap.get(partnerId).size();
        return numberOfOrders;
    }

    public List<String> getOrders(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public Integer unassignedOrders() {
        Integer number = 0;
        Collection<List<String>> assignedOrders = partnerMap.values();
        for(String orderId: orderMap.keySet()) if(!assignedOrders.contains(orderId))  number++;
        return number;
    }

    public Integer getOrdersLeft(String time, String partnerId) {
        Integer number = 0;
        List<String> orders = partnerMap.get(partnerId);
        for(String order : orders){
            int deliveryTime = orderMap.get(order).getDeliveryTime();
            int hour = ((int) time.charAt(0) * 10 + (int) time.charAt(1)) * 60;
            int timeInteger = hour + ((int)time.charAt(3)*10 + (int)time.charAt(4));
            if(timeInteger<deliveryTime) number++;
        }
        return number;
    }

    public String getLastDeliver(String partnerId) {
        int deliverTime = 0;
        List<String> orders = partnerMap.get(partnerId);
        for(String order : orders) {
            int time = orderMap.get(order).getDeliveryTime();
            deliverTime= Math.max(time,deliverTime);
        }
        int hours = deliverTime/60;
        int minutes = deliverTime%60;
        String time = "";
        if(hours<10) time = 0+""+hours+":"+minutes;
        else time = hours+":"+minutes;
        return time;
    }

    public void deletePartner(String partnerId) {
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId) {
        for(List<String> orders : new ArrayList<>(partnerMap.values())){
            if(orders.contains(orderId)) orders.remove(orderId);
        }
        orderMap.remove(orderId);
    }
}