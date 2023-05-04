package com.csye6225.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.entity.Image;
import com.csye6225.entity.Product;
import com.csye6225.entity.User;
import com.csye6225.errors.*;
import com.csye6225.mapper.ImageMapper;
import com.csye6225.mapper.ProductMapper;
import com.csye6225.mapper.UserMapper;
import com.csye6225.models.ImageModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j

public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    String BUCKET = System.getenv("BUCKET_NAME");

//    String BUCKET = "csye6225-s3-02-24";
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    ImageMapper imageMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    AmazonS3 s3client;

    public ImageModel addProductImage(Integer id, String token, MultipartFile file) throws IOException {
        // check user existence
        Product oldProduct = productMapper.findById(id).get();
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        String username=values[0];
        String password=values[1];
        User dbUser = userMapper.getUserByUsername(username);
        String dbPassword = dbUser.getPassword();

        if (dbUser == null
                || !bCryptPasswordEncoder.matches(password, dbPassword)) {
            throw new AuthenticationError("");
        }
        if (!dbUser.getId().equals(oldProduct.getOwnerUserId())){
            throw new IllegalChangeError("You can not change other users products");
        }
        // check product existence
        checkProdExist(id);

        // add image to product

        String fileName = file.getOriginalFilename();
        String filePath = UUID.randomUUID().toString();

        byte[] imageData = file.getBytes();
        if (imageData.length == 0){
            throw new badRequest("Empty File");
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(imageData.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET,
                filePath + "/" + fileName, new ByteArrayInputStream(imageData), metadata);
        s3client.putObject(putObjectRequest);


        Image image = new Image();
//        image.setUser(dbUser);
        image.setProduct(oldProduct);

        image.setFileName(fileName);
        String s3ObjectPath = s3client.getUrl(BUCKET, filePath).toString();
        image.setS3BucketPath(s3ObjectPath);
        ImageModel imageModel = new ImageModel();
        imageMapper.save(image);
        BeanUtils.copyProperties(image, imageModel);

        Object json = gson(imageModel);
        logger.info("Image created: " +  json);

        return imageModel;
    }

    public List<Image> getProductImage1(Integer id, String token){
        // check user existence
        authcheck(id, token);
        // check product existencexx
        checkProdExist(id);

        List<Image> res = imageMapper.findByProductId(id);

        List<Object>jsonList = getJsonList(res);
        logger.info("Image get: " +  jsonList);

        return res;
    }

    public List<Image> getProductImage2(Integer prod_id, String img_id, String token){
        // check user existence
        authcheck(prod_id, token);
        // check product existencexx
        checkProdExist(prod_id);

        List<Image> res = imageMapper.findByImageId(img_id);

        List<Object>jsonList = getJsonList(res);
        logger.info("Get " +img_id+": " +  jsonList);
        return res;
    }

    public ResponseEntity deleteProductImage(Integer prod_id, String img_id, String token){
        // check user existence
        authcheck(prod_id, token);
        // check product existencexx
        checkProdExist(prod_id);
        checkImageExist(img_id);

        // delete s3
        Image delImage = imageMapper.findByImageId(img_id).get(0);
        String delFilenName = delImage.getFileName();
        String s3ObjectPath = delImage.getS3BucketPath();

        String[] path = s3ObjectPath.split("/");
        String delPath = path[path.length - 1];

        s3client.deleteObject(BUCKET, delPath+'/'+ delFilenName);

        // delete db
        imageMapper.deleteById(img_id);
        logger.info("Product: "+prod_id+", image "+ img_id +" deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private void authcheck(Integer id, String token) {
        Product oldProduct = productMapper.findById(id).get();
        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        String username=values[0];
        String password=values[1];
        User dbUser = userMapper.getUserByUsername(username);
        String dbPassword = dbUser.getPassword();

        if (dbUser == null
                || !bCryptPasswordEncoder.matches(password, dbPassword)) {
            logger.error("AuthenticationError");
            throw new AuthenticationError("");
        }
        if (!dbUser.getId().equals(oldProduct.getOwnerUserId())){
            logger.error("You can not change other users products");
            throw new IllegalChangeError("You can not change other users products");
        }
    }


    private void checkProdExist(Integer id) {
        if(!productMapper.existsById(id)) {
            logger.error("Product not found");
            throw new NoPoductFoundError("Product not found");
        }
    }

    private void checkImageExist(String img_id) {
        if(!imageMapper.existsById(img_id)) {
            logger.error("Image not found");
            throw new NoPoductFoundError("Image not found");
        }
    }

    private Object gson(ImageModel imageModel) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("image_id", imageModel.getImageId());
        jsonObject.addProperty("product_id", imageModel.getProduct_id());
        jsonObject.addProperty("file_name", imageModel.getFileName());
        jsonObject.addProperty("date_created", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(imageModel.getDateCreated()));
        jsonObject.addProperty("s3_bucket_path", imageModel.getS3BucketPath());

        String json = gson.toJson(jsonObject);
        return json;
    }

    private List<Object> getJsonList(List<Image> res){

        List<Object> jsonList = new ArrayList<>();
        for (Image image : res) {
            ImageModel imageModel = new ImageModel();
            BeanUtils.copyProperties(image, imageModel);

            Object json = gson(imageModel);
            jsonList.add(json);
        }
        return jsonList;
    }


}
