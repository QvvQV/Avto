package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubPayoutSummaryResponse(
    @JsonProperty("balance") Integer balance,
    @JsonProperty("availableForWithdrawal") Integer availableForWithdrawal,
    @JsonProperty("shareBonusExcludedAmount") Integer shareBonusExcludedAmount,
    @JsonProperty("minWithdrawalAmount") Integer minWithdrawalAmount,
    @JsonProperty("clubMembership") String clubMembership,
    @JsonProperty("profileValid") Boolean profileValid,
    @JsonProperty("canRequestWithdrawal") Boolean canRequestWithdrawal
) {
    public boolean isFullMembership() {
        return "full".equalsIgnoreCase(clubMembership);
    }

    public boolean hasWithdrawableBalance() {
        return balance != null && availableForWithdrawal != null
                && availableForWithdrawal > 0;
    }
}