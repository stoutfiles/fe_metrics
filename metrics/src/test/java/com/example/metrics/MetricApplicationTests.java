package com.example.metrics;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.metrics.repositories.MetricDataRepository;
import com.example.metrics.repositories.MetricRepository;
import com.example.metrics.resources.MetricResource;
import com.example.metrics.resources.MetricType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MetricApplicationTests {

	@Autowired
    private TestRestTemplate restTemplate;
     
    @LocalServerPort
    int randomServerPort;
    
    @Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricDataRepository metricDataRepository;
	
	@Before
    public void cleanupBefore() {
		cleanup();
    }
    
    @After
    public void cleanupAfter() {
    	cleanup();
    }
    
    public void cleanup() {
    	metricDataRepository.deleteAll();
    	metricRepository.deleteAll();
    }
 
    @Test
    public void testMetricCreate() throws URISyntaxException, JsonParseException, JsonMappingException, IOException
    {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setPropertyNamingStrategy(
    		     PropertyNamingStrategy.SNAKE_CASE);
    	
        URI uri = new URI("http://localhost:"+randomServerPort+"/metrics");
        MetricResource metricResource = new MetricResource();
        metricResource.setId("foo");
        metricResource.setMetricName("bar");
        metricResource.setMetricType(MetricType.gauge);
 
        HttpEntity<MetricResource> request = new HttpEntity<>(metricResource);
         
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        MetricResource metricResourceResult = mapper.readValue(result.getBody(), MetricResource.class);
        
        Assert.assertEquals(201, result.getStatusCodeValue());
        Assert.assertEquals(metricResourceResult.getId(), "foo");
        Assert.assertEquals(metricResourceResult.getMetricName(), "bar");
        Assert.assertEquals(metricResourceResult.getMetricType().toString(), "gauge");
       
        //re-run post to confirm I can't add the same value
        result = this.restTemplate.postForEntity(uri, request, String.class);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }

}

