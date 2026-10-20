package ru.tutor.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TotalCoursesResponse (

    @JsonProperty("products") Integer products,
    @JsonProperty("miniCourses") Integer miniCourses,
    @JsonProperty("shows") Integer shows,
    @JsonProperty("miniWorkouts") Integer miniWorkouts
)
{
    public boolean allNonNegative() {
        return nonNegative (products) && nonNegative(miniCourses)
                && nonNegative(shows) && nonNegative(miniWorkouts);
    }

    private static boolean nonNegative(Integer value) {
        return value != null && value >= 0;
    }
}
