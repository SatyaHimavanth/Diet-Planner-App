package com.example.pubfitnessstudio;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.json.JSONObject;

public class LoginRequest {

    public Map<String, Object> sendLoginRequest(String username, String password, String deviceid) throws InterruptedException {
        // CountDownLatch to wait for the background thread to finish
        CountDownLatch latch = new CountDownLatch(1);
        final Map<String, Object>[] responseMap = new Map[]{new HashMap<>()};

        new Thread(() -> {
            try {
                // API endpoint
                URL url = new URL("https://satya2002.pythonanywhere.com/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON payload
                String jsonInputString = "{"
                        + "\"username\":\"" + username + "\","
                        + "\"password\":\"" + password + "\","
                        + "\"deviceid\":\"" + deviceid + "\""
                        + "}";

                // Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    // Parse the response to JSON and convert it to a HashMap
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    System.out.println(jsonResponse);
                    Map<String, Object> map = new HashMap<>();

                    // Manually iterate over the keys
                    Iterator<String> keys = jsonResponse.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        map.put(key, jsonResponse.get(key));
                    }

                    responseMap[0] = map; // Set the response map
                } else {
                    // If the response code is not OK, set the failure status
                    responseMap[0].put("status", "Login Failed");
                }

                conn.disconnect();
            } catch (Exception e) {
                responseMap[0].put("status", "Login Request Failed");
                e.printStackTrace();
            } finally {
                latch.countDown(); // Signal that the request is complete
            }
        }).start();

        latch.await();

        return responseMap[0];
    }

    public Map<String, Object> checkLoginStatus(String deviceid) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        final Map<String, Object>[] responseMap = new Map[]{new HashMap<>()};

        new Thread(() -> {
            try {
                // API endpoint
                URL url = new URL("https://satya2002.pythonanywhere.com/check");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON payload
                String jsonInputString = "{"
                        + "\"deviceid\":\"" + deviceid + "\""
                        + "}";

                // Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    // Parse the response to JSON and convert it to a HashMap
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    System.out.println(jsonResponse);
                    Map<String, Object> map = new HashMap<>();

                    // Manually iterate over the keys
                    Iterator<String> keys = jsonResponse.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        map.put(key, jsonResponse.get(key));
                    }

                    responseMap[0] = map; // Set the response map
                } else {
                    // If the response code is not OK, set the failure status
                    responseMap[0].put("status", "Check Failed");
                }

                conn.disconnect();
            } catch (Exception e) {
                responseMap[0].put("status", "Check Request Failed");
                e.printStackTrace();
            } finally {
                latch.countDown(); // Signal that the request is complete
            }
        }).start();

        latch.await();

        return responseMap[0];
    }
}
