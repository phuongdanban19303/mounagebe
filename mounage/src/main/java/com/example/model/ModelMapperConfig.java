package com.example.model;

import com.example.entity.Product;
import com.example.entity.PurchaseOrder;
import com.example.entity.PurchaseOrderDetail;
import com.example.model.Oder.PurchaseOrderDetailRequest;
import com.example.model.Oder.PurchaseOrderRequest;
import com.example.model.Oder.PurchaseOrderResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // Map ProductRequest to Product (đã có)
        modelMapper.addMappings(new PropertyMap<ProductRequest, Product>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCategory());
                skip(destination.getSupplier());
                skip(destination.getPackaging());
            }
        });

        // Map PurchaseOrderRequest to PurchaseOrder
        modelMapper.addMappings(new PropertyMap<PurchaseOrderRequest, PurchaseOrder>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getSupplier());
                skip(destination.getPurchaseOrderDetails());
                skip(destination.getCreatedAt());
                skip(destination.getUpdatedAt());
                skip(destination.getCreatedBy());
            }
        });

        // Map PurchaseOrderDetailRequest to PurchaseOrderDetail
        modelMapper.addMappings(new PropertyMap<PurchaseOrderDetailRequest, PurchaseOrderDetail>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getPurchaseOrder());
                skip(destination.getProduct());
                skip(destination.getTotalCost());
                skip(destination.getCreatedAt());
            }
        });

        return modelMapper;
    }

}