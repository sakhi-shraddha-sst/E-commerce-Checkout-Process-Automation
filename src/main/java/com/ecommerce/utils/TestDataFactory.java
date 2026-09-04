package com.ecommerce.utils;

import com.ecommerce.models.CheckoutModel;
import com.ecommerce.models.ContactUsModel;
import com.ecommerce.models.UserModel;
import net.datafaker.Faker;

import java.util.Locale;

/**
 * Enterprise Dynamic Test Data Factory powered by Datafaker.
 * Generates collision-free, realistic test data on-the-fly for parallel and cloud test execution.
 */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataFactory() {}

    /**
     * Generates a completely unique, realistic UserModel with randomized credentials,
     * address, and telephone numbers.
     */
    public static UserModel generateRandomUser() {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        String uniqueSuffix = System.currentTimeMillis() + "_" + FAKER.number().digits(4);
        String email = "autouser_" + uniqueSuffix + "@example.com";

        return UserModel.builder()
                .name(firstName + " " + lastName)
                .email(email)
                .password("Pass@" + FAKER.number().digits(4) + "Xy!")
                .day(String.valueOf(FAKER.number().numberBetween(1, 28)))
                .month(String.valueOf(FAKER.number().numberBetween(1, 12)))
                .year(String.valueOf(FAKER.number().numberBetween(1980, 2002)))
                .firstName(firstName)
                .lastName(lastName)
                .company(FAKER.company().name().replaceAll("[^a-zA-Z0-9 ]", ""))
                .address(FAKER.address().streetAddress())
                .address2("Suite " + FAKER.number().digits(3))
                .country("United States")
                .state(FAKER.address().state())
                .city(FAKER.address().city())
                .zipcode(FAKER.number().digits(5))
                .mobile(FAKER.phoneNumber().cellPhone())
                .build();
    }

    /**
     * Generates valid dummy credit card and payment details for checkout test execution.
     */
    public static CheckoutModel generatePaymentData() {
        return CheckoutModel.builder()
                .nameOnCard(FAKER.name().fullName())
                .cardNumber("4111111111111111") // Standard Visa test card
                .cvc(FAKER.number().digits(3))
                .expiryMonth(String.format("%02d", FAKER.number().numberBetween(1, 12)))
                .expiryYear("2028")
                .orderComment("Automated order placement - " + FAKER.lorem().sentence(5))
                .build();
    }

    /**
     * Generates realistic contact support form inquiry data.
     */
    public static ContactUsModel generateContactInquiry() {
        return ContactUsModel.builder()
                .name(FAKER.name().fullName())
                .email("inquiry_" + System.currentTimeMillis() + "@example.com")
                .subject(FAKER.company().buzzword() + " Order Inquiry")
                .message("Automated support inquiry: " + FAKER.lorem().paragraph(2))
                .build();
    }

    /**
     * Generates a unique subscription email address.
     */
    public static String generateSubscriptionEmail() {
        return "subscribe_" + System.currentTimeMillis() + "_" + FAKER.number().digits(3) + "@example.com";
    }
}
