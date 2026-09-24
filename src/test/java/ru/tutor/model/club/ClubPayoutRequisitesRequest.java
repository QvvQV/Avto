package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubPayoutRequisitesRequest(
    @JsonProperty("type") String type,
    @JsonProperty("idEdo") String idEdo,
    @JsonProperty("phone") String phone,
    @JsonProperty("isResident") Boolean isResident,
    @JsonProperty("isSameAddress") Boolean isSameAddress,
    @JsonProperty("fullName") String fullName,
    @JsonProperty("ogrnip") String ogrnip,
    @JsonProperty("inn") String inn,
    @JsonProperty("addressRegister") String addressRegister,
    @JsonProperty("addressFact") String addressFact,
    @JsonProperty("email") String email,
    @JsonProperty("bankAccount") String bankAccount,
    @JsonProperty("bik") String bik,
    @JsonProperty("bankKorAccount") String bankKorAccount,
    @JsonProperty("bankName") String bankName
) {}