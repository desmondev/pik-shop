package pl.elka.pw.pik.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.elka.pw.pik.shop.domain.model.orders.Order;
import pl.elka.pw.pik.shop.dto.DeliveryFormTypeData;
import pl.elka.pw.pik.shop.dto.OrderData;
import pl.elka.pw.pik.shop.dto.OrderItemData;
import pl.elka.pw.pik.shop.dto.PaymentTypeData;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
// TODO: Klasa jest po to aby ukryć obsługę ciastek, koszyka zalogowanego itp. przed logiką z klasy OrderService. Teraz jest tylko obsługa ciastek.
// TODO: Jak już będzie uwierzytelnianie trzeba sprawdzić czy jest zalogowany użytkownik i ewentualnie u niego szukać order-a.
public class CartService {
    private OrderService orderService;

    @Autowired
    public CartService(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderData findOrder(Long orderId) {
        Optional<Order> order = findCurrentOrder(orderId);
        if (order.isPresent() && order.get().isNotConfirmed())
            return new OrderData(order.get());
        return new OrderData();
    }

    public OrderData deleteItem(Long orderItemId, Long orderId) {
        Optional<Order> order = findCurrentOrder(orderId);
        if (order.isPresent()) {
            orderService.deleteItem(orderItemId, order.get());
        }
        return new OrderData(order.get());
    }

    public OrderData updateOrder(Long orderId, OrderData orderData) {
        if (orderData.getId().equals(orderId))
            return orderService.updateOrder(orderData);
        else
            throw new RuntimeException("Unable to modify order with id: " + orderData.getId());
    }

    public void setStatusConfirmed(Long orderId) {
        orderService.setStatusConfirmed(orderId);
    }

    public void addItem(OrderItemData orderItemData, Long orderId, HttpServletResponse response) {
        Optional<Order> orderOpt = findCurrentOrder(orderId);
        Order order = new Order(new Date());
        if (orderOpt.isPresent() && orderOpt.get().isNotConfirmed())
            order = orderOpt.get();
        order = orderService.addItem(order, orderItemData);
        saveOrderIdInCookies(order, response);
    }

    public List<DeliveryFormTypeData> getDeliveryFormTypes() {
        return orderService.getDeliveryFormTypes();
    }

    public List<PaymentTypeData> getPaymentTypes() {
        return orderService.getPaymentTypes();
    }

    private Optional<Order> findCurrentOrder(Long orderId) {
        return orderService.findOrder(orderId);
    }

    private void saveOrderIdInCookies(Order order, HttpServletResponse response) {
        response.addCookie(new Cookie("orderId", String.valueOf(order.getId())));
    }
}
