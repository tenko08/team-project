                    # Search By Route Use Case Walkthrough

## 1. User Story

**As a** TTC bus rider  
**I want to** search for all active buses on a specific route number  
**So that** I can see the real-time locations and status of buses on my route

**Use Case Focus:** Search By Route - Finding all active buses for a given route number

---

## 2. Before and After Views    

### Before (Initial State)
- **View:** SearchByRouteView displays welcome message
- **Status:** "Ready"
- **Input Field:** Empty route number text field
- **Results Area:** Shows instructions:
  ```
  TTC Bus Search by Route
  =======================
  
  How to use:
  1. Enter a route number (e.g., 36, 501, 95) in the field on the left.
  2. Click 'Search Route' to fetch live buses for that route.
  3. If cached data appears, click 'Retry' to fetch live data again.
  4. Use 'Clear' to reset the view.
  
  Example route numbers: 36, 501, 502, 29, 32, 95.
  ```

### After (Success State)
- **View:** SearchByRouteView displays search results
- **Status:** "Live data refreshed" or "Showing cached data"
- **Input Field:** Shows the searched route number (e.g., "36")
- **Results Area:** Displays formatted bus information:
  ```
  === Route 36 - Live Bus Locations ===
  Found 2 bus(es) on this route
  
  Bus ID: 1001
    Location: (43.653200, -79.383200)
    Bearing: 45.0°
    Speed: 10.0 m/s
    Occupancy: MANY_SEATS_AVAILABLE
  
  Bus ID: 1002
    Location: (43.654000, -79.384000)
    Bearing: 90.0°
    Speed: 12.5 m/s
    Occupancy: FEW_SEATS_AVAILABLE
  ```

### After (Error State)
- **Status:** "Error"
- **Results Area:** Shows error message:
  ```
  ❌ Error: Route not found
  Please check the route number and try again.
  ```

---

## 3. UML Class Diagram (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────────┐
│                         VIEW LAYER                              │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │         SearchByRouteView (JPanel)                         │ │
│  │  - routeNumberField: JTextField                           │ │
│  │  - resultArea: JTextArea                                  │ │
│  │  - searchButton: JButton                                  │ │
│  │  + execute() → calls controller                            │ │
│  └──────────────────────┬───────────────────────────────────┘ │
│                         │ uses                                │
└─────────────────────────┼─────────────────────────────────────┘
                          │
┌─────────────────────────┼─────────────────────────────────────┐
│              INTERFACE ADAPTER LAYER                           │
│  ┌──────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteController                                │ │
│  │  - searchByRouteUseCaseInteractor: SearchByRouteInputBoundary│
│  │  + execute(routeNumber: String)                          │ │
│  └──────────────────────┬───────────────────────────────────┘ │
│                          │ creates                            │
│  ┌───────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteInputData                                 │ │
│  │  - routeNumber: String                                    │ │
│  └──────────────────────┬───────────────────────────────────┘ │
│                         │                                      │
│  ┌──────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRoutePresenter                                │ │
│  │  - viewModel: SearchByRouteViewModel                     │ │
│  │  + prepareSuccessView(outputData)                       │ │
│  │  + prepareCachedView(outputData)                        │ │
│  │  + prepareFailView(errorMessage)                         │ │
│  └──────────────────────┬───────────────────────────────────┘ │
│                         │ updates                             │
│  ┌──────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteViewModel                                 │ │
│  │  - success: boolean                                       │ │
│  │  - route: Route                                           │ │
│  │  - buses: List<Bus>                                       │ │
│  │  - isCachedData: boolean                                  │ │
│  │  - errorMessage: String                                   │ │
│  │  + firePropertyChanged()                                  │ │
│  └──────────────────────┬───────────────────────────────────┘ │
│                         │                                      │
│  ┌──────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteGatewayImpl                              │ │
│  │  - busDataAccessObject: BusDataAccessObject             │ │
│  │  + getBusesByRoute(routeNumber): Map<String, Object>   │ │
│  └──────────────────────┬───────────────────────────────────┘ │
└──────────────────────────┼──────────────────────────────────────┘
                           │ implements
┌──────────────────────────┼──────────────────────────────────────┐
│                    USE CASE LAYER                              │
│  ┌───────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteInteractor                                │ │
│  │  - searchByRouteGateway: SearchByRouteGateway             │ │
│  │  - outputBoundary: SearchByRouteOutputBoundary            │ │
│  │  + execute(inputData: SearchByRouteInputData)            │ │
│  └───────────────────────┬───────────────────────────────────┘ │
│                           │ uses                               │
│  ┌───────────────────────▼───────────────────────────────────┐ │
│  │    SearchByRouteOutputData                                │ │
│  │  - success: boolean                                       │ │
│  │  - route: Route                                           │ │
│  │  - buses: List<Bus>                                       │ │
│  │  - errorMessage: String                                   │ │
│  │  - isCached: boolean                                      │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │    SearchByRouteInputBoundary (interface)                 │ │
│  │  + execute(inputData: SearchByRouteInputData)             │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │    SearchByRouteOutputBoundary (interface)                │ │
│  │  + prepareSuccessView(outputData)                       │ │
│  │  + prepareCachedView(outputData)                          │ │
│  │  + prepareFailView(errorMessage)                          │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │    SearchByRouteGateway (interface)                       │ │
│  │  + getBusesByRoute(routeNumber): Map<String, Object>    │ │
│  └───────────────────────┬──────────────────────────────────┘ │
└───────────────────────────┼────────────────────────────────────┘
                            │
┌───────────────────────────┼────────────────────────────────────┐
│                    ENTITIES LAYER                              │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │    Route                                                   │ │
│  │  - routeNumber: int                                       │ │
│  │  - busList: List<Bus>                                     │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │    Bus                                                     │ │
│  │  - id: int                                                 │ │
│  │  - position: Position                                     │ │
│  │  - occupancy: String                                       │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │    Position                                               │ │
│  │  - latitude: double                                       │ │
│  │  - longitude: double                                      │ │
│  │  - bearing: float                                         │ │
│  │  - speed: float                                           │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            │
┌───────────────────────────┼────────────────────────────────────┐
│                 DATA ACCESS LAYER                               │
│  ┌──────────────────────────▼───────────────────────────────┐ │
│  │    BusDataAccessObject                                    │ │
│  │  + getBusesByRoute(routeNumber): Map<String, Object>    │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘

Clean Architecture Principles Demonstrated:
✓ Dependency Rule: Inner layers don't know about outer layers
✓ Interfaces: Use case layer defines interfaces, adapters implement them
✓ Entities: Independent business objects in the center
✓ Dependency Inversion: Use case depends on abstractions (interfaces)
```

---

## 4. Use Case Interactor Code

```java
package use_case.search_by_route;

import entities.Bus;
import entities.Route;
import interface_adapter.search_by_route.SearchByRouteGateway;

import java.util.List;
import java.util.Map;

public class SearchByRouteInteractor implements SearchByRouteInputBoundary {
    private final SearchByRouteGateway searchByRouteGateway;
    private final SearchByRouteOutputBoundary outputBoundary;

    public SearchByRouteInteractor(SearchByRouteGateway searchByRouteGateway,
                                   SearchByRouteOutputBoundary outputBoundary) {
        this.searchByRouteGateway = searchByRouteGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(SearchByRouteInputData inputData) {
        String routeNumber = inputData.getRouteNumber();

        // Input validation
        if (routeNumber == null || routeNumber.trim().isEmpty()) {
            outputBoundary.prepareFailView("Route number cannot be empty");
            return;
        }

        try {
            // Fetch data from gateway
            Map<String, Object> result = searchByRouteGateway.getBusesByRoute(routeNumber);

            boolean success = (Boolean) result.getOrDefault("success", false);
            boolean isCached = (Boolean) result.getOrDefault("cached", false);

            if (success) {
                Route route = (Route) result.get("route");
                @SuppressWarnings("unchecked")
                List<Bus> buses = (List<Bus>) result.get("buses");

                SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                        true,
                        route,
                        buses,
                        null,
                        isCached
                );

                // Route to appropriate view based on cache status
                if (isCached) {
                    outputBoundary.prepareCachedView(outputData);
                } else {
                    outputBoundary.prepareSuccessView(outputData);
                }
            } else {
                String errorMessage = (String) result.getOrDefault("message", "Route not found");
                outputBoundary.prepareFailView(errorMessage);
            }

        } catch (Exception e) {
            outputBoundary.prepareFailView("System error: " + e.getMessage());
        }
    }
}
```

---

## 5. Flow of Control

### Step-by-Step Execution Flow:

1. **User Action (View Layer)**
   - User enters route number "36" in `SearchByRouteView`
   - User clicks "Search Route" button
   - View validates input is not empty

2. **Controller (Interface Adapter Layer)**
   - `SearchByRouteController.execute("36")` is called
   - Controller creates `SearchByRouteInputData("36")`
   - Controller calls `searchByRouteUseCaseInteractor.execute(inputData)`

3. **Interactor (Use Case Layer)**
   - `SearchByRouteInteractor.execute(inputData)` receives the input
   - **Input Validation:** Checks if route number is null or empty
   - **Gateway Call:** Calls `searchByRouteGateway.getBusesByRoute("36")`
     - Gateway delegates to `BusDataAccessObject.getBusesByRoute("36")`
     - Data access object makes API call to TTC GTFS-RT feed
     - Returns Map with success status, route, buses, and cache flag

4. **Interactor Processing**
   - Extracts `success`, `isCached`, `route`, and `buses` from result Map
   - Creates `SearchByRouteOutputData` with the results
   - **Decision Point:**
     - If `success == true` and `isCached == false` → calls `outputBoundary.prepareSuccessView()`
     - If `success == true` and `isCached == true` → calls `outputBoundary.prepareCachedView()`
     - If `success == false` → calls `outputBoundary.prepareFailView()`

5. **Presenter (Interface Adapter Layer)**
   - `SearchByRoutePresenter` receives the output data
   - Updates `SearchByRouteViewModel` with:
     - Success status
     - Route object
     - List of buses
     - Cache flag
     - Error message (if any)
   - Calls `viewModel.firePropertyChanged()` to notify listeners

6. **View Update (View Layer)**
   - `SearchByRouteView` property change listener is triggered
   - View reads updated state from `SearchByRouteViewModel`
   - View calls `renderResults()` or `renderError()` based on success status
   - UI displays formatted bus information or error message

### Key Clean Architecture Principles:

✓ **Dependency Rule:** 
  - View → Controller → Interactor (outer to inner)
  - Interactor depends on interfaces (Gateway, OutputBoundary), not implementations
  - Entities are independent and used by all layers

✓ **Separation of Concerns:**
  - View handles UI rendering
  - Controller handles input formatting
  - Interactor handles business logic
  - Gateway handles data access abstraction
  - Entities represent domain objects

✓ **Testability:**
  - Each layer can be tested independently using mocks
  - Interfaces allow easy substitution of implementations

---

## Summary

The Search By Route use case demonstrates Clean Architecture by:
- **Clear layer separation** with defined boundaries
- **Dependency inversion** through interfaces
- **Single responsibility** for each component
- **Testability** through dependency injection
- **Independence** of business logic from frameworks and UI

The flow follows a clean request-response pattern: View → Controller → Interactor → Gateway → Data Access → Gateway → Interactor → Presenter → ViewModel → View

