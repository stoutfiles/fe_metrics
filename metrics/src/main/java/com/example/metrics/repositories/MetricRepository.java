package com.example.metrics.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.metrics.entities.Metric;


public interface MetricRepository extends PagingAndSortingRepository<Metric, String> {	
}
