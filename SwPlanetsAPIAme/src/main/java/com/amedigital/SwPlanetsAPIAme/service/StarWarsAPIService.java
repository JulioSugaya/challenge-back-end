package com.amedigital.SwPlanetsAPIAme.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.amedigital.SwPlanetsAPIAme.entity.StarWarPlanet;

import reactor.core.publisher.Mono;

@Service
public interface StarWarsAPIService {

	Mono<List<StarWarPlanet>> findAll();
	
	int getAmountFilmsFromSwapiByName(String name);
}
