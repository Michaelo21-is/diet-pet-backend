package Util;

import Enums.ActivityLevel;
import Enums.PetType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PetAiPromptBuilderUtils {
    public String buildPromptForARecommendedWalkout(String petBreed, Double age, boolean neutered, Double wight, boolean hasYard){
        String petContext = """
                pet details:
                - Pet type: DOG
                - Pet breed: %s
                - Pet age: %s years
                - Pet weight: %s kg
                - Pet has a yard: %s
                - Pet is neutered: %s
                """.formatted(
                petBreed,
                age ,
                wight ,
                hasYard,
                neutered
        );
        return """
            You are a dog walking recommendation assistant.

            Your task is to estimate a healthy daily walking routine for this dog.

            %s

            Based on the dog's breed, age, weight, whether it is neutered, and whether it has a yard,
            estimate the following:
            1. How many times per day the dog should go outside
            2. How many total kilometers the dog should walk per day
            3. How many total hours of outdoor/walking time the dog should have per day
            4. A short AI review explaining the recommendation

            Important instructions:
            - Return only valid JSON
            - Do not return markdown
            - Do not explain outside the JSON
            - Base the answer on a realistic daily recommendation for this specific dog
            - If the dog has a yard, you may slightly reduce the outdoor walk need, but do not assume yard time fully replaces walks
            - Younger and more energetic dogs usually need more activity
            - Older dogs may need shorter or lighter walks
            - Working, athletic, or highly active breeds may need more activity
            - The aiReview must be short and practical

            Return JSON in this exact format:
            {
              "recommendedWalkoutDistance": 0.0,
              "recommendedWalkoutTime": 0.0,
              "recommendedWalkoutTimeToTake": 0.0,
              "aiReview": ""
            }

            Field rules:
            - recommendedWalkoutDistance = total recommended kilometers per day
            - recommendedWalkoutTime = total recommended hours outside per day
            - recommendedWalkoutTimeToTake = number of times per day the dog should go outside
            - aiReview = short summary for the owner
            - all numeric fields must be numbers
            - no extra fields
            - no extra text
            """.formatted(petContext);


    }

    public String buildPromptForAnazlyzingImage(Double grams, String petBreed, PetType petType, Double age, String foodName) {
        String gramsInstruction = (grams == null)
                ? """
              The grams field was not provided by the user.
              Estimate the visible portion in grams as best as possible.
              """
                : "The exact food weight is " + grams + " grams. Use this exact value in the grams field.";

        String foodNameInstruction = (foodName == null)
                ? """
              The food name was not provided by the user.
              Identify the food from the image as accurately as possible and fill the foodName field.
              """
                : "The user provided the food name: " + foodName + ". Use this exact value in the foodName field.";

        String petContext = """
        Pet details:
        - Pet type: %s
        - Pet breed: %s
        - Pet age: %s years

        Use these pet details when evaluating whether this food is appropriate, safe, and healthy for this specific pet.
        Consider the pet type, breed, and age when writing the aiReview and assigning the foodScore and foodSafetyLevel.
        """
                .formatted(
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

        Rules:
        - calories, protein, fat, grams must be numbers
        - foodScore must be an integer from 1 to 100
        - foodName must always be a short string
        - aiReview should be short
        - no markdown
        - no extra text
        - Evaluate the food for this specific pet, not in general
        - If the food looks unsafe or unsuitable for the pet, reflect that in foodSafetyLevel, foodScore, and aiReview
        - If the image is unclear, use the best reasonable estimate

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
    public String buildPromptForAWalk(Double km, Double duration, Double weight, Double age, String petBreed, ActivityLevel activityLevel){
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
              "burnedCalories": 0.0,
              "equivalentStandardWalks": 0,
              "aiReview": ""
            }
            
            Rules:
               - burnedCalories must be a number
               - equivalentStandardWalks must be a whole number integer
               - equivalentStandardWalks must be at least 1
               - aiReview must be a string
               - no extra fields
               - no extra text
            """.formatted(walkContext);
                }
}
