package com.ecommerce.order.infrastructure.controller;

import com.ecommerce.order.application.dto.CreateOrderRequest;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande", description = "Crée une nouvelle commande pour un client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données de la commande invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.createOrder(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par ID", description = "Récupère les détails d'une commande spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.getOrderById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Récupérer une commande par numéro", description = "Récupère les détails d'une commande par son numéro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @Parameter(description = "Numéro de la commande") @PathVariable String orderNumber,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.getOrderByNumber(orderNumber, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupérer les commandes d'un client", description = "Récupère toutes les commandes d'un client spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getOrdersByCustomer(
            @Parameter(description = "ID du client") @PathVariable Long customerId,
            @Parameter(description = "Paramètres de pagination") Pageable pageable,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        Page<OrderResponse> response = orderApplicationService.getOrdersByCustomer(customerId, pageable, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Rechercher des commandes", description = "Recherche des commandes avec filtres optionnels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> searchOrders(
            @Parameter(description = "ID du client (optionnel)") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Statut de la commande (optionnel)") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Paramètres de pagination") Pageable pageable,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        Page<OrderResponse> response = orderApplicationService.searchOrders(customerId, status, pageable, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une commande", description = "Met à jour le statut d'une commande existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @orderSecurityService.canUpdateOrderStatus(#id, authentication.name))")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.updateOrderStatus(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une commande", description = "Confirme une commande en attente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande confirmée avec succès"),
        @ApiResponse(responseCode = "400", description = "Impossible de confirmer la commande"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<OrderResponse> confirmOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.confirmOrder(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une commande", description = "Annule une commande si possible")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande annulée avec succès"),
        @ApiResponse(responseCode = "400", description = "Impossible d'annuler la commande"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('ADMIN') or @orderSecurityService.canCancelOrder(#id, authentication.name)")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Raison de l'annulation") @RequestParam String reason,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        OrderResponse response = orderApplicationService.cancelOrder(id, reason, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Récupérer l'historique d'une commande", description = "Récupère l'historique des modifications d'une commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse.OrderModificationHistoryResponse>> getOrderHistory(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        List<OrderResponse.OrderModificationHistoryResponse> response = 
                orderApplicationService.getOrderHistory(id, currentUser);
        return ResponseEntity.ok(response);
    }
}

