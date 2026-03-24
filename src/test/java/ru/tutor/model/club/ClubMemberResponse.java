package ru.tutor.model.club;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubMemberResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("memberType")
    private String memberType;

    @JsonProperty("membershipDate")
    private String membershipDate;

    @JsonProperty("referralLink")
    private String referralLink;

    @JsonProperty("details")
    private String details;

   // getters/setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getMembershipDate() {
        return membershipDate;
    }

    public String getReferralLink() {
        return referralLink;
    }

    public String getDetails() {
        return details;
    }

    // Дополнительные методы

    public boolean hasReferralLink() {
        return referralLink != null && !referralLink.trim().isEmpty();
    }

    public LocalDate getMembershipDateAsLocalDate() {
        if (membershipDate != null && !membershipDate.isEmpty()) {
            try {
                return LocalDate.parse(membershipDate, DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean isMembershipActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public boolean isMembershipDateValid() {
        if (membershipDate == null) return false;
        try{
            LocalDate date = LocalDate.parse(membershipDate, DateTimeFormatter.ISO_DATE);
            return !date.isAfter(LocalDate.now()); // Дата не должна быть в будущем
        } catch(Exception e){
            return false;
        }
    }
}
