package use_case.outfit_image_generation;

public interface OutfitImageGenerationOutputBoundary {

    void prepareSuccessView(OutfitImageGenerationOutputData data);

    void prepareFailureView(String errorMessage);
}
