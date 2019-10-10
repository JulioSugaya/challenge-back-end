package com.amedigital.SwPlanetsAPIAme.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Planet {
    private String id;
    private String name;
    private String climate;
    private String terrain;
    private int amountFilms;
    
    @JsonCreator
    public Planet(@JsonProperty("id") String id,
    			 @JsonProperty("name") String name,
                 @JsonProperty("climate") String climate,
                 @JsonProperty("terrain") String terrain,
                 @JsonProperty("amountFilms") int amountFilms) {
    	this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this.amountFilms = amountFilms;
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClimate() {
		return climate;
	}
	public void setClimate(String climate) {
		this.climate = climate;
	}
	public String getTerrain() {
		return terrain;
	}
	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}
	public int getAmountFilms() {
		return amountFilms;
	}
	public void setAmountFilms(int amountFilms) {
		this.amountFilms = amountFilms;
	}
    
    
}
