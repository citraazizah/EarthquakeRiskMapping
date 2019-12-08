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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private Spinner spinnerSupport;
    @FXML
    private Spinner spinnerConfidence;
    @FXML
    private Spinner spinnerLift;
    @FXML
    private ComboBox  comboboxTransaction;

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
        
        //SET SPINNER PARAMETER ASSOCIATION
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.1, 0.1);
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.1, 0.1);
        
        spinnerSupport.setValueFactory(valueFactory);
        spinnerConfidence.setValueFactory(valueFactory1);
        
        SpinnerValueFactory valueFactorydb = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1, 1);
        spinnerLift.setValueFactory(valueFactorydb);
        
        //SET TRANSACTION
        for(int i = 1;i<=7;i++){
            comboboxTransaction.getItems().add(i);
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
        
     ArrayList<String[]> dataFiltering(int ys, int ye, int mmin, int mmax ) throws ParseException{
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
     
    @FXML
    void transaksiGempa(ActionEvent event) throws  ParseException{
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
        }
    }
    
    @FXML
    void associateButtonAction(ActionEvent event) throws ParseException, IOException {
        ArrayList<String[]> dataEqFilter = dataFiltering();
        associationFunction(dataEqFilter);
    }

    private void associationFunction(ArrayList<String[]> data) throws IOException, ParseException{
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
            //setting name
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

    private void tableAntecendentFunction() {
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

    private void tableAssociationFunction() {
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
}
