/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.function;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dell
 */
public class EarthquakePDF {

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
    public static  double[][] runPDFCluster(ArrayList<String[]> dataEqCluster, int k) {
        int n = dataEqCluster.size();
        double[][] spdfc = new double[k][2];
        int[] nDataCluster = new int[k];

        for (String[] d : dataEqCluster) {
            int cluster = Integer.parseInt(d[7]);
            nDataCluster[cluster]++;
        }
        for (int i = 0; i < k; i++) {
            double pdfc = Double.valueOf(nDataCluster[i]) / Double.valueOf(n);
            spdfc[i][0] = Double.valueOf(i);
            spdfc[i][1] = pdfc;
        }
        return spdfc;
    }

    public static  double[][] runPDFArea(ArrayList<String[]> dataEqFilter) {
        int n = dataEqFilter.size();
        int nProv = provinceName.length;
        DecimalFormat df = new DecimalFormat("#.#");
        double[][] spdfp = new double[nProv][2];
        int nDataProv[] = new int[nProv];
        for (String[] d : dataEqFilter) {
            int prov = Integer.parseInt(df.format(Double.valueOf(d[5]))) - 1;
            nDataProv[prov]++;
        }
        //System.out.println(Arrays.toString(nDataProv));
        for (int i = 0; i < nProv; i++) {
            double pdfp = Double.valueOf(nDataProv[i]) / Double.valueOf(n);
            spdfp[i][0] = Double.valueOf(i + 1);
            spdfp[i][1] = pdfp;
        }

        return spdfp;
    }

    public static  double[][][] runPDFAreaYearly(ArrayList<String[]> dataEqFilter, int yearStart, int yearEnd) throws ParseException {
        DecimalFormat df = new DecimalFormat("#.#");
        int year = (yearEnd - yearStart) + 1;
        int nProv = provinceName.length;
        int n = dataEqFilter.size();
        double[][][] spdfp = new double[year][nProv][2];
        int nDataProv[][] = new int[year][nProv];
        int nDataYearly[] = new int[year];
        int yNow = 0, yTemp = yearStart;
        for (int i = 0; i < year; i++) {
            for (String[] d : dataEqFilter) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(formatDate.parse(d[0]));
                    yNow = calendar.get(Calendar.YEAR);
                if (yNow == yTemp) {
                    nDataYearly[i]++;
                    int prov = Integer.parseInt(df.format(Double.valueOf(d[5]))) - 1;
                    nDataProv[i][prov]++;
                }
            }
            yTemp++;
        }
        for (int i = 0; i < year; i++) {
            for (int j = 0; j < nProv; j++) {
                double pdfp = Double.valueOf(nDataProv[i][j]) / Double.valueOf(nDataYearly[i]);
                spdfp[i][j][0] = Double.valueOf(j + 1);
                spdfp[i][j][1] = pdfp;
            }
        }
//        for (int i = 0; i < year; i++) {
//            System.out.println((yearStart + i));
//            for (int j = 0; j < nProv; j++) {
//                System.out.println(Arrays.toString(spdfp[i][j]));
//            }
//        }
        return spdfp;
    }

}
