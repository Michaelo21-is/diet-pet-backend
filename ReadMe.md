
pet-service - 
 upload a picture to analyze image : 
     upload-picture -> check if the details is valid -> seting up the pet detailse then sending message and image ->  clean the json that has return from the ai
     -> conert it to a java object AiAnalyzePictureResponse
    
how image process look like - 
1)take the file image and convert it to byte
2) we cannot put binar bits into json in conftrombole way so we convert them to base 64, base64 is String that respesnt the file
3) sending to the ai the image content the data  = content type and then base64 = bytes
4) open ai recgonize the type of the file understand it base64 encode it to the original picture bit, building out of it the image, then analyzing the image
 