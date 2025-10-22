package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentClient {

    public String initiateBankPayment(double amount, int orderId, String returnUrl) {
        String apiUrl = System.getenv("PAYMENT_API_URL");
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            // Fallback: simulate success by redirecting to callback directly
            return returnUrl + "?status=success&orderId=" + orderId;
        }
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{" +
                    "\"amount\":" + amount + "," +
                    "\"orderId\":" + orderId + "," +
                    "\"returnUrl\":\"" + escape(returnUrl) + "\"" +
                    "}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }

            int status = conn.getResponseCode();
            if (status >= 200 && status < 300) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    String body = sb.toString();
                    // naive extract of redirectUrl
                    int idx = body.indexOf("redirectUrl");
                    if (idx >= 0) {
                        int start = body.indexOf('"', idx + 12);
                        int end = body.indexOf('"', start + 1);
                        if (start >= 0 && end > start) {
                            return body.substring(start + 1, end);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // On failure, just return to checkout with error
        return returnUrl + "?status=failed&orderId=" + orderId;
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


