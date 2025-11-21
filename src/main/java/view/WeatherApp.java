package view;

import data_access.weather.ForecastAPIGatewayImpl;
import data_access.weather.LocationServiceImpl;
import entity.User;
import interface_adapter.weather.DailyForecastController;
import interface_adapter.weather.DailyForecastPresenter;
import interface_adapter.weather.RuleBasedAdviceService;
import interface_adapter.weather.WeatherViewModel;
import use_case.weather.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * WeatherApp: main window composed of top controls + forecast grid + advice panel.
 */
public class WeatherApp extends JFrame {

    private final JTextField cityField = new JTextField(16);
    private final JButton searchBtn = new JButton("Search Weather");
    private final JButton locationBtn = new JButton("Use My Location");
    private final JLabel statusLabel = new JLabel(" ");

    private final ForecastPanel forecastPanel = new ForecastPanel();
    private final AdvicePanel advicePanel = new AdvicePanel();

    private final DailyForecastController controller;
    private final WeatherViewModel viewModel;

    public WeatherApp(User currentUser) {

        super("Weather");

        //ViewModel
        viewModel = new WeatherViewModel();

        // Presenter
        DailyForecastPresenter presenter = new DailyForecastPresenter(viewModel);

        // Gateways & services
        ForecastAPIGateway forecastGateway = new ForecastAPIGatewayImpl();
        LocationService locationService = new LocationServiceImpl();
        AdviceService adviceService = new RuleBasedAdviceService();

        // Interactor (use case)
        DailyForecastInputBoundary interactor =
                new DailyForecastInteractor(forecastGateway, locationService, adviceService, presenter);

        // Controller
        controller = new DailyForecastController(interactor, viewModel);



        initUI(currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI(User currentUser) {
        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("City:"));
        //top.add(cityField);
        top.add(searchBtn);
        top.add(locationBtn);

        // Center: left forecast grid + right advice
        JPanel center = new JPanel(new BorderLayout());
        center.setPreferredSize(new Dimension(800, 500)); // Set preferred size (width, height)
        center.add(forecastPanel, BorderLayout.CENTER);
        //center.add(advicePanel, BorderLayout.EAST);

        // Bottom status
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);


        setLayout(new BorderLayout(6, 6));
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> runInBackground(() -> {
//            controller.searchByCity(cityField.getText());
            controller.searchByCity(currentUser.getLocation().trim());
        }));

        locationBtn.addActionListener(e -> runInBackground(() -> {
            controller.useMyLocation();
        }));
    }

    /** Run controller call in background, then refresh UI from ViewModel on EDT. */
    private void runInBackground(Runnable task) {
        setControlsEnabled(false);
        statusLabel.setText("Loading...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                task.run(); // call controller (which calls use case synchronously)
                return null;
            }

            @Override
            protected void done() {
                // Refresh forecast grid + advice + status
                refreshFromViewModel();
                setControlsEnabled(true);
            }
        }.execute();
    }

    /** Read ViewModel and render components. */
    private void refreshFromViewModel() {
        // Table
        List<WeatherViewModel.SlotView> slots = viewModel.getTodaySlots();
        forecastPanel.render(slots);

        // Set icons (keep separate so text renders immediately even if icon slower)
        if (slots != null) {
            for (int i = 0; i < slots.size(); i++) {
                String code = slots.get(i).iconCode;
                Icon icon = IconLoader.getIcon(code);
                forecastPanel.setRowIcon(i, icon);
            }
        }

        // Advice
        advicePanel.setAdviceText(viewModel.getAdviceText());

        // Status
        statusLabel.setText(viewModel.getStatusMessage());
    }

    private void setControlsEnabled(boolean enabled) {
        searchBtn.setEnabled(enabled);
        locationBtn.setEnabled(enabled);
        cityField.setEnabled(enabled);
    }
}
