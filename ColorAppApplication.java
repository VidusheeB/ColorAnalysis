@Controller
public class ColorAppController {
  @Autowired
  private OpenAIService openAIService;
  @PostMapping("/upload")
  public String handleUpload(@RequestParam("image") MultipartFile image, Model model) throws IOException {
      String result = openAIService.analyzeImage(image);
      List<String> hexCodes = extractHexColors(result);
      model.addAttribute("resultText", result);   
      model.addAttribute("swatches", hexCodes);   
      return "index";
  }
  private List<String> extractHexColors(String text) {
      List<String> hexColors = new ArrayList<>();
      Matcher matcher = Pattern.compile("#[0-9a-fA-F]{6}").matcher(text);
      while (matcher.find()) {
          hexColors.add(matcher.group());
      }
      return hexColors;
  }
}
