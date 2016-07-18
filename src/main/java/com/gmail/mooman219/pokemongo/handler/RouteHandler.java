package com.gmail.mooman219.pokemongo.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class RouteHandler implements Handler<RoutingContext> {

    /**
     * The fully qualified address with the route included.
     */
    private final String address;

    /**
     * The route this handler lives.
     */
    private final String route;

    /**
     * Creates a new AuthentcationHandler.
     *
     * @param address the fully qualified address with the route included.
     * @param route the route this handler lives.
     */
    public RouteHandler(String address, String route) {
        this.address = address + route;
        this.route = route;
    }

    public String getAddress() {
        return address;
    }

    public String getRoute() {
        return route;
    }

    /**
     * Registers this handler with the given router.
     *
     * @param router the router to register with.
     */
    public void register(Router router) {
        router.route(this.route).handler(this);
    }
}
