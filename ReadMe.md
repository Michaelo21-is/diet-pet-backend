
pet-service - 
 upload a pet - 
   1) checking if the user is real and existing by the access token 
   2) calculating the pet age by the birth of the pet
   3) calculating the petIntake
   4) saving the pet in the table
   5) using ai to analyze recomnded walkout & saving the ai response in the recomnded walkout table
   6) return the pet overview that basicly give suggestion to the user and it will work simply as that: 
   pet diet overview and suggestion (maybe in the future put an ai overview text but for now is just the suggested calorie and protien fat) ->
   then we give an overview about the recomended walkout and the best way to keep your pet healy as possible.(but is only for dogs this page will be shown for cat is only the diet suggestion)


 upload a picture to analyze image : 
     upload-picture -> check if the details is valid -> seting up the pet detailse then sending message and image ->  clean the json that has return from the ai
     -> conert it to a java object AiAnalyzePictureResponse
    
how image process look like - 
1)take the file image and convert it to byte
2) we cannot put binar bits into json in conftrombole way so we convert them to base 64, base64 is String that respesnt the file
3) sending to the ai the image content the data  = content type and then base64 = bytes
4) open ai recgonize the type of the file understand it base64 encode it to the original picture bit, building out of it the image, then analyzing the image
 