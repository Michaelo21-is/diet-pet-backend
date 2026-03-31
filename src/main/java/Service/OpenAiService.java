package Service;

import Enums.ActivityLevel;
import Enums.PetType;
import Response.AiAnalyzePictureResponse;
import Util.PetAiPromptBuilderUtils;
import Response.WalkOutOverviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import Response.AiAnalyzeRecommendedWalkoutResponse;

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
    public AiAnalyzeRecommendedWalkoutResponse calculateHowManyTimeTheDogNeedTOGoOut(String petBreed, Double age, boolean neutered, Double weight, boolean hasYard) throws Exception{
        String prompt = PetAiPromptBuilderUtils.buildPromptForARecommendedWalkout(petBreed, age, neutered, weight, hasYard);
        List<ResponseInputItem> inputItems = List.of(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.USER)
                                .addContent(ResponseInputText.builder()
                                        .text(prompt)
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

        return objectMapper.readValue(json, AiAnalyzeRecommendedWalkoutResponse.class);
    }

    public AiAnalyzePictureResponse analyzeFoodPicture(MultipartFile file, Double grams, String petBreed, PetType petType, Double age) throws Exception {

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String prompt = PetAiPromptBuilderUtils.buildPromptForAnazlyzingImage(grams, petBreed, petType, age);
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
    public WalkOutOverviewResponse getWalkOverViewByAi(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevel activityLevel) throws Exception{
         String prompt = PetAiPromptBuilderUtils.buildPromptForAWalk(km, duration, weight, age, petBreed, activityLevel);
        // setting up the request for the ai
         List<ResponseInputItem> inputItems = List.of(
                 ResponseInputItem.ofMessage(
                         ResponseInputItem.Message.builder()
                                 .role(ResponseInputItem.Message.Role.USER)
                                 .addContent(ResponseInputText.builder()
                                         .text(prompt)
                                         .build())
                                 .build()
                 )
         );

         //creating the request
         ResponseCreateParams params = ResponseCreateParams.builder()
                 .model(ChatModel.GPT_4O)
                 .input(ResponseCreateParams.Input.ofResponse(inputItems))
                 .build();

         // the ai response
         Response response = openAIClient.responses().create(params);

         //extracting the response and take out of it the json text
         String json = response.output().stream()
                 .flatMap(item -> item.message().stream())
                 .flatMap(message -> message.content().stream())
                 .flatMap(content -> content.outputText().stream())
                 .map(text -> text.text())
                 .findFirst()
                 .orElseThrow(() -> new RuntimeException("No text returned from OpenAI"));


        json = cleanJson(json);
        return objectMapper.readValue(json, WalkOutOverviewResponse.class);
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