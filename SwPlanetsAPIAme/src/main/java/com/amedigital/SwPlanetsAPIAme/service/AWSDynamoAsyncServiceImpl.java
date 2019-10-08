package com.amedigital.SwPlanetsAPIAme.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.amedigital.SwPlanetsAPIAme.entity.Planet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDBAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;


public class AWSDynamoAsyncServiceImpl implements AWSDynamoService {

    private static final String tableName = "Planet";
    private DynamoDBAsyncClient client;
    
    @Autowired
    private StarWarsAPIService swService;
    
    public AWSDynamoAsyncServiceImpl(final DynamoDBAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<Planet> save(final Planet planet) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        
        attributeValueHashMap.put("Id", AttributeValue.builder().s(planet.getId()).build());
        attributeValueHashMap.put("Name", AttributeValue.builder().s(planet.getName()).build());
        attributeValueHashMap.put("Climate", AttributeValue.builder().s(planet.getClimate()).build());
        attributeValueHashMap.put("Terrain", AttributeValue.builder().s(planet.getTerrain()).build());
        
        planet.setAmountFilms(swService.getAmountFilmsFromSwapiByName(planet.getName()));
        
        attributeValueHashMap.put("AmountFilms", AttributeValue.builder().s(String.valueOf(planet.getAmountFilms())).build());
        
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(tableName)
                .item(attributeValueHashMap)
                .build();

        CompletableFuture<PutItemResponse> completableFuture = client.putItem(putItemRequest);

        CompletableFuture<Planet> planetCompletableFuture = completableFuture.thenApplyAsync(PutItemResponse::attributes)
                .thenApplyAsync(map -> createPlanet(map));

        return Mono.fromFuture(planetCompletableFuture);
    }
    
    @Override
    public Mono<Planet> findById(final String id) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("Id", AttributeValue.builder().s(id).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(attributeValueHashMap)
                .build();

        CompletableFuture<GetItemResponse> completableFuture = client.getItem(getItemRequest);

        CompletableFuture<Planet> planetCompletableFuture = completableFuture.thenApplyAsync(GetItemResponse::item)
                .thenApplyAsync(map -> createPlanet(map));

        return Mono.fromFuture(planetCompletableFuture);
    }

    @Override
    public Flux<List<Planet>> findAll() {
        Map<String, Condition> conditionHashMap = new HashMap<>();

        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName)
                .scanFilter(conditionHashMap)
                .build();

        CompletableFuture<ScanResponse> future = client.scan(scanRequest);

        CompletableFuture<List<Planet>> response =
                future.thenApplyAsync(ScanResponse::items)
                        .thenApplyAsync(list -> list.parallelStream()
                                .map(map -> createPlanet(map)).collect(Collectors.toList())
                        );

        return Flux.from(Mono.fromFuture(response));
    }

    @Override
    public Flux<List<Planet>> findByName(final String name) {
        Map<String, Condition> conditionHashMap = new HashMap<>();
        
        Condition condition = Condition.builder()
                .comparisonOperator(ComparisonOperator.CONTAINS)
                .attributeValueList(AttributeValue.builder().s(name).build())
                .build();

        conditionHashMap.put("Name", condition);
        
        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName)
                .scanFilter(conditionHashMap)
                .build();

        CompletableFuture<ScanResponse> future = client.scan(scanRequest);

        CompletableFuture<List<Planet>> response =
                future.thenApplyAsync(ScanResponse::items)
                        .thenApplyAsync(list -> list.parallelStream()
                                .map(map -> createPlanet(map)).collect(Collectors.toList())
                        );

        return Flux.from(Mono.fromFuture(response));
    }
    
    @Override
    public Mono<Void> delete(final String id) {
        Map<String, AttributeValue> attributeValueHashMap = new HashMap<>();
        attributeValueHashMap.put("Id", AttributeValue.builder().s(id).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(attributeValueHashMap)
                .build();

        client.deleteItem(deleteItemRequest);

        return Mono.empty();
    }

    private Planet createPlanet(Map<String, AttributeValue> map) {
        if (map != null) {
            return new Planet(
            		map.get("Id").s(),
            		map.get("Name").s(),
            		map.get("Climate").s(),
            		map.get("Terrain").s(),
            		Integer.valueOf(map.get("AmountFilms").s())
            		);
        }
        return null;
    }
}
