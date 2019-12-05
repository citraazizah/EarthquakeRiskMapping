/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.association.rule;

import earthquake.function.EarthquakeAssociationRule;
import association_rule_apriori.AssocRules;
import com.jfoenix.controls.JFXButton;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;

/**
 *
 * @author Dell
 */
public class FXMLDocumentController implements Initializable {

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
    private ScrollPane scrollPane;
    @FXML
    private ComboBox comboboxYearStart;
    @FXML
    private ComboBox comboboxYearEnd;
    @FXML
    private ComboBox comboboxMagMin;
    @FXML
    private ComboBox comboboxMagMax;
    @FXML
    private Button buttonVisualize;
    @FXML
    private Button buttonAssociate;
    @FXML
    private Button buttonPrediction;
        @FXML
    private Button buttonPDF;
          @FXML
    private Button buttonDensity;
    @FXML
    private Button buttonAssociateRisk;
    @FXML
    private Spinner spinnerSupport;
    @FXML
    private Spinner spinnerK;
    @FXML
    private Spinner spinnerConfidence;
    @FXML
    private Spinner spinnerLift;
    @FXML
    private ComboBox comboboxTransaction;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelCoordinate;
    @FXML
    private Label labelProvince;
    @FXML
    private Label labelMag;
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
    private TextFlow textAssociation;
    @FXML
    private WebView webView;

    @FXML
    private Button discreteRiskButton;
    @FXML
    private LineChart<String, Number> chart;
    @FXML
    private CategoryAxis X;
    @FXML
    private NumberAxis Y;
    @FXML
    private JFXButton buttonRisk;
    @FXML
    private TableView consequentTable;
    @FXML
    private TableColumn consProvColumn;
    @FXML
    private TableColumn averageProvColumn;

    @FXML
    private StackPane loadingPane;
    @FXML
    private ImageView img;

    @FXML
    private ComboBox comboBoxdiskritRange;
    @FXML
    private ComboBox comboBoxdiskritYears;

    @FXML
    private ComboBox comboBoxincrementalRange;
    @FXML
    private ComboBox comboBoxincrementalYears;
    @FXML
    private Label labelRiskValue;

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
            // TODO
            inisializeData();
            tableAction();
        } catch (ParseException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        textAssociation.setPadding(new Insets(5, 5, 5, 5));

    }

    void inisializeData() throws ParseException {
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
        //SET YEARS
        firstYear = calendarFirstDate.get(Calendar.YEAR);
        lastYear = calendarLastDate.get(Calendar.YEAR);
        for (int i = calendarFirstDate.get(Calendar.YEAR); i <= calendarLastDate.get(Calendar.YEAR); i++) {
            comboboxYearStart.getItems().add(i);
            comboboxYearEnd.getItems().add(i);
            totYear++;
        }
        comboboxYearStart.getSelectionModel().select(totYear - 2);
        comboboxYearEnd.getSelectionModel().select(totYear - 1);
        //SET MAGNITUDE
        for (int i = 1; i <= 10; i++) {
            comboboxMagMin.getItems().add(i);
            comboboxMagMax.getItems().add(i);
        }
        comboboxMagMin.getSelectionModel().select(4);
        comboboxMagMax.getSelectionModel().select(8);

        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.1, 0.1);
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.1, 0.1);
        spinnerSupport.setValueFactory(valueFactory);
        spinnerConfidence.setValueFactory(valueFactory1);

        SpinnerValueFactory valueFactorydb = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 1);
        spinnerLift.setValueFactory(valueFactorydb);

        SpinnerValueFactory vfK = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 10, 1);
        spinnerK.setValueFactory(vfK);

        for (int i = 1; i <= 7; i++) {
            comboboxTransaction.getItems().add(i);

        }
        comboboxTransaction.getSelectionModel().selectLast();
        for (int i = 5; i <= 15; i += 5) {
            comboBoxdiskritRange.getItems().add(i);
            comboBoxincrementalRange.getItems().add(i);
        }
        comboBoxdiskritRange.getSelectionModel().selectFirst();
        comboBoxincrementalRange.getSelectionModel().selectFirst();

        int range = Integer.valueOf(comboBoxdiskritRange.getSelectionModel().selectedItemProperty().getValue().toString());
        int startYear = firstYear;
        int endYear = lastYear;
        int value = startYear;
        do {
            if (value + (range - 1) > endYear) {
                comboBoxdiskritYears.getItems().add(value + "-2018");
            } else {
                comboBoxdiskritYears.getItems().add(value + "-" + (value + (range - 1)));
            }
            value += range;
        } while (value <= endYear);
        comboBoxdiskritYears.getItems().add("Sum of All Period");
        comboBoxdiskritYears.getSelectionModel().selectLast();

        range = Integer.valueOf(comboBoxincrementalRange.getSelectionModel().selectedItemProperty().getValue().toString());
        int fixValue = 1963;
        value = fixValue;
        do {
            if (value + (range - 1) > endYear) {
                comboBoxincrementalYears.getItems().add(fixValue + "-2018");
            } else {
                comboBoxincrementalYears.getItems().add(fixValue + "-" + (value + (range - 1)));
            }
            value += range;
        } while (value <= endYear);
        comboBoxincrementalYears.getSelectionModel().selectLast();

    }

    private void clearViewresult() {
        webEngine.executeScript("clearMarker();");
        tableAntecendent.getItems().clear();
        tableAssociation.getItems().clear();
        textAssociation.getChildren().clear();
        consequentTable.getItems().clear();
        chart.getData().clear();
        labelRiskValue.setText("-");
        clearInfoEq();
    }

    @FXML
    private void visualizeButtonAction(ActionEvent event) throws ParseException {
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

    public void showInfoEq(int i) {
        labelDate.setText(dataEqFilter.get(i)[0]);
        labelCoordinate.setText(dataEqFilter.get(i)[1] + ", " + dataEqFilter.get(i)[2]);
        labelProvince.setText(dataEqFilter.get(i)[6]);
        labelMag.setText(dataEqFilter.get(i)[4]);
        labelDepth.setText(dataEqFilter.get(i)[3]);
    }

    public void clearInfoEq() {
        labelDate.setText("-");
        labelCoordinate.setText("-");
        labelProvince.setText("-");
        labelMag.setText("-");
        labelDepth.setText("-");
    }

    @FXML
    private void associateButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        associationFunction(dataEqFilter);
    }
    @FXML
    private void predictionButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        associationFunction(dataEqFilter);
    }
    @FXML
    private void densityButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        associationFunction(dataEqFilter);
    }
    @FXML
    private void PDFButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        associationFunction(dataEqFilter);
    }

    private void associationFunction(ArrayList<String[]> data) throws IOException, ParseException {
        // minsup,minconf,minLift,pilTransaksi
        clearViewresult();
        double minsup = (double) spinnerSupport.getValue();
        double minconf = (double) spinnerConfidence.getValue();
        double minLift = (double) spinnerLift.getValue();
        int pilTransaksi = (int) comboboxTransaction.getSelectionModel().selectedItemProperty().getValue();
        rules = EarthquakeAssociationRule.run(data, minsup, minconf, minLift, pilTransaksi);
        if (rules.getRulesCount() == 0) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning Association Rule");
            alert.setHeaderText("No Rule Founds");
            alert.setContentText("There are no rules!");
            alert.showAndWait();
        }
        List<Integer> dataGempaAvailable = new ArrayList<Integer>();
        for (int i = 0; i < rules.getRulesCount(); i++) {
            dataGempaAvailable.add(rules.getRules().get(i).getItemset1()[0]);
        }
        dataGempaAvailable = dataGempaAvailable.stream().distinct().collect(Collectors.toList());
        Collections.sort(dataGempaAvailable);
        ObservableList<DataColumns> dataProv = FXCollections.observableArrayList();
        for (int i = 0; i < dataGempaAvailable.size(); i++) {
            String idProv = dataGempaAvailable.get(i).toString();
            String provName = "";
            for (int j = 0; j < provinceName.length; j++) {
                if (dataGempaAvailable.get(i).doubleValue() == Double.valueOf(provinceName[j][1])) {
                    provName = provinceName[j][0];
                    break;
                }
            }
            DataColumns newData = new DataColumns(idProv, provName);
            dataProv.add(newData);
        }
        colIDAntecendent.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data1"));
        colProvAntecendent.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data2"));
        colIDAntecendent.prefWidthProperty().bind(tableAntecendent.widthProperty().multiply(0.15));
        colProvAntecendent.prefWidthProperty().bind(tableAntecendent.widthProperty().multiply(0.85));
        colIDAntecendent.setResizable(false);
        colProvAntecendent.setResizable(false);
        tableAntecendent.setItems(dataProv);
        tableAntecendent.getSortOrder().add(colIDAntecendent);
        tableAntecendent.getSelectionModel().selectFirst();
        tableAntecendentFunction();
        tableAssociation.getSelectionModel().selectFirst();
        tableAssociationFunction();
    }

    public void provinceAssociationRisk(String nameProv) {
        System.out.println(nameProv);
        ArrayList<String[]> provAntec = new ArrayList<>();
        for (int i = 0; i < rules.getRulesCount(); i++) {
            String prov = "";
            for (int j = 0; j < rules.getRules().get(i).getItemset2().length; j++) {
                for (int k = 0; k < provinceName.length; k++) {
                    if (Double.valueOf(provinceName[k][1]) == (double) rules.getRules().get(i).getItemset2()[j]) {
                        prov = provinceName[k][0];
                        break;
                    }
                }

            }
            if (nameProv.equalsIgnoreCase(prov)) {
                for (int j = 0; j < rules.getRules().get(i).getItemset1().length; j++) {
                    String prov1 = "";
                    for (int k = 0; k < provinceName.length; k++) {
                        if (Double.valueOf(provinceName[k][1]) == (double) rules.getRules().get(i).getItemset1()[j]) {
                            prov1 = provinceName[k][0];
                            break;
                        }
                    }
                    provAntec.add(new String[]{prov1, String.valueOf(rules.getRules().get(i).confidence)});
                }
            }
        }
        for (int i = 0; i < provAntec.size(); i++) {
            System.out.println(Arrays.toString(provAntec.get(i)));
        }
        chart.getData().clear();
        chart.setAnimated(false);

        XYChart.Series series = new XYChart.Series<>();
        for (int i = 0; i < provAntec.size(); i++) {
            series.getData().add(new XYChart.Data<>(provAntec.get(i)[0], Double.valueOf(provAntec.get(i)[1])));
        }
        chart.getData().addAll(series);

    }

    @FXML
    private void associateRiskButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        incremental = false;
        clearViewresult();
        
        animationFade(0.0, 1.0);
        loadingPane.setVisible(true);

        new Thread() {
            @Override
            public void run() {
                try {
                    double minsup = (double) spinnerSupport.getValue();
                    double minconf = (double) spinnerConfidence.getValue();
                    double minLift = (double) spinnerLift.getValue();
                    int pilTransaksi = (int) comboboxTransaction.getSelectionModel().selectedItemProperty().getValue();
                    rules = EarthquakeAssociationRule.run(dataEqFilter, minsup, minconf, minLift, pilTransaksi);
                    if (rules.getRulesCount() == 0) {
                        if (rules.getRulesCount() == 0) {
                            Platform.runLater(() -> {
                                animationFade(1.0, 0.0);
                                loadingPane.setVisible(false);
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Warning Association Rule");
                                alert.setHeaderText("No Rule Founds");
                                alert.setContentText("There are no rules!");
                                alert.showAndWait();
                            });
                        }
                    }
                    rulesProvAll = provNameFromRuleFunction(rules, yearStart, yearEnd);
                    riskMapFunction(rulesProvAll);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }

    @FXML
    private void diskritRangeButtonAction(ActionEvent event) {
        comboBoxdiskritYears.getItems().clear();
        int range = Integer.valueOf(comboBoxdiskritRange.getSelectionModel().selectedItemProperty().getValue().toString());
        int startYear = firstYear;
        int endYear = lastYear;
        int value = startYear;
        do {
            if (value + (range - 1) > endYear) {
                comboBoxdiskritYears.getItems().add(value + "-2018");
            } else {
                comboBoxdiskritYears.getItems().add(value + "-" + (value + (range - 1)));
            }
            value += range;
        } while (value <= endYear);
        comboBoxdiskritYears.getItems().add("Sum of All Period");
        comboBoxdiskritYears.getSelectionModel().selectLast();
    }

    @FXML
    private void incrementalRangeButtonAction(ActionEvent event) {
        comboBoxincrementalYears.getItems().clear();
        int range = Integer.valueOf(comboBoxincrementalRange.getSelectionModel().selectedItemProperty().getValue().toString());
        int fixValue = 1963;
        int value = fixValue;
        int endYear = lastYear;
        do {
            if (value + (range - 1) > endYear) {
                comboBoxincrementalYears.getItems().add(fixValue + "-2018");
            } else {
                comboBoxincrementalYears.getItems().add(fixValue + "-" + (value + (range - 1)));
            }
            value += range;
        } while (value <= endYear);
        comboBoxincrementalYears.getSelectionModel().selectLast();
    }

    @FXML
    private void discreteRiskButtonAction(ActionEvent event) {
        incremental = false;
        clearViewresult();
        animationFade(0.0, 1.0);
        loadingPane.setVisible(true);
        new Thread() {
            @Override
            public void run() {
                try {
                    String syear = comboBoxdiskritYears.getSelectionModel().selectedItemProperty().getValue().toString();
                    double minsup = 0.1;
                    double minconf = 0.1;
                    double minLift = 1;
                    int pilTransaksi = 7;
                    ArrayList<AssocRules> rulesAll = new ArrayList<AssocRules>();
                    if (syear.equalsIgnoreCase("Sum of All Period")) {
                        int range = Integer.valueOf(comboBoxdiskritRange.getSelectionModel().selectedItemProperty().getValue().toString());
                        int tahunAwal = firstYear;
                        int tahunBatas = tahunAwal + (range - 1);
                        int tahunMax = lastYear;
                        for (int i = tahunBatas; i <= tahunMax; i += range) {
                            ArrayList<String[]> data;
                            data = dataFiltering(tahunAwal, i, 5, 10);
                            rulesAll.add(EarthquakeAssociationRule.run(data, minsup, minconf, minLift, pilTransaksi));
                            tahunAwal += range;
                        }
                        ArrayList<String[]> data = dataFiltering(tahunAwal, tahunMax, 5, 10);
                        rulesAll.add(EarthquakeAssociationRule.run(data, minsup, minconf, minLift, pilTransaksi));
                        rulesProvAll = new ArrayList<String[]>();
                        tahunAwal = firstYear;
                        for (int i = 0; i < rulesAll.size(); i++) {
                            System.out.println(tahunAwal + "-" + tahunBatas + " ==> " + rulesAll.get(i).getRulesCount());
                            for (int j = 0; j < rulesAll.get(i).getRulesCount(); j++) {
                                int sumItemset1 = rulesAll.get(i).getRules().get(j).getItemset1().length;
                                int sumItemset2 = rulesAll.get(i).getRules().get(j).getItemset2().length;
                                String[] itemset1 = new String[sumItemset1];
                                String[] itemset2 = new String[sumItemset2];
                                for (int k = 0; k < sumItemset1; k++) {
                                    int index = (rulesAll.get(i).getRules().get(j).getItemset1()[k] - 1);
                                    itemset1[k] = provinceName[index][0];
                                }
                                for (int k = 0; k < sumItemset2; k++) {
                                    int index = (rulesAll.get(i).getRules().get(j).getItemset2()[k] - 1);
                                    itemset2[k] = provinceName[index][0];
                                }
                                rulesProvAll.add(new String[]{
                                    Arrays.toString(itemset1)
                                    .replace("[", "")
                                    .replace("]", ""),
                                    Arrays.toString(itemset2)
                                    .replace("[", "")
                                    .replace("]", ""),
                                    String.valueOf(rulesAll.get(i).getRules().get(j).getConfidence()),
                                    String.valueOf(tahunAwal) + "-" + String.valueOf(tahunBatas)});
                            }
                            tahunBatas += range;
                            if (tahunBatas == 2019) {
                                tahunBatas = 2018;
                            }
                            tahunAwal += range;
                        }
                        riskMapFunction(rulesProvAll);
                    } else {
                        String[] yearFilter = syear.split("-");
                        ArrayList<String[]> data = dataFiltering(Integer.valueOf(yearFilter[0]), Integer.valueOf(yearFilter[1]), 5, 10);
                        AssocRules rules = EarthquakeAssociationRule.run(data, minsup, minconf, minLift, pilTransaksi);

                        if (rules.getRulesCount() == 0) {
                            Platform.runLater(() -> {
                                animationFade(1.0, 0.0);
                                loadingPane.setVisible(false);
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Warning Association Rule");
                                alert.setHeaderText("No Rule Founds");
                                alert.setContentText("There are no rules!");
                                alert.showAndWait();
                            });
                        }
                        rulesProvAll = provNameFromRuleFunction(rules, Integer.valueOf(yearFilter[0]), Integer.valueOf(yearFilter[1]));
                        riskMapFunction(rulesProvAll);
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }

    private ArrayList<String[]> provNameFromRuleFunction(AssocRules rules, int yearStart, int yearEnd) {
        rulesProvAll = new ArrayList<String[]>();
        for (int i = 0; i < rules.getRulesCount(); i++) {
            int sumItemset1 = rules.getRules().get(i).getItemset1().length;
            int sumItemset2 = rules.getRules().get(i).getItemset2().length;
            String[] itemset1 = new String[sumItemset1];
            String[] itemset2 = new String[sumItemset2];
            for (int j = 0; j < sumItemset1; j++) {
                int index = (rules.getRules().get(i).getItemset1()[j] - 1);
                itemset1[j] = provinceName[index][0];
            }
            for (int j = 0; j < sumItemset2; j++) {
                int index = (rules.getRules().get(i).getItemset2()[j] - 1);
                itemset2[j] = provinceName[index][0];
            }
            rulesProvAll.add(new String[]{
                Arrays.toString(itemset1)
                .replace("[", "")
                .replace("]", ""),
                Arrays.toString(itemset2)
                .replace("[", "")
                .replace("]", ""),
                String.valueOf(rules.getRules().get(i).getConfidence()),
                String.valueOf(yearStart) + "-" + String.valueOf(yearEnd)});
        }
        return rulesProvAll;
    }

    @FXML
    private void incrementalRiskButtonAction(ActionEvent event) {
        incremental = true;
        clearViewresult();
        
        animationFade(0.0, 1.0);
        loadingPane.setVisible(true);
        new Thread() {
            @Override
            public void run() {
                double minsup = 0.1;
                double minconf = 0.1;
                double minLift = 1;
                int pilTransaksi = 7;
                String syear = comboBoxincrementalYears.getSelectionModel().selectedItemProperty().getValue().toString();
                int range = Integer.valueOf(comboBoxincrementalRange.getSelectionModel().selectedItemProperty().getValue().toString());
                String[] yearFilter = syear.split("-");
                try {
                    ArrayList<String[]> data = dataFiltering(Integer.valueOf(yearFilter[0]), Integer.valueOf(yearFilter[1]), 5, 10);
                    AssocRules rules = EarthquakeAssociationRule.run(data, minsup, minconf, minLift, pilTransaksi);
                    if (rules.getRulesCount() == 0) {
                        Platform.runLater(() -> {
                            animationFade(1.0, 0.0);
                            loadingPane.setVisible(false);
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Warning Association Rule");
                            alert.setHeaderText("No Rule Founds");
                            alert.setContentText("There are no rules!");
                            alert.showAndWait();
                        });
                    }
                    rulesProvAll = provNameFromRuleFunction(rules, Integer.valueOf(yearFilter[0]), Integer.valueOf(yearFilter[1]));
                    riskMapFunction(rulesProvAll);
                    //===================================================================================================
                    int tahunAwal = Integer.valueOf(yearFilter[0]);
                    int tahunBatas = tahunAwal + (range - 1);
                    int tahunMax = Integer.valueOf(yearFilter[1]);
                    ArrayList<String[]> data1 = new ArrayList<>();
                    ArrayList<AssocRules> rulesAll = new ArrayList<AssocRules>();
                    for (int i = tahunBatas; i < tahunMax; i += range) {
                        data1 = dataFiltering(tahunAwal, i, 5, 10);
                        rulesAll.add(EarthquakeAssociationRule.run(data1, minsup, minconf, minLift, pilTransaksi));
                    }
                    data1 = dataFiltering(tahunAwal, tahunMax, 5, 10);
                    rulesAll.add(EarthquakeAssociationRule.run(data1, minsup, minconf, minLift, pilTransaksi));
                    rulesCoba = new ArrayList<String[]>();
                    tahunAwal = Integer.valueOf(yearFilter[0]);
                    for (int i = 0; i < rulesAll.size(); i++) {
                        System.out.println(tahunAwal + "-" + tahunBatas + " ==> " + rulesAll.get(i).getRulesCount());
                        for (int j = 0; j < rulesAll.get(i).getRulesCount(); j++) {
                            int sumItemset1 = rulesAll.get(i).getRules().get(j).getItemset1().length;
                            int sumItemset2 = rulesAll.get(i).getRules().get(j).getItemset2().length;
                            String[] itemset1 = new String[sumItemset1];
                            String[] itemset2 = new String[sumItemset2];
                            for (int k = 0; k < sumItemset1; k++) {
                                int index = (rulesAll.get(i).getRules().get(j).getItemset1()[k] - 1);
                                itemset1[k] = provinceName[index][0];
                            }
                            for (int k = 0; k < sumItemset2; k++) {
                                int index = (rulesAll.get(i).getRules().get(j).getItemset2()[k] - 1);
                                itemset2[k] = provinceName[index][0];
                            }
                            rulesCoba.add(new String[]{
                                Arrays.toString(itemset1)
                                .replace("[", "")
                                .replace("]", ""),
                                Arrays.toString(itemset2)
                                .replace("[", "")
                                .replace("]", ""),
                                String.valueOf(rulesAll.get(i).getRules().get(j).getConfidence()),
                                String.valueOf(tahunBatas)});
                        }
                        tahunBatas += range;
                        if (tahunBatas > 2018) {
                            tahunBatas = 2018;
                        }
                    }
//                    for (int i = 0; i < rulesCoba.size(); i++) {
//                        System.out.println(Arrays.toString(rulesCoba.get(i)));
//                    }
                    //===================================================================================================
                } catch (ParseException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public void tryIncrementalTable(String consequent) {
        cons = consequent;
        double riskTotal = 0.0;
        chart.getData().clear();
        labelRiskValue.setText("-");
        //antecendet    consequent    confidence      tahun
        //         0             1             2          3
        List<String> provSebelum = new ArrayList<String>();
        for (int i = 0; i < rulesProvAll.size(); i++) {
            if (cons.equalsIgnoreCase(rulesProvAll.get(i)[1])) {
                provSebelum.add(rulesProvAll.get(i)[0]);
            }
        }
        provSebelum = provSebelum.stream().distinct().collect(Collectors.toList());
        Collections.sort(provSebelum);
//        double sumProvConf[][] = new double[provSebelum.size()][2];
        double riskConf[] = new double[provSebelum.size()];
        for (int i = 0; i < provSebelum.size(); i++) {
            for (int j = 0; j < rulesProvAll.size(); j++) {
                if (provSebelum.get(i).equalsIgnoreCase(rulesProvAll.get(j)[0]) && cons.equalsIgnoreCase(rulesProvAll.get(j)[1])) {
                    double risk = riskCalculation(Double.valueOf(rulesProvAll.get(j)[2]));
                    riskConf[i] += risk;
                    riskTotal += risk;
                }
            }
        }
        ObservableList<DataColumns> dataProv = FXCollections.observableArrayList();
        for (int i = 0; i < provSebelum.size(); i++) {
            String provname = provSebelum.get(i);
//          double temp = (sumProvConf[i][0] / sumProvConf[i][1]);
            String conf = df.format(riskConf[i]);
            System.out.println(provname);
            DataColumns newData = new DataColumns(provname, conf);
            dataProv.add(newData);
        }

        consProvColumn.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data1"));
        averageProvColumn.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data2"));
        consProvColumn.prefWidthProperty().bind(consequentTable.widthProperty().multiply(0.70));
        averageProvColumn.prefWidthProperty().bind(consequentTable.widthProperty().multiply(0.30));
        consProvColumn.setResizable(true);
        averageProvColumn.setResizable(true);
        consequentTable.setItems(dataProv);
        labelRiskValue.setText(df.format(riskTotal));
    }

    private String[][] associationRisk(AssocRules rules) {
        List<Integer> provDampak = new ArrayList<Integer>();
        for (int i = 0; i < rules.getRulesCount(); i++) {
            for (int j = 0; j < rules.getRules().get(i).getItemset2().length; j++) {
                provDampak.add(rules.getRules().get(i).getItemset2()[j]);
            }
        }
        provDampak = provDampak.stream().distinct().collect(Collectors.toList());
        Collections.sort(provDampak);
        double[][] provRisk = new double[provDampak.size()][2];
        double[] sumProv = new double[provDampak.size()];
        double[] sumProvConf = new double[provDampak.size()];
        for (int i = 0; i < provDampak.size(); i++) {
            for (int j = 0; j < rules.getRulesCount(); j++) {
                for (int k = 0; k < rules.getRules().get(j).getItemset2().length; k++) {
                    if (provDampak.get(i) == rules.getRules().get(j).getItemset2()[k]) {
                        sumProvConf[i] += rules.getRules().get(j).getConfidence();
                        sumProv[i]++;
                    }
                }
            }
        }
        String[][] provAssocRisk = new String[provDampak.size()][2];
        for (int i = 0; i < provDampak.size(); i++) {
            for (int j = 0; j < provinceName.length; j++) {
                if (provDampak.get(i).doubleValue() == Double.valueOf(provinceName[j][1])) {
                    provAssocRisk[i][0] = provinceName[j][0];
                    break;
                }
            }
            double temp = (double) (sumProvConf[i] / sumProv[i]);
            provAssocRisk[i][1] = String.valueOf(temp);
        }

        return (provAssocRisk);
    }

    private void riskMapFunction(ArrayList<String[]> rulesProvAll) {
        List<String> provDampak = new ArrayList<String>();
        for (int i = 0; i < rulesProvAll.size(); i++) {
            provDampak.add(rulesProvAll.get(i)[1]);
        }
        provDampak = provDampak.stream().distinct().collect(Collectors.toList());
        Collections.sort(provDampak);
        String[] provAssocRisk = new String[provDampak.size()];
        double[] provRiskConf = new double[provDampak.size()];
        for (int i = 0; i < provDampak.size(); i++) {
            List<String> provSebelum = new ArrayList<String>();
            for (int j = 0; j < rulesProvAll.size(); j++) {
                if (provDampak.get(i).equalsIgnoreCase(rulesProvAll.get(j)[1])) {
                    provSebelum.add(rulesProvAll.get(j)[0]);
                }
            }
            provSebelum = provSebelum.stream().distinct().collect(Collectors.toList());
            Collections.sort(provSebelum);
            double riskConf = 0.0;
            for (int k = 0; k < provSebelum.size(); k++) {
                for (int j = 0; j < rulesProvAll.size(); j++) {
                    if (provSebelum.get(k).equalsIgnoreCase(rulesProvAll.get(j)[0]) && provDampak.get(i).equalsIgnoreCase(rulesProvAll.get(j)[1])) {
                        riskConf += riskCalculation(Double.valueOf(rulesProvAll.get(j)[2]));
                    }
                }
            }
            provAssocRisk[i] = provDampak.get(i).toString();
            provRiskConf[i] = riskConf;
            System.out.println(provAssocRisk[i] + " , " + provRiskConf[i]);
        }
        Platform.runLater(() -> {
            for (int i = 0; i < provAssocRisk.length; i++) {
                String color = "";
                if (provRiskConf[i] < riskCalculation(0.42)) {
                    color = "LightSlateGray";
                } else if (provRiskConf[i] >= riskCalculation(0.42) && provRiskConf[i] < riskCalculation(0.54)) {
                    color = "green";
                } else if (provRiskConf[i] >= riskCalculation(0.54) && provRiskConf[i] < riskCalculation(0.61)) {
                    color = "yellow";
                } else if (provRiskConf[i] >= riskCalculation(0.61) && provRiskConf[i] < riskCalculation(0.69)) {
                    color = "orange";
                } else if (provRiskConf[i] >= riskCalculation(0.69)) {
                    color = "red";
                }
                webEngine.executeScript("createLegend(" + 300 + ");");
                webEngine.executeScript("associationRisk(\"" + provAssocRisk[i] + "\", \"" + color + "\");"); //hasilnya cuman buat provinsi yang satu doang
                animationFade(1.0, 0.0);
                loadingPane.setVisible(false);
            }
        });
    }

    private double riskCalculation(double conf) {
        int k = (int) spinnerK.getValue();
        double risk = Math.exp(k * conf);
        return risk;
    }

    void tableAction() {
        tableAntecendent.setOnMousePressed((event) -> {
            webEngine.executeScript("clearMarker();");
            tableAntecendentFunction();
        });

        tableAssociation.setOnMousePressed((event) -> {
            webEngine.executeScript("clearMarker();");
            tableAssociationFunction();
        });
        consequentTable.setOnMousePressed((event) -> {

            chart.getData().clear();
            chart.setAnimated(false);
            DataColumns data = (DataColumns) consequentTable.getSelectionModel().getSelectedItem();
            String ante = data.getData1();
            //antecendet    consequent    confidence      tahun
            //         0             1             2          3
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Confidence");
            ArrayList<String[]> tempRules = new ArrayList<>();
            if (incremental) {
                tempRules = rulesCoba;
            } else {
                tempRules = rulesProvAll;
            }
            System.out.println("===============================================================================");
            for (int i = 0; i < tempRules.size(); i++) {
                if (cons.equalsIgnoreCase(tempRules.get(i)[1]) && ante.equalsIgnoreCase(tempRules.get(i)[0])) {
                    XYChart.Data<String, Number> tempD = new XYChart.Data<>(tempRules.get(i)[3], Double.valueOf(tempRules.get(i)[2]));
                    System.out.println(tempRules.get(i)[3] + ", " + Double.valueOf(tempRules.get(i)[2]));
                    series.getData().add(tempD);
                    //                                      tahun                 conf
                }
            }
            chart.getData().addAll(series);
        });

    }

    void tableAntecendentFunction() {
        ObservableList<DataColumns> dataAssoc = FXCollections.observableArrayList();
        webEngine.executeScript("clearMarker();");
        int idProv = 0;
        String nameProv = "";
        DataColumns assocProv = (DataColumns) tableAntecendent.getSelectionModel().getSelectedItem();
        idProv = Integer.parseInt(assocProv.getData1());
        nameProv = assocProv.getData2();
        for (int i = 0; i < rules.getRulesCount(); i++) {
            if (rules.getRules().get(i).getItemset1()[0] == idProv) {
                String tempPattern = "";
                String tempPattern1 = "";
                for (int j = 0; j < rules.getRules().get(i).getItemset1().length; j++) {
                    for (int k = 0; k < provinceName.length; k++) {
                        if (Double.valueOf(provinceName[k][1]).intValue() == rules.getRules().get(i).getItemset1()[j]) {
                            if (j < rules.getRules().get(i).getItemset1().length - 1) {
                                tempPattern = tempPattern + provinceName[k][0] + ",";
                            } else {
                                tempPattern = tempPattern + provinceName[k][0];
                            }
                        }
                    }
                }
                for (int j = 0; j < rules.getRules().get(i).getItemset2().length; j++) {
                    for (int k = 0; k < provinceName.length; k++) {
                        if (Double.valueOf(provinceName[k][1]).intValue() == rules.getRules().get(i).getItemset2()[j]) {
                            if (j < rules.getRules().get(i).getItemset2().length - 1) {
                                tempPattern1 = tempPattern1 + provinceName[k][0] + ",";
                            } else {
                                tempPattern1 = tempPattern1 + provinceName[k][0];
                            }
                        }
                    }
                }
                DataColumns newAssoc = new DataColumns(tempPattern, tempPattern1, df.format(rules.getRules().get(i).getConfidence()));
                System.out.println(tempPattern + " => " + tempPattern1 + " : " + rules.getRules().get(i).getConfidence());
                dataAssoc.add(newAssoc);
            }
        }
        colAntecen.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data1"));
        colConse.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data2"));
        colConf.setCellValueFactory(new PropertyValueFactory<DataColumns, String>("data3"));
        colAntecen.prefWidthProperty().bind(tableAssociation.widthProperty().multiply(0.40));
        colConse.prefWidthProperty().bind(tableAssociation.widthProperty().multiply(0.40));
        colConf.prefWidthProperty().bind(tableAssociation.widthProperty().multiply(0.2));
        tableAssociation.setItems(dataAssoc);
        webEngine.executeScript("clearMarker();");
        webEngine.executeScript("association(\"" + dataAssoc.get(0).getData1() + "\", \"" + dataAssoc.get(0).getData2() + "\");");
        webEngine.executeScript("createLegend(" + 200 + ");");
        tableAssociation.getSelectionModel().selectFirst();
        tableAssociationFunction();
    }

    void tableAssociationFunction() {
        textAssociation.getChildren().clear();
        webEngine.executeScript("clearMarker();");
        DataColumns dataAssoc = (DataColumns) tableAssociation.getSelectionModel().getSelectedItem();
        Text t = new Text(dataAssoc.getData1() + " ==> " + dataAssoc.getData2() + " : " + dataAssoc.getData3());
        t = new Text("Based on earthquake event data taken from the beginning of the year " + yearStart + " - " + yearEnd + " end "
                + "with magnitude " + magMin + " - " + magMax + ", and minimum level of support " + spinnerSupport.getValue().toString() + " and minimum confidence level " + spinnerConfidence.getValue().toString()
                + " Obtained facts, if an earthquake occurs in the province " + dataAssoc.getData1()
                + " then in the span of " + comboboxTransaction.getSelectionModel().getSelectedItem().toString() + " days, there will be an earthquake in the province " + dataAssoc.getData2() + " with a confidence level of " + dataAssoc.getData3());
        //" with a magnitude of "+ ...
        t.setFont(Font.font(15));
        textAssociation.getChildren().add(t);
        webEngine.executeScript("association(\"" + dataAssoc.getData1() + "\", \"" + dataAssoc.getData2() + "\");");
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

    ArrayList<String[]> dataFiltering(int ys, int ye, int mmin, int mmax) throws ParseException {
        int n = dataEq.size();
        int yearStart = ys;
        int yearEnd = ye;
        int magMin = mmin;
        int magMax = mmax;
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

    private static double[][] getLocation(ArrayList<String[]> dataEq) {
        int n = dataEq.size();
        double[][] locations = new double[n][2];
        for (int i = 0; i < n; i++) {
            locations[i][0] = Double.valueOf(dataEq.get(i)[1]);//latitude
            locations[i][1] = Double.valueOf(dataEq.get(i)[2]);
        }
        return locations;
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

                //masukkan datanya
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

    public void setProxy(String host, String port) {
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxy", host); // JavaFX WebView
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);

        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxy", host); // JavaFX WebView
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
    }

    private void animationFade(double from, double to) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), loadingPane);
        fadeOut.setFromValue(from);
        fadeOut.setToValue(to);
        fadeOut.play();
    }
    @FXML
    private Button coba;

    @FXML
    private void transaksiGempa(ActionEvent event) throws ParseException {//7 hari
        ArrayList<String[]> data = dataFiltering(2000, 2004, 5, 10);
        ArrayList<ArrayList<String[]>> transaksi = new ArrayList<>();
        int k = 0;

        Date dateAwal = new Date();
        Date dateAkhir = new Date();
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(formatDate.parse(data.get(i)[0]));
                dateAwal = c.getTime();
                c.add(Calendar.DATE, 6);
                dateAkhir = c.getTime();
                transaksi.add(new ArrayList<String[]>());
                transaksi.get(k).add(data.get(i));
            } else if ((formatDate.parse(data.get(i)[0]).after(dateAwal) && (formatDate.parse(data.get(i)[0]).before(dateAkhir))) || (formatDate.parse(data.get(i)[0]).equals(dateAwal) || formatDate.parse(data.get(i)[0]).equals(dateAkhir))) {
                transaksi.get(k).add((data.get(i)));
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(dateAkhir);
                c.add(Calendar.DATE, 1);
                dateAwal = c.getTime();
                c.add(Calendar.DATE, 6);
                dateAkhir = c.getTime();
                transaksi.add(new ArrayList<String[]>());
                k++;
                transaksi.get(k).add(data.get(i));
            }
        }
        String[] search = new String[]{"Bali", "Jawa Timur"};
        // tanggal  latitude  longitude  depth  mag  id  prov
        // 0        1         2          3      4    5   6
        boolean[] sea = new boolean[]{false, false};
        ArrayList<String[]> coba = new ArrayList<>();
        for (int i = 0; i < transaksi.size(); i++) {
            for (int j = 0; j < transaksi.get(i).size(); j++) {
                if (transaksi.get(i).get(j)[6].equalsIgnoreCase(search[0])) {
                    sea[0] = true;
                    coba.add(transaksi.get(i).get(j));
                } else if (transaksi.get(i).get(j)[6].equalsIgnoreCase(search[1])) {
                    sea[1] = true;
                    coba.add(transaksi.get(i).get(j));
                }

            }
            if (sea[0] && sea[1]) {
                System.out.println("=====Transaksi " + i + " ======");
                for (int j = 0; j < coba.size(); j++) {
                    System.out.println(Arrays.toString(coba.get(j)));
                }
            }
            sea[0] = false;
            sea[1] = false;
            coba = new ArrayList<>();
            //System.out.println("===========================================");
        }
    }
}
