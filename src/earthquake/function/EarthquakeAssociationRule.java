/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.function;

import association_rule_apriori.AlgoAgrawalFaster94;
import association_rule_apriori.AlgoApriori;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import association_rule_apriori.AssocRules;
import association_rule_apriori.Itemsets;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dell
 */
public class EarthquakeAssociationRule {

    static final DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    static double e = 2.71828182846;
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

    public static AssocRules run(ArrayList<String[]> dataEqFilter, double minsup, double minconf, double minLift, int pilTransaksi) throws IOException, ParseException {
        AssocRules rules = null;
        int databaseSize = 0;
        Itemsets result = null;
        // String input = null;
        String output = null;
        AlgoApriori apriori = new AlgoApriori();
        AlgoAgrawalFaster94 algoAgrawal = new AlgoAgrawalFaster94();
        List<List<Integer>> inputData = new ArrayList<List<Integer>>();
        //=============================================================================================== 
        //STEP 1 : Get Frequent Itemset
        inputData = createArrayListDataAssociationRule(dataEqFilter, pilTransaksi);
        result = apriori.runAlgorithm(minsup, inputData, output);
        //apriori.printStats();
        databaseSize = apriori.getDatabaseSize();
       // result.printItemsets(databaseSize);
        //===============================================================================================        
        // STEP 2: Generating all rules from the set of frequent itemsets (based on Agrawal & Srikant, 94)
        rules = algoAgrawal.runAlgorithm(result, null, databaseSize, minconf, minLift);//tambahin lift buk, ini D4 belum ada ya
        //rules.printRules(databaseSize);
        rules.sortByConfidence();
        return rules;
    }

    private static List<List<Integer>> createArrayListDataAssociationRule(ArrayList<String[]> dataEqFilter, int pilTransaksi) throws ParseException {

        // System.out.println("==============================================================");
        List<List<Integer>> gempaTemps = new ArrayList<List<Integer>>();
        
        Date dateAwal = null;
        Date dateAkhir = null;
        int k = 0;
        //DATE LAT LONG DEPTH MAG IDPROV PROV
        //0    1   2    3     4   5      6
        //ambil tipe transaksinya apa pke switch case aja
        switch (pilTransaksi) {
            case 1:
                Date dateTemp = new Date();
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    Date dateNow  = formatDate.parse(dataEqFilter.get(i)[0]);
                    if (i == 0) {
                        dateTemp = dateNow;
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if (dateTemp.equals(dateNow)) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        dateTemp = dateNow;
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 2:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 1);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 1);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 3:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 2);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 2);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 4:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 3);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 3);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 5:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 4);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 4);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 6:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 5);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 5);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            case 7:
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 6);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAwal) || formatDate.parse(dataEqFilter.get(i)[0]).equals(dateAkhir))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.DATE, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.DATE, 6);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
                break;
            default:
                SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(fd.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.HOUR, 1);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((fd.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (fd.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (dataEqFilter.get(i)[0].equals(fd.format(dateAwal)) || dataEqFilter.get(i)[0].equals(fd.format(dateAkhir)))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        //c.add(Calendar.HOUR, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.HOUR, 1);
                        dateAkhir = c.getTime();
                        //System.out.println(dateAwal.toString() +" "+ dateAkhir.toString());
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
               // System.out.println("perhari");
                break;
        }
        //------------------------------------------------------------------------------------------
       /* String tipe = "hari";
        if (tipe.equalsIgnoreCase("hari")) {
            for (int i = 0; i < dataEqFilter.size(); i++) {
                    if (i == 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatDate.parse(dataEqFilter.get(i)[0]));
                        dateAwal = c.getTime();
                        c.add(Calendar.HOUR, 1);  // number of days to add
                        dateAkhir = c.getTime();  // dt is now the new date
                        gempaTemps.add(new ArrayList<Integer>());
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else if ((formatDate.parse(dataEqFilter.get(i)[0]).after(dateAwal) && (formatDate.parse(dataEqFilter.get(i)[0]).before(dateAkhir))) || (dataEqFilter.get(i)[0].equals(formatDate.format(dateAwal)) || dataEqFilter.get(i)[0].equals(formatDate.format(dateAkhir)))) {
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    } else {
                        Collections.sort(gempaTemps.get(k));
                        gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(dateAkhir);
                        c.add(Calendar.HOUR, 1);
                        dateAwal = c.getTime();
                        c.add(Calendar.HOUR, 1);
                        dateAkhir = c.getTime();
                        gempaTemps.add(new ArrayList<Integer>());
                        k++;
                        gempaTemps.get(k).add((int) Double.parseDouble(dataEqFilter.get(i)[5]));
                    }
                }
        }
        else if (tipeTransaksi.equalsIgnoreCase("bulan")) {
            for (int i = 0; i < dataEqFilter.size(); i++) {
                if (i == 0) {
                    dateTemp = dataEqFilter.get(i)[0].substring(0, 7);//ubah bulan
                    gempaTemps.add(new ArrayList<Integer>());
                    gempaTemps.get(k).add((int)Double.parseDouble(dataEqFilter.get(i)[5]));
                } else if (dateTemp.equals(dataEqFilter.get(i)[0].substring(0, 7))) {//ubah bulan
                    gempaTemps.get(k).add((int)Double.parseDouble(dataEqFilter.get(i)[5]));
                } else {
                    Collections.sort(gempaTemps.get(k));
                    gempaTemps.set(k, gempaTemps.get(k).stream().distinct().collect(Collectors.toList()));
                    dateTemp = dataEqFilter.get(i)[0].substring(0, 7);//ubah bulan
                    gempaTemps.add(new ArrayList<Integer>());
                    k++;
                    gempaTemps.get(k).add((int)Double.parseDouble(dataEqFilter.get(i)[5]));
                }
            }
        }*/
//                for(int i = 0;i<gempaTemps.size();i++){
//                    System.out.println(gempaTemps.get(i).toString());
//                    
//                }
        return gempaTemps;

    }
}
