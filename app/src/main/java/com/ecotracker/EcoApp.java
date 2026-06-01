package com.ecotracker;

import android.app.Application;

import com.ecotracker.models.EcoAction;
import com.ecotracker.models.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class EcoApp extends Application {
    private static EcoApp instance;
    private DatabaseHelper dbHelper;
    private List<EcoAction> actions;
    private UserProfile profile;
    private String currentUserEmail = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new DatabaseHelper(this);
        actions = new ArrayList<>();
        profile = new UserProfile();
    }

    public static EcoApp getInstance() {
        return instance;
    }

    public void setCurrentUser(String email) {
        this.currentUserEmail = email;
        loadData();
    }

    private void loadData() {
        if (!currentUserEmail.isEmpty()) {
            profile = dbHelper.getProfile(currentUserEmail);
            actions = dbHelper.getActions(currentUserEmail);
        } else {
            actions = new ArrayList<>();
            profile = new UserProfile();
        }
    }

    public void saveData() {
        if (!currentUserEmail.isEmpty()) {
            dbHelper.updateProfile(currentUserEmail, profile);
        }
    }

    public void addAction(EcoAction action) {
        actions.add(0, action);
        profile.totalCO2 += action.co2Saved;
        profile.totalPoints += action.points;
        updateLevelAndTrees();
        dbHelper.addAction(currentUserEmail, action);
        dbHelper.updateProfile(currentUserEmail, profile);
    }

    public List<EcoAction> getActions() {
        if (actions == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(actions);
    }

    public UserProfile getProfile() {
        if (profile == null) {
            profile = new UserProfile();
        }
        return profile;
    }

    private void updateLevelAndTrees() {
        int points = profile.totalPoints;
        int cyclePoints = points % 1000;
        if (cyclePoints < 100) {
            profile.level = 0;
        } else if (cyclePoints < 300) {
            profile.level = 1;
        } else if (cyclePoints < 600) {
            profile.level = 2;
        } else if (cyclePoints < 1000) {
            profile.level = 3;
        } else {
            profile.level = 4;
        }
        if (profile.level == 4) {
            profile.forestCycles++;
            if (!profile.achievements.contains("🌲 Лес №" + profile.forestCycles)) {
                profile.achievements.add("🌲 Лес №" + profile.forestCycles);
            }
            profile.level = 0;
            saveData();
        }
        int expectedTrees = points / 1000;
        while (profile.treesCount < expectedTrees) {
            profile.treesCount++;
            if (!profile.achievements.contains("🌳 Посажено дерево №" + profile.treesCount)) {
                profile.achievements.add("🌳 Посажено дерево №" + profile.treesCount);
            }
        }
        int expectedForests = profile.treesCount / 15;
        while (profile.forestsCount < expectedForests) {
            profile.forestsCount++;
            if (!profile.achievements.contains("🌲 Лес из 15 деревьев №" + profile.forestsCount)) {
                profile.achievements.add("🌲 Лес из 15 деревьев №" + profile.forestsCount);
            }
        }
    }
}