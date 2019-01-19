package com.example.metrics;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.metrics.entities.Metric;
import com.example.metrics.entities.MetricData;
import com.example.metrics.repositories.MetricDataRepository;
import com.example.metrics.repositories.MetricRepository;
import com.example.metrics.resources.MetricDataResource;
import com.example.metrics.resources.MetricResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;
import io.swagger.annotations.ApiImplicitParams;

@Controller
@RequestMapping("/metrics")
@Api(tags = "Metrics")
public class MetricService {
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricDataRepository metricDataRepository;
	
	@PostMapping()
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(value = "Creates a new metric.")
	public @ResponseBody MetricResource createMetric(
			@ApiParam(name = "metric_resource", value = "The metric to create") @Valid @RequestBody MetricResource metricResource) {
		
		if(metricRepository.findById(metricResource.getId()).orElse(null) != null) {
			throw new ResponseStatusException(
			          HttpStatus.BAD_REQUEST, ErrorMessages.METRIC_ALREADY_EXIST);
		}
		
		//sets name if null
		if(metricResource.getMetricName() == null) {
			metricResource.setMetricName(metricResource.getId());
		}
		
		//convert resource into entity
		Metric metric = new Metric(metricResource);		
		
		//save metric
		try {	
			metricRepository.save(metric);
		} catch (Exception e) {
		}

		return metricResource;
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Updates an existing metric.", notes = "Metric id and metric type cannot be changed.")
	public void updateMetric(
			@ApiParam(name = "metric_resource", value = "The metric to update") @Valid @RequestBody MetricResource metricResource, 
		    @ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		Metric oldMetric = metricRepository.findById(id).orElse(null);
		if(oldMetric == null) {
			throw new ResponseStatusException(
			          HttpStatus.BAD_REQUEST, ErrorMessages.METRIC_NOT_EXIST);
		}
		
		//convert resource into entity
		Metric metric = new Metric(metricResource);
		metric.setId(id);
		metric.setMetricType(oldMetric.getMetricType());
		metric.setUpdatedDate(new Date());
		
		//update metric
		try {	
			metricRepository.save(metric);
		} catch (Exception e) {
			throw new ResponseStatusException(
			          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.METRIC_DATABASE);
		}

	}
	
	
	@DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Deletes a metric.")
	public void deleteMetric(@ApiParam("The metric id") @PathVariable String id) {
		
		//delete metric data so it is not orphaned
		metricDataRepository.deleteByMetricId(id);
		
		try {
		metricRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			//no need to tell them the resource didn't exist
		} 
	}
	
	
	@GetMapping("/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Gets a metric.")
	public @ResponseBody MetricResource getMetric(@ApiParam("The metric id") @PathVariable String id) {
		
		Metric metric = metricRepository.findById(id).orElse(null);
		if(metric == null) {
			throw new ResponseStatusException(
			          HttpStatus.NOT_FOUND, ErrorMessages.METRIC_NOT_EXIST);
		}
		
		return new MetricResource(metric);
	}
	
	@GetMapping()
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Lists metrics.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "page", dataType = "int", paramType = "query", value = "Results page you want to retrieve (0..N)"),
	    @ApiImplicitParam(name = "size", dataType = "int", paramType = "query", value = "Number of records per page."),
	    @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property,asc (or property,desc). "
	            + "Default sort order is ascending. " + "Multiple sort criteria are supported.") })

	public @ResponseBody Page<MetricResource> getMetrics(@ApiIgnore(
            "Please use implicit param values."
    )Pageable pageable) {
 
		Page<Metric> metrics = metricRepository.findAll(pageable);
		return Metric.toResourcePage(metrics, pageable);
	}
	

	@PostMapping("/{id}/data")
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(value = "Post metric data for a given metric.")
	public @ResponseBody MetricResource postMetricData(
			@ApiParam(name = "metric_data_resource", value = "The metric data to post") @Valid @RequestBody MetricDataResource metricDataResource,
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		MetricResource metricResource = getMetric(id);
		
		//convert resource into entity
		MetricData metricData = new MetricData(metricDataResource);
		metricData.setMetricId(id);
		metricData.setMetricType(metricResource.getMetricType().toString());
		metricData.setTimestamp(new Date());
		
		//save metric
		try {	
			metricDataRepository.save(metricData);
		} catch (Exception e) {
			throw new ResponseStatusException(
			          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.METRIC_DATABASE);
		}

		return metricResource;
	}
	
	
	@GetMapping("/{id}/min")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get the minimum metric data value for a given metric.")
	public @ResponseBody BigDecimal getMetricMin(
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		getMetric(id);
		
		return metricDataRepository.getMin(id);
	}
	
	@GetMapping("/{id}/max")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get the maximum metric data value for a given metric.")
	public @ResponseBody BigDecimal getMetricMax(
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		getMetric(id);
		
		return metricDataRepository.getMax(id);
	}
	
	@GetMapping("/{id}/average")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get the average metric data value for a given metric.")
	public @ResponseBody BigDecimal getMetricAverage(
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		getMetric(id);
		
		return metricDataRepository.getAverage(id);
	}
	
	@GetMapping("/{id}/median")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get the median metric data value for a given metric.")
	public @ResponseBody BigDecimal getMetricMedian(
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		getMetric(id);
		
		return metricDataRepository.getMedian(id);
	}
	
	@GetMapping("/{id}/count")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Get the metric data count for a given metric.")
	public @ResponseBody Long getMetricCount(
			@ApiParam("The metric id") @PathVariable String id) {
		
		//confirm metric exists
		getMetric(id);
		
		return metricDataRepository.countByMetricId(id);
	}
	
	@DeleteMapping()
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Delete all the metrics and their data.")
	public void deleteMetricData() {
		
		metricDataRepository.deleteAll();
		metricRepository.deleteAll();
	}
	
	
	
	
}
