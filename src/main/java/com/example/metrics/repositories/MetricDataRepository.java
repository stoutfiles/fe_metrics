package com.example.metrics.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.metrics.entities.MetricData;


public interface MetricDataRepository extends CrudRepository<MetricData, String> {
	
	Long countByMetricId(String metricId);
	
	void deleteByMetricId(String metricId);

	@Query(value = "SELECT AVG(m.value) from metric_data m where metric_id = ?1", nativeQuery = true)
    BigDecimal getAverage(String metricId);
	
	@Query(value = "SELECT MAX(m.value) from metric_data m where metric_id = ?1", nativeQuery = true)
	BigDecimal getMax(String metricId);
	
	@Query(value = "SELECT MIN(m.value) from metric_data m where metric_id = ?1", nativeQuery = true)
	BigDecimal getMin(String metricId);
	
	@Query(value = "SELECT AVG(value) median FROM(" + 
			"			  SELECT x.value, SUM(SIGN(1.0-SIGN(y.value-x.value))) diff, floor(count(*)+1/2)" + 
			"			  FROM metric_data x, metric_data y" + 
			"			  WHERE x.metric_id = ?1" + 
			"			  GROUP BY x.value" + 
			"			  HAVING SUM(SIGN(1.0-SIGN(y.value-x.value))) = floor((COUNT(*)+1)/2)" + 
			"			      OR SUM(SIGN(1.0-SIGN(y.value-x.value))) = ceiling((COUNT(*)+1)/2)" + 
			"			) x;", nativeQuery = true)
	BigDecimal getMedian(String metricId);
}
