package com.rajcloud.api;

public final class ApiResource {
    public static final String API = "/api";
    public static final String V1 = API + "/v1";
    public static final String V2 = API + "/v2";

    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_TOKEN = AUTH + "/token";
    public static final String AUTH_VALIDATE = AUTH + "/validate";

    public static final String USERS = V1 + "/users";
    public static final String USER_BY_ID = "/{id}";

    public static final String INVENTORY = V1 + "/inventory";
    public static final String INVENTORY_BY_PRODUCT_ID = "/{productId}";

    public static final String ORDERS = V1 + "/orders";
    public static final String ORDER_BY_ID = "/{id}";

    public static final String PAYMENTS = V1 + "/payments";
    public static final String NOTIFICATIONS = V1 + "/notifications";

    public static final String SEARCH = V1 + "/search";
    public static final String SEARCH_ORDERS = "/orders";

    private ApiResource() {
    }
}
