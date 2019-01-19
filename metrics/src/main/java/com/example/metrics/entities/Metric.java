package com.example.metrics.entities;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.metrics.resources.MetricResource;


@Entity
@Table(name = "metric")
public class Metric {

	@Id
    private String id;

    private String metricName;
    
    private String metricType;
    
    private Date createdDate;
    
    private Date updatedDate;
    
    public Metric() {
    	
    }
    
    public Metric(MetricResource metricResource) {
    	this.id = metricResource.getId();
    	this.metricName = metricResource.getMetricName();
    	this.metricType = metricResource.getMetricType().toString();
    	this.createdDate = new Date();
    	this.updatedDate = new Date();
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public static Page<MetricResource> toResourcePage(Page<Metric> metrics, Pageable pageable) {
        List<MetricResource> list = new ArrayList<>(metrics.getNumber());
        for (Metric metric : metrics.getContent()) {
            list.add(new MetricResource(metric));
        }
        return new PageImpl<>(list, pageable, metrics.getTotalElements());
    }

}

