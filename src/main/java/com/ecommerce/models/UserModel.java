package com.ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model representing user registration and profile information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("day")
    private String day;

    @JsonProperty("month")
    private String month;

    @JsonProperty("year")
    private String year;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("company")
    private String company;

    @JsonProperty("address")
    private String address;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("country")
    private String country;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("mobile")
    private String mobile;

    public UserModel() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public static class Builder {
        private final UserModel user = new UserModel();

        public Builder name(String name) { user.name = name; return this; }
        public Builder email(String email) { user.email = email; return this; }
        public Builder password(String password) { user.password = password; return this; }
        public Builder day(String day) { user.day = day; return this; }
        public Builder month(String month) { user.month = month; return this; }
        public Builder year(String year) { user.year = year; return this; }
        public Builder firstName(String firstName) { user.firstName = firstName; return this; }
        public Builder lastName(String lastName) { user.lastName = lastName; return this; }
        public Builder company(String company) { user.company = company; return this; }
        public Builder address(String address) { user.address = address; return this; }
        public Builder address2(String address2) { user.address2 = address2; return this; }
        public Builder country(String country) { user.country = country; return this; }
        public Builder state(String state) { user.state = state; return this; }
        public Builder city(String city) { user.city = city; return this; }
        public Builder zipcode(String zipcode) { user.zipcode = zipcode; return this; }
        public Builder mobile(String mobile) { user.mobile = mobile; return this; }

        public UserModel build() {
            return user;
        }
    }
}
