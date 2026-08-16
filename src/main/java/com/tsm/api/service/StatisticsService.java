package com.tsm.api.service;

import com.tsm.api.dto.response.StatisticsResponse;

import java.util.UUID;

public interface StatisticsService {
    StatisticsResponse getByCommerceId(UUID commerceId);
}