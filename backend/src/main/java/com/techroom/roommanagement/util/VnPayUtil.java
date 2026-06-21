package com.techroom.roommanagement.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPayUtil {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(keySpec);
            byte[] macData = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(macData);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi sinh hash HMAC SHA512", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff)); // ⚠️ QUAN TRỌNG: lowercase 'x'
        }
        return sb.toString();
    }

    public static String buildVnpUrl(String baseUrl, Map<String, String> params, String secret) {
        try {
            // Remove null/empty values
            Map<String, String> cleanParams = new HashMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    cleanParams.put(entry.getKey(), value);
                }
            }

            // Sort keys
            List<String> fieldNames = new ArrayList<>(cleanParams.keySet());
            Collections.sort(fieldNames);

            // Build hash data (NO encoding)
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String value = cleanParams.get(fieldName);
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(value); // NO URLEncoder here
            }

            // Calculate secure hash
            String secureHash = hmacSHA512(secret, hashData.toString());

            // Build query string (WITH encoding)
            StringBuilder query = new StringBuilder();
            for (String fieldName : fieldNames) {
                String value = cleanParams.get(fieldName);
                if (query.length() > 0) {
                    query.append('&');
                }
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
            }

            // Append secure hash
            query.append("&vnp_SecureHash=").append(secureHash);

            return baseUrl + "?" + query.toString();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL VnPay", e);
        }
    }

    public static boolean verifySecureHash(Map<String, String> params, String secret) {
        try {
            Map<String, String> copy = new HashMap<>(params);
            String receivedHash = copy.remove("vnp_SecureHash");
            copy.remove("vnp_SecureHashType");

            if (receivedHash == null) return false;

            // Sort keys
            List<String> fieldNames = new ArrayList<>(copy.keySet());
            Collections.sort(fieldNames);

            // Build hash data (NO encoding)
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String value = copy.get(fieldName);
                if (value == null || value.isEmpty()) continue;

                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(value); // NO URLEncoder
            }

            // Calculate and compare
            String calculated = hmacSHA512(secret, hashData.toString());
            return calculated.equalsIgnoreCase(receivedHash);

        } catch (Exception e) {
            return false;
        }
    }
}