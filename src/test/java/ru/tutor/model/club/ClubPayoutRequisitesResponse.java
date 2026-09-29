package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubPayoutRequisitesResponse(
    @JsonProperty("type") String type,
    @JsonProperty("typeLabel") String typeLabel,
    @JsonProperty("fullName") String fullName,
    @JsonProperty("details") Details details
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Details(
        @JsonProperty("addressFact") String addressFact,
        @JsonProperty("addressRegister") String addressRegister,
        @JsonProperty("bankAccount") String bankAccount,
        @JsonProperty("bankKorAccount") String bankKorAccount,
        @JsonProperty("bankName") String bankName,
        @JsonProperty("bik") String bik,
        @JsonProperty("email") String email,
        @JsonProperty("ogrnip") String ogrnip,
        @JsonProperty("idEdo") String idEdo,
        @JsonProperty("inn") String inn,
        @JsonProperty("fullName") String fullName,
        @JsonProperty("phone") String phone,
        @JsonProperty("isSameAddress") Boolean isSameAddress,
        @JsonProperty("isResident") Boolean isResident,
        @JsonProperty("dateBirth") String dateBirth,
        @JsonProperty("passSerial") String passSerial,
        @JsonProperty("passNumber") String passNumber,
        @JsonProperty("passIssuer") String passIssuer,
        @JsonProperty("passIssued") String passIssued,
        @JsonProperty("passDepartment") String passDepartment
    ) {}
}