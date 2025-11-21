package use_case.outfit_image_generation;

import java.util.List;

/**
 * Use case interactor: generates AI outfit images from text descriptions.
 */
public class OutfitImageGenerationInteractor implements OutfitImageGenerationInputBoundary {

    private final OutfitImageGenerationDataAccessInterface dataAccess;
    private final OutfitImageGenerationOutputBoundary presenter;

    public OutfitImageGenerationInteractor(OutfitImageGenerationDataAccessInterface dataAccess,
                                           OutfitImageGenerationOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void generateImages(List<String> outfits) {
        if (outfits == null || outfits.isEmpty()) {
            presenter.prepareFailureView("No outfits to generate images for.");
            return;
        }

        List<String> images = dataAccess.generateImages(outfits);
        if (images == null || images.isEmpty()) {
            presenter.prepareFailureView("Gemini did not return any images. Please try again.");
        } else {
            presenter.prepareSuccessView(new OutfitImageGenerationOutputData(images));
        }
    }
}
