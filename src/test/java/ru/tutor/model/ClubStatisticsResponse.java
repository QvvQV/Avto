package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubStatisticsResponse {

    @JsonProperty("balance")
    private Integer balance;

    @JsonProperty("referrals")
    private Referrals referrals;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Referrals {
        @JsonProperty("own")
        private Integer own;

        @JsonProperty("all")
        private Integer all;

        public Integer getOwn() {
            return own;
        }

        public void setOwn(Integer own) {
            this.own = own;
        }

        public Integer getAll() {
            return all;
        }

        public void setAll(Integer all) {
            this.all = all;
        }

        public Integer getTotalReferrals() {
            if (own != null && all != null) {
                return own + all;
            }
            return null;
        }
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Referrals getReferrals() {
        return referrals;
    }

    public void setReferrals(Referrals referrals) {
        this.referrals = referrals;
    }

    // Дополнительные методы
    public boolean hasBalance() {
        return balance != null;
    }

    public boolean hasPositiveBalance() {
        return balance != null && balance > 0;
    }

    public boolean hasReferrals() {
        return referrals != null &&
                referrals.getOwn() != null &&
                referrals.getAll() != null &&
                (referrals.getOwn() > 0 || referrals.getAll() > 0);
    }
}
