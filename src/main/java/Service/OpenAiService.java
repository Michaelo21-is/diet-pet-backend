package Service;

import Enums.ActivityLevel;
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

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String prompt = buildPromptForAnazlyzingImage(grams, petBreed, petType, age);
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
    private Double calculateCaloriesByAWalk(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevel activityLevel){
        private String = buildPromptForAWalk(km, duration, weight, age, petBreed, activityLevel);
    }


    /// ////////////////////////////////////////////////////////////PROMPT///////////////////////////////////////////////////////////////////////////////////////////////

    private String buildPromptForAWalk(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevel activityLevel){
        String walkContext = """
        Pet details:
        - Pet type: DOG
        - Pet breed: %s
        - Pet age: %s years
        - Pet weight: %s kg
        - Walking distance: %s km
        - Walking duration: %s hours
        - Activity level: %s
        """.formatted(
                petBreed != null && !petBreed.isBlank() ? petBreed : "Unknown",
                age != null ? age : "Unknown",
                weight != null ? weight : "Unknown",
                km != null ? km : "Unknown",
                duration != null ? duration : "Unknown",
                activityLevel != null ? activityLevel.name() : "Unknown"
        );

        return """
        You are a dog activity calorie estimator.

        Calculate how many calories the dog burned during this activity.

        %s

        Instructions:
        - Use the dog's breed, age, weight, walking distance, walking duration, and activity level.
        - Activity level can affect calorie burn:
          - CHILLWALK = relaxed walk
          - PLAYWALK = walk with more movement and play
          - INTENSESPORT = intense physical activity
        - Return only the estimated burned calories for this activity.
        - Return only a valid JSON object.
        - Do not return markdown.
        - Do not explain anything.

        Return JSON in this exact format:
        {
          "burnedCalories": 0.0
        }

        Rules:
        - burnedCalories must be a number
        - no extra fields
        - no extra text
        """.formatted(walkContext);
    }

    private String buildPromptForAnazlyzingImage(Double grams, String petBreed, PetType petType, Double age) {
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
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text is null or blank");
        }
        String cleaned = text.trim();
        return cleaned
                .replaceFirst("^```json\\s*", "")
                .replaceFirst("^```\\s*", "")
                .replaceFirst("\\s*```$", "")
                .trim();
    }
}