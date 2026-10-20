package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubLinksResponse(
    @JsonProperty("items") List<ClubLinkResponse> items,
    @JsonProperty("pagination") ClubReferralsResponse.Pagination pagination

) {
    public List<ClubLinkResponse> safeItems() {
        return items == null ? List.of() : items;
    }
    public boolean hasPagination() {
        return pagination != null;
    }
}