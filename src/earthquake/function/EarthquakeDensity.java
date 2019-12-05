/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.function;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Dell
 */
public class EarthquakeDensity {

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

    public static double[][] run(ArrayList<String[]> dataEqCluster, int kcluster, String type) {
        /*
        * Type : high, medium, low
        *
         */
        int n = dataEqCluster.size();
        double[][] resultDensity = new double[kcluster][5];
        DecimalFormat df = new DecimalFormat();
        switch (type) {
            case "high":
                System.out.println("High Density");
                df = new DecimalFormat("#.##");
                for (int i = 0; i < kcluster; i++) {
                    ArrayList<ArrayList<Double>> densityData = new ArrayList<ArrayList<Double>>();
                    for (String[] d : dataEqCluster) {
                        int cluster = Integer.parseInt(d[7]);
                        if (i == cluster) {
                            //----------------get lat long -------------------------------------
                            densityData.add(new ArrayList<Double>());
                            df.setRoundingMode(RoundingMode.DOWN);//high, medium,low
                            Double la = Double.valueOf(df.format(Double.parseDouble(d[1])).replaceAll(",", "."));
                            Double lo = Double.valueOf(df.format(Double.parseDouble(d[2])).replaceAll(",", "."));
                            densityData.get(densityData.size() - 1).add(0, la); //lat
                            densityData.get(densityData.size() - 1).add(1, lo); //log
                        }
                    }
                    df.setRoundingMode(RoundingMode.HALF_UP);

                    int firstPoint = densityData.size();
                    System.out.println("jumlah point : " + firstPoint);

                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(0).compareTo(o2.get(0));
                        }
                    });

                    Double maxLat = densityData.get(densityData.size() - 1).get(0);
                    Double minLat = densityData.get(0).get(0);

                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(1).compareTo(o2.get(1));
                        }
                    });
                    Double maxLog = densityData.get(densityData.size() - 1).get(1);
                    Double minLog = densityData.get(0).get(1);
                    System.out.println("Max Lat = " + maxLat + " Min Lat = " + minLat);
                    System.out.println("Max Log = " + maxLog + " Min Log = " + minLog);
                    System.out.println();
                    //var to keep the upper and bottom limit each grid
                    Double top[] = new Double[Double.valueOf(df.format((maxLog - minLog) * 100 + 1).replaceAll(",", ".")).intValue()];
                    Double bottom[] = new Double[Double.valueOf(df.format((maxLog - minLog) * 100 + 1).replaceAll(",", ".")).intValue()];

                    boolean point1 = true, point0 = true;
                    int curpoint1count = 0, prevpoint1count = 0, iteration = 0;
                    do {
                        prevpoint1count = curpoint1count;
                        curpoint1count = 0;
                        if (point1) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.01 * j).replaceAll(",", "."))).replaceAll(",", "."));

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);

                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }
                            //end measuring
                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;
                            for (Double j = Double.valueOf(df.format(minLog + 0.01).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.01).replaceAll(",", "."))) {
                                gridCount = 0;
                                //System.out.println(j);
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else if (Objects.equals(j, densityData.get(k).get(1))) {
                                        gridCount++;
                                        indexVal = k;
                                    }
                                }
                                if (gridCount == 1) {
                                    curpoint1count++;
                                    //1 for before current grid, 2 after current grid
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    //System.out.println("index : "+tb_index+" log : "+j);

                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }
                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }
                                    Double dist1, dist2;
                                    //previous latitude value in the cluster
                                    Double prevVal = Double.valueOf(df.format(j - 0.01).replaceAll(",", "."));

                                    double x = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(1) - prevVal).replaceAll(",", ".")), 2);
                                    double y = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(0) - upper_lim1).replaceAll(",", ".")), 2);

                                    dist1 = (Math.sqrt(x) + Math.sqrt(y));

                                    x = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(1) - prevVal).replaceAll(",", ".")), 2);
                                    y = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(0) - bottom_lim1).replaceAll(",", ".")), 2);

                                    dist2 = (Math.sqrt(x) + Math.sqrt(y));
                                    if (Objects.equals(dist1, dist2)) {
                                        if (upper_lim1 == bottom_lim1) {
                                            //System.out.println("batas sama");
                                            //if the grid have the same limit, asume as the point close to the bottom limit
                                            if (upper_lim2 == null) {
                                                for (int k = tb_index + 2; k < top.length; k++) {
                                                    if (top[k] != null) {
                                                        upper_lim2 = top[k];
                                                        break;
                                                    }
                                                }
                                            }
                                            if (upper_lim1 != null && upper_lim2 != null) {
                                                double d = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                                df.setRoundingMode(RoundingMode.DOWN);
                                                d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));

                                                //System.out.println("atas kiri : "+upper_lim1+" atas kanan : "+upper_lim2);
                                                //System.out.println("rata2 : "+d);
                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    {
                                                        if (Objects.equals(j, densityData.get(k).get(1))) {
                                                            if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                                densityData.add(new ArrayList<Double>());
                                                                densityData.get(densityData.size() - 1).add(0, d); //lat
                                                                densityData.get(densityData.size() - 1).add(1, j); //log
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else //if the grid have a different upper and bottom limit, assume as point close to the upper limit
                                        {
                                            if (bottom_lim2 == null) {
                                                for (int k = tb_index + 2; k < bottom.length; k++) {
                                                    if (bottom[k] != null) {
                                                        bottom_lim2 = bottom[k];
                                                        break;
                                                    }
                                                }
                                            }
                                            if (bottom_lim1 != null && bottom_lim2 != null) {
                                                double d = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                                df.setRoundingMode(RoundingMode.DOWN);
                                                d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                                //                                    System.out.println("batas bawah kiri : "+bottom_lim1+" batas bawah kanan : "+bottom_lim2);
                                                //                                    System.out.println("rata2 : "+d);
                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                                        if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                            densityData.add(new ArrayList<Double>());
                                                            densityData.get(densityData.size() - 1).add(0, d); //lat
                                                            densityData.get(densityData.size() - 1).add(1, j); //log
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    if (dist1 < dist2) {//System.out.println("dekat dengan batas atas");
                                        if (bottom_lim2 == null) {
                                            for (int k = tb_index + 2; k < bottom.length; k++) {
                                                if (bottom[k] != null) {
                                                    bottom_lim2 = bottom[k];
                                                    break;
                                                }
                                            }
                                        }
                                        double d = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                        df.setRoundingMode(RoundingMode.DOWN);
                                        d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }
                                    }
                                    if (dist1 > dist2) {
                                        if (upper_lim2 == null) {
                                            for (int k = tb_index + 2; k < top.length; k++) {
                                                if (top[k] != null) {
                                                    upper_lim2 = top[k];
                                                    break;
                                                }
                                            }
                                        }
                                        double d = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                        df.setRoundingMode(RoundingMode.DOWN);
                                        d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }
                                    }
                                }
                                tb_index++;
                                df.setRoundingMode(RoundingMode.HALF_UP);
                            }
                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });
                        }
                        if (point0) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.01 * j).replaceAll(",", "."))).replaceAll(",", "."));

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);

                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }
                            //end measuring
                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;
                            for (Double j = Double.valueOf(df.format(minLog + 0.01).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.01).replaceAll(",", "."))) {
                                gridCount = 0;
                                //System.out.println(j);
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else //System.out.println(a.get(k).get(1));
                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                        gridCount++;
                                        indexVal = k;
                                    }
                                }
                                if (gridCount == 0) {
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }
                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (upper_lim2 == null) {
                                        for (int k = tb_index + 2; k < top.length; k++) {
                                            if (top[k] != null) {
                                                upper_lim2 = top[k];
                                                break;
                                            }
                                        }
                                    }
                                    if (bottom_lim2 == null) {
                                        for (int k = tb_index + 2; k < bottom.length; k++) {
                                            if (bottom[k] != null) {
                                                bottom_lim2 = bottom[k];
                                                break;
                                            }
                                        }
                                    }
                                    double d1 = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                    double d2 = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    d1 = Double.valueOf(df.format(d1 / 2).replaceAll(",", "."));
                                    d2 = Double.valueOf(df.format(d2 / 2).replaceAll(",", "."));

                                    densityData.add(new ArrayList<Double>());
                                    densityData.get(densityData.size() - 1).add(0, d1); //lat
                                    densityData.get(densityData.size() - 1).add(1, j); //log

                                    if (d1 != d2) {
                                        densityData.add(new ArrayList<Double>());
                                        densityData.get(densityData.size() - 1).add(0, d2); //lat
                                        densityData.get(densityData.size() - 1).add(1, j); //log
                                    }
                                    //measure the upper and bottom limit
                                    for (int l = 0; l < top.length; l++) {
                                        df.setRoundingMode(RoundingMode.HALF_UP);
                                        Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.01 * l).replaceAll(",", "."))).replaceAll(",", "."));

                                        top[l] = UpperLimit(densityData, k);
                                        bottom[l] = BottomLimit(densityData, k);

                                        //System.out.println(top[j]+"\t"+bottom[j]);
                                    }
                                    //end measuring

                                }
                                tb_index++;
                                df.setRoundingMode(RoundingMode.HALF_UP);
                            }
                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });
                        }
                        point1 = Point1CheckHigh(densityData, minLog, maxLog);
                        //System.out.println("point 1 check : "+point1);

                        point0 = Point0CheckHigh(densityData, minLog, maxLog);
                        //System.out.println("point 0 check : "+point0);
                        if (curpoint1count != 0) {
                            if (prevpoint1count == curpoint1count) {
                                iteration++;
                            }
                        }
                    } while ((point1 == true || point0 == true) && iteration < 3 && firstPoint > 1);
                    //calculate wide area
                    int wideArea = 0;
                    DecimalFormat df2 = new DecimalFormat("#");
                    for (int j = 0; j < top.length; j++) {
                        wideArea += Double.valueOf(df2.format((top[j] - bottom[j]) * 100 + 1).replaceAll(",", ".")).intValue();
                    }
                    resultDensity[i][0] = i;
                    resultDensity[i][1] = firstPoint;
                    resultDensity[i][2] = firstPoint;
                    resultDensity[i][3] = wideArea;
                    if (firstPoint == 1 && wideArea == 1) {
                        resultDensity[i][4] = 0;
                    } else {
                        resultDensity[i][4] = Double.valueOf(firstPoint) / Double.valueOf(wideArea);
                    }
                     System.out.println( resultDensity[i][4]);
                }
                break;
            case "medium":
                System.out.println("Medium Density");
                df = new DecimalFormat("#.#");
                for (int i = 0; i < kcluster; i++) {
                    ArrayList<ArrayList<Double>> densityData = new ArrayList<ArrayList<Double>>();
                    for (String[] d : dataEqCluster) {
                        int cluster = Integer.parseInt(d[7]);
                        if (i == cluster) {
                            //----------------get lat long -------------------------------------
                            densityData.add(new ArrayList<Double>());
                            df.setRoundingMode(RoundingMode.DOWN);//high, medium,low
                            Double la = Double.valueOf(df.format(Double.parseDouble(d[1])).replaceAll(",", "."));
                            Double lo = Double.valueOf(df.format(Double.parseDouble(d[2])).replaceAll(",", "."));
                            densityData.get(densityData.size() - 1).add(0, la); //lat
                            densityData.get(densityData.size() - 1).add(1, lo); //log
                        }
                    }
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    int firstPoint = densityData.size();
                    System.out.println("jumlah point : " + firstPoint);
                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(0).compareTo(o2.get(0));
                        }
                    });
                    Double maxLat = densityData.get(densityData.size() - 1).get(0);
                    Double minLat = densityData.get(0).get(0);
                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(1).compareTo(o2.get(1));
                        }
                    });
                    Double maxLog = densityData.get(densityData.size() - 1).get(1);
                    Double minLog = densityData.get(0).get(1);
                    System.out.println("Max Lat = " + maxLat + " Min Lat = " + minLat);
                    System.out.println("Max Log = " + maxLog + " Min Log = " + minLog);
                    System.out.println();

                    //var to keep the upper and bottom limit each grid
                    Double top[] = new Double[Double.valueOf(df.format((maxLog - minLog) * 10 + 1).replaceAll(",", ".")).intValue()];
                    Double bottom[] = new Double[Double.valueOf(df.format((maxLog - minLog) * 10 + 1).replaceAll(",", ".")).intValue()];
                    //grid status-------------------------------------------------
                    boolean point1 = true, point0 = true;

                    int curpoint1count = 0, prevpoint1count = 0, iteration = 0;
                    do {
                        prevpoint1count = curpoint1count;
                        curpoint1count = 0;
                        if (point1) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.1 * j).replaceAll(",", "."))).replaceAll(",", "."));

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);
                                System.out.println(top[j] + " , " + bottom[j]);
                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }
                            //end measuring

                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;

                            for (Double j = Double.valueOf(df.format(minLog + 0.1).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.1).replaceAll(",", "."))) {
                                gridCount = 0;
                                //System.out.println(j);
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else //System.out.println(a.get(k).get(1));
                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                        gridCount++;
                                        indexVal = k;
                                    }
                                }

                                if (gridCount == 1) {
                                    curpoint1count++;
                                    //1 for before current grid, 2 after current grid
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    //System.out.println("index : "+tb_index+" log : "+j);

                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    //System.out.println("atas kiri : "+upper_lim1+" bawah kiri : "+bottom_lim1);
                                    //System.out.println("atas kanan : "+upper_lim2+" bawah kanan : "+bottom_lim2);
                                    Double dist1, dist2;

                                    //previous latitude value in the cluster
                                    Double prevVal = Double.valueOf(df.format(j - 0.1).replaceAll(",", "."));

                                    double x = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(1) - prevVal).replaceAll(",", ".")), 2);
                                    double y = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(0) - upper_lim1).replaceAll(",", ".")), 2);

                                    dist1 = (Math.sqrt(x) + Math.sqrt(y));

                                    x = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(1) - prevVal).replaceAll(",", ".")), 2);
                                    y = Math.pow(Double.valueOf(df.format(densityData.get(indexVal).get(0) - bottom_lim1).replaceAll(",", ".")), 2);

                                    dist2 = (Math.sqrt(x) + Math.sqrt(y));

                                    //System.out.println("jarak atas = "+dist1+ " jarak bawah = "+dist2);
                                    //System.out.println("cur point : "+a.get(indexVal).get(0)+" batas bawah : "+bottom[tb_index-1]);
                                    if (Objects.equals(dist1, dist2)) {
                                        if (upper_lim1 == bottom_lim1) {
                                            //System.out.println("batas sama");
                                            //if the grid have the same limit, asume as the point close to the bottom limit
                                            if (upper_lim2 == null) {
                                                for (int k = tb_index + 2; k < top.length; k++) {
                                                    if (top[k] != null) {
                                                        upper_lim2 = top[k];
                                                        break;
                                                    }
                                                }
                                            }

                                            if (upper_lim1 != null && upper_lim2 != null) {
                                                double d = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                                df.setRoundingMode(RoundingMode.DOWN);
                                                d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                                        if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                            densityData.add(new ArrayList<Double>());
                                                            densityData.get(densityData.size() - 1).add(0, d); //lat
                                                            densityData.get(densityData.size() - 1).add(1, j); //log
                                                        }
                                                    }
                                                }
                                            }

                                            //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                        } else {
                                            //System.out.println("batas berbeda");
                                            //if the grid have a different upper and bottom limit, assume as point close to the upper limit
                                            if (bottom_lim2 == null) {
                                                for (int k = tb_index + 2; k < bottom.length; k++) {
                                                    if (bottom[k] != null) {
                                                        bottom_lim2 = bottom[k];
                                                        break;
                                                    }
                                                }
                                            }

                                            if (bottom_lim1 != null && bottom_lim2 != null) {
                                                double d = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                                df.setRoundingMode(RoundingMode.DOWN);
                                                d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                                //                                    System.out.println("batas bawah kiri : "+bottom_lim1+" batas bawah kanan : "+bottom_lim2);
                                                //                                    System.out.println("rata2 : "+d);
                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                                        if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                            densityData.add(new ArrayList<Double>());
                                                            densityData.get(densityData.size() - 1).add(0, d); //lat
                                                            densityData.get(densityData.size() - 1).add(1, j); //log
                                                        }
                                                    }
                                                }
                                            }

                                            //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                        }
                                    }

                                    if (dist1 < dist2) {
                                        //System.out.println("dekat dengan batas atas");

                                        if (bottom_lim2 == null) {
                                            for (int k = tb_index + 2; k < bottom.length; k++) {
                                                if (bottom[k] != null) {
                                                    bottom_lim2 = bottom[k];
                                                    break;
                                                }
                                            }
                                        }

                                        double d = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                        df.setRoundingMode(RoundingMode.DOWN);
                                        d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                        //                            System.out.println("bawah kiri : "+bottom_lim1+" kanan : "+bottom_lim2);
                                        //                            System.out.println("rata2 : "+d);

                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }

                                        //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                    }

                                    if (dist1 > dist2) {
                                        //System.out.println("dekat dengan batas bawah");

                                        if (upper_lim2 == null) {
                                            for (int k = tb_index + 2; k < top.length; k++) {
                                                if (top[k] != null) {
                                                    upper_lim2 = top[k];
                                                    break;
                                                }
                                            }
                                        }

                                        double d = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                        df.setRoundingMode(RoundingMode.DOWN);
                                        d = Double.valueOf(df.format(d / 2).replaceAll(",", "."));
                                        //                            System.out.println("atas kiri : "+upper_lim1+" kanan : "+upper_lim2);
                                        //                            System.out.println("rata2 : "+d);

                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }

                                        //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                    }
                                }
                                //System.out.println();
                                tb_index++;
                                df.setRoundingMode(RoundingMode.HALF_UP);
                            }

                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });

                            //                for(int j=0;j<densityData.size();j++)
                            //                    System.out.println(densityData.get(j));
                            //                System.out.println();
                        }
                        if (point0) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                df.setRoundingMode(RoundingMode.HALF_UP);
                                Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.1 * j).replaceAll(",", "."))).replaceAll(",", "."));

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);

                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }
                            //end measuring
                            //System.out.println();

                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;
                            for (Double j = Double.valueOf(df.format(minLog + 0.1).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.1).replaceAll(",", "."))) {

                                gridCount = 0;
                                //System.out.println(j);
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else //System.out.println(a.get(k).get(1));
                                    {
                                        if (Objects.equals(j, densityData.get(k).get(1))) {
                                            gridCount++;
                                            indexVal = k;
                                        }
                                    }
                                }

                                if (gridCount == 0) {
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    //System.out.println("index : "+tb_index+" log : "+j);

                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (upper_lim2 == null) {
                                        for (int k = tb_index + 2; k < top.length; k++) {
                                            if (top[k] != null) {
                                                upper_lim2 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim2 == null) {
                                        for (int k = tb_index + 2; k < bottom.length; k++) {
                                            if (bottom[k] != null) {
                                                bottom_lim2 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    double d1 = Double.valueOf(df.format(upper_lim1 + upper_lim2).replaceAll(",", "."));
                                    double d2 = Double.valueOf(df.format(bottom_lim1 + bottom_lim2).replaceAll(",", "."));
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    d1 = Double.valueOf(df.format(d1 / 2).replaceAll(",", "."));
                                    d2 = Double.valueOf(df.format(d2 / 2).replaceAll(",", "."));

                                    densityData.add(new ArrayList<Double>());
                                    densityData.get(densityData.size() - 1).add(0, d1); //lat
                                    densityData.get(densityData.size() - 1).add(1, j); //log

                                    if (d1 != d2) {
                                        densityData.add(new ArrayList<Double>());
                                        densityData.get(densityData.size() - 1).add(0, d2); //lat
                                        densityData.get(densityData.size() - 1).add(1, j); //log
                                    }

                                    //measure the upper and bottom limit
                                    for (int l = 0; l < top.length; l++) {
                                        df.setRoundingMode(RoundingMode.HALF_UP);
                                        Double k = Double.valueOf(df.format(minLog + Double.valueOf(df.format(0.1 * l).replaceAll(",", "."))).replaceAll(",", "."));

                                        top[l] = UpperLimit(densityData, k);
                                        bottom[l] = BottomLimit(densityData, k);
                                    }
                                    //end measuring
                                }
                                //System.out.println();
                                tb_index++;

                                df.setRoundingMode(RoundingMode.HALF_UP);
                            }

                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });
                        }
                        point1 = Point1CheckMed(densityData, minLog, maxLog);

                        point0 = Point0CheckMed(densityData, minLog, maxLog);
                        if (curpoint1count != 0) {
                            if (prevpoint1count == curpoint1count) {
                                iteration++;
                            }
                        }

                    } while ((point1 == true || point0 == true) && iteration < 3 && firstPoint > 1);
                    int wideArea = 0;
                    DecimalFormat df2 = new DecimalFormat("#");
                    for (int j = 0; j < top.length; j++) {
                        wideArea += Double.valueOf(df2.format((top[j] - bottom[j]) * 10 + 1).replaceAll(",", ".")).intValue();
                    }
                    resultDensity[i][0] = i;
                    resultDensity[i][1] = firstPoint;
                    resultDensity[i][2] = firstPoint;
                    resultDensity[i][3] = wideArea;
                    if (firstPoint == 1 && wideArea == 1) {
                        resultDensity[i][4] = 0;
                    } else {
                        resultDensity[i][4] = Double.valueOf(firstPoint) / Double.valueOf(wideArea);
                    }
                     System.out.println( resultDensity[i][4]);
                }
                break;
            case "low":
                System.out.println("Low Density");
                df = new DecimalFormat("#");
                df.setRoundingMode(RoundingMode.DOWN);
                for (int i = 0; i < kcluster; i++) {
                    ArrayList<ArrayList<Double>> densityData = new ArrayList<ArrayList<Double>>();
                    for (String[] d : dataEqCluster) {
                        int cluster = Integer.parseInt(d[7]);
                        if (i == cluster) {
                            //----------------get lat long -------------------------------------
                            densityData.add(new ArrayList<Double>());
                            df.setRoundingMode(RoundingMode.DOWN);//high, medium,low
                            Double la = Double.valueOf(df.format(Double.parseDouble(d[1])).replaceAll(",", "."));
                            Double lo = Double.valueOf(df.format(Double.parseDouble(d[2])).replaceAll(",", "."));
                            densityData.get(densityData.size() - 1).add(0, la); //lat
                            densityData.get(densityData.size() - 1).add(1, lo); //log
                        }
                    }
                    int firstPoint = densityData.size();
                    densityData = CheckMultipleData(densityData);
                    int firstPoint2 = densityData.size();
                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(0).compareTo(o2.get(0));
                        }
                    });

                    Double maxLat = Double.valueOf(df.format(densityData.get(densityData.size() - 1).get(0)));
                    Double minLat = Double.valueOf(df.format(densityData.get(0).get(0)));

                    Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                        @Override
                        public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                            return o1.get(1).compareTo(o2.get(1));
                        }
                    });

                    Double maxLog = Double.valueOf(df.format(densityData.get(densityData.size() - 1).get(1)));
                    Double minLog = Double.valueOf(df.format(densityData.get(0).get(1)));
                    System.out.println("Max Lat = " + maxLat + " Min Lat = " + minLat);
                    System.out.println("Max Log = " + maxLog + " Min Log = " + minLog);
                    System.out.println();

                    //var to keep the upper and bottom limit each grid
                    Double top[] = new Double[(int) (maxLog - minLog) + 1];
                    Double bottom[] = new Double[(int) (maxLog - minLog) + 1];
                    //grid status
                    boolean point1 = true, point0 = true;

                    int curpoint1count = 0, prevpoint1count = 0, iteration = 0;
                    do {
                        prevpoint1count = curpoint1count;
                        curpoint1count = 0;

                        if (point1) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                Double k = minLog + j;

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);

                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }
                            //end measuring
                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;

                            for (Double j = minLog + 1; j < maxLog; j++) {
                                gridCount = 0;
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else //System.out.println(a.get(k).get(1));
                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                        gridCount++;
                                        indexVal = k;
                                    }
                                }

                                if (gridCount == 1) {
                                    curpoint1count++;
                                    //1 for before current grid, 2 after current grid
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    //System.out.println("index : "+tb_index+" log : "+j);

                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    //System.out.println("atas kiri : "+upper_lim1+" bawah kiri : "+bottom_lim1);
                                    //System.out.println("atas kanan : "+upper_lim2+" bawah kanan : "+bottom_lim2);
                                    Double dist1, dist2;

                                    //previous latitude value in the cluster
                                    Double prevVal = j - 1;

                                    double x = Math.pow(densityData.get(indexVal).get(1) - prevVal, 2);
                                    double y = Math.pow(densityData.get(indexVal).get(0) - upper_lim1, 2);

                                    dist1 = (Math.sqrt(x) + Math.sqrt(y));

                                    x = Math.pow(densityData.get(indexVal).get(1) - prevVal, 2);
                                    y = Math.pow(densityData.get(indexVal).get(0) - bottom_lim1, 2);

                                    dist2 = (Math.sqrt(x) + Math.sqrt(y));

                                    //System.out.println("jarak atas = "+dist1+ " jarak bawah = "+dist2);
                                    //System.out.println("cur point : "+a.get(indexVal).get(0)+" batas bawah : "+bottom[tb_index-1]);
                                    if (Objects.equals(dist1, dist2)) {
                                        if (upper_lim1 == bottom_lim1) {
                                            //System.out.println("batas sama");
                                            //if the grid have the same limit, asume as the point close to the bottom limit
                                            if (upper_lim2 == null) {
                                                for (int k = tb_index + 2; k < top.length; k++) {
                                                    if (top[k] != null) {
                                                        upper_lim2 = top[k];
                                                        break;
                                                    }
                                                }
                                            }

                                            if (upper_lim1 != null && upper_lim2 != null) {
                                                double d = (upper_lim1 + upper_lim2) / 2;
                                                d = Double.valueOf(df.format(d));
                                                //System.out.println("atas kiri : "+upper_lim1+" atas kanan : "+upper_lim2);
                                                //System.out.println("rata2 : "+d);

                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                                        if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                            densityData.add(new ArrayList<Double>());
                                                            densityData.get(densityData.size() - 1).add(0, d); //lat
                                                            densityData.get(densityData.size() - 1).add(1, j); //log
                                                        }
                                                    }
                                                }
                                            }

                                            //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                        } else {
                                            //System.out.println("batas berbeda");
                                            //if the grid have a different upper and bottom limit, assume as point close to the upper limit
                                            if (bottom_lim2 == null) {
                                                for (int k = tb_index + 2; k < bottom.length; k++) {
                                                    if (bottom[k] != null) {
                                                        bottom_lim2 = bottom[k];
                                                        break;
                                                    }
                                                }
                                            }

                                            if (bottom_lim1 != null && bottom_lim2 != null) {
                                                double d = (bottom_lim1 + bottom_lim2) / 2;
                                                d = Double.valueOf(df.format(d));
                                                //                                    System.out.println("batas bawah kiri : "+bottom_lim1+" batas bawah kanan : "+bottom_lim2);
                                                //                                    System.out.println("rata2 : "+d);
                                                for (int k = 0; k < densityData.size(); k++) {
                                                    if (densityData.get(k).get(1) == null) {
                                                        continue;
                                                    } else //System.out.println(a.get(k).get(1));
                                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                                        if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                            densityData.add(new ArrayList<Double>());
                                                            densityData.get(densityData.size() - 1).add(0, d); //lat
                                                            densityData.get(densityData.size() - 1).add(1, j); //log
                                                        }
                                                    }
                                                }
                                            }

                                            //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                        }
                                    }

                                    if (dist1 < dist2) {
                                        //System.out.println("dekat dengan batas atas");

                                        if (bottom_lim2 == null) {
                                            for (int k = tb_index + 2; k < bottom.length; k++) {
                                                if (bottom[k] != null) {
                                                    bottom_lim2 = bottom[k];
                                                    break;
                                                }
                                            }
                                        }

                                        double d = (bottom_lim1 + bottom_lim2) / 2;
                                        d = Double.valueOf(df.format(d));
                                        //                            System.out.println("bawah kiri : "+bottom_lim1+" kanan : "+bottom_lim2);
                                        //                            System.out.println("rata2 : "+d);

                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }

                                        //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                    }

                                    if (dist1 > dist2) {
                                        //System.out.println("dekat dengan batas bawah");

                                        if (upper_lim2 == null) {
                                            for (int k = tb_index + 2; k < top.length; k++) {
                                                if (top[k] != null) {
                                                    upper_lim2 = top[k];
                                                    break;
                                                }
                                            }
                                        }

                                        double d = (upper_lim1 + upper_lim2) / 2;
                                        d = Double.valueOf(df.format(d));
                                        //                            System.out.println("atas kiri : "+upper_lim1+" kanan : "+upper_lim2);
                                        //                            System.out.println("rata2 : "+d);

                                        for (int k = 0; k < densityData.size(); k++) {
                                            if (densityData.get(k).get(1) == null) {
                                                continue;
                                            } else //System.out.println(a.get(k).get(1));
                                            if (Objects.equals(j, densityData.get(k).get(1))) {
                                                if (!Objects.equals(d, densityData.get(k).get(0))) {
                                                    densityData.add(new ArrayList<Double>());
                                                    densityData.get(densityData.size() - 1).add(0, d); //lat
                                                    densityData.get(densityData.size() - 1).add(1, j); //log
                                                }
                                            }
                                        }
                                        //System.out.println("titik baru pada lat : "+a.get(a.size()-1).get(0));
                                    }
                                }
                                //System.out.println();
                                tb_index++;
                            }
                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });
                        }

                        if (point0) {
                            //measure the upper and bottom limit
                            for (int j = 0; j < top.length; j++) {
                                Double k = minLog + j;

                                top[j] = UpperLimit(densityData, k);
                                bottom[j] = BottomLimit(densityData, k);

                                //System.out.println(top[j]+"\t"+bottom[j]);
                            }

                            //System.out.println();
                            int gridCount = 0;
                            int tb_index = 1;
                            int indexVal = 0;

                            for (Double j = minLog + 1; j < maxLog; j++) {
                                gridCount = 0;
                                //System.out.println(j);
                                for (int k = 0; k < densityData.size(); k++) {
                                    if (densityData.get(k).get(1) == null) {
                                        continue;
                                    } else //System.out.println(a.get(k).get(1));
                                    if (Objects.equals(j, densityData.get(k).get(1))) {
                                        gridCount++;
                                        indexVal = k;
                                    }
                                }

                                if (gridCount == 0) {
                                    Double upper_lim1, upper_lim2, bottom_lim1, bottom_lim2;
                                    //System.out.println("index : "+tb_index+" log : "+j);

                                    upper_lim1 = top[tb_index - 1];
                                    upper_lim2 = top[tb_index + 1];
                                    bottom_lim1 = bottom[tb_index - 1];
                                    bottom_lim2 = bottom[tb_index + 1];

                                    if (upper_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (top[k] != null) {
                                                upper_lim1 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim1 == null) {
                                        for (int k = tb_index - 2; k >= 0; k--) {
                                            if (bottom[k] != null) {
                                                bottom_lim1 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (upper_lim2 == null) {
                                        for (int k = tb_index + 2; k < top.length; k++) {
                                            if (top[k] != null) {
                                                upper_lim2 = top[k];
                                                break;
                                            }
                                        }
                                    }

                                    if (bottom_lim2 == null) {
                                        for (int k = tb_index + 2; k < bottom.length; k++) {
                                            if (bottom[k] != null) {
                                                bottom_lim2 = bottom[k];
                                                break;
                                            }
                                        }
                                    }

                                    double d1 = upper_lim1 + upper_lim2;
                                    double d2 = bottom_lim1 + bottom_lim2;
                                    d1 = Double.valueOf(df.format(d1 / 2));
                                    d2 = Double.valueOf(df.format(d2 / 2));

                                    densityData.add(new ArrayList<Double>());
                                    densityData.get(densityData.size() - 1).add(0, d1); //lat
                                    densityData.get(densityData.size() - 1).add(1, j); //log

                                    if (d1 != d2) {
                                        densityData.add(new ArrayList<Double>());
                                        densityData.get(densityData.size() - 1).add(0, d2); //lat
                                        densityData.get(densityData.size() - 1).add(1, j); //log
                                    }

                                    //measure the upper and bottom limit
                                    for (int l = 0; l < top.length; l++) {
                                        Double k = minLog + l;

                                        top[l] = UpperLimit(densityData, k);
                                        bottom[l] = BottomLimit(densityData, k);

                                        //System.out.println(top[l]+"\t"+bottom[l]);
                                    }
                                    //end measuring
                                    //                        System.out.println();
                                    //
                                    //                        System.out.println("atas kiri : "+upper_lim1+" bawah kiri : "+bottom_lim1);
                                    //                        System.out.println("atas kanan : "+upper_lim2+" bawah kanan : "+bottom_lim2);
                                    //                        System.out.println("titik atas : "+d1);
                                    //                        System.out.println("titik bawah : "+d2);
                                }
                                //System.out.println();
                                tb_index++;
                            }
                            Collections.sort(densityData, new Comparator<ArrayList<Double>>() {
                                @Override
                                public int compare(ArrayList<Double> o1, ArrayList<Double> o2) {
                                    return o1.get(1).compareTo(o2.get(1));
                                }
                            });

                        }

                        point1 = Point1CheckLow(densityData, minLog, maxLog);

                        point0 = Point0CheckLow(densityData, minLog, maxLog);
                        if (curpoint1count != 0) {
                            if (prevpoint1count == curpoint1count) {
                                iteration++;
                            }
                        }
                    } while ((point1 == true || point0 == true) && iteration < 3 && firstPoint > 1);
                    int wideArea = 0;
                    for (int j = 0; j < top.length; j++) {
                        wideArea += (top[j] - bottom[j]) + 1;
                    }
                    resultDensity[i][0] = i;
                    resultDensity[i][1] = firstPoint;
                    resultDensity[i][2] = firstPoint2;
                    resultDensity[i][3] = wideArea;
                    if (firstPoint == 1 && wideArea == 1) {
                        resultDensity[i][4] = 0;
                    } else {
                        resultDensity[i][4] = Double.valueOf(firstPoint2) / Double.valueOf(wideArea);
                    }
                     System.out.println( resultDensity[i][4]);
                }
                break;
            default:
                System.out.println("No such as type : high, medium or low");
                break;
        }

        //------------------------------------------------------------------
        return resultDensity;
    }

    private static boolean Point1CheckHigh(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point1 = false;
        DecimalFormat df = new DecimalFormat("#.##");

        for (Double j = Double.valueOf(df.format(minLog + 0.01).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.01).replaceAll(",", "."))) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 1) {
                //System.out.println(j);
                point1 = true;
            }
        }

        return point1;
    }

    private static boolean Point0CheckHigh(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point0 = false;
        DecimalFormat df = new DecimalFormat("#.##");

        for (Double j = Double.valueOf(df.format(minLog + 0.01).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.01).replaceAll(",", "."))) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 0) {
                point0 = true;
            }
        }

        return point0;
    }

    private static boolean Point1CheckMed(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point1 = false;
        DecimalFormat df = new DecimalFormat("#.#");

        for (Double j = Double.valueOf(df.format(minLog + 0.1).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.1).replaceAll(",", "."))) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 1) {
                //System.out.println(j);
                point1 = true;
            }
        }

        return point1;
    }

    private static boolean Point0CheckMed(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point0 = false;
        DecimalFormat df = new DecimalFormat("#.#");

        for (Double j = Double.valueOf(df.format(minLog + 0.1).replaceAll(",", ".")); j < maxLog; j = Double.valueOf(df.format(j + 0.1).replaceAll(",", "."))) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 0) {
                point0 = true;
            }
        }

        return point0;
    }

    private static boolean Point1CheckLow(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point1 = false;
        //DecimalFormat df = new DecimalFormat("#.#");

        for (Double j = minLog + 1; j < maxLog; j++) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 1) {
                //System.out.println(j);
                point1 = true;
            }
        }

        return point1;
    }

    private static boolean Point0CheckLow(ArrayList<ArrayList<Double>> a, Double minLog, Double maxLog) {
        int gridCount = 0;
        boolean point0 = false;
        //DecimalFormat df = new DecimalFormat("#.#");

        for (Double j = minLog + 1; j < maxLog; j++) {
            gridCount = 0;
            for (int k = 0; k < a.size(); k++) {
                if (a.get(k).get(1) == null) {
                    continue;
                } else if (Objects.equals(j, a.get(k).get(1))) {
                    gridCount++;
                }
            }
            //System.out.println(gridCount);
            if (gridCount == 0) {
                point0 = true;
            }
        }

        return point0;
    }

    private static Double UpperLimit(ArrayList<ArrayList<Double>> a, Double y) {
        Double top = null;
        for (int k = 0; k < a.size(); k++) {
            if (Objects.equals(a.get(k).get(1), y)) {
                if (top == null) {
                    top = a.get(k).get(0);
                } else if (a.get(k).get(0) > top) {
                    top = a.get(k).get(0);
                }
            }
        }

        return top;
    }

    private static Double BottomLimit(ArrayList<ArrayList<Double>> a, Double y) {
        Double bottom = null;
        for (int k = 0; k < a.size(); k++) {
            if (Objects.equals(a.get(k).get(1), y)) {
                if (bottom == null) {
                    bottom = a.get(k).get(0);
                } else if (a.get(k).get(0) < bottom) {
                    bottom = a.get(k).get(0);
                }
            }
        }

        return bottom;
    }

    private static ArrayList<ArrayList<Double>> CheckMultipleData(ArrayList<ArrayList<Double>> a) {
        for (int j = 0; j < a.size(); j++) {
            if (Objects.equals(a.get(j).get(0), -0.0)) {
                a.get(j).set(0, 0.0);
            }

            for (int k = 0; k < a.size(); k++) {
                if (j == k) {
                    continue;
                }
                if (j == a.size() || j > a.size()) {
                    break;
                }
                if (Objects.equals(a.get(j), a.get(k))) {
                    a.remove(k);
                }
            }
        }

        return a;
    }
}
