/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.function;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Dell
 */
public class EarthquakePrediction {

    static final DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    static double e = 2.71828182846;

    public static double[][][] run(ArrayList<String[]> dataEqCluster, int k, int yearStart, int yearEnd) {

        int[] nDataEachCluster = new int[k];
        double[] nMagEachCluster = new double[k];
        double[] mAVE = new double[k];
        double[] mMIN = new double[k];
        double[] a = new double[k];
        double[] b = new double[k];

        for (int i = 0; i < k; i++) {
            mMIN[i] = Double.MAX_VALUE;
        }
        int NM[][] = new int[k][6];
        double[] magnitude = new double[]{6.0, 7.0, 8.0, 9.0, 10.0};
        int m = magnitude.length;
        double[][][] resultPrediction = new double[k][m][3];
        int n = dataEqCluster.size();
        for (int i = 0; i < k; i++) {
            for (String[] d : dataEqCluster) {
                int cluster = Integer.parseInt(d[7]);
                if (i == cluster) {
                    double mag = Double.parseDouble(d[4]);
                    nDataEachCluster[i]++;//total data each cluster
                    nMagEachCluster[i] += mag;//total magnitude each cluster
                    if (mMIN[i] > mag) {
                        mMIN[i] = mag;
                    }
                    //total data each cluster based on magnitude
                    if (mag > 5 && mag < 6) {
                        NM[i][0]++;
                    } else if (mag > 6 && mag < 7) {
                        NM[i][1]++;
                    } else if (mag > 7 && mag < 8) {
                        NM[i][2]++;
                    } else if (mag > 8 && mag < 9) {
                        NM[i][3]++;
                    } else if (mag > 9 && mag < 10) {
                        NM[i][4]++;
                    } else {
                        NM[i][5]++;
                    }
                }
            }
            mAVE[i] = nMagEachCluster[i] / nDataEachCluster[i];
            if (mAVE[i] == mMIN[i]) {
                b[i] = 0;
                a[i] = 0;
            } else {
                b[i] = (1 / (mAVE[i] - mMIN[i])) * Math.log10(e);
                a[i] = Math.log10(nDataEachCluster[i]) + (Math.log10(b[i] * (1 / (Math.log10(e))))) + (mMIN[i] * b[i]);
            }
            System.out.println("Mave = " + mAVE[i]);
            System.out.println("Mmin = " + mMIN[i]);
        }
        //------------

        double[] N1 = new double[m];
        double[] a1 = new double[k];
        double[] a2 = new double[k];
        double[] a3 = new double[k];
        double[] b1 = new double[k];
        double[][] seismisitas = new double[k][m];
        int Time = (yearEnd - yearStart) + 1;
        for (int i = 0; i < k; ++i) {
            a1[i] = a[i];
            b1[i] = b[i];
            double LogN = Math.log10(b1[i] * Math.log(10.0));
            a2[i] = a1[i] - LogN;
            a3[i] = a2[i] - Math.log10(Time);
            for (int j = 0; j < m; ++j) {
                double pangkat = a3[i] - magnitude[j] * b1[i];
                N1[j] = Math.pow(10.0, pangkat);
                seismisitas[i][j] = N1[j];

            }
        }
        int M = seismisitas[0].length;
        double[][] period = new double[k][M];
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < M; ++j) {
                period[i][j] = Math.ceil(1.0 / seismisitas[i][j]);
            }
        }
        //---------------------
        int[] iterTahun = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1500};
        int t = iterTahun.length;
        double[][][] probabilitas = new double[m][k][t];
        double[][][] pTemp = new double[k][m][t];
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int l = 0; l < t; ++l) {
                    pTemp[i][j][l] = 1.0 - Math.pow(e, -seismisitas[i][j] * (double) iterTahun[l]);
                }
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                for (int l = 0; l < t; l++) {
                    double temp = pTemp[j][i][l] * 100.0;
                    probabilitas[i][j][l] = Math.round(temp);
                }
            }
        }
        //------------------------------
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + i + " : Nilai a = " + a[i] + ", Nilai b = " + b[i]);
            for (int j = 0; j < m; j++) {
                System.out.println("Tr dengan Magnitude " + magnitude[j] + " adalah " + period[i][j]);
                resultPrediction[i][j][0] = magnitude[j];
                resultPrediction[i][j][1] = period[i][j];
                resultPrediction[i][j][2] = probabilitas[j][i][0];
            }
        }
        boolean available = false;
        int indeks;
        for (indeks = 0; indeks < iterTahun.length; indeks++) {
            if (Time == iterTahun[indeks]) {
                available = true;
                break;
            }
        }
        if (!available) {
            for (indeks = 0; indeks < iterTahun.length; indeks++) {
                if (Time < iterTahun[indeks]) {
                    break;
                }
            }
        }
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + i + " : ");
            for (int j = 0; j < m; j++) {
                System.out.println(Arrays.toString(resultPrediction[i][j]));
            }
        }
        return resultPrediction;
    }
}
