package com.amedigital.SwPlanetsAPIAme.exception;

@SuppressWarnings("serial")
public class PlanetNotFoundException extends RuntimeException {

    public PlanetNotFoundException() {
        super("Planet not found");
    }
}