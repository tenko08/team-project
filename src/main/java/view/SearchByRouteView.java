package view;

import interface_adapter.search_by_route.SearchByRouteController;
import interface_adapter.search_by_route.SearchByRouteViewModel;

public class SearchByRouteView {
    private final SearchByRouteController controller;
    private final SearchByRouteViewModel viewModel;

    public SearchByRouteView(SearchByRouteController controller, SearchByRouteViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
    }


}
