package com.ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model representing contact inquiry form submission.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactUsModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("message")
    private String message;

    @JsonProperty("uploadFilePath")
    private String uploadFilePath;

    public ContactUsModel() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUploadFilePath() { return uploadFilePath; }
    public void setUploadFilePath(String uploadFilePath) { this.uploadFilePath = uploadFilePath; }

    public static class Builder {
        private final ContactUsModel model = new ContactUsModel();

        public Builder name(String name) { model.name = name; return this; }
        public Builder email(String email) { model.email = email; return this; }
        public Builder subject(String subject) { model.subject = subject; return this; }
        public Builder message(String message) { model.message = message; return this; }
        public Builder uploadFilePath(String uploadFilePath) { model.uploadFilePath = uploadFilePath; return this; }

        public ContactUsModel build() {
            return model;
        }
    }
}
