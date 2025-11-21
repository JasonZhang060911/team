package interface_adapter.outfit_image_generation;

import use_case.outfit_image_generation.OutfitImageGenerationInputBoundary;

import java.util.List;

public class OutfitImageGenerationController {

    private final OutfitImageGenerationInputBoundary inputBoundary;

    public OutfitImageGenerationController(OutfitImageGenerationInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public void generateImages(List<String> outfits) {
        inputBoundary.generateImages(outfits);
    }
}
