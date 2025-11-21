package app;

import data_access.user_storage.UserSession;
import entity.User;
import view.LoginPanel;
import view.OutfitSuggestionPanel;
import view.WeatherApp;

public class Main {

    public static void main(String[] args) {
        LoginPanel loginPanel = new LoginPanel();

        // callback to execute after a successful login
        loginPanel.setOnLoginSuccess(() -> {
            // Now UserSession has been populated with the logged-in user
            User currentUser = UserSession.getInstance().getCurrentUser();
            System.out.println("Login successful! Current user: " + currentUser.getName());

            // Initialize and show the weather and gemini windows here
            // For example, launch weather app with currentUser
            OutfitSuggestionPanel outfitPanel = new OutfitSuggestionPanel(currentUser);
            outfitPanel.setVisible(true);

            WeatherApp weatherApp = new WeatherApp(currentUser);
            weatherApp.setVisible(true);

        });

        loginPanel.setVisible(true);

        // After successful login, the user is stored in UserSession (called a singleton class that only has one global instance)
        // Access it at any point with: UserSession.getInstance().getCurrentUser()

//        SignupPanel signupPanel = new SignupPanel();
//        signupPanel.setVisible(true);
//
//        // ViewModel
//        WeatherViewModel viewModel = new WeatherViewModel();
//
//        // Presenter
//        DailyForecastPresenter presenter = new DailyForecastPresenter(viewModel);
//
//        // Gateways & services
//        ForecastAPIGateway forecastGateway = new ForecastAPIGatewayImpl();
//        LocationService locationService = new LocationServiceImpl();
//        AdviceService adviceService = new RuleBasedAdviceService();
//
//        // Interactor (use case)
//        DailyForecastInputBoundary interactor =
//                new DailyForecastInteractor(forecastGateway, locationService, adviceService, presenter);
//
//        // Controller
//        DailyForecastController controller = new DailyForecastController(interactor, viewModel);
//
//        // View (Swing)
//        WeatherApp app = new WeatherApp(controller, viewModel);
//        app.setVisible(true);
    }
}
