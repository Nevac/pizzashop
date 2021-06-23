package ch.cagatay.pizzashop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("in-process")
    IN_PROCESS,

    @JsonProperty("in-delivery")
    IN_DELIVERY,

    @JsonProperty("completed")
    COMPLETED,

    @JsonProperty("canceled")
    CANCELED
}
