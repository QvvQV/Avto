package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubMemberResponse(
    @JsonProperty("id") Integer id,
    @JsonProperty("isActive") Boolean isActive,
    @JsonProperty("memberType") String memberType,
    @JsonProperty("membershipDate") String membershipDate,
    @JsonProperty("referralLink") String referralLink,
    @JsonProperty("details") String details
) {
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
        try {
            LocalDate date = LocalDate.parse(membershipDate, DateTimeFormatter.ISO_DATE);
            return !date.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
