package use_case.outfit_image_generation;

import java.util.List;

/**
 * Gateway for generating outfit images using an AI service.
 * Returns a list of Base64-encoded images (PNG/JPEG).
 */
public interface OutfitImageGenerationDataAccessInterface {

    /**
     * Generate one image per outfit description.
     *
     * @param outfits list of outfit descriptions (plain text)
     * @return list of Base64-encoded image strings; may be empty/null on failure
     */
    List<String> generateImages(java.util.List<String> outfits);
}
