package com.csye6225.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@Table(name = "ImageTable")
public class Image {
    public Image() {}

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonProperty("image_id")
    private String imageId;

//    @JsonProperty("user_id")
//    @Column(nullable = false, length = 64)
//    private Integer userId;
//
//    @JsonProperty("product_id")
//    @Column(nullable = false, length = 64)
//    private Integer productId;

    @JsonProperty("file_name")
    @Column(nullable = false, length = 64)
    private String fileName;

    @CreatedDate
    @JsonProperty("date_created")
    @DateTimeFormat()
    private Date dateCreated;

    @JsonProperty("s3_bucket_path")
    @Column(nullable = false)
    private String s3BucketPath;


//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;

//    public Integer getUserId(){
//        return user.getId();
//    }

    public Integer getProduct_id(){
        return product.getId();
    }

}
