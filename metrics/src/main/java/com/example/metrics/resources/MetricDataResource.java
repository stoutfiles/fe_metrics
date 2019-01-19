package com.example.metrics.resources;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class MetricDataResource {

    @ApiModelProperty(value = "The value of the datapoint", required = true)
    @NotNull
    private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
