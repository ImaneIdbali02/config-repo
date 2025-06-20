package com.ecommerce.order.infrastructure.controller;

import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import com.ecommerce.order.domain.service.OrderAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
@Tag(name = "Admin Orders", description = "API d'administration des commandes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderAdminService orderAdminService;

    public AdminOrderController(OrderApplicationService orderApplicationService,
                               OrderAdminService orderAdminService) {
        this.orderApplicationService = orderApplicationService;
        this.orderAdminService = orderAdminService;
    }

    @GetMapping("/statistics")
    @Operation(summary = "Récupérer les statistiques des commandes", 
               description = "Récupère les statistiques globales des commandes par statut")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<OrderAdminService.OrderStatistics> getOrderStatistics() {
        OrderAdminService.OrderStatistics statistics = orderAdminService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/pending-old")
    @Operation(summary = "Récupérer les commandes en attente anciennes", 
               description = "Récupère les commandes en attente depuis plus de X heures")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<Page<OrderResponse>> getOldPendingOrders(
            @Parameter(description = "Nombre d'heures") @RequestParam(defaultValue = "24") int hours,
            @Parameter(description = "Paramètres de pagination") Pageable pageable) {
        
        // Pour l'exemple, on convertit la liste en Page
        var oldOrders = orderAdminService.getPendingOrdersOlderThan(hours);
        var responses = oldOrders.stream()
                .map(order -> {
                    // Ici, vous devriez utiliser un mapper ou le service application
                    // Pour simplifier, on retourne une réponse basique
                    OrderResponse response = new OrderResponse();
                    response.setId(order.getId());
                    response.setOrderNumber(order.getOrderNumber());
                    response.setCustomerId(order.getCustomerId());
                    response.setStatus(order.getStatus());
                    response.setCreatedAt(order.getCreatedAt());
                    return response;
                })
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        Page<OrderResponse> page = new org.springframework.data.domain.PageImpl<>(
                responses.subList(start, end), pageable, responses.size());
        
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}/force-status")
    @Operation(summary = "Forcer le changement de statut d'une commande", 
               description = "Force le changement de statut d'une commande (admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut forcé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<OrderResponse> forceStatusChange(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        
        String adminUser = authentication.getName();
        var order = orderAdminService.forceStatusChange(id, request.getNewStatus(), 
                                                       request.getReason(), adminUser);
        
        // Convertir en réponse (simplifiée)
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/internal-notes")
    @Operation(summary = "Ajouter des notes internes à une commande", 
               description = "Ajoute des notes internes à une commande (admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notes ajoutées avec succès"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<OrderResponse> addInternalNotes(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Notes internes") @RequestBody Map<String, String> notesRequest,
            Authentication authentication) {
        
        String adminUser = authentication.getName();
        String notes = notesRequest.get("notes");
        
        var order = orderAdminService.addInternalNotes(id, notes, adminUser);
        
        // Convertir en réponse (simplifiée)
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setInternalNotes(order.getInternalNotes());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-old-pending")
    @Operation(summary = "Annuler les commandes en attente anciennes", 
               description = "Annule en masse les commandes en attente depuis plus de X heures")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commandes annulées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<Map<String, Object>> cancelOldPendingOrders(
            @Parameter(description = "Nombre d'heures seuil") @RequestParam(defaultValue = "48") int hoursThreshold,
            Authentication authentication) {
        
        String adminUser = authentication.getName();
        var cancelledOrders = orderAdminService.cancelOldPendingOrders(hoursThreshold, adminUser);
        
        Map<String, Object> result = Map.of(
                "cancelledCount", cancelledOrders.size(),
                "hoursThreshold", hoursThreshold,
                "cancelledBy", adminUser
        );
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche avancée de commandes (admin)", 
               description = "Recherche avancée de commandes avec tous les filtres disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commandes trouvées"),
        @ApiResponse(responseCode = "401", description = "Non autorisé"),
        @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<Page<OrderResponse>> advancedSearch(
            @Parameter(description = "ID du client (optionnel)") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Statut de la commande (optionnel)") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Numéro de commande (optionnel)") @RequestParam(required = false) String orderNumber,
            @Parameter(description = "Paramètres de pagination") Pageable pageable,
            Authentication authentication) {
        
        String currentUser = authentication.getName();
        
        // Utiliser le service application pour la recherche
        Page<OrderResponse> results = orderApplicationService.searchOrders(
                customerId, status, pageable, currentUser);
        
        return ResponseEntity.ok(results);
    }
}

