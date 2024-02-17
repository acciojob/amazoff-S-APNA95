package com.driver;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public void addPartner(String partnerId) {
        orderRepository.addPartner(partnerId);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        orderRepository.addOrderPartnerPair(orderId,partnerId);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return orderRepository.getPartnerById(partnerId);
    }

    public Integer getOrderCount(String partnerId) {
        return orderRepository.getOrderCount(partnerId);
    }

    public List<String> getOrders(String partnerId) {
        return orderRepository.getOrders(partnerId);
    }

    public List<String> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Integer unassignedOrder() {
        return orderRepository.unassignedOrders();
    }

    public Integer getOrdersLef(String time, String partnerId) {
        return orderRepository.getOrdersLeft(time,partnerId);
    }

    public String getLastDeliver(String partnerId) {
        return orderRepository.getLastDeliver(partnerId);
    }

    public void deletePartner(String partnerId) {
        orderRepository.deletePartner(partnerId);
    }

    public void deleteOrder(String orderId) {
        orderRepository.deleteOrder(orderId);
    }
}