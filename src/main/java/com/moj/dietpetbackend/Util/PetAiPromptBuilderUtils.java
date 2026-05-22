package com.moj.dietpetbackend.Util;
import com.moj.dietpetbackend.Enums.ActivityLevels;
import com.moj.dietpetbackend.Enums.PetType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PetAiPromptBuilderUtils {
public String buildPromptForPetRecommendation(
        String petBreed,
        Double age,
        boolean neutered,
        Double weight,
        boolean hasYard,
        PetType type,
        boolean isTendToBeFat
) {
    String petContext = """
            pet details:
            - Pet type: %s
            - Pet breed: %s
            - Pet age: %s years
            - Pet weight: %s kg
            - Pet has a yard: %s
            - Pet is neutered: %s
            - Pet tends to gain fat easily: %s
            """.formatted(
            type,
            petBreed,
            age,
            weight,
            hasYard,
            neutered,
            isTendToBeFat
    );

    if (type == PetType.DOG) {
        return """
                You are a pet wellness recommendation assistant.

                Your task is to estimate a healthy daily nutrition and walking routine for this pet.

                %s

                This pet is a DOG.
                Based on the pet's breed, age, weight, whether it is neutered, whether it has a yard,
                and whether it tends to gain fat easily, estimate the following:
                1. Recommended daily calories
                2. Recommended daily protein in grams
                3. Recommended daily fat in grams
                4. How many times per day the dog should go outside
                5. How many total kilometers the dog should walk per day
                6. How many total hours of outdoor/walking time the dog should have per day
                7. A short AI review explaining the recommendation

                Important instructions:
                - Return only valid JSON
                - Do not return markdown
                - Do not explain outside the JSON
                - Base the answer on a realistic daily recommendation for this specific dog
                - If the dog has a yard, you may slightly reduce the outdoor walk need, but do not assume yard time fully replaces walks
                - Younger and more energetic dogs usually need more activity
                - Older dogs may need shorter or lighter walks
                - Working, athletic, or highly active breeds may need more activity
                - If the dog tends to gain fat easily, be slightly more conservative with calorie recommendations
                - The aiReview must be short and practical
                - All numeric nutrition fields must be numbers
                - recommendedWalkoutDistance must be a number
                - recommendedWalksPerDay must be a whole number
                - recommendedWalkDurationMinutes must be a number
                - No extra fields
                - No extra text

                Return JSON in this exact format:
                {
                  "recommendedDailyCalories": 0.0,
                  "recommendedDailyProtein": 0.0,
                  "recommendedDailyFat": 0.0,
                  "recommendedWalkoutDistance": 0.0,
                  "recommendedWalksPerDay": 0,
                  "recommendedWalkDurationMinutes": 0,
                  "aiReview": ""
                }
                """.formatted(petContext);
    }

    return """
            You are a pet wellness recommendation assistant.

            Your task is to estimate a healthy daily nutrition routine for this pet.

            %s

            This pet is a CAT.
            Based on the pet's breed, age, weight, whether it is neutered, whether it has a yard,
            and whether it tends to gain fat easily, estimate the following:
            1. Recommended daily calories
            2. Recommended daily protein in grams
            3. Recommended daily fat in grams
            4. A short AI review explaining the recommendation

            Important instructions:
            - Return only valid JSON
            - Do not return markdown
            - Do not explain outside the JSON
            - Base the answer on a realistic daily recommendation for this specific cat
            - Cats should not receive dog-style walking recommendations
            - If the cat tends to gain fat easily, be slightly more conservative with calorie recommendations
            - For cats, the walkout fields must be null
            - The aiReview must be short and practical
            - recommendedDailyCalories must be a number
            - recommendedDailyProtein must be a number
            - recommendedDailyFat must be a number
            - recommendedWalkoutDistance must be null
            - recommendedWalkoutTime must be null
            - recommendedWalkoutTimeToTake must be null
            - No extra fields
            - No extra text

            Return JSON in this exact format:
            {
              "recommendedDailyCalories": 0.0,
              "recommendedDailyProtein": 0.0,
              "recommendedDailyFat": 0.0,
              "recommendedWalkoutDistance": null,
              "recommendedWalkoutTime": null,
              "recommendedWalkoutTimeToTake": null,
              "aiReview": ""
            }
            """.formatted(petContext);
}

    public String buildPromptForAnazlyzingImage(
            Double grams,
            String petBreed,
            PetType petType,
            Double age,
            String foodName
    ) {
        String gramsInstruction = (grams == null)
                ? """
          The grams field was not provided by the user.
          You MUST estimate the visible portion in grams from the image.
          The grams value must be a positive number greater than 0.
          Do not return 0, null, or an empty value.
          If the image is unclear, make the best reasonable estimate based on the visible portion size.
          """
                : """
          The exact food weight is %.1f grams.
          Use this exact value in the grams field.
          """.formatted(grams);

        String foodNameInstruction = (foodName == null || foodName.isBlank())
                ? """
          The food name was not provided by the user.
          You MUST identify the food from the image as accurately as possible.
          If you are not fully sure, use the most likely food name.
          Do not return an empty foodName.
          """
                : """
          The user provided the food name: %s.
          Use this exact value in the foodName field.
          """.formatted(foodName);

            String petContext = """
        Pet details:
        - Pet type: %s
        - Pet breed: %s
        - Pet age: %.1f years
    
        Use these pet details when evaluating whether this food is appropriate, safe, and healthy for this specific pet.
        Consider the pet type, breed, and age when writing the aiReview and assigning the foodScore and foodSafetyLevel.
        """.formatted(
                    petType,
                    petBreed,
                    age
            );

            return """
        Analyze the attached pet food image.
    
        %s
    
        %s
    
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
    
        Critical nutrition rules:
        - You MUST always estimate calories, protein, fat, and grams.
        - calories must be a positive number greater than 0.
        - grams must be a positive number greater than 0.
        - protein and fat must be numbers. They can be 0 only if the food realistically contains almost none.
        - Do NOT return null.
        - Do NOT return empty values.
        - Do NOT return "unknown".
        - Do NOT omit any field.
        - If the exact nutrition values are unknown, estimate them using common nutrition knowledge.
        - If the image is unclear, still provide the best reasonable estimate.
        - If the food cannot be identified with certainty, choose the most likely food and estimate based on that.
    
        JSON field rules:
        - calories, protein, fat, grams must be numbers
        - foodScore must be an integer from 1 to 100
        - foodName must always be a short non-empty string
        - aiReview should be short, clear, and specific to the pet
        - no markdown
        - no extra text
        - everything must be filled
    
        Safety evaluation rules:
        - Evaluate the food for this specific pet, not in general
        - If the food looks unsafe or unsuitable for the pet, reflect that in foodSafetyLevel, foodScore, and aiReview
        - If the food contains or may contain dangerous ingredients for the pet type, mark it as CAUTION or UNSAFE
        - Be stricter with dogs/cats when the food may include chocolate, onion, garlic, grapes, raisins, alcohol, caffeine, bones, xylitol, or very salty/fatty food
    
        Allowed foodSafetyLevel values:
        - SAFE
        - CAUTION
        - UNSAFE
        """.formatted(
                    petContext,
                    gramsInstruction,
                    foodNameInstruction
            );
        }
    public String buildPromptForAWalk(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevels activityLevel){
        String walkContext = """
        Pet details:
        - Pet type: DOG
        - Pet breed: %s
        - Pet age: %s years
        - Pet weight: %s kg
        - Walking distance: %s km
        - Walking duration: %s minutes
        - Activity level: %s
        """.formatted(
                petBreed ,
                age ,
                weight ,
                km ,
                duration ,
                activityLevel
        );

        return """
            You are a dog activity calorie estimator.
            
            Calculate how many calories the dog burned during this activity,
            estimate how many standard walks this activity is equivalent to,
            and provide a very short AI review of the activity.
            
            %s
            
            Instructions:
            - Use the dog's breed, age, weight, walking distance, walking duration, and activity level.
            - Activity level can affect calorie burn:
              - CHILLWALK = relaxed walk
              - PLAYWALK = walk with more movement and play
              - INTENSESPORT = intense physical activity
            - A standard walk means a relaxed 20-25 minute walk for the same dog.
            - The AI review must be short, clear, and user-friendly.
            - The AI review should briefly describe the activity load for the dog.
            - Keep the AI review between 15 and 60 words.
            - Return only a valid JSON object.
            - Do not return markdown.
            - Do not explain anything.
            
            Return JSON in this exact format:
            {
              "caloriesBurned": 0.0,
              "equivalentStandardWalks": 0,
              "aiReview": ""
            }
            
            Rules:
               - caloriesBurned must be a number
               - equivalentStandardWalks must be a whole number integer
               - equivalentStandardWalks must be at least 1
               - aiReview must be a string
               - no extra fields
               - no extra text
            """.formatted(walkContext);
                }
}
