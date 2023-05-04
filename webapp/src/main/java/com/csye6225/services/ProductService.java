package com.csye6225.services;

import com.csye6225.entity.Product;
import com.csye6225.entity.User;
import com.csye6225.errors.*;
import com.csye6225.mapper.ProductMapper;
import com.csye6225.mapper.UserMapper;
import com.csye6225.models.ProductModel;
import com.csye6225.models.UserModel;
import com.csye6225.restController.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.List;

@Service
@Slf4j
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;



    public ProductModel addProduct(Product product, String token) {

        String base64Credentials = token.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        String username=values[0];
        String password=values[1];
        User dbUser = userMapper.getUserByUsername(username);
        String dbPassword = dbUser.getPassword();
        checkDuplicatedSku(product);

        if (dbUser == null
                || !bCryptPasswordEncoder.matches(password, dbPassword)) {
            logger.error("AuthenticationError");
            throw new AuthenticationError("");
        }

        checkProduct(product);
        product.setOwnerUserId(dbUser.getId());
        ProductModel productModel = new ProductModel();
        productMapper.save(product);
        BeanUtils.copyProperties(product, productModel);

        Object json = gson(productModel);
        logger.info("Product created: " +  json);

        return productModel;
    }

    public ProductModel getProduct(Integer id) {
        checkProdExist(id);
        Product product = productMapper.findById(id).get();
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(product, productModel);

        Object json = gson(productModel);
        logger.info("Product get: " +  json);

        return productModel;
    }

    public ResponseEntity updateProduct(Integer id, Product product, String token) {
        checkProdExist(id);
        authCheck(id, token);
        checkProduct(product);

        Product currentProduct = productMapper.findById(id).get();
        if (!currentProduct.getSku().equals(product.getSku())) {
            checkDuplicatedSku(product);
        }

        BeanUtils.copyProperties(product, currentProduct, getNullPropertyNames(product));
        productMapper.save(currentProduct);
        logger.info("Product "+id+" update");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity patchProduct(Integer id, Product product, String token) {
        checkProdExist(id);
        authCheck(id, token);

        if (StringUtils.isEmpty(product.getName())
                && StringUtils.isEmpty(product.getDescription())
                && StringUtils.isEmpty(product.getManufacturer())
                && StringUtils.isEmpty(product.getSku())
                && product.getQuantity() == null) {
            logger.error("Product has missing data");
            throw new badRequest("Product has missing data");
        }

        Product currentProduct = productMapper.findById(id).get();
        if (!currentProduct.getSku().equals(product.getSku())) {
            checkDuplicatedSku(product);
        }

        BeanUtils.copyProperties(product, currentProduct, getNullPropertyNames(product));
        productMapper.save(currentProduct);
        logger.info("Product "+id+" pached successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity deleteProduct(Integer id, String token) {
        checkProdExist(id);
        authCheck(id, token);
        productMapper.deleteById(id);
        logger.info("Product "+id+" deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void checkProduct(Product product) {
        if (StringUtils.isEmpty(product.getName())
                || StringUtils.isEmpty(product.getDescription())
                || StringUtils.isEmpty(product.getManufacturer())
                || StringUtils.isEmpty(product.getSku())
                || product.getQuantity() == null) {
            logger.error("Product has missing data");
            throw new badRequest("Product has missing data");
        }

        if (product.getQuantity() < 0 ) {
            logger.error("Quantity cannot be negative");
            throw new UpdateError("Quantity cannot be negative");
        } else if (product.getQuantity() > 100) {
            logger.error("Quantity cannot be greater than 100");
            throw new UpdateError("Quantity cannot be greater than 100");
        }
        if (product.getOwnerUserId()!=null
                || product.getId()!=null
                || product.getDateAdded()!=null
                || product.getDateLastUpdated()!=null) {
            logger.error("bad request");
            throw new badRequest("");
        }
    }
    private void checkDuplicatedSku(Product product) {

        List<Product> oldProducts = productMapper.findBySku(product.getSku());
        if (oldProducts!=null && oldProducts.size()>0) {
            logger.error("Sku already exists: " + product.getSku());
            throw new DuplicatedSku("Sku already exists: " + product.getSku() );
        }
    }

    private void authCheck(Integer id, String token) {
        Product oldProduct = productMapper.findById(id).get();
        if (token != null && token.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = token.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username=values[0];
            String password=values[1];
            User dbUser = userMapper.getUserByUsername(username);
            String dbPassword = dbUser.getPassword();

            if (dbUser==null
                    || !bCryptPasswordEncoder.matches(password, dbPassword)) {
                logger.error("Authentication failed");
                throw new AuthenticationError("");
            }

            if (!dbUser.getId().equals(oldProduct.getOwnerUserId())){
                logger.error("You can not change other users products");
                throw new IllegalChangeError("You can not change other users products");
            }
        }
    }

    private void checkProdExist(Integer id) {
        if(!productMapper.existsById(id)) {
            logger.error("Product not found");
            throw new NoPoductFoundError("Product not found");
        }
    }


    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    private String gson(ProductModel productModel) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", productModel.getId());
        jsonObject.addProperty("name", productModel.getName());
        jsonObject.addProperty("description", productModel.getDescription());
        jsonObject.addProperty("sku", productModel.getSku());
        jsonObject.addProperty("manufacturer", productModel.getManufacturer());
        jsonObject.addProperty("quantity", productModel.getQuantity());
        jsonObject.addProperty("date_added", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(productModel.getDateAdded()));
        jsonObject.addProperty("date_last_updated", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(productModel.getDateLastUpdated()));
        jsonObject.addProperty("owner_user_id", productModel.getOwnerUserId());

        return gson.toJson(jsonObject);
    }

}
