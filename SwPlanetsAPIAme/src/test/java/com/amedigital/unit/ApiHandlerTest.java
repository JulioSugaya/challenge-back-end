package com.amedigital.unit;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.amedigital.SwPlanetsAPIAme.SwPlanetsApplication;
import com.amedigital.SwPlanetsAPIAme.entity.Planet;
import com.amedigital.SwPlanetsAPIAme.service.AWSDynamoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SwPlanetsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiHandlerTest {
	
	private static final String uri = "/planets";

	@MockBean
	private AWSDynamoService awsDynamoService;

    @Autowired
    private ApplicationContext context;
    
    private WebTestClient webTestClient;

    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }
    
	private Planet createPlanet() {
		return new Planet(null, "Test Planet", "Test Climate", "Test Terrain", 2);
	}
	
	@Test
	public void testCreatePlanet() {
		Planet planet = createPlanet();
		
		when(awsDynamoService.save(any())).thenReturn(Mono.just(planet));
		
		webTestClient.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(planet), Planet.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Planet.class)
        .consumeWith(planetResponse -> {
            Assertions.assertThat(planetResponse.getResponseBody().getId()).isEqualTo(planet.getId());
            Assertions.assertThat(planetResponse.getResponseBody().getName()).isEqualTo(planet.getName());
            Assertions.assertThat(planetResponse.getResponseBody().getAmountFilms()).isEqualTo(planet.getAmountFilms());
        });
	}
    
    @Test
    public void testFindAllFunction() {
    	Planet planet = createPlanet();
    	Planet planet2 = createPlanet();
    	List<Planet> planets = Arrays.asList(planet, planet2);
    	
    	when(awsDynamoService.findAll()).thenReturn(Flux.just(planets));
    	
    	webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk().expectBodyList(Planet.class)
            .consumeWith(response ->
			Assertions.assertThat(response.getResponseBody()).isNotNull());
    }
    
    @Test
    public void testFindByIdFunction() {
	    Planet planet = createPlanet();
	    
	    when(awsDynamoService.findById(any())).thenReturn(Mono.just(planet));   
	    
	    webTestClient
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
    public void testFindByNameFunction() {
	    Planet planet = createPlanet();
    	Planet planet2 = createPlanet();
    	List<Planet> planets = Arrays.asList(planet, planet2);
    	
	    when(awsDynamoService.findByName(any())).thenReturn(Flux.just(planets));   
   
        webTestClient
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
    	when(awsDynamoService.findByName(any())).thenReturn(Flux.empty());
    	
        webTestClient
            .get()
            .uri(uri + "0")
            .exchange()
            .expectStatus()
            .isNotFound();
    }
    
    @Test
    public void tesSwPlanetsAPIDeleteFunction() {
	    Planet planet = createPlanet();
	    when(awsDynamoService.findById(any())).thenReturn(Mono.just(planet));
	    when(awsDynamoService.delete(any())).thenReturn(Mono.empty()); 
   
        webTestClient
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
    	when(awsDynamoService.findById(any())).thenReturn(Mono.empty());
    	when(awsDynamoService.delete(any())).thenReturn(Mono.empty());
    	
        webTestClient
            .delete()
            .uri(uri + "/0")
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}