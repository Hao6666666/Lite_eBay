package com.csye6225.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ImageModel {

    @JsonProperty("image_id")
    private String imageId;

//    @JsonProperty("user_id")
//    private Integer userId;

    @JsonProperty("product_id")
    private Integer Product_id;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("date_created")
    @DateTimeFormat()
    private Date dateCreated;

    @JsonProperty("s3_bucket_path")
    private String s3BucketPath;
}
