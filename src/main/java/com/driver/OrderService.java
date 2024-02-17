
package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
//@Component
public class OrderService {
    OrderRepository repoObj = new OrderRepository();
    public void addOrder(Order order){
        repoObj.addOrder(order);
    }
    public void addPartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        repoObj.addPartner(partner);
    }
    public void addOrderPartnerPair(String orderId, String partnerId){
        HashMap<String, List<String>> orderDeliveryPartnersDb = repoObj.getOrdersOfDeliveryPartnersDb();
        HashMap<String, DeliveryPartner> deliveryPartnerDb = repoObj.getDeliveryPartnerDb();
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, String> orderXPartnerDb = repoObj.getOrderXPartnerDb();
        if(!deliveryPartnerDb.containsKey(partnerId) || !orderDb.containsKey(orderId)){
            return;
        }

        List<String> temp = orderDeliveryPartnersDb.getOrDefault(partnerId, new ArrayList<>());
        temp.add(orderId);
        orderDeliveryPartnersDb.put(partnerId, temp);
        deliveryPartnerDb.get(partnerId).setNumberOfOrders(orderDeliveryPartnersDb.get(partnerId).size());//orderDeliveryPartnerDb's list.size
        orderXPartnerDb.put(orderId, partnerId);
        repoObj.setDeliveryPartnerDb(deliveryPartnerDb);
        repoObj.setOrdersOfDeliveryPartnersDb(orderDeliveryPartnersDb);
        repoObj.setOrderXPartnerDb(orderXPartnerDb);

    }
    public Order getOrderById(String orderId){
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        return orderDb.getOrDefault(orderId, null);
//        return repoObj.getOrderById(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        HashMap<String, DeliveryPartner>  partnerDb = repoObj.getDeliveryPartnerDb();
        return  partnerDb.getOrDefault(partnerId, null);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        HashMap<String, DeliveryPartner> deliveryPartnerDb = repoObj.getDeliveryPartnerDb();
        if(deliveryPartnerDb.containsKey(partnerId)){
            return deliveryPartnerDb.get(partnerId).getNumberOfOrders();
        }
        return 0;
//        if(!deliveryPartnerDb.containsKey(partnerId) || deliveryPartnerDb.get(partnerId) == null){
//            return 0;
//        }
//        return deliveryPartnerDb.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        HashMap<String, List<String>> orderDeliveryPartnerDb = repoObj.getOrdersOfDeliveryPartnersDb();
        return orderDeliveryPartnerDb.getOrDefault(partnerId, null);
    }

    public List<String> getAllOrders(){
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        List<String> orders = new ArrayList<>();
        //List<String> orders = new ArrayList(orderDb.keySet()); // in 1 line, it addes all order string in list
        for(String order : orderDb.keySet()){
            orders.add(order);
        }
//        if(orders.size() == 0) return null;
        return orders;
    }
    public Integer getCountOfUnassignedOrders(){//can have problems for sure
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, String> orderXPartnerDb = repoObj.getOrderXPartnerDb();
//        HashMap<String, List<String>> ordersOfDeliveryPartners = repoObj.getOrdersOfDeliveryPartnersDb();
//        HashSet<String> assignedOrdersAll = new HashSet<>();
//        for(String deliveryPartner : ordersOfDeliveryPartners.keySet()){
//            assignedOrdersAll.addAll(ordersOfDeliveryPartners.get(deliveryPartner));
//        }
//        Integer totalOrders = orderDb.size();
//        Integer ordersHavingDeliveryPartners = assignedOrdersAll.size();
//        Integer unAssignedOrders = totalOrders - ordersHavingDeliveryPartners;
//        return unAssignedOrders;
        return orderDb.size() - orderXPartnerDb.size();
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        //countOfOrders that are left after a particular time of a DeliveryPartner
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, List<String>>  orderPartnerDb = repoObj.getOrdersOfDeliveryPartnersDb();
        if(!orderPartnerDb.containsKey(partnerId) || orderPartnerDb.get(partnerId) == null || time.length() == 0){
            return 0;
        }
        Integer undelivered = 0;
        // convert time from string to integer
        String hrs = time.substring(0, 2);
        String mints = time.substring(3);
        Integer maxTime = Integer.parseInt(hrs)*60 + Integer.parseInt(mints);//converted everything into mints
        List<String> ordersOfAPartnerList = orderPartnerDb.get(partnerId);
        for(String ord : ordersOfAPartnerList){
            if(orderDb.get(ord).getDeliveryTime() > maxTime){
                undelivered++;
            }
        }
        return undelivered;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
//Return the time when that partnerId will deliver his last delivery order.
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, List<String>>  orderPartnerDb = repoObj.getOrdersOfDeliveryPartnersDb();
        List<String> ordersOfAPartnerList = orderPartnerDb.get(partnerId);
        if(!orderPartnerDb.containsKey(partnerId)){
            return null;
        }
        int lastDeliveryTime = Integer.MIN_VALUE;
        for(String ord : ordersOfAPartnerList){
            if(orderDb.get(ord).getDeliveryTime() > lastDeliveryTime){
                lastDeliveryTime = orderDb.get(ord).getDeliveryTime();
            }
        }
        int hr =  lastDeliveryTime / 60 ;
        int mint = lastDeliveryTime % 60;//int m = lastDeliveryTime - (h*60);
        // now convert this time from int to string
        String hours =  String.format("%02d", hr);//String.valueOf(hr);
        String minutes = String.format("%02d", mint);//String.valueOf(mint);
        return (hours + ":" + minutes);
    }

    public void deletePartnerById(String partnerId){
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
//        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, List<String>>  orderPartnerDb = repoObj.getOrdersOfDeliveryPartnersDb();
        HashMap<String, DeliveryPartner>  deliveryPartnerDb = repoObj.getDeliveryPartnerDb();
        HashMap<String, String>  orderXPartnerDb = repoObj.getOrderXPartnerDb();
        if(deliveryPartnerDb.containsKey(partnerId)){
            // remove from partnersDb
            deliveryPartnerDb.remove(partnerId);
        }
        List<String> orders = new ArrayList<>();
        if(orderPartnerDb.containsKey(partnerId)){
            orders = orderPartnerDb.get(partnerId);
            orderPartnerDb.remove(partnerId);
        }
        for(String oId: orders){
            // this order should be removed from order vs partner map
            orderXPartnerDb.remove(oId);
        }
        repoObj.setOrdersOfDeliveryPartnersDb(orderPartnerDb);
        repoObj.setDeliveryPartnerDb(deliveryPartnerDb);
        repoObj.setOrderXPartnerDb(orderXPartnerDb);
    }
    public void deleteOrderById(String orderId){
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        HashMap<String, Order> orderDb = repoObj.getOrderDb();
        HashMap<String, List<String>>  orderPartnerDb = repoObj.getOrdersOfDeliveryPartnersDb();
        HashMap<String, DeliveryPartner>  deliveryPartnerDb = repoObj.getDeliveryPartnerDb();
        HashMap<String, String>  orderXPartnerDb = repoObj.getOrderXPartnerDb();
        if(orderDb.containsKey(orderId)){
            orderDb.remove(orderId);
        }
        if(orderXPartnerDb.containsKey(orderId)){
            String partnerId = orderXPartnerDb.get(orderId);
            orderXPartnerDb.remove(orderId);
            List<String> orders = orderPartnerDb.get(partnerId);
            for(String order : orders){
                if(order.equals(orderId)){
                    orders.remove(orderId);
                    break;
                }
            }
            orderPartnerDb.put(partnerId, orders);
            deliveryPartnerDb.get(partnerId).setNumberOfOrders(orders.size());
        }
        repoObj.setOrdersOfDeliveryPartnersDb(orderPartnerDb);
        repoObj.setDeliveryPartnerDb(deliveryPartnerDb);
        repoObj.setOrderDb(orderDb);
        repoObj.setOrdersOfDeliveryPartnersDb(orderPartnerDb);

//    setNumberOfOrders(deliveryPartnerDb.get(partnerId).size());
    }

}