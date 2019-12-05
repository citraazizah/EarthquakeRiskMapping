/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package earthquake.association.rule;

import java.util.Objects;
//import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Dell
 */
public class DataColumns {

    private SimpleStringProperty data1 = null;
    private SimpleStringProperty data2 = null;
    private SimpleStringProperty data3 = null;
    private SimpleStringProperty data4 = null;
    private SimpleStringProperty data5 = null;

    public DataColumns(String data1, String data2) {
        this.data1 = new SimpleStringProperty(data1);
        this.data2 = new SimpleStringProperty(data2);
    }

    public DataColumns(String data1, String data2, String data3) {
        this.data1 = new SimpleStringProperty(data1);
        this.data2 = new SimpleStringProperty(data2);
        this.data3 = new SimpleStringProperty(data3);
    }

    public DataColumns(String data1, String data2, String data3, String data4, String data5) {
        this.data1 = new SimpleStringProperty(data1);
        this.data2 = new SimpleStringProperty(data2);
        this.data3 = new SimpleStringProperty(data3);
        this.data4 = new SimpleStringProperty(data4);
        this.data5 = new SimpleStringProperty(data5);

    }

    public String getData1() {
        return data1.get();
    }

    public String getData2() {
        return data2.get();
    }

    public String getData3() {
        return data3.get();
    }

    public String getData4() {
        return data4.get();
    }

    public String getData5() {
        return data5.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DataColumns)) {
            return false;
        }
        DataColumns c = (DataColumns) o;

        // persons.id.equals() leads to the default implementation in Object
        // --> instead use this one.
        // The Property classes have their own isEqualTo method
        // with get(), you will get your simple boolean from the returned BooleanBinding
        return c.data1.isEqualTo(this.data1).get()
                && c.data2.isEqualTo(data2).get();
    }

}
