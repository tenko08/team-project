# Search By Route - 1 Minute Walkthrough

## User Story
**As a TTC bus rider, I want to search for all active buses on a specific route number, so that I can see real-time locations and status of buses on my route.**

---

## Before & After Views

**BEFORE:**
- Welcome screen with instructions
- Empty route number field
- Status: "Ready"

**AFTER (Success):**
- Route number field shows "36"
- Results display:
  ```
  === Route 36 - Live Bus Locations ===
  Found 2 bus(es) on this route
  
  Bus ID: 1001
    Location: (43.653200, -79.383200)
    Bearing: 45.0°
    Speed: 10.0 m/s
    Occupancy: MANY_SEATS_AVAILABLE
  ```
- Status: "Live data refreshed"

---

## UML Class Diagram (Clean Architecture)

```
VIEW LAYER
  SearchByRouteView
    ↓ uses
INTERFACE ADAPTER LAYER
  SearchByRouteController → SearchByRouteInputData
  SearchByRoutePresenter ← SearchByRouteOutputData
  SearchByRouteViewModel
  SearchByRouteGatewayImpl
    ↓ implements
USE CASE LAYER
  SearchByRouteInteractor
    ↓ uses interfaces
  SearchByRouteInputBoundary (interface)
  SearchByRouteOutputBoundary (interface)
  SearchByRouteGateway (interface)
    ↓
ENTITIES LAYER
  Route, Bus, Position
    ↓
DATA ACCESS LAYER
  BusDataAccessObject
```

**CA Principles:**
- ✓ Dependency Rule: Inner layers don't know outer layers
- ✓ Interfaces: Use case defines contracts, adapters implement
- ✓ Entities: Independent business objects

---

## Interactor Code

```java
public class SearchByRouteInteractor implements SearchByRouteInputBoundary {
    private final SearchByRouteGateway searchByRouteGateway;
    private final SearchByRouteOutputBoundary outputBoundary;

    @Override
    public void execute(SearchByRouteInputData inputData) {
        String routeNumber = inputData.getRouteNumber();
        
        // 1. Validate input
        if (routeNumber == null || routeNumber.trim().isEmpty()) {
            outputBoundary.prepareFailView("Route number cannot be empty");
            return;
        }

        try {
            // 2. Fetch data via gateway
            Map<String, Object> result = searchByRouteGateway.getBusesByRoute(routeNumber);
            
            boolean success = (Boolean) result.getOrDefault("success", false);
            boolean isCached = (Boolean) result.getOrDefault("cached", false);

            if (success) {
                Route route = (Route) result.get("route");
                List<Bus> buses = (List<Bus>) result.get("buses");
                
                SearchByRouteOutputData outputData = new SearchByRouteOutputData(
                    true, route, buses, null, isCached);
                
                // 3. Route to appropriate view
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

## Flow of Control

1. **User clicks "Search Route"** → View calls `Controller.execute("36")`

2. **Controller** → Creates `InputData("36")` → Calls `Interactor.execute(inputData)`

3. **Interactor:**
   - Validates input (not empty)
   - Calls `Gateway.getBusesByRoute("36")`
   - Gateway delegates to `BusDataAccessObject` (API call)
   - Receives Map with route and buses

4. **Interactor creates OutputData** → Calls `OutputBoundary.prepareSuccessView()`

5. **Presenter** → Updates `ViewModel` → Fires property change

6. **View** → Listener triggered → Reads `ViewModel` → Renders results

**Key Points:**
- Clean separation: View → Controller → Interactor → Gateway → Data
- Interactor depends on interfaces, not implementations
- Business logic isolated in use case layer
- Easy to test with mocks

---

## Summary

This use case demonstrates Clean Architecture through:
- **Layer separation** with clear boundaries
- **Dependency inversion** via interfaces
- **Testability** through dependency injection
- **Independence** of business logic from UI/frameworks

