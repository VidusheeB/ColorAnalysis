import java.util.Base64            
import java.net.http.*          
import java.net.URI             
import com.fasterxml.jackson.databind.*
@Service
public class OpenAIService {

private static final String API_KEY = "sk-..."; 
private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String analyzeImage(MultipartFile image) throws IOException, InterruptedException {
    byte[] imageBytes = image.getBytes();
    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
    String mimeType = image.getContentType();
    String dataUrl = "data:" + mimeType + ";base64," + base64Image;
    
    String payload = """
    {
      "model": "gpt-4-vision-preview",
      "messages": [
        {"role": "system", "content": "You are a personal color stylist."},
        {"role": "user", "content": [
          {"type": "text", "text": "Please analyze this person's image..."},
          {"type": "image_url", "image_url": {"url": "%s"}}
        ]}
      ],
      "max_tokens": 300
    }
    """.formatted(dataUrl);
    
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(API_URL))
        .header("Authorization", "Bearer " + API_KEY)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .build();
    
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return new ObjectMapper().readTree(response.body()).path("choices").get(0).path("message").path("content").asText();
    }
}
