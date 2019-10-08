package com.amedigital.integration;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebHandler;

import com.amedigital.SwPlanetsAPIAme.SwPlanetsApplication;
import com.amedigital.SwPlanetsAPIAme.entity.Planet;
import com.amedigital.SwPlanetsAPIAme.service.AWSDynamoService;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SwPlanetsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTestClientIntegrationTest {

    @LocalServerPort
    private int port;

	@Autowired
	private AWSDynamoService awsDynamoService;
	
    private final RouterFunction<ServerResponse> ROUTER_FUNCTION = RouterFunctions
    		.route(RequestPredicates.GET("/planets"), request -> ServerResponse.ok().build())
    		.andRoute(RequestPredicates.POST("/planets"), request -> ServerResponse.ok().build());
    
    private final WebHandler WEB_HANDLER = exchange -> Mono.empty();

    
	private Planet createPlanet() {
		return new Planet(null, "Kamino", "Test Climate", "Test Terrain", 2);
	}
	
    @Test
    public void testWebTestClientWithServerWebHandler() {
        WebTestClient.bindToWebHandler(WEB_HANDLER)
            .build();
    }

    @Test
    public void testWebTestClientWithRouterFunction() {
        WebTestClient.bindToRouterFunction(ROUTER_FUNCTION)
            .build()
            .get()
            .uri("/planets")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .isEmpty();
    }

    @Test
    public void testWebTestClientPostFunction() {
    	Planet planet = createPlanet();
//    	Mono<Planet> planetMono = awsDynamoService.save(planet);
    	
        WebTestClient.bindToRouterFunction(ROUTER_FUNCTION)
            .build()
            .post()
            .uri("/planets")
            .body(Mono.just(planet), Planet.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody().consumeWith(response ->
			Assertions.assertThat(response.getResponseBody()).isNotNull());
    }
    
    @Test
    public void testWebTestClientWithContentFunction() {
    	Planet planet = createPlanet();
    	Mono<Planet> planetMono = awsDynamoService.save(planet);
    	
        WebTestClient.bindToRouterFunction(ROUTER_FUNCTION)
            .build()
            .get()
            .uri("/planets")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody().consumeWith(response ->
			Assertions.assertThat(response.getResponseBody()).isNotNull());
    }
    
    @Test
    public void testWebTestClientWithServerURL() {
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build()
            .get()
            .uri("/planets")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody();
    }

}