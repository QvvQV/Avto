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
//        public void setCover(String cover) { this.cover = cover; }
//
//        @JsonProperty("coverMobile")
//        public String getCoverMobile() { return coverMobile; }
//        public void setCoverMobile(String coverMobile) { this.coverMobile = coverMobile; }
//
//        @JsonProperty("modal")
//        public String getModal() { return modal; }
//        public void setModal(String modal) { this.modal = modal; }
//
//        @JsonProperty("modalMobile")
//        public String getModalMobile() { return modalMobile; }
//        public void setModalMobile(String modalMobile) { this.modalMobile = modalMobile; }

        @JsonProperty("authors")
        public List<Author> getAuthors() { return authors; }
//        public void setAuthors(List<Author> authors) { this.authors = authors; }
//
//        @JsonProperty("reviews")
//        public List<Review> getReviews() { return reviews; }
//        public void setReviews(List<Review> reviews) { this.reviews = reviews; }
//
//        @JsonProperty("previews")
//        public List<Preview> getPreviews() { return previews; }
//        public void setPreviews(List<Preview> previews) { this.previews = previews; }

        @JsonProperty("description")
        public String getDescription() { return description; }
//        public void setDescription(String description) { this.description = description; }
//
//        @JsonProperty("tariffDescription")
//        public String getTariffDescription() { return tariffDescription; }
//        public void setTariffDescription(String tariffDescription) { this.tariffDescription = tariffDescription; }

        @JsonProperty("price")
        public Integer getPrice() { return price; }
//        public void setPrice(Integer price) { this.price = price; }
//
//        @JsonProperty("oldPrice")
//        public Integer getOldPrice() { return oldPrice; }
//        public void setOldPrice(Integer oldPrice) { this.oldPrice = oldPrice; }
//
//        @JsonProperty("displayPrice")
//        public Integer getDisplayPrice() { return displayPrice; }
//        public void setDisplayPrice(Integer displayPrice) { this.displayPrice = displayPrice; }
//
//        @JsonProperty("displayOldPrice")
//        public Integer getDisplayOldPrice() { return displayOldPrice; }
//        public void setDisplayOldPrice(Integer displayOldPrice) { this.displayOldPrice = displayOldPrice; }
//
//        @JsonProperty("refundInitial")
//        public Boolean getRefundInitial() { return refundInitial; }
//        public void setRefundInitial(Boolean refundInitial) { this.refundInitial = refundInitial; }
//
//        @JsonProperty("initialPrice")
//        public Integer getInitialPrice() { return initialPrice; }
//        public void setInitialPrice(Integer initialPrice) { this.initialPrice = initialPrice; }
//
//        @JsonProperty("partPrice")
//        public Integer getPartPrice() { return partPrice; }
//        public void setPartPrice(Integer partPrice) { this.partPrice = partPrice; }
//
//        @JsonProperty("tariff")
//        public List<String> getTariff() { return tariff; }
//        public void setTariff(List<String> tariff) { this.tariff = tariff; }
//
//        @JsonProperty("dashboard")
//        public Object getDashboard() { return dashboard; }
//        public void setDashboard(Object dashboard) { this.dashboard = dashboard; }

        @JsonProperty("currency")
        public String getCurrency() { return currency; }
//        public void setCurrency(String currency) { this.currency = currency; }
//
//        @JsonProperty("displayCurrency")
//        public String getDisplayCurrency() { return displayCurrency; }
//        public void setDisplayCurrency(String displayCurrency) { this.displayCurrency = displayCurrency; }
//
//        @JsonProperty("currencySymbol")
//        public String getCurrencySymbol() { return currencySymbol; }
//        public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
//
//        @JsonProperty("displayCurrencySymbol")
//        public String getDisplayCurrencySymbol() { return displayCurrencySymbol; }
//        public void setDisplayCurrencySymbol(String displayCurrencySymbol) { this.displayCurrencySymbol = displayCurrencySymbol; }
//
//        @JsonProperty("initialInterval")
//        public Integer getInitialInterval() { return initialInterval; }
//        public void setInitialInterval(Integer initialInterval) { this.initialInterval = initialInterval; }
//
//        @JsonProperty("followingInterval")
//        public Integer getFollowingInterval() { return followingInterval; }
//        public void setFollowingInterval(Integer followingInterval) { this.followingInterval = followingInterval; }
//
//        @JsonProperty("language")
//        public String getLanguage() { return language; }
//        public void setLanguage(String language) { this.language = language; }
//
//        @JsonProperty("firstCheckbox")
//        public String getFirstCheckbox() { return firstCheckbox; }
//        public void setFirstCheckbox(String firstCheckbox) { this.firstCheckbox = firstCheckbox; }
//
//        @JsonProperty("secondCheckbox")
//        public String getSecondCheckbox() { return secondCheckbox; }
//        public void setSecondCheckbox(String secondCheckbox) { this.secondCheckbox = secondCheckbox; }
//
//        @JsonProperty("courseNovelty")
//        public Integer getCourseNovelty() { return courseNovelty; }
//        public void setCourseNovelty(Integer courseNovelty) { this.courseNovelty = courseNovelty; }
//
//        @JsonProperty("shortDescription")
//        public String getShortDescription() { return shortDescription; }
//        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
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
//        public void setProfession(String profession) { this.profession = profession; }

        @JsonProperty("description")
        public String getDescription() { return description; }
//        public void setDescription(String description) { this.description = description; }
//
//        @JsonProperty("photoUrl")
//        public String getPhotoUrl() { return photoUrl; }
//        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
//
//        @JsonProperty("showAvatarMobile")
//        public Boolean getShowAvatarMobile() { return showAvatarMobile; }
//        public void setShowAvatarMobile(Boolean showAvatarMobile) { this.showAvatarMobile = showAvatarMobile; }
//
//        @JsonProperty("showAvatarDesktop")
//        public Boolean getShowAvatarDesktop() { return showAvatarDesktop; }
//        public void setShowAvatarDesktop(Boolean showAvatarDesktop) { this.showAvatarDesktop = showAvatarDesktop; }
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

//        @JsonProperty("reviewerName")
//        public String getReviewerName() { return reviewerName; }
//        public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
//
//        @JsonProperty("reviewerProfession")
//        public String getReviewerProfession() { return reviewerProfession; }
//        public void setReviewerProfession(String reviewerProfession) { this.reviewerProfession = reviewerProfession; }
//
//        @JsonProperty("reviewerPhotoUrl")
//        public String getReviewerPhotoUrl() { return reviewerPhotoUrl; }
//        public void setReviewerPhotoUrl(String reviewerPhotoUrl) { this.reviewerPhotoUrl = reviewerPhotoUrl; }

        @JsonProperty("text")
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Preview {
        private Integer id;
//        private String fileUrl;

        @JsonProperty("id")
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

//        @JsonProperty("fileUrl")
//        public String getFileUrl() { return fileUrl; }
//        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    }
}
