package com.vit.assignment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class AssignmentApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate rest = new RestTemplate();

        String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> body = new HashMap<>();
        body.put("name", "Akshat Kumar Jha");
        body.put("regNo", "22BEC0420");
        body.put("email", "akshat.jha1404@gmail.com");

        ResponseEntity<Map> resp = rest.postForEntity(registerUrl, body, Map.class);
        String webhook = (String) resp.getBody().get("webhook");
        String token = (String) resp.getBody().get("accessToken");

        int lastTwo = Integer.parseInt(body.get("regNo").substring(body.get("regNo").length() - 2));
        String query;

        if (lastTwo % 2 == 1) {
            query = "SELECT p.AMOUNT AS SALARY, " +
                    "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                    "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                    "d.DEPARTMENT_NAME " +
                    "FROM PAYMENTS p " +
                    "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                    "ORDER BY p.AMOUNT DESC LIMIT 1;";
        } else {
            query = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, " +
                    "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM EMPLOYEE e " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "LEFT JOIN EMPLOYEE e2 ON e.DEPARTMENT = e2.DEPARTMENT " +
                    "AND e2.DOB > e.DOB " +
                    "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME " +
                    "ORDER BY e.EMP_ID DESC;";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> ans = new HashMap<>();
        ans.put("finalQuery", query);

        ResponseEntity<String> out = rest.postForEntity(webhook, new HttpEntity<>(ans, headers), String.class);
        System.out.println("Response: " + out.getBody());
    }
}
