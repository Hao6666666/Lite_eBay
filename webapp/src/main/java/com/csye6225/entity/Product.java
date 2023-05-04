package com.csye6225.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Table(name = "ProductTable")
public class Product {

    public Product() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("name")
    @Column(nullable = false, length = 64)
    private String name;

    @JsonProperty("description")
    @Column(nullable = false, length = 64)
    private String description;

    @JsonProperty("sku")
    @Column(nullable = false, length = 64)
    private String sku;

    @JsonProperty("manufacturer")
    @Column(nullable = false, length = 64)
    private String manufacturer;

    @JsonProperty("quantity")
    @Column(nullable = false, length = 20, updatable = true, columnDefinition = "integer")
    @Range(max=100, min=0)
    private Integer quantity;

    @CreatedDate
    @JsonProperty("date_added")
    @DateTimeFormat()
    private Date dateAdded;

    @LastModifiedDate
    @JsonProperty("date_last_updated")
    @DateTimeFormat()
    private Date dateLastUpdated;

    @JsonProperty("owner_user_id")
    @Column(nullable = false)
    private Integer ownerUserId;

}
