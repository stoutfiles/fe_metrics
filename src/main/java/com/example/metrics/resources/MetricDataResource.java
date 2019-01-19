package com.example.metrics.resources;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import io.swagger.annotations.ApiModelProperty;

public class MetricDataResource {

    @ApiModelProperty(value = "The value of the datapoint", required = true)
    @NotNull
    @Range(min = 0, max = 100000000)
    private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
