import java.util.Base64            
import java.net.http.*          
import java.net.URI             
import com.fasterxml.jackson.databind.*
@Service
public class OpenAIService {

    private static final String OPENAI_API_KEY = "sk-..."; // Use your real key
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String analyzeImage(MultipartFile image) throws IOException {
        byte[] imageBytes = image.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String payload = """
        {
          "model": "gpt-4-vision-preview",
          "messages": [
            {"role": "system", "content": "You are a personal color stylist."},
            {"role": "user", "content": [
              {"type": "text", "text": "Please analyze this person's image based on Korean seasonal color theory and return their best color season along with 12 matching hex color swatches (color swatches need not be in their palette). Analyze the image based on undertone of skin, primary eye color, and primary hair color. Also return the hex codes of these colors (separately labeled). The options for color seasons are: bright spring, true spring, light spring, light summer, true summer, soft summer, soft autumn, true autumn, dark autumn, dark winter, true winter, bright winter."},
              {"type": "image_url", "image_url": {"url": "data:image/jpeg;base64:%s"}}
            ]}
          ],
          "max_tokens": 300
        }
        """.formatted(base64Image);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        return root.path("choices").get(0).path("message").path("content").asText();
    }
}
