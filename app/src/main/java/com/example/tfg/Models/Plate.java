package com.example.tfg.Models;

public class Plate implements Comparable<Plate>{
    String matricula;
    Incidence incidence;
    int count;

    public Plate(Incidence e) {
        matricula = e.getMatricula();
        count = 1;
        incidence=e;
    }

    public Plate() {
    }

    public String getMatricula() {
        return matricula;
    }

    public Incidence getIncidence() {
        return incidence;
    }

    public int getCount() {
        return count;
    }

    public void setIncidence(Incidence incidence) {
        if(incidence.compareTo(this.incidence)==-1) this.incidence=incidence;
    }

    public void setCount() {
        this.count = count+1;
    }


    @Override
    public int compareTo(Plate o) {
        if(this.count> o.count)return -1;
        if(this.count< o.count)return 1;

        else return this.incidence.compareTo(o.getIncidence());    }
}
