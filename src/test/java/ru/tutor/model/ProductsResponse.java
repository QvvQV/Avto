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
    private String productId;

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

        public boolean isCompleted() {
            return total != null && total.equals(progress);
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

    public String getCategoryAsString() {
        if (categories == null || categories.isEmpty()) {
            return "";
        }
        return categories.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
    private final Map<String, Object> additionalProperties = new HashMap<>();

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
