package org.example.model;

import java.util.HashMap;
import java.util.*;

public class Polinom {
    HashMap<Integer, Double> monom = new HashMap<Integer, Double>();
    public Boolean C = false;
    public Polinom() {
    }

    public void adaugaMonom(double ax, int nx) {
        if(ax != 0)
            monom.put(nx, ax);
    }

    double getMonom(int nx) {
        if(monom.containsKey(nx))
            return monom.get(nx);
        return 0;
    }

    int getGrad() {
        NavigableSet<Integer> invers = new TreeSet<>(Comparator.reverseOrder());
        invers.addAll(monom.keySet());
        if (monom.isEmpty()) {
            return 0;
        }
        return invers.first();
    }

    public String toString() {
        NavigableSet<Integer> invers = new TreeSet<>(Comparator.reverseOrder());
        invers.addAll(monom.keySet());
        String result = "";
        boolean prim = true;
        for (Integer i : invers) {
            String monom = "";
            if(getMonom(i) > 0 && !prim) {
                monom += "+";
            }
            prim = false;
            if(getMonom(i) == -1)
                monom += "-";
            if(getMonom(i) != 1 && getMonom(i) != -1)
                monom += String.format("%.2f",getMonom(i));
            if(getMonom(i) == 1 && i == 0)
                monom += String.format("%.2f",getMonom(i));
            if(i!=0) {
                monom += "x";
                if(i>1)
                    monom += "^" + i;
            }
            if(getMonom(i) == -1 && i == 0)
                monom += "1";
            result += monom;
        }
        if(this.C)
            result += "+C";
        if(result == "")
            result = "0";
        return result;
    }
}


