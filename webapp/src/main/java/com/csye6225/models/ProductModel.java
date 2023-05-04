package com.csye6225.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ProductModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("date_added")
    @DateTimeFormat()
    private Date dateAdded;

    @JsonProperty("date_last_updated")
    @DateTimeFormat()
    private Date dateLastUpdated;

    @JsonProperty("owner_user_id")
    private Integer ownerUserId;
}
