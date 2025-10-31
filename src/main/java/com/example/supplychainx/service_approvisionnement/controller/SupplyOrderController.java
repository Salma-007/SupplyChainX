package com.example.supplychainx.service_approvisionnement.controller;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderResponseDTO;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderMapper;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import com.example.supplychainx.service_approvisionnement.service.SupplyOrderService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supply-orders")
@RequiredArgsConstructor
public class SupplyOrderController {
    private final SupplyOrderService supplyOrderService;
    private final SupplyOrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<SupplyOrderResponseDTO> createOrder(
            @Valid @RequestBody SupplyOrderRequestDTO requestDTO) {

        SupplyOrder createdOrder = supplyOrderService.createOrder(requestDTO);
        SupplyOrderResponseDTO responseDTO = orderMapper.toResponseDto(createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDTO> getOrderById(@PathVariable Long id) {

        SupplyOrder order = supplyOrderService.getOrderById(id);
        SupplyOrderResponseDTO responseDTO = orderMapper.toResponseDto(order);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<SupplyOrderResponseDTO>> getAllOrders() {

        List<SupplyOrder> orders = supplyOrderService.getAllOrders();
        List<SupplyOrderResponseDTO> responseDTOs = orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        supplyOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
