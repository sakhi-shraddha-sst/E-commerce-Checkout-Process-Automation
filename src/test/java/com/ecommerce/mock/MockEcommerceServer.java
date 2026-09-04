package com.ecommerce.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Embedded lightweight HTTP server providing the exact PrestaShop Automation Practice
 * web interface locally for offline, deterministic, and reliable E2E test execution.
 */
public class MockEcommerceServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockEcommerceServer.class);
    private static HttpServer server;
    private static final int PORT = 8099;
    private static String lastOrderReference = "REF" + (System.currentTimeMillis() % 100000000);

    public static synchronized int start() {
        if (server == null) {
            try {
                server = HttpServer.create(new InetSocketAddress(PORT), 0);
                server.createContext("/", new EcommerceHandler());
                server.setExecutor(null);
                server.start();
                LOGGER.info("Mock Ecommerce Server successfully started on http://localhost:{}", PORT);
            } catch (IOException e) {
                LOGGER.error("Failed to start Mock Ecommerce Server on port {}", PORT, e);
                throw new RuntimeException("Could not start MockEcommerceServer on port " + PORT, e);
            }
        }
        return PORT;
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            LOGGER.info("Mock Ecommerce Server stopped.");
        }
    }

    private static class EcommerceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().toString();
            String responseHtml = getHtmlForUri(uri);

            byte[] bytes = responseHtml.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String getHtmlForUri(String uri) {
            if (uri.contains("controller=authentication")) {
                return getLoginPageHtml();
            } else if (uri.contains("controller=my-account")) {
                return getMyAccountPageHtml();
            } else if (uri.contains("controller=search")) {
                return getSearchResultPageHtml();
            } else if (uri.contains("controller=order&step=1")) {
                return getAddressPageHtml();
            } else if (uri.contains("controller=order&step=2")) {
                return getShippingPageHtml();
            } else if (uri.contains("controller=order&step=3")) {
                return getPaymentPageHtml();
            } else if (uri.contains("controller=order-confirm-step")) {
                return getOrderConfirmStepHtml();
            } else if (uri.contains("controller=order-confirmation")) {
                return getOrderConfirmationPageHtml();
            } else if (uri.contains("controller=history")) {
                return getOrderHistoryPageHtml();
            } else if (uri.contains("controller=order")) {
                return getCartSummaryPageHtml();
            } else {
                return getHomePageHtml();
            }
        }

        private String getHeaderHtml() {
            return "<!DOCTYPE html><html><head><title>My Store</title>"
                    + "<style>"
                    + "body{font-family:Arial,sans-serif;margin:20px;background:#f8f9fa;}"
                    + "header{background:#fff;padding:15px;border-bottom:1px solid #ddd;display:flex;justify-content:space-between;align-items:center;}"
                    + "a{color:#007bff;text-decoration:none;margin-right:15px;}"
                    + "button{cursor:pointer;padding:8px 15px;background:#333;color:#fff;border:none;border-radius:3px;}"
                    + "input[type='text'],input[type='email'],input[type='password']{padding:8px;border:1px solid #ccc;border-radius:3px;width:250px;}"
                    + ".box{background:#fff;padding:20px;border:1px solid #ddd;margin-top:20px;border-radius:4px;}"
                    + ".price{color:#d9534f;font-weight:bold;font-size:16px;}"
                    + ".cheque-indent strong{color:#28a745;font-size:18px;}"
                    + "table{width:100%;border-collapse:collapse;margin-top:15px;background:#fff;}"
                    + "th,td{border:1px solid #ddd;padding:10px;text-align:left;}"
                    + "th{background:#f1f1f1;}"
                    + "</style></head><body>"
                    + "<header>"
                    + "  <div><h2>My Store</h2></div>"
                    + "  <div>"
                    + "    <form action='/index.php' method='get' style='display:inline;'>"
                    + "      <input type='hidden' name='controller' value='search'/>"
                    + "      <input type='text' id='search_query_top' name='search_query' placeholder='Search products...' value='Dress'/>"
                    + "      <button type='submit' name='submit_search'>Search</button>"
                    + "    </form>"
                    + "  </div>"
                    + "  <div>"
                    + "    <a class='login' href='/index.php?controller=authentication'>Sign in</a>"
                    + "    <a class='account' href='/index.php?controller=my-account'>Test Account</a>"
                    + "    <a class='logout' href='/index.php'>Sign out</a>"
                    + "    <a class='shopping_cart' title='View my shopping cart' href='/index.php?controller=order'>Cart</a>"
                    + "  </div>"
                    + "</header>";
        }

        private String getHomePageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1>Welcome to My Store</h1>"
                    + "  <p>Featured Products, Dresses, T-shirts and more.</p>"
                    + "</div></body></html>";
        }

        private String getLoginPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>AUTHENTICATION</h1>"
                    + "  <form action='/index.php?controller=my-account' method='get'>"
                    + "    <input type='hidden' name='controller' value='my-account'/>"
                    + "    <p><label>Email address</label><br/><input type='email' id='email' name='email' value='test@example.com'/></p>"
                    + "    <p><label>Password</label><br/><input type='password' id='passwd' name='passwd' value='Password@123'/></p>"
                    + "    <p><button type='submit' id='SubmitLogin' name='SubmitLogin'>Sign in</button></p>"
                    + "  </form>"
                    + "</div></body></html>";
        }

        private String getMyAccountPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>MY ACCOUNT</h1>"
                    + "  <p>Welcome to your account.</p>"
                    + "  <p><a title='Orders' href='/index.php?controller=history'>Order history and details</a></p>"
                    + "  <p><a title='Addresses' href='#'>My addresses</a></p>"
                    + "</div></body></html>";
        }

        private String getSearchResultPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <span class='heading-counter'>7 results have been found.</span>"
                    + "  <ul class='product_list' style='list-style:none;padding:0;margin-top:20px;'>"
                    + "    <li class='ajax_block_product' style='background:#fff;border:1px solid #eee;padding:15px;width:300px;'>"
                    + "      <p><a class='product-name' href='#'>Printed Summer Dress</a></p>"
                    + "      <p class='price'>$28.98</p>"
                    + "      <a class='ajax_add_to_cart_button' href='javascript:void(0);' onclick=\"document.getElementById('layer_cart').style.display='block';\" style='display:inline-block;padding:8px 12px;background:#007bff;color:#fff;border-radius:3px;'>Add to cart</a>"
                    + "    </li>"
                    + "  </ul>"
                    + "  <div id='layer_cart' style='display:none;background:#e9f7ef;border:1px solid #28a745;padding:15px;margin-top:20px;'>"
                    + "    <h2>Product successfully added to your shopping cart</h2>"
                    + "    <a class='btn btn-default button button-medium' title='Proceed to checkout' href='/index.php?controller=order' style='background:#28a745;color:#fff;padding:10px 15px;border-radius:3px;display:inline-block;'>Proceed to checkout</a>"
                    + "  </div>"
                    + "</div></body></html>";
        }

        private String getCartSummaryPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 id='cart_title' class='page-heading'>SHOPPING-CART SUMMARY</h1>"
                    + "  <table>"
                    + "    <tr><th>Description</th><th>Unit Price</th><th>Qty</th><th>Total</th></tr>"
                    + "    <tr>"
                    + "      <td class='cart_description'><p class='product-name'><a href='#'>Printed Summer Dress</a></p></td>"
                    + "      <td class='cart_unit'><span class='price'>$28.98</span></td>"
                    + "      <td class='cart_quantity'><input class='cart_quantity_input' type='text' value='1'/></td>"
                    + "      <td class='cart_total'><span class='price'>$28.98</span></td>"
                    + "    </tr>"
                    + "  </table>"
                    + "  <p style='text-align:right;font-size:18px;'>Total: <span id='total_price'>$30.98</span></p>"
                    + "  <div class='cart_navigation' style='text-align:right;'>"
                    + "    <a href='/index.php?controller=order&step=1' class='standard-checkout' title='Proceed to checkout' style='background:#007bff;color:#fff;padding:10px 20px;border-radius:3px;'>Proceed to checkout</a>"
                    + "  </div>"
                    + "</div></body></html>";
        }

        private String getAddressPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>ADDRESSES</h1>"
                    + "  <div id='address_delivery' class='box'>"
                    + "    <li class='address_title'>Your delivery address</li>"
                    + "    <li class='address_firstname'>Test User</li>"
                    + "    <li class='address_address1'>123 Automation Way</li>"
                    + "    <li class='address_city'>New York, NY 10001</li>"
                    + "  </div>"
                    + "  <p><textarea name='message' placeholder='If you would like to add a comment about your order...'></textarea></p>"
                    + "  <form action='/index.php' method='get'>"
                    + "    <input type='hidden' name='controller' value='order'/>"
                    + "    <input type='hidden' name='step' value='2'/>"
                    + "    <button type='submit' name='processAddress' style='background:#007bff;color:#fff;padding:10px 20px;'>Proceed to checkout</button>"
                    + "  </form>"
                    + "</div></body></html>";
        }

        private String getShippingPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>SHIPPING</h1>"
                    + "  <div class='delivery_option_price'><span class='price'>$2.00</span></div>"
                    + "  <form action='/index.php' method='get'>"
                    + "    <input type='hidden' name='controller' value='order'/>"
                    + "    <input type='hidden' name='step' value='3'/>"
                    + "    <p><input type='checkbox' id='cgv' name='cgv' value='1'/> <label for='cgv'>I agree to the terms of service and will adhere to them unconditionally.</label></p>"
                    + "    <button type='submit' name='processCarrier' style='background:#007bff;color:#fff;padding:10px 20px;'>Proceed to checkout</button>"
                    + "  </form>"
                    + "</div></body></html>";
        }

        private String getPaymentPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>PLEASE CHOOSE YOUR PAYMENT METHOD</h1>"
                    + "  <p>Total amount: <span id='total_price'>$30.98</span></p>"
                    + "  <p><a class='bankwire' href='/index.php?controller=order-confirm-step&method=bankwire' style='display:block;padding:15px;background:#fff;border:1px solid #ddd;margin-bottom:10px;'>Pay by bank wire</a></p>"
                    + "  <p><a class='cheque' href='/index.php?controller=order-confirm-step&method=check' style='display:block;padding:15px;background:#fff;border:1px solid #ddd;'>Pay by check</a></p>"
                    + "</div></body></html>";
        }

        private String getOrderConfirmStepHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>ORDER SUMMARY</h1>"
                    + "  <h3 class='page-subheading'>Bank-wire payment.</h3>"
                    + "  <form action='/index.php' method='get'>"
                    + "    <input type='hidden' name='controller' value='order-confirmation'/>"
                    + "    <div id='cart_navigation'>"
                    + "      <button type='submit' class='button btn btn-default' style='background:#007bff;color:#fff;padding:10px 20px;'>I confirm my order</button>"
                    + "    </div>"
                    + "  </form>"
                    + "</div></body></html>";
        }

        private String getOrderConfirmationPageHtml() {
            lastOrderReference = "REF" + (System.currentTimeMillis() % 100000000);
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>ORDER CONFIRMATION</h1>"
                    + "  <p class='cheque-indent'><strong>Your order on My Store is complete.</strong></p>"
                    + "  <div class='box'>"
                    + "    An email has been sent with this information.<br/>"
                    + "    Please send us a bank wire with:<br/>"
                    + "    - Amount: <span class='price'><strong>$30.98</strong></span><br/>"
                    + "    - Do not forget to insert your order reference <strong>" + lastOrderReference + "</strong> in the subject of your bank wire.<br/>"
                    + "  </div>"
                    + "  <p><a title='Back to orders' href='/index.php?controller=history' style='background:#333;color:#fff;padding:10px 15px;border-radius:3px;display:inline-block;'>Back to orders</a></p>"
                    + "</div></body></html>";
        }

        private String getOrderHistoryPageHtml() {
            return getHeaderHtml()
                    + "<div class='box'>"
                    + "  <h1 class='page-heading'>ORDER HISTORY</h1>"
                    + "  <table id='order-list'>"
                    + "    <thead><tr><th>Order reference</th><th>Date</th><th>Total price</th><th>Payment</th><th>Status</th></tr></thead>"
                    + "    <tbody>"
                    + "      <tr>"
                    + "        <td class='history_link'><a href='#'>" + lastOrderReference + "</a></td>"
                    + "        <td class='history_date'>09/02/2026</td>"
                    + "        <td class='history_price'><span class='price'>$30.98</span></td>"
                    + "        <td class='history_method'>Bank wire</td>"
                    + "        <td class='history_state'><span class='label label-success' style='color:#28a745;'>Payment accepted</span></td>"
                    + "      </tr>"
                    + "    </tbody>"
                    + "  </table>"
                    + "</div></body></html>";
        }
    }
}
