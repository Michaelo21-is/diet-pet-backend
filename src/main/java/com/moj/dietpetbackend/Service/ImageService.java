package com.moj.dietpetbackend.Service;

import com.moj.dietpetbackend.Entity.Image;
import com.moj.dietpetbackend.Repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;


@Service
public class ImageService {
    private final ImageRepository imageRepository;

    // קונפיגורציה ל-ID ייחודי
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();
    // temp in production it will be saved in s3
    private final String IMAGE_PATH = "C:\\Users\\micha\\dietpet-backend\\src\\main\\resources\\static\\Images";

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Integer IMAGE_NAME_LENGTH = 10;
    private final Integer MAX_IMAGE_SIZE_MB = 10;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }
    public Image uploadImage(MultipartFile file, String alt) throws Exception{
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }

        // check if the image is to heavy avoiding suspiceous file into the system
        Long fileSize = file.getSize();
        if (fileSize > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
            throw new Exception("File is too large");
        }

        // check the image foramt if it supported
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new Exception("File has no valid extension");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String contentType = file.getContentType();
        boolean looksLikeImage = contentType != null && contentType.toLowerCase().startsWith("image/");
        if (!ALLOWED_EXTENSIONS.contains(extension) || !looksLikeImage) {
            throw new Exception("unsupported image format");
        }
        // transfering the image to the image file
        String uniqueFileName = generateRandomImageName(IMAGE_NAME_LENGTH);
        String filePath = IMAGE_PATH + File.separator + uniqueFileName;
        file.transferTo(new File(filePath));

        Image image = Image.builder()
                .imageName(uniqueFileName)
                .alt(alt)
                .imageType(contentType)
                .uploadTime(LocalDateTime.now().withNano(0).withSecond(0))
                .build();
        imageRepository.save(image);
        return image;
    }
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found"));
        String imagePath = IMAGE_PATH + File.separator + image.getImageName();
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
        imageRepository.deleteById(id);
    }

    // creating new unique name for the image file
    private String generateRandomImageName(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}
