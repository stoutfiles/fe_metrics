package com.example.metrics.resources;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.metrics.entities.Metric;

import io.swagger.annotations.ApiModelProperty;


public class MetricResource {

	@ApiModelProperty(value = "The id of the metric. Once created, cannot be changed.", required = true)
    @NotNull
    @Size(min = 1, max = 40)
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]+$", message = "Must be only letters, numbers, and underscores.  Must start with a letter.  40 characters max.")
    private String id;
	
	@ApiModelProperty(value = "The name of the metric for display.  Defaults to id value if not provided.  255 characters max.", required = false)
	@Size(min = 1, max = 255)
	private String metricName;
	
	@ApiModelProperty(value = "The type of metric; gauge or counter.", required = true)
    private MetricType metricType;
	
	public MetricResource() {
		
	}

	public MetricResource(Metric metric) {
		this.id = metric.getId();
		this.metricName = metric.getMetricName();
		this.metricType = MetricType.valueOf(metric.getMetricType());
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

	public MetricType getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}
	
	
}
