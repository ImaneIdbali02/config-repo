package com.ecommerce.order.infrastructure.repository;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.infrastructure.persistence.entity.OrderEntity;
import com.ecommerce.order.infrastructure.persistence.mapper.OrderMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository, OrderMapper orderMapper) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderMapper.toOrderEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(orderEntity);
        return orderMapper.toOrder(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(orderMapper::toOrder);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return jpaOrderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toOrder);
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        // Note: JpaOrderRepository n'a pas de méthode findByCustomerId directe
        // Il faudrait l'ajouter ou implémenter une requête personnalisée
        // Pour l'exemple, on va chercher toutes les commandes et filtrer en mémoire (non optimal pour la prod)
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getCustomerId().equals(customerId))
                .map(orderMapper::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        // Note: JpaOrderRepository n'a pas de méthode findByStatus directe
        // Il faudrait l'ajouter ou implémenter une requête personnalisée
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getStatus().equals(status.name()))
                .map(orderMapper::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        // Implémentation manquante dans JpaOrderRepository
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getCreatedAt().isAfter(startDate) && entity.getCreatedAt().isBefore(endDate))
                .map(orderMapper::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status) {
        // Implémentation manquante dans JpaOrderRepository
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getCustomerId().equals(customerId) && entity.getStatus().equals(status.name()))
                .map(orderMapper::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findPendingOrdersOlderThan(LocalDateTime dateTime) {
        // Implémentation manquante dans JpaOrderRepository
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getStatus().equals(OrderStatus.PENDING.name()) && entity.getCreatedAt().isBefore(dateTime))
                .map(orderMapper::toOrder)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(OrderStatus status) {
        // Implémentation manquante dans JpaOrderRepository
        return jpaOrderRepository.findAll().stream()
                .filter(entity -> entity.getStatus().equals(status.name()))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaOrderRepository.existsById(id);
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        return jpaOrderRepository.existsByOrderNumber(orderNumber);
    }
}

