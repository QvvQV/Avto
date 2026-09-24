package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubLinkResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("name") String name,
    @JsonProperty("code") String code,
    @JsonProperty("url") String url,
    @JsonProperty("createdAt") String createdAt,
    @JsonProperty("clicks") Integer clicks,
    @JsonProperty("product") Product product
) {
    public boolean hasUrl() {
        return url != null && !url.trim().isEmpty();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Product(
        @JsonProperty("id") Integer id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug
    ) {}
}