package Service;

import Enums.PetType;
import Response.AiAnalyzePictureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class OpenAiService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    public OpenAiService(OpenAIClient openAIClient, ObjectMapper objectMapper) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
    }

    public AiAnalyzePictureResponse analyzeFoodPicture(MultipartFile file, Double grams, String petBreed, PetType petType, Double age) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String prompt = buildPrompt(grams, petBreed, petType, age);
        String contentType = file.getContentType();

        List<ResponseInputItem> inputItems = List.of(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.USER)
                                .addContent(ResponseInputText.builder()
                                        .text(prompt)
                                        .build())
                                .addContent(ResponseInputImage.builder()
                                        .imageUrl("data:" + contentType + ";base64," + base64Image)
                                        .build())
                                .build()
                )
        );

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4O)
                .input(ResponseCreateParams.Input.ofResponse(inputItems))
                .build();

        Response response = openAIClient.responses().create(params);

        String json = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(text -> text.text())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No text returned from OpenAI"));

        json = cleanJson(json);

        return objectMapper.readValue(json, AiAnalyzePictureResponse.class);
    }

    private String buildPrompt(Double grams, String petBreed, PetType petType, Double age) {
        String gramsInstruction = (grams == null)
                ? """
              The grams field was not provided by the user.
              Estimate the visible portion in grams as best as possible.
              """
                : "The exact food weight is " + grams + " grams. Use this exact value in the grams field.";

        String petContext = """
            Pet details:
            - Pet type: %s
            - Pet breed: %s
            - Pet age: %s years

            Use these pet details when evaluating whether this food is appropriate, safe, and healthy for this specific pet.
            Consider the pet type, breed, and age when writing the aiReview and assigning the foodScore and foodSafetyLevel.
            """
                .formatted(
                        petType != null ? petType.name() : "Unknown",
                        petBreed != null && !petBreed.isBlank() ? petBreed : "Unknown",
                        age != null ? age : "Unknown"
                );

        return """
            Analyze the attached pet food image.

            %s

            Return ONLY valid JSON with this exact shape:
            {
              "calories": 0.0,
              "protein": 0.0,
              "fat": 0.0,
              "foodName": "",
              "grams": 0.0,
              "foodScore": 0,
              "foodSafetyLevel": "SAFE",
              "aiReview": ""
            }

            Rules:
            - calories, protein, fat, grams must be numbers
            - foodScore must be integer 1-100
            - aiReview should be short
            - no markdown
            - no extra text
            - Evaluate the food for this specific pet, not in general
            - If the food looks unsafe or unsuitable for the pet, reflect that in foodSafetyLevel, foodScore, and aiReview

            %s
            """.formatted(petContext, gramsInstruction);
    }

    private String cleanJson(String text) {
        String cleaned = text.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }
}