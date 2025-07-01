package com.example.service;

import com.example.model.returns.ReturnRequest;
import com.example.model.returns.ReturnResponse;

import java.util.List;

public interface ReturnService {
    ReturnResponse createReturn(ReturnRequest request, Integer userId);
    List<ReturnResponse> getAllReturns();

}
