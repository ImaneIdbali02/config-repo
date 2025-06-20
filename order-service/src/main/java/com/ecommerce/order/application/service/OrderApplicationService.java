package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.CreateOrderRequest;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.entity.OrderLine;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import com.ecommerce.order.domain.service.OrderCreationService;
import com.ecommerce.order.domain.service.OrderLifecycleService;
import com.ecommerce.order.domain.service.OrderHistoryService;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.infrastructure.exception.OrderNotFoundException;
import com.ecommerce.order.infrastructure.exception.UnauthorizedAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderCreationService orderCreationService;
    private final OrderLifecycleService orderLifecycleService;
    private final OrderHistoryService orderHistoryService;

    public OrderApplicationService(OrderRepository orderRepository,
                                 OrderCreationService orderCreationService,
                                 OrderLifecycleService orderLifecycleService,
                                 OrderHistoryService orderHistoryService) {
        this.orderRepository = orderRepository;
        this.orderCreationService = orderCreationService;
        this.orderLifecycleService = orderLifecycleService;
        this.orderHistoryService = orderHistoryService;
    }

    /**
     * Crée une nouvelle commande
     */
    public OrderResponse createOrder(CreateOrderRequest request, String currentUser) {
        // Convertir les DTOs en objets du domaine
        List<OrderLine> orderLines = request.getOrderLines().stream()
                .map(dto -> new OrderLine(dto.getProductId(), dto.getProductName(), 
                                        dto.getProductSku(), dto.getQuantity(), dto.getUnitPrice()))
                .collect(Collectors.toList());

        // Créer la commande
        Order order = orderCreationService.createOrder(
                request.getCustomerId(),
                request.getDeliveryAddress().toDomainObject(),
                request.getBillingAddress().toDomainObject(),
                orderLines
        );

        if (request.getCustomerNotes() != null && !request.getCustomerNotes().isEmpty()) {
            order.setCustomerNotes(request.getCustomerNotes());
            orderRepository.save(order);
        }

        return convertToResponse(order);
    }

    /**
     * Récupère une commande par ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Vérifier les permissions (simplifiée pour l'exemple)
        // Dans un vrai système, il faudrait vérifier si l'utilisateur peut accéder à cette commande
        
        return convertToResponse(order);
    }

    /**
     * Récupère une commande par numéro
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber, String currentUser) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber, true));

        return convertToResponse(order);
    }

    /**
     * Récupère les commandes d'un client
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomer(Long customerId, Pageable pageable, String currentUser) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    /**
     * Recherche des commandes avec filtres
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(Long customerId, OrderStatus status, 
                                          Pageable pageable, String currentUser) {
        List<Order> orders;
        
        if (customerId != null && status != null) {
            orders = orderRepository.findByCustomerIdAndStatus(customerId, status);
        } else if (customerId != null) {
            orders = orderRepository.findByCustomerId(customerId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            // Pour l'exemple, on limite la recherche sans filtre
            orders = orderRepository.findByStatus(OrderStatus.PENDING);
        }

        List<OrderResponse> responses = orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    /**
     * Met à jour le statut d'une commande
     */
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request, String currentUser) {
        Order order = orderLifecycleService.progressOrder(
                id, request.getNewStatus(), request.getReason(), currentUser);
        
        return convertToResponse(order);
    }

    /**
     * Confirme une commande
     */
    public OrderResponse confirmOrder(Long id, String currentUser) {
        Order order = orderLifecycleService.confirmOrder(id, currentUser);
        return convertToResponse(order);
    }

    /**
     * Annule une commande
     */
    public OrderResponse cancelOrder(Long id, String reason, String currentUser) {
        Order order = orderLifecycleService.cancelOrder(id, reason, currentUser);
        return convertToResponse(order);
    }

    /**
     * Récupère l'historique d'une commande
     */
    @Transactional(readOnly = true)
    public List<OrderResponse.OrderModificationHistoryResponse> getOrderHistory(Long id, String currentUser) {
        return orderHistoryService.getOrderHistory(id).stream()
                .map(this::convertHistoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un Order en OrderResponse
     */
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setConfirmedAt(order.getConfirmedAt());
        response.setShippedAt(order.getShippedAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCustomerNotes(order.getCustomerNotes());
        response.setInternalNotes(order.getInternalNotes());

        // Convertir l'adresse de livraison
        OrderResponse.DeliveryAddressResponse deliveryResponse = new OrderResponse.DeliveryAddressResponse();
        deliveryResponse.setRecipientName(order.getDeliveryAddress().getRecipientName());
        deliveryResponse.setStreetAddress(order.getDeliveryAddress().getStreetAddress());
        deliveryResponse.setCity(order.getDeliveryAddress().getCity());
        deliveryResponse.setPostalCode(order.getDeliveryAddress().getPostalCode());
        deliveryResponse.setCountry(order.getDeliveryAddress().getCountry());
        deliveryResponse.setAdditionalInstructions(order.getDeliveryAddress().getAdditionalInstructions());
        response.setDeliveryAddress(deliveryResponse);

        // Convertir l'adresse de facturation
        OrderResponse.BillingAddressResponse billingResponse = new OrderResponse.BillingAddressResponse();
        billingResponse.setBillingName(order.getBillingAddress().getBillingName());
        billingResponse.setStreetAddress(order.getBillingAddress().getStreetAddress());
        billingResponse.setCity(order.getBillingAddress().getCity());
        billingResponse.setPostalCode(order.getBillingAddress().getPostalCode());
        billingResponse.setCountry(order.getBillingAddress().getCountry());
        billingResponse.setCompanyName(order.getBillingAddress().getCompanyName());
        billingResponse.setVatNumber(order.getBillingAddress().getVatNumber());
        response.setBillingAddress(billingResponse);

        // Convertir le résumé de paiement
        OrderResponse.PaymentSummaryResponse paymentResponse = new OrderResponse.PaymentSummaryResponse();
        paymentResponse.setTotalAmount(order.getPaymentSummary().getTotalAmount());
        paymentResponse.setTaxAmount(order.getPaymentSummary().getTaxAmount());
        paymentResponse.setShippingAmount(order.getPaymentSummary().getShippingAmount());
        paymentResponse.setDiscountAmount(order.getPaymentSummary().getDiscountAmount());
        paymentResponse.setNetAmount(order.getPaymentSummary().getNetAmount());
        paymentResponse.setCurrency(order.getPaymentSummary().getCurrency());
        response.setPaymentSummary(paymentResponse);

        // Convertir les lignes de commande
        List<OrderResponse.OrderLineResponse> lineResponses = order.getOrderLines().stream()
                .map(line -> {
                    OrderResponse.OrderLineResponse lineResponse = new OrderResponse.OrderLineResponse();
                    lineResponse.setId(line.getId());
                    lineResponse.setProductId(line.getProductId());
                    lineResponse.setProductName(line.getProductName());
                    lineResponse.setProductSku(line.getProductSku());
                    lineResponse.setQuantity(line.getQuantity());
                    lineResponse.setUnitPrice(line.getUnitPrice());
                    lineResponse.setTotalPrice(line.getTotalPrice());
                    lineResponse.setProductImageUrl(line.getProductImageUrl());
                    lineResponse.setProductDescription(line.getProductDescription());
                    return lineResponse;
                })
                .collect(Collectors.toList());
        response.setOrderLines(lineResponses);

        // Convertir l'historique
        List<OrderResponse.OrderModificationHistoryResponse> historyResponses = 
                order.getModificationHistory().stream()
                        .map(this::convertHistoryToResponse)
                        .collect(Collectors.toList());
        response.setModificationHistory(historyResponses);

        return response;
    }

    /**
     * Convertit un OrderModificationHistory en OrderModificationHistoryResponse
     */
    private OrderResponse.OrderModificationHistoryResponse convertHistoryToResponse(
            com.ecommerce.order.domain.model.entity.OrderModificationHistory history) {
        OrderResponse.OrderModificationHistoryResponse response = new OrderResponse.OrderModificationHistoryResponse();
        response.setId(history.getId());
        response.setPreviousStatus(history.getPreviousStatus());
        response.setNewStatus(history.getNewStatus());
        response.setReason(history.getReason());
        response.setModifiedBy(history.getModifiedBy());
        response.setModifiedAt(history.getModifiedAt());
        response.setAdditionalNotes(history.getAdditionalNotes());
        return response;
    }
}

