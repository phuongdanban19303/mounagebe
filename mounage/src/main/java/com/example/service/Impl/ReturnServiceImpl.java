package com.example.service.Impl;

import com.example.entity.*;
import com.example.model.returns.ReturnItemRequest;
import com.example.model.returns.ReturnRequest;
import com.example.model.returns.ReturnResponse;
import com.example.repository.*;
import com.example.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {
    private final SaleRepository saleRepo;
    private final SaleDetailRepository saleDetailRepo;
    private final ReturnRepository returnRepo;
    private final ReturnDetailRepository returnDetailRepo;
    private final InventoryRepository inventoryRepo;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ReturnDetailRepository returnDetailRepository;

    @Override
    @Transactional
    public ReturnResponse createReturn(ReturnRequest request, Integer userId) {
        // 1. Lấy đơn hàng gốc
        Sale sale = saleRepo.findBySaleNumber(request.getSaleNumber())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với số hóa đơn: " + request.getSaleNumber()));


        // 2. Tạo Return entity
        Return returnEntity = new Return();
        returnEntity.setOriginalSale(sale);
        returnEntity.setReturnNumber(generateReturnNumber());
        returnEntity.setCustomerId(request.getCustomerId());
        returnEntity.setProcessedBy(userId);
        returnEntity.setReason(request.getReason());
        returnEntity.setRefundMethod(request.getRefundMethod());
        returnEntity.setStatus("processed");
        returnEntity.setReturnDate(Instant.now());
        returnEntity.setCreatedAt(Instant.now());
        returnEntity.setNotes(request.getNotes());

        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        Set<ReturnDetail> returnDetails = new HashSet<>();

        for (ReturnItemRequest item : request.getItems()) {
            SaleDetail saleDetail = saleDetailRepo.findById(item.getOriginalDetailId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng gốc"));
            // Tính tiền trả
            BigDecimal itemAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantityReturned()));
            totalReturnAmount = totalReturnAmount.add(itemAmount);
            Product product = productRepository.findByBarcode(item.getBarcode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với barcode: " + item.getBarcode()));
            Integer totalReturnedBefore = returnDetailRepository
                    .sumQuantityReturnedByOriginalDetailId(item.getOriginalDetailId())
                    .orElse(0);

            int maxReturnable = saleDetail.getQuantity() - totalReturnedBefore;
            if (item.getQuantityReturned() > maxReturnable) {
                throw new IllegalArgumentException("Số lượng trả không được lớn hơn số lượng đã bán. Đã bán: "
                        + saleDetail.getQuantity() + ", yêu cầu trả: " + item.getQuantityReturned());
            }
            int productId = product.getId();
            // Tạo ReturnDetail
            ReturnDetail detail = new ReturnDetail();
            detail.setReturnField(returnEntity);
            detail.setOriginalDetailId(item.getOriginalDetailId());
            detail.setProductId(productId);
            detail.setQuantityReturned(item.getQuantityReturned());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setReturnAmount(itemAmount);
            detail.setCreatedAt(Instant.now());

            returnDetails.add(detail);

            // Cập nhật tồn kho
            Inventory inventory = inventoryRepo.findByProductId(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho cho sản phẩm ID: " + productId));

            inventory.setQuantityOnHand(inventory.getQuantityOnHand() + item.getQuantityReturned());
            inventory.setLastMovementDate(Instant.now());
            inventoryRepo.save(inventory);
        }

        returnEntity.setReturnAmount(totalReturnAmount);
        Return savedReturn = returnRepo.save(returnEntity);

        for (ReturnDetail detail : returnDetails) {
            detail.setReturnField(savedReturn);
            returnDetailRepo.save(detail);
        }

        return modelMapper.map(savedReturn, ReturnResponse.class);
    }

    private String generateReturnNumber() {
        long count = returnRepo.count() + 1;
        return String.format("RT%05d", count);
    }

    @Override
    public List<ReturnResponse> getAllReturns() {
       return returnRepo.findAll().stream().map(returnEntity -> modelMapper.map(returnEntity, ReturnResponse.class)).collect(Collectors.toList());
    }
}
