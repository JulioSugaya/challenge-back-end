package com.amedigital.SwPlanetsAPIAme.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.amedigital.SwPlanetsAPIAme.entity.Planet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface AWSDynamoService {

    Mono<Planet> save(Planet planet);

    Mono<Planet> findById(String id);

    Mono<Void> delete(String id);

    Flux<List<Planet>> findAll();
    
    Flux<List<Planet>> findByName(String name);
    
}
