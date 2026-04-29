package com.moj.dietpetbackend.Service;
import com.moj.dietpetbackend.Enums.ActivityLevels;
import com.moj.dietpetbackend.Enums.PetType;
import com.moj.dietpetbackend.Response.AiAnalyzePictureResponse;
import com.moj.dietpetbackend.Util.PetAiPromptBuilderUtils;
import com.moj.dietpetbackend.Response.WalkOutOverviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.moj.dietpetbackend.Response.AiAnalyzeRecommendedForPetResponse;

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
    public AiAnalyzeRecommendedForPetResponse aiAnalyzeRecommendedForPetResponse(String petBreed, Double age, boolean neutered, Double weight, boolean hasYard, PetType type, boolean isTendToBeFat) throws Exception{
        String prompt = PetAiPromptBuilderUtils.buildPromptForPetRecommendation(petBreed, age, neutered, weight, hasYard, type, isTendToBeFat);
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
        System.out.println("AI raw json: " + json);
        return objectMapper.readValue(json, AiAnalyzeRecommendedForPetResponse.class);
    }

    public AiAnalyzePictureResponse analyzeFoodPicture(MultipartFile file, Double grams, String petBreed, PetType petType, Double age, String foodName) throws Exception {

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String prompt = PetAiPromptBuilderUtils.buildPromptForAnazlyzingImage(grams, petBreed, petType, age, foodName);
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
    public WalkOutOverviewResponse getWalkOverViewByAi(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevels activityLevel) throws Exception{
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