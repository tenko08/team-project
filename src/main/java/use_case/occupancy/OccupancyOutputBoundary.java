package use_case.occupancy;

public interface OccupancyOutputBoundary {
    void prepareSuccessView(OccupancyOutputData outputData);

    void prepareFailView(String error);
}
