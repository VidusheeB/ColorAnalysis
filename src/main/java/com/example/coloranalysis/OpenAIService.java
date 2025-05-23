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
                      "text": "Please analyze the color palette of the image with a Korean color analysis based on undertones, skin tone, hair color, and eye color. Please choose from one of the following color palettes to assign them: bright spring, true spring, light spring, light summer, true summer, soft summer, soft autumn, true autumn, dark autumn, dark winter, true winter, bright winter. Generate 10 hex codes with colors that would best suit the individual in the image, if some of the hex codes don't fit in a color palette, it is okay, just make sure to separate them from the others. Try to confine it to a palette, but if the image truly doesn't match one, it is okay to step outside. Also, don't generate neutral (skin tone) colors."
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

