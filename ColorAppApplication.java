@Service
public class OpenAIService {
  //private static final String OPENAI_API_KEY = *NEED REAL API KEY*
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  public String analyzeImage(MultipartFile image) throws IOException {
  byte[] imageBytes = image.getBytes();
  String base64Image = Base64.getEncoder().encodeToString(imageBytes);
  
  
