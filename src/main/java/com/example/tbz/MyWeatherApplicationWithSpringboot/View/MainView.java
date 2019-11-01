package com.example.tbz.MyWeatherApplicationWithSpringboot.View;

import com.example.tbz.MyWeatherApplicationWithSpringboot.Controller.WeatherService;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringUI(path = "")
@StyleSheet("/resources/style.com")
public class MainView extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeatherButton;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMIn;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeed;
    private Label sunSetLabel;
    private Label sunRiseLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashBoardMain;
    private VerticalLayout descriptionLayout;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout pressureLayout;
    private VerticalLayout currentTempLayout;

    @Override
    protected void init(VaadinRequest request) {
        setUpLayout();
        //setHeader();
        //setLogo();
        setupForm();
        dashBoardTitle();
        dashBoardDescription();

        showWeatherButton.addClickListener(event -> {
           if(!cityTextField.getValue().equals("")){
               try {
                   updateUI();
               } catch (JSONException e) {
                   e.printStackTrace();
                   Notification.show("Something went wrong. Please try again.");
               }
           }else Notification.show("Please enter a city!");
        });

    }

    private void setUpLayout() {
        
        iconImage = new Image();
        weatherMIn = new Label("Min: 56F");
        weatherMax = new Label("Max: 85F");
        pressureLabel = new Label("pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeed = new Label("wind speed: 123/hr");
        sunRiseLabel = new Label("sunrise: ");
        sunSetLabel = new Label("sunset: ");
        weatherDescription = new Label("Description: Clear Sky");
        
        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.isResponsive();

        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }

    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("My Weather Application!");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponents(title);
        mainLayout.addComponents(headerLayout);
    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image icon = new Image(null, new ClassResource(MainView.class,"/weather_icon.png"));
        icon.setWidth("200px");
        icon.setHeight("200px");

        logoLayout.addComponents(icon);

        mainLayout.addComponents(logoLayout);
    }

    private void setupForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        //creat dropdown option
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("50px");
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponents(unitSelect);

        //add textfield
        cityTextField = new TextField();
        cityTextField.setWidth("250px");
        cityTextField.setPlaceholder("city name..");
        formLayout.addComponents(cityTextField);

        //add button
        showWeatherButton = new Button();
        showWeatherButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showWeatherButton.setWidth("50px");
        showWeatherButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(showWeatherButton);

        mainLayout.addComponents(formLayout);

    }

    private void dashBoardTitle() {
        dashBoardMain = new HorizontalLayout();
        dashBoardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //current temperature label
        currentTemp = new Label("19f");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);

        currentTempLayout = new VerticalLayout();
        currentTempLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        currentTempLayout.setSpacing(false);

        // border of layouts
       /*Page.Styles styles = Page.getCurrent().getStyles();
        String css = ".layout-with-border {\n" +
                "    border: 1px solid black;\n" +
                "}";
        styles.add(css);*/
        currentTempLayout.setWidth("150px");
        currentTempLayout.setHeight("150px");
        currentTempLayout.setStyleName("layout-with-border");

        currentLocationTitle = new Label("default location is karben");
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);

        //currentTempLayout.addComponents(currentLocationTitle,currentTemp);
        currentTempLayout.addComponents(currentTemp);

    }

    private void dashBoardDescription() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //description vertical layout
        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

       // weatherDescription = new Label("Description: Clear Sky");
        descriptionLayout.addComponents(weatherDescription);
        descriptionLayout.addComponents(weatherMIn);
        descriptionLayout.addComponents(weatherMax);

        //pressure, humidity etc..
        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        pressureLayout.addComponents(pressureLabel);
        pressureLayout.addComponents(humidityLabel);
        pressureLayout.addComponents(windSpeed);
        pressureLayout.addComponents(sunRiseLabel);
        pressureLayout.addComponents(sunSetLabel);

    }

    private void updateUI() throws JSONException {
        String cityName = cityTextField.getValue();
        String defaultUnit;
        String unit;

        if(unitSelect.getValue().equals("C")){
            defaultUnit = "metric";
            unitSelect.setValue("C");
            //design sign °C
            unit = "\u00b0" + "C";
        }else{
            defaultUnit = "imperial";
            unitSelect.setValue("F");
            unit = "\u00b0" + "F";
        }

        weatherService.setCityName(cityName);
        weatherService.setUnit(defaultUnit);

        currentLocationTitle.setValue(cityName);
        currentLocationTitle.setStyleName(ValoTheme.LABEL_H4);

        //getting temperature
        JSONObject myObject = weatherService.returnMainObject();
        int temp = myObject.getInt("temp");
        currentTemp.setValue(temp+ unit);

        //getting min max pressure humidity
        JSONObject mainObject = weatherService.returnMainObject();
        int minTemp = mainObject.getInt("temp_min");
        int maxTemp = mainObject.getInt("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");

        //getting wind speed
        JSONObject windObject = weatherService.returnWindObject();
        double speed= windObject.getDouble("speed");

        // Get Sunrise and Sunset
        JSONObject sysObject = weatherService.returnSunState();
        long sunrise= sysObject.getLong("sunrise") * 1000;
        long sunset= sysObject.getLong("sunset") * 1000;

        //getting up icon image
        String iconCode="";
        String description ="";
        JSONArray weatherObjectArray = weatherService.returnWeatherArray();

        for(int i=0; i < weatherObjectArray.length(); i++){
            JSONObject weatherObject = weatherObjectArray.getJSONObject(i);
            description = weatherObject.getString("description");
            iconCode = weatherObject.getString("icon");
        }

        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/wn/"+iconCode+"@2x.png"));
        /*Page.Styles styles = Page.getCurrent().getStyles();
        String css = ".layout-with-border {\n" +
                "    border: 1px solid black;\n" +
                "}";
        styles.add(css);*/
        iconImage.setStyleName("layout-with-border");
        iconImage.setWidth("150px");

//        dashBoardMain.addComponents(currentLocationTitle, iconImage, currentTemp);
        dashBoardMain.addComponents(currentTempLayout, iconImage);
        mainLayout.addComponents(dashBoardMain);

        //Update Description UI
        weatherDescription.setValue("Description: " + description);
        weatherMIn.setValue("Min: " + minTemp + unit);
        weatherMax.setValue("Max: " + maxTemp + unit);
        pressureLabel.setValue("Pressure: " + pressure + "hpa");
        humidityLabel.setValue("Humidity: " + humidity + "%");
        windSpeed.setValue("Wind Speed: " + speed + "m/s");
        sunRiseLabel.setValue("Sunrise: " + convertTime(sunrise));
        sunSetLabel.setValue("Sunset: " + convertTime(sunset));

        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);
        mainLayout.addComponents(mainDescriptionLayout);
    }

    private String convertTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh.mm aa");
        return  dateFormat.format(new Date(time));
    }

}









/*

try {
        JSONArray jsonArray = weatherService.returnWeatherArray("karben");
        for(int i=0; i < jsonArray.length(); i++){
        JSONObject weatherObject = jsonArray.getJSONObject(i);


        System.out.println("Id: "+weatherObject.getInt("id")
        + ", main: " + weatherObject.getString("main")
        + ", description: " + weatherObject.getString("description"));


        //without helper function
                */
/*System.out.println("Id: "+weatherObject.getInt("id") + ", name: " + weatherObject.getString("name")
                                    + ", lat: "+ weatherObject.getJSONObject("coord").getDouble("lat")
                                    + ", lon: "+ weatherObject.getJSONObject("coord").getDouble("lon")
                                    + ", temperature: "+ weatherObject.getJSONObject("main").getInt("temp")
                                    + ", pressure: "+ weatherObject.getJSONObject("main").getInt("pressure")
                                    + ", humidity: "+ weatherObject.getJSONObject("main").getInt("humidity")
                                    + ", minimum-temperature: "+ weatherObject.getJSONObject("main").getInt("temp_min")
                                    + ", maximum-temperature: "+ weatherObject.getJSONObject("main").getInt("temp_max"));*//*

        };

        JSONObject mainObject = weatherService.returnMainObject("karben");
        System.out.println("pressure: " + mainObject.getLong("pressure"));

        JSONObject windObject = weatherService.returnWindObject("karben");
        System.out.println("speed: " + windObject.getDouble("speed")
        + ", degree: " + windObject.getDouble("deg") );

        JSONObject sysObject = weatherService.returnSunState("karben");
        System.out.println("sunrise: " + sysObject.getInt("sunrise ")
        + ", sunset: " + sysObject.getInt("sunset") );
        } catch (JSONException e) {
        e.printStackTrace();
        }*/
