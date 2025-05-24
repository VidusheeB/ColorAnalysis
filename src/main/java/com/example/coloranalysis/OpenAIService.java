package com.example.coloranalysis;

import java.util.Base64;
import java.net.http.*;
import java.net.URI;
import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
      String mimeType = image.getContentType();
      String imageUrl = "data:" + mimeType + ";base64," + base64Image;

      ObjectMapper mapper = new ObjectMapper();

      ObjectNode imageUrlNode = mapper.createObjectNode();
      imageUrlNode.put("url", imageUrl);

      ObjectNode imagePart = mapper.createObjectNode();
      imagePart.put("type", "image_url");
      imagePart.set("image_url", imageUrlNode);

      ObjectNode textPart = mapper.createObjectNode();
      textPart.put("type", "text");
      textPart.put("text", "Please analyze the image using a Korean-style personal color analysis, focusing on skin color, hair color, and eye color. Pick one of the following 12 seasonal color palettes that would most suit the face in the image in terms of personal style and clothing: Bright Spring, True Spring, Light Spring, Light Summer, True Summer, Soft Summer, Soft Autumn, True Autumn, Dark Autumn, Dark Winter, True Winter, Bright Winter. Then, generate 10 hex codes that reflect colors ideal for this individual based on the palette you assigned them. The 10 hex codes you provide should reflect the most flattering colors for the individual. Avoid neutral colors and browns. This is for fashion/personal styling purposes, so focus on wearable and harmonious colors that align with the subject’s natural coloring using Korean color theory standards.");

      ArrayNode contentArray = mapper.createArrayNode();
      contentArray.add(textPart);
      contentArray.add(imagePart);

      ObjectNode userMessage = mapper.createObjectNode();
      userMessage.put("role", "user");
      userMessage.set("content", contentArray);

      ObjectNode systemMessage = mapper.createObjectNode();
      systemMessage.put("role", "system");
      systemMessage.put("content", "You are a personal color stylist");

      ArrayNode messages = mapper.createArrayNode();
      messages.add(systemMessage);
      messages.add(userMessage);

      ObjectNode payloadNode = mapper.createObjectNode();
      payloadNode.put("model", "gpt-4o");
      payloadNode.set("messages", messages);
      payloadNode.put("max_tokens", 3000);

      String payload = mapper.writeValueAsString(payloadNode);

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

