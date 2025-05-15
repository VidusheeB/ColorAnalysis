@Controller
public class ColorAppController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("image") MultipartFile image, Model model) throws IOException {
        String result = openAIService.analyzeImage(image); // Send image to OpenAI
        model.addAttribute("resultText", result);          // Pass result to HTML
        return "index"; // Renders index.html with result
    }
}

