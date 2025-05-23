package com.example.coloranalysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ColorAppController {

    @Autowired
    private OpenAIService openAIService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "index"; // maps to templates/index.html
    }

    // Upload and analyze image
    @PostMapping("/upload")
    public String upload(@RequestParam("image") MultipartFile image, Model model) throws IOException, InterruptedException {
        String result = openAIService.analyzeImage(image);
        List<String> hexCodes = getHexColors(result);
        String palette = getPaletteName(result);

        model.addAttribute("resultText", result);
        model.addAttribute("swatches", hexCodes);
        model.addAttribute("palette", palette);

        return "index";
    }

    // Extract palette name
    private String getPaletteName(String text) {
        String lower = text.toLowerCase();
        int start = lower.indexOf("fits the ");
        int end = lower.indexOf(" palette");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start + 9, end).trim();
        }
        return "Unknown";
    }

    // Extract hex codes
    private List<String> getHexColors(String text) {
        List<String> hexColors = new ArrayList<>();
        for (int i = 0; i < text.length() - 6; i++) {
            if (text.charAt(i) == '#') {
                if (i + 7 <= text.length()) {
                    String possibleHex = text.substring(i, i + 7);
                    if (isValidColor(possibleHex)) {
                        hexColors.add(possibleHex);
                    }
                }
            }
        }
        return hexColors;
    }

    // Validate hex code format
    private boolean isValidColor(String s) {
        if (s.length() != 7 || s.charAt(0) != '#') return false;
        for (int i = 1; i < 7; i++) {
            char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') ||
                  (c >= 'A' && c <= 'F') ||
                  (c >= 'a' && c <= 'f'))) {
                return false;
            }
        }
        return true;
    }
}
