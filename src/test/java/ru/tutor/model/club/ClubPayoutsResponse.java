package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubPayoutsResponse (
        @JsonProperty("items") List<ClubWithdrawalResponse> items,
        @JsonProperty("pagination")
        ClubReferralsResponse.Pagination pagination
) {
    public List<ClubWithdrawalResponse> safeItems() {
        return items == null ? List.of() : items;
    }

    public boolean hasPagination() {
        return pagination != null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pagination (
            @JsonProperty("count") Integer count,
            @JsonProperty("total") Integer total,
            @JsonProperty("per_page") Integer perPage,
            @JsonProperty("page") Integer page,
            @JsonProperty("pages") Integer pages
    ) {}

}
