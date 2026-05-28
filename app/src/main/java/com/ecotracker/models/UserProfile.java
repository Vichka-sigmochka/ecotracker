package com.ecotracker.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProfile {
    public String name = "Эко-герой";
    public int totalCO2 = 0, totalPoints = 0, level = 0, treesCount = 0;
    public int forestsCount = 0;
    public int forestCycles = 0;
    public List<String> achievements = new ArrayList<>(Arrays.asList("🌱 Первый шаг", "🚴 Эко-активист"));

    public String getLevelName() {
        String[] levels = {"🌱 Росток", "🌿 Саженец", "🌳 Молодое дерево", "🍃 Взрослое дерево", "🌲 Лес"};
        if (level >= 0 && level < levels.length) return levels[level];
        return levels[0];
    }
}