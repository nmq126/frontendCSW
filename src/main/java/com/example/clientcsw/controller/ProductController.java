package com.example.clientcsw.controller;

import com.example.clientcsw.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductController {

    private final String REST_API_BASE = "http://localhost:8084/api/v1/products";

    private static Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();

        // Config logging for client side
        clientConfig.register(
                new LoggingFeature(
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
                        Level.INFO,
                        LoggingFeature.Verbosity.PAYLOAD_ANY,
                        10000));

        return ClientBuilder.newClient(clientConfig);
    }

    @GetMapping
    public String index(Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE);
        List<Product> list = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        model.addAttribute("listProduct", list);
        return "index";
    }

    @GetMapping("add-product")
    public String addProduct(){
        return "add-product";
    }

    @PostMapping("add-product")
    public String addProduct(@RequestParam String name,
                              @RequestParam Integer quantity,
                              @RequestParam Double price){
        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setPrice(price);

        String jsonUser = convertToJson(product);

        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonUser, MediaType.APPLICATION_JSON));
        return "redirect:/";
    }

    @GetMapping("sell-product/{id}")
    public String sellProduct(@PathVariable(value = "id") Long id, Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE + "/" + id);
        Product product = target.request(MediaType.APPLICATION_JSON_TYPE).get(Product.class);
        model.addAttribute("existProduct", product);
        return "sell-product";
    }

    @PostMapping("sell-product")
    public String sellProduct(@RequestParam Long id, @RequestParam Integer quantity){

        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE + "/" + id + "?quantity=" + quantity);

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(null, MediaType.APPLICATION_JSON));
        return "redirect:/";
    }

//
//    @RequestMapping(value = "updateProduct", method = RequestMethod.GET)
//    public String updateProduct(@PathParam(value = "id") Long id, Model model){
//        Client client = createJerseyRestClient();
//        WebTarget target = client.target(REST_API_BASE + "/" + id);
//        Product product = target.request(MediaType.APPLICATION_JSON_TYPE).get(Product.class);
//        model.addAttribute("existProduct", product);
//        return "updateproduct";
//    }
//

//


    private static String convertToJson(Product product) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(product));
            return mapper.writeValueAsString(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
