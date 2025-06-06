package com.nexbus.frontendNex.dto;

public class RouteDTO {
    private Integer routeId;
    private String name;
    private String start;
    private String end;
    private Double distance;

    // Getters and setters
    public Integer getRouteId() { return routeId; }
    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public String getRouteName() { return name; }
    public void setRouteName(String value) { this.name = value; }
    public String getStartLocation() { return start; }
    public void setStartLocation(String value) { this.start = value; }
    public String getEndLocation() { return end; }
    public void setEndLocation(String value) { this.end = value; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}