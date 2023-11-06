package com.abc.gw.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.cloud.gateway.route.RouteDefinition;

@SuppressWarnings("serial")
public class RouteConfig implements Serializable{


    private Spring spring;
    
    public Spring getSpring() {
        return spring;
    }

    public void setSpring(Spring spring) {
        this.spring = spring;
    }

    private static class Spring {
        
        @SuppressWarnings("unused")
        public Spring() {
        }

        private Cloud cloud;
 
        @SuppressWarnings("unused")
        public Cloud getCloud() {
            return cloud;
        }
        @SuppressWarnings("unused")
        public void setCloud(Cloud cloud) {
            this.cloud = cloud;
        }
        
    }

    private static class Cloud{
        @SuppressWarnings("unused")
        public Cloud() {
        }

        private Gateway gateway;

        @SuppressWarnings("unused")
        public Gateway getGateway() {
            return gateway;
        }
   
        @SuppressWarnings("unused")
        public void setGateway(Gateway gateway) {
            this.gateway = gateway;
        }
        
    }

    private static class Gateway{


        private List<RouteDefinition> routes;
        
        @SuppressWarnings("unused")
        public Gateway() {
        }
        
        @SuppressWarnings("unused")
        public List<RouteDefinition> getRoutes() {
            return routes;
        }

        @SuppressWarnings("unused")
        public void setRoutes(List<RouteDefinition> routes) {
            this.routes = routes;
        }
    }

    public List<RouteDefinition> getRouteDefinition(){
        return spring.cloud.gateway.routes;
    }
}