package com.amedigital.SwPlanetsAPIAme.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StarWarPlanet {
	
	String name;
	
	String terrain;
	
	String climate;
	
	List<String> films;

	public String getName() {
		return name;
	}

	public String getTerrain() {
		return terrain;
	}

	public String getClimate() {
		return climate;
	}

	public List<String> getFilms() {
		return films;
	}
}
