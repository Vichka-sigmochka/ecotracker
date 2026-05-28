package com.ecotracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ecotracker.models.EcoAction;
import com.ecotracker.models.UserProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ecotracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "name";

    private static final String TABLE_ACTIONS = "actions";
    private static final String COLUMN_ACTION_ID = "action_id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ACTION_NAME = "action_name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_CO2_SAVED = "co2_saved";
    private static final String COLUMN_POINTS = "points";
    private static final String COLUMN_DATE = "date";

    private static final String TABLE_PROFILE = "profile";
    private static final String COLUMN_TOTAL_CO2 = "total_co2";
    private static final String COLUMN_TOTAL_POINTS = "total_points";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_TREES_COUNT = "trees_count";
    private static final String COLUMN_FORESTS_COUNT = "forests_count";
    private static final String COLUMN_FOREST_CYCLES = "forest_cycles";
    private static final String COLUMN_ACHIEVEMENTS = "achievements";

    private static final String TABLE_CHALLENGES = "challenges";
    private static final String COLUMN_CHALLENGE_ID = "challenge_id";
    private static final String COLUMN_CHALLENGE_NAME = "challenge_name";
    private static final String COLUMN_PROGRESS = "progress";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_REWARD_CLAIMED = "reward_claimed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createUsersTable);

        String createActionsTable = "CREATE TABLE " + TABLE_ACTIONS + " (" +
                COLUMN_ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_ACTION_NAME + " TEXT, " +
                COLUMN_QUANTITY + " REAL, " +
                COLUMN_UNIT + " TEXT, " +
                COLUMN_CO2_SAVED + " REAL, " +
                COLUMN_POINTS + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createActionsTable);

        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_USER_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_TOTAL_CO2 + " INTEGER, " +
                COLUMN_TOTAL_POINTS + " INTEGER, " +
                COLUMN_LEVEL + " INTEGER, " +
                COLUMN_TREES_COUNT + " INTEGER, " +
                COLUMN_FORESTS_COUNT + " INTEGER, " +
                COLUMN_FOREST_CYCLES + " INTEGER, " +
                COLUMN_ACHIEVEMENTS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createProfileTable);

        String createChallengesTable = "CREATE TABLE " + TABLE_CHALLENGES + " (" +
                COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_CHALLENGE_NAME + " TEXT, " +
                COLUMN_PROGRESS + " INTEGER, " +
                COLUMN_COMPLETED + " INTEGER, " +
                COLUMN_REWARD_CLAIMED + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createChallengesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);

        long result = db.insert(TABLE_USERS, null, values);

        if (result != -1) {
            createDefaultProfile(email);
            createDefaultChallenges(email);
            return true;
        }
        return false;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public boolean userExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String name = "Эко-герой";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public void updateUserName(String email, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
    }

    private void createDefaultProfile(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_TOTAL_CO2, 0);
        values.put(COLUMN_TOTAL_POINTS, 0);
        values.put(COLUMN_LEVEL, 0);
        values.put(COLUMN_TREES_COUNT, 0);
        values.put(COLUMN_FORESTS_COUNT, 0);
        values.put(COLUMN_FOREST_CYCLES, 0);
        values.put(COLUMN_ACHIEVEMENTS, "🌱 Первый шаг,🚴 Эко-активист");
        db.insert(TABLE_PROFILE, null, values);
    }

    public UserProfile getProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserProfile profile = new UserProfile();

        String query = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            profile.totalCO2 = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CO2));
            profile.totalPoints = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_POINTS));
            profile.level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL));
            profile.treesCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TREES_COUNT));
            profile.forestsCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FORESTS_COUNT));
            profile.forestCycles = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOREST_CYCLES));

            String achievementsStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENTS));
            if (achievementsStr != null && !achievementsStr.isEmpty()) {
                profile.achievements = new ArrayList<>(Arrays.asList(achievementsStr.split(",")));
            } else {
                profile.achievements = new ArrayList<>(Arrays.asList("🌱 Первый шаг", "🚴 Эко-активист"));
            }
        }
        cursor.close();

        profile.name = getUserName(email);
        return profile;
    }

    public void updateProfile(String email, UserProfile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTAL_CO2, profile.totalCO2);
        values.put(COLUMN_TOTAL_POINTS, profile.totalPoints);
        values.put(COLUMN_LEVEL, profile.level);
        values.put(COLUMN_TREES_COUNT, profile.treesCount);
        values.put(COLUMN_FORESTS_COUNT, profile.forestsCount);
        values.put(COLUMN_FOREST_CYCLES, profile.forestCycles);

        String achievementsStr = "";
        for (int i = 0; i < profile.achievements.size(); i++) {
            if (i > 0) achievementsStr += ",";
            achievementsStr += profile.achievements.get(i);
        }
        values.put(COLUMN_ACHIEVEMENTS, achievementsStr);

        db.update(TABLE_PROFILE, values, COLUMN_USER_EMAIL + " = ?", new String[]{email});
    }

    public void addAction(String email, EcoAction action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_CATEGORY, action.category);
        values.put(COLUMN_ACTION_NAME, action.name);
        values.put(COLUMN_QUANTITY, action.quantity);
        values.put(COLUMN_UNIT, action.unit);
        values.put(COLUMN_CO2_SAVED, action.co2Saved);
        values.put(COLUMN_POINTS, action.points);
        values.put(COLUMN_DATE, action.date);
        db.insert(TABLE_ACTIONS, null, values);
    }

    public List<EcoAction> getActions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<EcoAction> actions = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COLUMN_USER_EMAIL + " = ? ORDER BY " +
                COLUMN_ACTION_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        while (cursor.moveToNext()) {
            EcoAction action = new EcoAction(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTION_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CO2_SAVED)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS))
            );
            action.date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            actions.add(action);
        }
        cursor.close();
        return actions;
    }

    private void createDefaultChallenges(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] challengeNames = {
                "Неделя без мяса", "30 дней на велике", "Откажись от пакетов",
                "Эко-детокс", "Сортировка отходов", "Веганский вызов"
        };

        for (String name : challengeNames) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_EMAIL, email);
            values.put(COLUMN_CHALLENGE_NAME, name);
            values.put(COLUMN_PROGRESS, 0);
            values.put(COLUMN_COMPLETED, 0);
            values.put(COLUMN_REWARD_CLAIMED, 0);
            db.insert(TABLE_CHALLENGES, null, values);
        }
    }

    public int getChallengeProgress(String email, String challengeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PROGRESS + " FROM " + TABLE_CHALLENGES +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_CHALLENGE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, challengeName});
        int progress = 0;
        if (cursor.moveToFirst()) {
            progress = cursor.getInt(0);
        }
        cursor.close();
        return progress;
    }

    public boolean isChallengeCompleted(String email, String challengeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_COMPLETED + " FROM " + TABLE_CHALLENGES +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_CHALLENGE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, challengeName});
        boolean completed = false;
        if (cursor.moveToFirst()) {
            completed = cursor.getInt(0) == 1;
        }
        cursor.close();
        return completed;
    }

    public boolean isRewardClaimed(String email, String challengeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_REWARD_CLAIMED + " FROM " + TABLE_CHALLENGES +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_CHALLENGE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, challengeName});
        boolean claimed = false;
        if (cursor.moveToFirst()) {
            claimed = cursor.getInt(0) == 1;
        }
        cursor.close();
        return claimed;
    }

    public void updateChallengeProgress(String email, String challengeName, int progress, boolean completed, boolean rewardClaimed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS, progress);
        values.put(COLUMN_COMPLETED, completed ? 1 : 0);
        values.put(COLUMN_REWARD_CLAIMED, rewardClaimed ? 1 : 0);

        db.update(TABLE_CHALLENGES, values,
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_CHALLENGE_NAME + " = ?",
                new String[]{email, challengeName});
    }

    public void loadAllChallenges(String email, List<ChallengesFragment.ChallengeItem> challenges) {
        for (ChallengesFragment.ChallengeItem challenge : challenges) {
            challenge.currentProgress = getChallengeProgress(email, challenge.name);
            challenge.completed = isChallengeCompleted(email, challenge.name);
            challenge.isRewardClaimed = isRewardClaimed(email, challenge.name);
        }
    }
}