package com.amedigital.SwPlanetsAPIAme.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.amedigital.SwPlanetsAPIAme.entity.StarWarPlanet;
import com.amedigital.SwPlanetsAPIAme.entity.StarWarResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StarWarsAPIServiceImpl implements StarWarsAPIService{

	final String BASE_URL = "https://swapi.co";
	
	final String URI = "/api/planets/";
	
	final String FILTER = "?search=";
	
    private WebClient webClient;

    public StarWarsAPIServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

	public int getAmountFilmsFromSwapiByName(String name) {
		
		RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0");
	    HttpEntity<?> entity = new HttpEntity<>(headers);

	    HttpEntity<StarWarResponse> response = restTemplate.exchange(BASE_URL + URI + FILTER + name, HttpMethod.GET, entity, StarWarResponse.class);
	    
	    StarWarResponse swapi = response.getBody();
	    
	    return swapi.getResults().isEmpty() ? 0 : swapi.getResults().get(0).getFilms().size();
	}
	
	public Mono<List<StarWarPlanet>> findAll() {
	    return fetchItems(URI).expand(response -> {
	        if (response.getNext() == null) {
	            return Mono.empty();
	        }
	        return fetchItems(response.getNext());
	    }).flatMap(response -> Flux.fromIterable(response.getResults())).collectList();
	}

	private Mono<StarWarResponse> fetchItems(String url) {

	         return webClient.get().uri(url).retrieve()
	                    .bodyToMono(StarWarResponse.class);
	}
	
	
}
