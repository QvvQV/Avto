package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    private List<Product> products;

    @JsonProperty("products")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private Integer id;
        private String name;
        private String cover;
        private String coverMobile;
        private String modal;
        private String modalMobile;
        private List<Author> authors;
        private List<Review> reviews;
        private List<Preview> previews;
        private String description;
        private String tariffDescription;
        private Integer price;
        private Integer oldPrice;
        private Integer displayPrice;
        private Integer displayOldPrice;
        private Boolean refundInitial;
        private Integer initialPrice;
        private Integer partPrice;
        private List<String> tariff;
        private Object dashboard;
        private String currency;
        private String displayCurrency;
        private String currencySymbol;
        private String displayCurrencySymbol;
        private Integer initialInterval;
        private Integer followingInterval;
        private String language;
        private String firstCheckbox;
        private String secondCheckbox;
        private Integer courseNovelty;
        private String shortDescription;

        // Getters and Setters
        @JsonProperty("id")
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("cover")
        public String getCover() { return cover; }

        @JsonProperty("authors")
        public List<Author> getAuthors() { return authors; }

        @JsonProperty("description")
        public String getDescription() { return description; }

        @JsonProperty("price")
        public Integer getPrice() { return price; }

        @JsonProperty("currency")
        public String getCurrency() { return currency; }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private Integer id;
        private String name;
        private String profession;
        private String description;
        private String photoUrl;
        private Boolean showAvatarMobile;
        private Boolean showAvatarDesktop;

        @JsonProperty("id")
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        @JsonProperty("name")
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("profession")
        public String getProfession() { return profession; }


        @JsonProperty("description")
        public String getDescription() { return description; }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Review {
        private Integer id;
        private String reviewerName;
        private String reviewerProfession;
        private String reviewerPhotoUrl;
        private String text;

        @JsonProperty("id")
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        @JsonProperty("text")
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Preview {
        private Integer id;

        @JsonProperty("id")
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }
}
