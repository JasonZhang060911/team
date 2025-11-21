package use_case.outfit_image_generation;

import java.util.List;

public class OutfitImageGenerationOutputData {

    private final List<String> base64Images;

    public OutfitImageGenerationOutputData(List<String> base64Images) {
        this.base64Images = base64Images;
    }

    public List<String> getBase64Images() {
        return base64Images;
    }
}
