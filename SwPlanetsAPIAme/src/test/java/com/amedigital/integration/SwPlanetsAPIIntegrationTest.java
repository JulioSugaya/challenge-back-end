package com.amedigital.integration;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
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
public class SwPlanetsAPIIntegrationTest {
    
	private static final String uri = "/planets";
	
    @Autowired
    private ApplicationContext context;
    
    @LocalServerPort
    private int port;

	@Autowired
	private AWSDynamoService awsDynamoService;
	
    private final RouterFunction<?> ROUTER_FUNCTION = RouterFunctions.route(
    		RequestPredicates.GET(uri), request -> ServerResponse.ok().build()
    );
    
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
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .isEmpty();
    }

    @Test
    public void testSwPlanetsAPIPostFunction() {
    	Planet planet = createPlanet();
    	awsDynamoService.save(planet);
    	
        WebTestClient.bindToApplicationContext(context)
            .build()
            .post()
            .uri(uri)
            .body(Mono.just(planet), Planet.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Planet.class)
            .consumeWith(planetResponse -> {
                Assertions.assertThat(planetResponse.getResponseBody().getId()).isEqualTo(planet.getId());
                Assertions.assertThat(planetResponse.getResponseBody().getName()).isEqualTo(planet.getName());
                Assertions.assertThat(planetResponse.getResponseBody().getAmountFilms()).isEqualTo(planet.getAmountFilms());
            });
    }
    
    @Test
    public void testSwPlanetsAPIFindAllFunction() {
    	Planet planet = createPlanet();
    	awsDynamoService.save(planet);
    	
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk().expectBody().consumeWith(response ->
			Assertions.assertThat(response.getResponseBody()).isNotNull());
    }
    
    @Test
    public void tesSwPlanetsAPIFindByIdFunction() {
	    Planet planet = createPlanet();
	    awsDynamoService.save(planet);
   
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri(uri + "/" + planet.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Planet.class)
            .consumeWith(planetResponse -> {
                Assertions.assertThat(planetResponse.getResponseBody().getId()).isEqualTo(planet.getId());
                Assertions.assertThat(planetResponse.getResponseBody().getName()).isEqualTo(planet.getName());
                Assertions.assertThat(planetResponse.getResponseBody().getAmountFilms()).isEqualTo(planet.getAmountFilms());
            });
    }
    
    @Test
    public void tesSwPlanetsAPIFindByNameFunction() {
	    Planet planet = createPlanet();
	    awsDynamoService.save(planet);
   
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri(uri + "?search=" + planet.getName())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(planetResponse ->
                Assertions.assertThat(planetResponse.getResponseBody()).isNotNull());
    }
    
    @Test
    public void testSwPlanetsAPIFindByIdNotFoundFunction() {
   
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri(uri + "0")
            .exchange()
            .expectStatus()
            .isNotFound();
    }
    
    @Test
    public void tesSwPlanetsAPIDeleteFunction() {
	    Planet planet = createPlanet();
	    awsDynamoService.save(planet);
   
        WebTestClient.bindToApplicationContext(context)
            .build()
            .delete()
            .uri(uri + "/" + planet.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(planetResponse ->
                Assertions.assertThat(planetResponse.getResponseBody()).isNull());
    }
    
    @Test
    public void tesSwPlanetsAPIDeleteNotFoundFunction() {
        WebTestClient.bindToApplicationContext(context)
            .build()
            .delete()
            .uri(uri + "/0")
            .exchange()
            .expectStatus()
            .isNotFound();
    }
    
    @Test
    public void testSwPlanetsAPIFindAllSwapiFunction() {
    	Planet planet = createPlanet();
    	awsDynamoService.save(planet);
    	
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri(uri + "/swapi/all")
            .exchange()
            .expectStatus()
            .isOk().expectBody().consumeWith(response ->
			Assertions.assertThat(response.getResponseBody()).isNotNull());
    }
    
    @Test
    public void testWebTestClientWithServerURL() {
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build()
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody();
    }
}
