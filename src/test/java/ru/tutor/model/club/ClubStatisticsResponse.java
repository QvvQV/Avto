package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubStatisticsResponse(
    @JsonProperty("balance") Integer balance,
    @JsonProperty("referrals") Referrals referrals
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Referrals(
        @JsonProperty("own") Integer own,
        @JsonProperty("all") Integer all
    ) {
        public Integer getTotalReferrals() {
            if (own != null && all != null) {
                return own + all;
            }
            return null;
        }
    }

    public boolean hasBalance() {
        return balance != null;
    }

    public boolean hasPositiveBalance() {
        return balance != null && balance > 0;
    }

    public boolean hasReferrals() {
        return referrals != null &&
                referrals.own() != null &&
                referrals.all() != null &&
                (referrals.own() > 0 || referrals.all() > 0);
    }
}
