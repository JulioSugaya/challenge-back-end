package com.amedigital.SwPlanetsAPIAme.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StarWarResponse {

    public Integer count;
    
    public String next;
    
    public List<StarWarPlanet> results;

	public List<StarWarPlanet> getResults() {
		return results;
	}

	public Integer getCount() {
		return count;
	}

	public String getNext() {
		return next;
	}
}