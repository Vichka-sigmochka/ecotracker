package com.ecotracker.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EcoAction {
    public String category, name, date;
    public double quantity, co2Saved;
    public int points;
    public String unit;

    public EcoAction(String category, String name, double quantity, String unit, double co2Saved, int points) {
        this.category = category;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.co2Saved = co2Saved;
        this.points = points;
        this.date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
    }
}