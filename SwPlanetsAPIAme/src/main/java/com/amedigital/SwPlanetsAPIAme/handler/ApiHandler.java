package com.amedigital.SwPlanetsAPIAme.handler;

import java.util.List;
import java.util.Optional;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.amedigital.SwPlanetsAPIAme.entity.Planet;
import com.amedigital.SwPlanetsAPIAme.entity.StarWarPlanet;
import com.amedigital.SwPlanetsAPIAme.service.AWSDynamoService;
import com.amedigital.SwPlanetsAPIAme.service.StarWarsAPIService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ApiHandler {

    private static final String id = "id";
    
    private static final String name = "search";

    @Autowired
    private ErrorHandler errorHandler;

    @Autowired
    private AWSDynamoService awsDynamoService;
    
    @Autowired
    private StarWarsAPIService starWarsAPIService;

    public Mono<ServerResponse> findById(final ServerRequest request) {
        String planetId = request.pathVariable(id);
        Mono<Planet> planetResponseMono = awsDynamoService.findById(planetId);
        return planetResponseMono
                .flatMap(planet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(planet)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> findByName(final ServerRequest request) {
        Optional<String> planetName = request.queryParam(name);
        Flux<List<Planet>> planets = awsDynamoService.findByName(planetName.get());
        ParameterizedTypeReference<List<Planet>> typeRef = new ParameterizedTypeReference<List<Planet>>() {
        };
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(planets, typeRef);
    }
    
    public Mono<ServerResponse> createPlanet(final ServerRequest request) {
        Mono<Planet> planetMono = request.bodyToMono(Planet.class);
        return planetMono.doOnNext(awsDynamoService::save)
                .flatMap(planet -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(planet)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> findAll(final ServerRequest request) {
        Flux<List<Planet>> planets = this.awsDynamoService.findAll();
        ParameterizedTypeReference<List<Planet>> typeRef = new ParameterizedTypeReference<List<Planet>>() {
        };
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(planets, typeRef);
    }

    public Mono<ServerResponse> findAllBySwAPI(final ServerRequest request) {
    	Mono<List<StarWarPlanet>> planets = this.starWarsAPIService.findAll();
        ParameterizedTypeReference<List<StarWarPlanet>> typeRef = new ParameterizedTypeReference<List<StarWarPlanet>>() {
        };
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(planets, typeRef);
    }

    public Mono<ServerResponse> delete(final ServerRequest request) {
        String planetId = request.pathVariable(id);

        Mono<Planet> planetResponseMono = awsDynamoService.findById(planetId);
        
        return planetResponseMono
                .flatMap(planet -> ServerResponse.ok().build(awsDynamoService.delete(planetId)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::throwableError);
    }
}
