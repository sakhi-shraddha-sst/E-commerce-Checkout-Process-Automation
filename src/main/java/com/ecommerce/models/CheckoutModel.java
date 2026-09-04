package com.ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model representing checkout, payment, and credit card parameters.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutModel {

    @JsonProperty("nameOnCard")
    private String nameOnCard;

    @JsonProperty("cardNumber")
    private String cardNumber;

    @JsonProperty("cvc")
    private String cvc;

    @JsonProperty("expiryMonth")
    private String expiryMonth;

    @JsonProperty("expiryYear")
    private String expiryYear;

    @JsonProperty("orderComment")
    private String orderComment;

    public CheckoutModel() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getNameOnCard() { return nameOnCard; }
    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCvc() { return cvc; }
    public void setCvc(String cvc) { this.cvc = cvc; }

    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }

    public String getOrderComment() { return orderComment; }
    public void setOrderComment(String orderComment) { this.orderComment = orderComment; }

    public static class Builder {
        private final CheckoutModel model = new CheckoutModel();

        public Builder nameOnCard(String nameOnCard) { model.nameOnCard = nameOnCard; return this; }
        public Builder cardNumber(String cardNumber) { model.cardNumber = cardNumber; return this; }
        public Builder cvc(String cvc) { model.cvc = cvc; return this; }
        public Builder expiryMonth(String expiryMonth) { model.expiryMonth = expiryMonth; return this; }
        public Builder expiryYear(String expiryYear) { model.expiryYear = expiryYear; return this; }
        public Builder orderComment(String orderComment) { model.orderComment = orderComment; return this; }

        public CheckoutModel build() {
            return model;
        }
    }
}
