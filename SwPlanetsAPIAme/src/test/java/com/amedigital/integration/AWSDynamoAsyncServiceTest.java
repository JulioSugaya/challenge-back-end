package com.amedigital.integration;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.amedigital.SwPlanetsAPIAme.entity.Planet;
import com.amedigital.SwPlanetsAPIAme.handler.ApiHandler;
import com.amedigital.SwPlanetsAPIAme.handler.ErrorHandler;
import com.amedigital.SwPlanetsAPIAme.router.ApiRouter;
import com.amedigital.SwPlanetsAPIAme.service.AWSDynamoService;
import com.amedigital.SwPlanetsAPIAme.service.StarWarsAPIService;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDBAsyncClient;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {ApiRouter.class, ApiHandler.class})
@WebFluxTest
public class AWSDynamoAsyncServiceTest {

	@Autowired
	private AWSDynamoService awsDynamoService;
//
	@MockBean
	private DynamoDBAsyncClient dynamoDBAsyncClient;
//    
	private Planet createPlanet() {
		return new Planet(null, "Test Planet", "Test Climate", "Test Terrain", 2);
	}
	
	@Test
	public void testCreatePlanet() {
		Planet planet = createPlanet();

		Mono<Planet> createdPlanet = awsDynamoService.save(planet);
		
//		createdPlanet.assertEquals(expected, createdPlanet.map());
	}

	@Test
    public void testGetAllPlanets() {
		Planet planet = createPlanet();
		
	    webTestClient.get().uri("/planets")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

//    @Test
//    public void testGetSinglePlanet() {
//    	Planet planet = createPlanet();
//    	
//		Mono<Planet> planetMono = awsDynamoService.save(planet);
//
//        webTestClient.get()
//                .uri("/planets/{id}", Collections.singletonMap("id", planet.getId()) )
//				.accept(MediaType.APPLICATION_JSON_UTF8)
//                .exchange()
//				.expectStatus().isOk()
//				.expectBody()
//				.consumeWith(response ->
//						Assertions.assertThat(response.getResponseBody()).isNotNull());
//    }
//
//
//    @Test
//    public void testDeletePlanet() {
//		Planet planet = createPlanet();
//		Mono<Planet> planetMono = awsDynamoService.save(planet);
//
//	    webTestClient.delete()
//                .uri("/planets/{id}", Collections.singletonMap("id",  planet.getId()))
//                .exchange()
//                .expectStatus().isOk();
//    }
}