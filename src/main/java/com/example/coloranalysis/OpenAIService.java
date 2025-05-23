package com.example.coloranalysis;

import java.util.Base64;
import java.net.http.*;
import java.net.URI;
import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OpenAIService {

    private static final String API_KEY = "sk-...";


    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String analyzeImage(MultipartFile image) throws IOException, InterruptedException {
        try {
            byte[] bytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String imageUrl = "data:image/jpeg;base64," + base64Image;

            String payload = """
            {
              "model": "gpt-4o",
              "messages": [
                {
                  "role": "system",
                  "content": "You are a personal color stylist"
                },
                {
                  "role": "user",
                  "content": [
                    {
                      "type": "text",
                      "text": "Please analyze the image using a Korean-style personal color analysis, focusing on undertones, skin tone, hair color, and eye color. Based on your assessment, classify the individual into one of the following 12 seasonal color palettes: Bright Spring, True Spring, Light Spring, Light Summer, True Summer, Soft Summer, Soft Autumn, True Autumn, Dark Autumn, Dark Winter, True Winter, Bright Winter. Use the combined visual information to determine the most accurate seasonal match. Then, generate 10 hex codes that reflect colors ideal for this individual based on their assigned palette. Instructions: Carefully identify the undertone (cool, warm, neutral) of the skin, hair, and eyes. Use Korean-style seasonal color theory to match the individual to the most accurate seasonal category listed above. Provide 10 hex codes that reflect the most flattering colors for the individual's coloring. Avoid an overuse of neutrals and browns unless they are specifically part of that seasonal palette. This is for fashion/personal styling purposes, so focus on wearable and harmonious colors that align with the subject’s natural coloring using Korean color theory standards.
                    },
                    {
                      "type": "image_url",
                      "image_url": {
                        "url": "%s"
                      }
                    }
                  ]
                }
              ],
              "max_tokens": 300
            }
            """.formatted(imageUrl);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("OpenAI raw response:\n" + response.body());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                return "No response from OpenAI.";
            }

            return choices.get(0).path("message").path("content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "OpenAI error: " + e.getMessage();
        }
    }
}

