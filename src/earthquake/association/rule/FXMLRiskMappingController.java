/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.association.rule;

import earthquake.function.EarthquakeAssociationRule;
import association_rule_apriori.AssocRules;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;


/**
 * FXML Controller class
 *
 * @author citra
 */
public class FXMLRiskMappingController implements Initializable {
static String provinceName[][] = {
        {"Aceh", "1.0"},
        {"Sumatera Utara", "2.0"},
        {"Sumatera Barat", "3.0"},
        {"Kep Riau", "4.0"},
        {"Riau", "5.0"},
        {"Jambi", "6.0"},
        {"Bengkulu", "7.0"},
        {"Sumatera Selatan", "8.0"},
        {"Kep Bangka", "9.0"},
        {"Lampung", "10.0"},
        {"Maluku Utara", "11.0"},//selama ini ketuker
        {"Maluku", "12.0"},
        {"Papua Barat", "13.0"},
        {"Papua", "14.0"},
        {"Sulawesi Barat", "15.0"},
        {"Sulawesi Selatan", "16.0"},
        {"Sulawesi Tenggara", "17.0"},
        {"Sulawesi Tengah", "18.0"},
        {"Gorontalo", "19.0"},
        {"Sulawesi Utara", "20.0"},
        {"Banten", "21.0"},
        {"Jawa Barat", "22.0"},
        {"Jawa Tengah", "23.0"},
        {"Jawa Timur", "24.0"},
        {"Yogyakarta", "25.0"},
        {"Jakarta", "26.0"},
        {"Bali", "27.0"},
        {"NTB", "28.0"},
        {"NTT", "29.0"},
        {"Kalimantan Barat", "30.0"},
        {"Kalimantan Timur", "31.0"},
        {"Kalimantan Utara", "32.0"},
        {"Kalimantan Tengah", "33.0"},
        {"Kalimantan Selatan", "34.0"}
    };

    @FXML
    private ComboBox comboboxYearStart;
    @FXML
    private ComboBox comboboxYearEnd;
    @FXML
    private ComboBox comboboxMagMin;
    @FXML
    private ComboBox comboboxMagMax;
    @FXML
    private JFXButton buttonVisualize;
    @FXML
    private WebView webView;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelCoordinate;
    @FXML
    private Label labelProvince;
    @FXML
    private Label labelMagnitude;
    @FXML
    private Label labelDepth;
    @FXML
    private TableView tableAntecendent;
    @FXML
    private TableColumn colIDAntecendent;
    @FXML
    private TableColumn colProvAntecendent;
    @FXML
    private TableView tableAssociation;
    @FXML
    private TableColumn colAntecen;
    @FXML
    private TableColumn colConse;
    @FXML
    private TableColumn colConf;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow textAssociation;
    @FXML
    private Button coba;
    @FXML
    private StackPane loadingPane;
    @FXML
    private ImageView img;
    @FXML
    private Spinner<?> spinnerSupport;
    @FXML
    private Spinner<?> spinnerConfidence;
    @FXML
    private Spinner<?> spinnerLift;
    @FXML
    private ComboBox<?> comboboxTransaction;

    static int yearStart, yearEnd;
    static double magMin, magMax;
    static ArrayList<String[]> dataEq = new ArrayList<>();
    static ArrayList<String[]> dataEqFilter = new ArrayList<>();
    static ArrayList<String[]> dataEqCluster = new ArrayList<>();
    static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    static DecimalFormat df = new DecimalFormat("#.###");

    static AssocRules rules;
    private WebEngine webEngine;
    private JSObject jSObject;

    static ArrayList<String[]> rulesProvAll = new ArrayList<String[]>();
    static String cons = "";

    static int firstYear = 0, lastYear = 0;
    static ArrayList<String[]> rulesCoba = new ArrayList<String[]>();
    static boolean incremental = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        loadingPane.setVisible(false);
        img.setImage(new Image(this.getClass().getResource("/images/Interwind-1s-200px (1).gif").toExternalForm()));
        //setProxy("proxy3.eepis-its.edu", "3128");
        String string = getClass().getResource("map/map.html").toExternalForm();

        webEngine = webView.getEngine();
        webEngine.load(string);

        Worker worker = webEngine.getLoadWorker();
        worker.stateProperty().addListener((observable) -> {
            jSObject = (JSObject) webEngine.executeScript("window");
            jSObject.setMember("eqMap", this);
        });

        try {
            inisializeData();
           // tableAction();
        } catch (ParseException ex){
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void inisializeData() throws ParseException {
         String fileName = "dataEarthquake1900-2018.csv";
        //DATE LAT LONG DEPTH MAG IDPROV PROV
        //0    1   2    3     4   5      6
        dataEq = readCSV(fileName, "no");
        Date firstDate = formatDate.parse(dataEq.get(0)[0]);
        Date lastDate = formatDate.parse(dataEq.get(dataEq.size() - 1)[0]);
        Calendar calendarFirstDate = Calendar.getInstance();
        calendarFirstDate.setTime(firstDate);
        Calendar calendarLastDate = Calendar.getInstance();
        calendarLastDate.setTime(lastDate);
        int totYear = 0;
        
        //SET YEARS IN COMBOBOX
        firstYear = calendarFirstDate.get(Calendar.YEAR);
        lastYear = calendarLastDate.get(Calendar.YEAR);
        for (int i = calendarFirstDate.get(Calendar.YEAR); i <= calendarLastDate.get(Calendar.YEAR); i++) {
            comboboxYearStart.getItems().add(i);
            comboboxYearEnd.getItems().add(i);
            totYear++;
        }
        //SET MAGNITUDE IN COMBOBOX
        for (int i = 1; i <= 10; i++) {
            comboboxMagMin.getItems().add(i);
            comboboxMagMax.getItems().add(i);
        }
    }
    
    static ArrayList<String[]> readCSV(String fileName, String title) {
        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String[]> dataGempaList = new ArrayList<>();
        int iteration = 0;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                if (title.equalsIgnoreCase("yes")) {
                    if (iteration == 0) {
                        iteration++;
                        continue;
                    }
                }
                // use comma as separator
                String[] gempa = line.split(cvsSplitBy);
                //input datagGempa
                dataGempaList.add(gempa);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataGempaList;
    }
    
    @FXML
    void transaksiGempa(ActionEvent event) {

    }

    @FXML
    void visualizeButtonAction(ActionEvent event) throws ParseException{
        animationFade(0.0, 1.0);
        loadingPane.setVisible(true);
        clearViewresult();
        
        new Thread() {
            @Override
            public void run() {
                try {
                   dataEqFilter = dataFiltering();
                } catch (ParseException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                //DATE LAT LONG DEPTH MAG IDPROV PROV
                //0    1   2    3     4   5      6
                Platform.runLater(() -> {
                    for (int i = 0; i < dataEqFilter.size(); i++) {
                        webEngine.executeScript("visualize(" + i + "," + dataEqFilter.get(i)[1] + "," + dataEqFilter.get(i)[2] + "," + dataEqFilter.get(i)[3] + "," + dataEqFilter.get(i)[4] + ");");
                    }
                    webEngine.executeScript("createLegend(" + 100 + ");");
                    animationFade(1.0, 0.0);
                    loadingPane.setVisible(false);
                });
            }
        }.start();
    }
    
        private void animationFade(double from, double to) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), loadingPane);
        fadeOut.setFromValue(from);
        fadeOut.setToValue(to);
        fadeOut.play();
    }
        
        public void showInfoEarthquake(int i){
            labelDate.setText(dataEqFilter.get(i)[0]);
            labelCoordinate.setText(dataEqFilter.get(i)[1] + "," + dataEqFilter.get(i)[2]);
            labelMagnitude.setText(dataEqFilter.get(i)[4]);
            labelDepth.setText(dataEqFilter.get(i)[3]);
            labelProvince.setText(dataEqFilter.get(i)[6]);
        }
        
        public void clearInfoEarthquake(){
        labelDate.setText("-");
        labelCoordinate.setText("-");
        labelProvince.setText("-");
        labelMagnitude.setText("-");
        labelDepth.setText("-");
        }
        
        private void clearViewresult() {
        webEngine.executeScript("clearMarker();");
        clearInfoEarthquake();
    }
        
        ArrayList<String[]> dataFiltering() throws ParseException {
        int n = dataEq.size();
        yearStart = (int) comboboxYearStart.getValue();
        yearEnd = (int) comboboxYearEnd.getValue();
        magMin = (int) comboboxMagMin.getValue();
        magMax = (int) comboboxMagMax.getValue();
        ArrayList<String[]> dataEqFilter = new ArrayList<>();
        if (yearStart <= yearEnd && magMin <= magMax) {
            for (int i = 0; i < n; i++) {
                Date d = formatDate.parse(dataEq.get(i)[0]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                int yearData = calendar.get(Calendar.YEAR);
                double magData = Double.valueOf(dataEq.get(i)[4]);
                if (yearStart <= yearData && yearData <= yearEnd) {
                    if (magMin <= magData && magData <= magMax) {
                        dataEqFilter.add(dataEq.get(i));
                    }
                }

            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Wrong Input",
                    "Filtering Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return dataEqFilter;
    }  
}
