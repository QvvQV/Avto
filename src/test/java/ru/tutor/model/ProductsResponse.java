package ru.tutor.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("coverMobile")
    private String coverMobile;

    @JsonProperty("addMaterialsCost")
    private Integer addMaterialsCost;

    @JsonProperty("authors")
    private List<Object> authors;

    @JsonProperty("background")
    private String background;

    @JsonProperty("backgroundMini")
    private String backgroundMini;

    @JsonProperty("card")
    private ProductsResponse card;

    @JsonProperty("categories")
    private List<Object> categories;

    @JsonProperty("certIssueAvailable")
    private Boolean certIssueAvailable;

    @JsonProperty("certificate")
    private Object certificate;

    @JsonProperty("dashboard")
    private Object dashboard;

    @JsonProperty("dashboardData")
    private Object dashboardData;

    @JsonProperty("description")
    private String description;

    @JsonProperty("expires")
    private String expires;

    @JsonProperty("firstCheckbox")
    private String firstCheckbox;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    @JsonProperty("isExpired")
    private Boolean isExpired;

    @JsonProperty("isHasAttachedFiles")
    private Boolean isHasAttachedFiles;

    @JsonProperty("isLite")
    private Boolean isLite;

    @JsonProperty("isPinned")
    private Boolean isPinned;

    @JsonProperty("isPremium")
    private Boolean isPremium;

    @JsonProperty("isVideoPreviewEnabled")
    private Boolean isVideoPreviewEnabled;

    @JsonProperty("lessonsAvailable")
    private Integer lessonsAvailable;

    @JsonProperty("modules")
    private Modules modules;

    @JsonProperty("nextLessonDays")
    private Integer nextLessonDays;

    @JsonProperty("orderCreatedAt")
    private String orderCreatedAt;

    @JsonProperty("orderStatusUpdateAt")
    private String orderStatusUpdateAt;

    @JsonProperty("pausedTill")
    private String pausedTill;

    @JsonProperty("pinnedUpdateAt")
    private String pinnedUpdateAt;

    @JsonProperty("previewKinescopeId")
    private String previewKinescopeId;

    @JsonProperty("previews")
    private List<Object> previews;

    @JsonProperty("productId")
    private Integer productId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("renewalProductPrice")
    private Integer renewalProductPrice;

    @JsonProperty("reviews")
    private List<Object> reviews;

    @JsonProperty("secondCheckbox")
    private String secondCheckbox;

    @JsonProperty("shortDescription")
    private String shortDescription;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("tariff")
    private List<String> tariff;

    // Вложенные классы
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        @JsonProperty("firstSix")
        private String firstSix;

        @JsonProperty("lastFour")
        private String lastFour;

        @JsonProperty("issuer")
        private String issuer;

        @JsonProperty("type")
        private String type;

        public String getFirstSix() {
            return firstSix;
        }

        public void setFirstSix(String firstSix) {
            this.firstSix = firstSix;
        }

        public String getLastFour() {
            return lastFour;
        }

        public void setLastFour(String lastFour) {
            this.lastFour = lastFour;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Modules {

        @JsonProperty("total")
        private Integer total;

        @JsonProperty("progress")
        private Integer progress;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getProgress() {
            return progress;
        }

        public void setProgress(Integer progress) {
            this.progress = progress;
        }

        public Double getProgressPercentage() {
            if(total == null && progress !=null && total > 0) {
                return progress * 100.0 / total;
            }
            return 0.0;
        }

        public boolean isCompleted() {
            return total != null && progress != null && total.equals(progress);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCoverMobile() {
        return coverMobile;
    }

    public void setCoverMobile(String coverMobile) {
        this.coverMobile = coverMobile;
    }

    public Integer getAddMaterialsCost() {
        return addMaterialsCost;
    }

    public void setAddMaterialsCost(Integer addMaterialsCost) {
        this.addMaterialsCost = addMaterialsCost;
    }

    public List<Object> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Object> authors) {
        this.authors = authors;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBackgroundMini() {
        return backgroundMini;
    }

    public void setBackgroundMini(String backgroundMini) {
        this.backgroundMini = backgroundMini;
    }

//    public String getCard() {
//        return card;
//    }
//
//    public void setCard(String card) {
//        this.card = card;
//    }

//    public List<Object> getCategories() {
//        return categories;
//    }

    public String getCategoryAsString() {
        if (categories == null || categories.isEmpty()) {
            return "";
        }
        return categories.toString();
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

    public Boolean getCertIssueAvailable() {
        return certIssueAvailable;
    }

    public void setCertIssueAvailable(Boolean certIssueAvailable) {
        this.certIssueAvailable = certIssueAvailable;
    }

    public Object getCertificate() {
        return certificate;
    }

    public void setCertificate(Object certificate) {
        this.certificate = certificate;
    }

    public Object getDashboard() {
        return dashboard;
    }

    public void setDashboard(Object dashboard) {
        this.dashboard = dashboard;
    }

    public Object getDashboardData() {
        return dashboardData;
    }

    public void setDashboardData(Object dashboardData) {
        this.dashboardData = dashboardData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getFirstCheckbox() {
        return firstCheckbox;
    }

    public void setFirstCheckbox(String firstCheckbox) {
        this.firstCheckbox = firstCheckbox;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    public Boolean getHasAttachedFiles() {
        return isHasAttachedFiles;
    }

    public void setHasAttachedFiles(Boolean hasAttachedFiles) {
        isHasAttachedFiles = hasAttachedFiles;
    }

    public Boolean getLite() {
        return isLite;
    }

    public void setLite(Boolean lite) {
        isLite = lite;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public Boolean getVideoPreviewEnabled() {
        return isVideoPreviewEnabled;
    }

    public void setVideoPreviewEnabled(Boolean videoPreviewEnabled) {
        isVideoPreviewEnabled = videoPreviewEnabled;
    }

    public Integer getLessonsAvailable() {
        return lessonsAvailable;
    }

    public void setLessonsAvailable(Integer lessonsAvailable) {
        this.lessonsAvailable = lessonsAvailable;
    }

    public Modules getModules() {
        return modules;
    }

    public void setModules(Modules modules) {
        this.modules = modules;
    }

    public Integer getNextLessonDays() {
        return nextLessonDays;
    }

    public void setNextLessonDays(Integer nextLessonDays) {
        this.nextLessonDays = nextLessonDays;
    }

    public String getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public void setOrderCreatedAt(String orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public String getOrderStatusUpdateAt() {
        return orderStatusUpdateAt;
    }

    public void setOrderStatusUpdateAt(String orderStatusUpdateAt) {
        this.orderStatusUpdateAt = orderStatusUpdateAt;
    }

    public String getPausedTill() {
        return pausedTill;
    }

    public void setPausedTill(String pausedTill) {
        this.pausedTill = pausedTill;
    }

    public String getPinnedUpdateAt() {
        return pinnedUpdateAt;
    }

    public void setPinnedUpdateAt(String pinnedUpdateAt) {
        this.pinnedUpdateAt = pinnedUpdateAt;
    }

    public String getPreviewKinescopeId() {
        return previewKinescopeId;
    }

    public void setPreviewKinescopeId(String previewKinescopeId) {
        this.previewKinescopeId = previewKinescopeId;
    }

    public List<Object> getPreviews() {
        return previews;
    }

    public void setPreviews(List<Object> previews) {
        this.previews = previews;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRenewalProductPrice() {
        return renewalProductPrice;
    }

    public void setRenewalProductPrice(Integer renewalProductPrice) {
        this.renewalProductPrice = renewalProductPrice;
    }

    public List<Object> getReviews() {
        return reviews;
    }

    public void setReviews(List<Object> reviews) {
        this.reviews = reviews;
    }

    public String getSecondCheckbox() {
        return secondCheckbox;
    }

    public void setSecondCheckbox(String secondCheckbox) {
        this.secondCheckbox = secondCheckbox;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getTariff() {
        return tariff;
    }

    public void setTariff(List<String> tariff) {
        this.tariff = tariff;
    }

    // Дополнительные методы
    public boolean isLifetimeAccess() {
        return name != null && name.contains("Пожизненный доступ");
    }

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    public Object getCard() {
        return additionalProperties.get("card");
    }
}
