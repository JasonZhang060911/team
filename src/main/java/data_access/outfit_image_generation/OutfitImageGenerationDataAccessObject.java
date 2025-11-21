package data_access.outfit_image_generation;

import data_access.outfit_suggestion.GeminiConfig;
import entity.User;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.outfit_image_generation.OutfitImageGenerationDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public class OutfitImageGenerationDataAccessObject implements OutfitImageGenerationDataAccessInterface {

    private static final MediaType JSON_MEDIA =
            MediaType.get("application/json; charset=utf-8");

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent";

    private static final String API_KEY = GeminiConfig.API_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final User user;

    public OutfitImageGenerationDataAccessObject(User user) {
        this.user = user;
    }

    @Override
    public List<String> generateImages(List<String> outfits) {
        List<String> images = new ArrayList<>();

        for (String outfit : outfits) {
            String prompt = "Generate a realistic full-body outfit image in photographic style: "
                    + outfit;

            String base64 = callGemini(prompt);

            if (base64 != null) {
                images.add(base64);
            }
        }

        return images;
    }

    private String callGemini(String prompt) {
        try {
            if (API_KEY == null || API_KEY.isBlank()) {
                System.err.println("Gemini API key is missing. Please set it in GeminiConfig.API_KEY.");
                return null;
            }

            // Build JSON request body and explicitly ask Gemini for an image payload
            JSONObject requestJson = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("text", prompt)
                                            )
                                    )
                            )
                    )
                    .put("generationConfig", new JSONObject()
                            .put("response_mime_type", "image/png")
                    );

            RequestBody body = RequestBody.create(requestJson.toString(), JSON_MEDIA);

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                System.err.println("Gemini image API failed: " + response.code());
                if (response.body() != null) {
                    System.err.println("Response: " + response.body().string());
                }
                return null;
            }

            String resp = response.body().string();
            System.out.println("Gemini Image Raw Response: " + resp);

            JSONObject json = new JSONObject(resp);
            JSONArray candidates = json.optJSONArray("candidates");

            if (candidates == null || candidates.isEmpty()) {
                System.err.println("Gemini did not return any candidates.");
                return null;
            }

            JSONArray parts =
                    candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .optJSONArray("parts");

            if (parts == null) {
                System.err.println("Gemini candidates did not include any parts.");
                return null;
            }

            // Find inline_data part
            for (int i = 0; i < parts.length(); i++) {
                JSONObject p = parts.getJSONObject(i);

                if (p.has("inline_data")) {
                    return p.getJSONObject("inline_data").getString("data");
                }
            }
            System.err.println("Gemini candidates did not include inline image data.");
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
