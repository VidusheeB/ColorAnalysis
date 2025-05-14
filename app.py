# Flask app to receive image, call OpenAI Vision API, and return color palette

# Route: POST /analyze
# Steps:
# 1. Receive uploaded image
# 2. Convert to base64
# 3. Call OpenAI Vision API with styled prompt
# 4. Parse response and extract hex colors
# 5. Return JSON response to frontend
