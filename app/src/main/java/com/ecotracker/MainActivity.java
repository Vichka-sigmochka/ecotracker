package com.ecotracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private static boolean isLoggedIn = false;
    private static String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        currentUser = prefs.getString("current_user", "");

        if (isLoggedIn && !currentUser.isEmpty()) {
            EcoApp.getInstance().setCurrentUser(currentUser);
            showMainContent();
        } else {
            showLoginScreen();
        }
    }

    private void showLoginScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void showMainContent() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_actions) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ActionsFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_history) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HistoryFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_challenges) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ChallengesFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    public static void logout() {
        isLoggedIn = false;
        currentUser = "";
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
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

        public EcoApp.UserProfile getProfile(String email) {
            SQLiteDatabase db = this.getReadableDatabase();
            EcoApp.UserProfile profile = new EcoApp.UserProfile();

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

        public void updateProfile(String email, EcoApp.UserProfile profile) {
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

        public void addAction(String email, EcoApp.EcoAction action) {
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

        public List<EcoApp.EcoAction> getActions(String email) {
            SQLiteDatabase db = this.getReadableDatabase();
            List<EcoApp.EcoAction> actions = new ArrayList<>();

            String query = "SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COLUMN_USER_EMAIL + " = ? ORDER BY " +
                    COLUMN_ACTION_ID + " DESC";
            Cursor cursor = db.rawQuery(query, new String[]{email});

            while (cursor.moveToNext()) {
                EcoApp.EcoAction action = new EcoApp.EcoAction(
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

    public static class LoginFragment extends Fragment {
        private EditText etEmail, etPassword;
        private Button btnLogin, btnRegister;
        private TextView tvError;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);

            etEmail = v.findViewById(R.id.et_email);
            etPassword = v.findViewById(R.id.et_password);
            btnLogin = v.findViewById(R.id.btn_login);
            btnRegister = v.findViewById(R.id.btn_register);
            tvError = v.findViewById(R.id.tv_error);

            btnLogin.setOnClickListener(view -> login());
            btnRegister.setOnClickListener(view -> showRegisterDialog());

            return v;
        }

        private void login() {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                tvError.setText("Заполните все поля");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            if (dbHelper.loginUser(email, password)) {
                SharedPreferences authPrefs = getContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                authPrefs.edit()
                        .putBoolean("is_logged_in", true)
                        .putString("current_user", email)
                        .apply();

                MainActivity.currentUser = email;
                EcoApp.getInstance().setCurrentUser(email);

                Toast.makeText(getContext(), "Добро пожаловать, " + dbHelper.getUserName(email) + "!", Toast.LENGTH_SHORT).show();

                ((MainActivity) getActivity()).showMainContent();
            } else {
                tvError.setText("Неверный логин или пароль");
                tvError.setVisibility(View.VISIBLE);
            }
        }

        private void showRegisterDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_register, null);

            EditText etRegEmail = dialogView.findViewById(R.id.et_reg_email);
            EditText etRegPassword = dialogView.findViewById(R.id.et_reg_password);
            EditText etRegConfirm = dialogView.findViewById(R.id.et_reg_confirm);
            EditText etRegName = dialogView.findViewById(R.id.et_reg_name);

            builder.setTitle("Регистрация")
                    .setView(dialogView)
                    .setPositiveButton("Зарегистрироваться", (dialog, which) -> {
                        String email = etRegEmail.getText().toString().trim();
                        String password = etRegPassword.getText().toString();
                        String confirm = etRegConfirm.getText().toString();
                        String name = etRegName.getText().toString().trim();

                        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(confirm)) {
                            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        if (dbHelper.userExists(email)) {
                            Toast.makeText(getContext(), "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (dbHelper.registerUser(email, password, name)) {
                            Toast.makeText(getContext(), "Регистрация успешна! Теперь войдите", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        }
    }

    public static class EcoApp extends android.app.Application {
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
            if (actions == null) return new ArrayList<>();
            return new ArrayList<>(actions);
        }

        public UserProfile getProfile() {
            if (profile == null) profile = new UserProfile();
            return profile;
        }

        private void updateLevelAndTrees() {
            int points = profile.totalPoints;
            int previousLevel = profile.level;

            int cyclePoints = points % 1000;

            if (cyclePoints < 100) profile.level = 0;
            else if (cyclePoints < 300) profile.level = 1;
            else if (cyclePoints < 600) profile.level = 2;
            else if (cyclePoints < 1000) profile.level = 3;
            else profile.level = 4;

            if (profile.level == 4 && previousLevel != 4) {
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

        public static class EcoAction {
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

        public static class UserProfile {
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
    }

    public static class HomeFragment extends Fragment {
        private TextView tvLevel, tvPoints, tvCO2, tvMessage, tvPlantedTrees;
        private ProgressBar progress;
        private ImageView ivTree;
        private LinearLayout treesContainer;
        private LinearLayout forestsContainer;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home, container, false);
            tvLevel = v.findViewById(R.id.tv_level);
            tvPoints = v.findViewById(R.id.tv_points);
            tvCO2 = v.findViewById(R.id.tv_co2);
            tvMessage = v.findViewById(R.id.tv_message);
            progress = v.findViewById(R.id.progress);
            ivTree = v.findViewById(R.id.iv_tree);
            tvPlantedTrees = v.findViewById(R.id.tv_planted_trees);
            treesContainer = v.findViewById(R.id.trees_container);
            forestsContainer = v.findViewById(R.id.forests_container);

            updateUI();
            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            updateUI();
        }

        private void updateUI() {
            EcoApp app = EcoApp.getInstance();
            EcoApp.UserProfile p = app.getProfile();

            int cyclePoints = p.totalPoints % 1000;

            tvLevel.setText(p.getLevelName());
            tvPoints.setText(String.valueOf(p.totalPoints));
            tvCO2.setText(p.totalCO2 + " кг");

            if (tvPlantedTrees != null) {
                int totalTrees = p.totalPoints / 1000;
                int totalForests = totalTrees / 15;
                tvPlantedTrees.setText("🌳 Деревьев: " + totalTrees + " | 🌲 Лесов: " + totalForests);
            }

            if (forestsContainer != null) {
                forestsContainer.removeAllViews();
                int totalTrees = p.totalPoints / 1000;
                int totalForests = totalTrees / 15;

                for (int i = 0; i < totalForests; i++) {
                    CardView card = new CardView(getContext());
                    CardView.LayoutParams params = new CardView.LayoutParams(80, 80);
                    params.setMargins(8, 8, 8, 8);
                    card.setLayoutParams(params);
                    card.setRadius(12f);
                    card.setCardElevation(4f);
                    card.setCardBackgroundColor(Color.parseColor("#4CAF50"));

                    TextView emoji = new TextView(getContext());
                    emoji.setText("🌲");
                    emoji.setTextSize(32);
                    emoji.setGravity(Gravity.CENTER);
                    emoji.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

                    card.addView(emoji);
                    forestsContainer.addView(card);
                }
            }

            if (treesContainer != null) {
                treesContainer.removeAllViews();
                int totalTrees = p.totalPoints / 1000;
                int remainingTrees = totalTrees % 15;

                for (int i = 0; i < remainingTrees; i++) {
                    CardView card = new CardView(getContext());
                    CardView.LayoutParams params = new CardView.LayoutParams(70, 70);
                    params.setMargins(8, 8, 8, 8);
                    card.setLayoutParams(params);
                    card.setRadius(10f);
                    card.setCardElevation(3f);
                    card.setCardBackgroundColor(Color.parseColor("#8BC34A"));

                    TextView emoji = new TextView(getContext());
                    emoji.setText("🌳");
                    emoji.setTextSize(28);
                    emoji.setGravity(Gravity.CENTER);
                    emoji.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

                    card.addView(emoji);
                    treesContainer.addView(card);
                }
            }

            int current = cyclePoints;
            int max = 0, progressVal = 0;
            String message = "";

            if (current < 100) {
                progressVal = current;
                max = 100;
                message = "🌱 До саженца: " + (100 - current) + " очков";
            } else if (current < 300) {
                progressVal = current - 100;
                max = 200;
                message = "🌿 До молодого дерева: " + (300 - current) + " очков";
            } else if (current < 600) {
                progressVal = current - 300;
                max = 300;
                message = "🌳 До взрослого дерева: " + (600 - current) + " очков";
            } else if (current < 1000) {
                progressVal = current - 600;
                max = 400;
                message = "🍃 До леса: " + (1000 - current) + " очков";
            } else {
                progressVal = 0;
                max = 100;
                message = "🌲 Лес достигнут! Дерево перерождается в росток!";
            }

            if (p.forestCycles > 0 && current < 1000) {
                message = "🌲 Лес #" + p.forestCycles + " | " + message;
            }

            progress.setMax(max);
            progress.setProgress(progressVal);
            tvMessage.setText(message);

            int[] trees = {R.drawable.ic_sprout, R.drawable.ic_sapling, R.drawable.ic_young, R.drawable.ic_adult, R.drawable.ic_forest};
            int levelIndex = 0;
            if (current >= 1000) levelIndex = 4;
            else if (current >= 600) levelIndex = 3;
            else if (current >= 300) levelIndex = 2;
            else if (current >= 100) levelIndex = 1;
            else levelIndex = 0;

            ivTree.setImageResource(trees[levelIndex]);
        }
    }

    public static class ProfileFragment extends Fragment {
        private TextView tvName, tvCO2, tvPoints, tvLevel, tvTrees;
        private Button btnEdit, btnShare, btnLogout;
        private LinearLayout achLayout;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_profile, container, false);
            tvName = v.findViewById(R.id.tv_name);
            tvCO2 = v.findViewById(R.id.tv_co2_total);
            tvPoints = v.findViewById(R.id.tv_points_total);
            tvLevel = v.findViewById(R.id.tv_level_name);
            tvTrees = v.findViewById(R.id.tv_trees);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnShare = v.findViewById(R.id.btn_share);
            btnLogout = v.findViewById(R.id.btn_logout);
            achLayout = v.findViewById(R.id.ach_layout);

            updateUI();
            btnEdit.setOnClickListener(view -> editName());
            btnShare.setOnClickListener(view -> share());
            btnLogout.setOnClickListener(view -> logout());

            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            updateUI();
        }

        private void updateUI() {
            EcoApp.UserProfile p = EcoApp.getInstance().getProfile();
            int totalTrees = p.totalPoints / 1000;
            int totalForests = totalTrees / 15;

            tvName.setText(p.name);
            tvCO2.setText(String.valueOf(p.totalCO2));
            tvPoints.setText(String.valueOf(p.totalPoints));

            int cyclePoints = p.totalPoints % 1000;
            if (cyclePoints < 100) tvLevel.setText("🌱 Росток");
            else if (cyclePoints < 300) tvLevel.setText("🌿 Саженец");
            else if (cyclePoints < 600) tvLevel.setText("🌳 Молодое дерево");
            else if (cyclePoints < 1000) tvLevel.setText("🍃 Взрослое дерево");
            else tvLevel.setText("🌲 Лес");

            tvTrees.setText("🌳 Деревьев: " + totalTrees + " | 🌲 Лесов: " + totalForests);

            achLayout.removeAllViews();
            for (String a : p.achievements) {
                TextView tv = new TextView(getContext());
                tv.setText(a);
                tv.setTextSize(14);
                tv.setPadding(16, 8, 16, 8);
                achLayout.addView(tv);
            }
        }

        private void editName() {
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setTitle("Изменить имя");
            EditText input = new EditText(getContext());
            input.setText(EcoApp.getInstance().getProfile().name);
            b.setView(input);
            b.setPositiveButton("Сохранить", (d, which) -> {
                String newName = input.getText().toString();
                if (!newName.isEmpty()) {
                    EcoApp.getInstance().getProfile().name = newName;
                    EcoApp.getInstance().saveData();

                    String currentUser = MainActivity.getCurrentUser();
                    if (!currentUser.isEmpty()) {
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        dbHelper.updateUserName(currentUser, newName);
                    }

                    tvName.setText(newName);
                }
            });
            b.setNegativeButton("Отмена", null);
            b.show();
        }

        private void share() {
            EcoApp.UserProfile p = EcoApp.getInstance().getProfile();
            int totalTrees = p.totalPoints / 1000;
            int totalForests = totalTrees / 15;

            String text = String.format("🌍 Я сэкономил %d кг CO₂ в Эко-трекере! Посажено деревьев: %d, выращено лесов: %d! Присоединяйся!",
                    p.totalCO2, totalTrees, totalForests);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(intent, "Поделиться"));
        }

        private void logout() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Выход");
            builder.setMessage("Вы уверены, что хотите выйти?");
            builder.setPositiveButton("Да", (dialog, which) -> {
                EcoApp.getInstance().saveData();

                SharedPreferences authPrefs = getContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                authPrefs.edit()
                        .putBoolean("is_logged_in", false)
                        .putString("current_user", "")
                        .apply();

                MainActivity.logout();

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
            builder.setNegativeButton("Нет", null);
            builder.show();
        }
    }

    public static class ActionsFragment extends Fragment {
        private Spinner spCat, spAct;
        private EditText etQty;
        private TextView tvEstimate;
        private Button btnAdd;
        private Map<String, List<ActionItem>> actionsMap = new HashMap<>();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_actions, container, false);
            spCat = v.findViewById(R.id.sp_cat);
            spAct = v.findViewById(R.id.sp_act);
            etQty = v.findViewById(R.id.et_qty);
            tvEstimate = v.findViewById(R.id.tv_estimate);
            btnAdd = v.findViewById(R.id.btn_add);

            initActions();
            setupSpinners();
            btnAdd.setOnClickListener(view -> addAction());
            return v;
        }

        private void initActions() {
            actionsMap.put("Транспорт", Arrays.asList(
                    new ActionItem("Велосипед", 0.21, "км", "Поездка на велосипеде"),
                    new ActionItem("Общественный транспорт", 0.15, "км", "Поездка на транспорте"),
                    new ActionItem("Пешком", 0.18, "км", "Прогулка пешком")));
            actionsMap.put("Питание", Arrays.asList(
                    new ActionItem("Веганский день", 5.0, "день", "Отказ от животной пищи"),
                    new ActionItem("Вегетарианский день", 2.5, "день", "Без мяса")));
            actionsMap.put("Покупки", Arrays.asList(
                    new ActionItem("Без пакета", 0.1, "раз", "Эко-сумка"),
                    new ActionItem("Секонд-хенд", 2.0, "раз", "Покупка б/у"),
                    new ActionItem("Многоразовая бутылка", 0.05, "раз", "Без пластика")));
            actionsMap.put("Дом", Arrays.asList(
                    new ActionItem("Сортировка мусора", 0.5, "день", "Сортировка мусора"),
                    new ActionItem("Экономия энергии", 1.0, "день", "Энергосбережение"),
                    new ActionItem("Экономия воды", 0.3, "день", "Водосбережение")));
            actionsMap.put("Осознанность", Arrays.asList(
                    new ActionItem("Обучение", 0.2, "раз", "Эко-лекция"),
                    new ActionItem("Субботник", 2.0, "раз", "Уборка"),
                    new ActionItem("Посадка дерева", 10.0, "раз", "Посадка"),
                    new ActionItem("Волонтерство", 3.0, "час", "Эко-волонтерство")));
        }

        private void setupSpinners() {
            String[] cats = {"Транспорт", "Питание", "Покупки", "Дом", "Осознанность"};
            ArrayAdapter<String> ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cats);
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCat.setAdapter(ad);
            spCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    updateActionSpinner(cats[pos]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            updateActionSpinner(cats[0]);
            etQty.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateEstimate();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });
        }

        private void updateActionSpinner(String cat) {
            List<ActionItem> items = actionsMap.get(cat);
            String[] names = new String[items.size()];
            for (int i = 0; i < items.size(); i++) names[i] = items.get(i).display;
            ArrayAdapter<String> ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAct.setAdapter(ad);
            spAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    updateEstimate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        private void updateEstimate() {
            String q = etQty.getText().toString();
            if (q.isEmpty()) {
                tvEstimate.setText("≈ 0 кг CO₂");
                return;
            }
            try {
                double qty = Double.parseDouble(q);
                String cat = spCat.getSelectedItem().toString();
                int pos = spAct.getSelectedItemPosition();
                double co2 = qty * actionsMap.get(cat).get(pos).co2;
                tvEstimate.setText(String.format("≈ %.1f кг CO₂", co2));
            } catch (Exception e) {
                tvEstimate.setText("≈ 0 кг CO₂");
            }
        }

        private void addAction() {
            if (etQty == null || spCat == null || spAct == null) return;
            String q = etQty.getText().toString();
            if (q.isEmpty()) {
                Toast.makeText(getContext(), "Введите количество", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double qty = Double.parseDouble(q);
                String cat = spCat.getSelectedItem().toString();
                int pos = spAct.getSelectedItemPosition();
                List<ActionItem> items = actionsMap.get(cat);
                if (items == null || pos >= items.size()) return;
                ActionItem act = items.get(pos);
                double co2 = qty * act.co2;
                int points = (int) Math.round(co2);

                EcoApp.EcoAction ecoAction = new EcoApp.EcoAction(cat, act.full, qty, act.unit, co2, points);
                EcoApp.getInstance().addAction(ecoAction);

                Toast.makeText(getContext(), "Добавлено! +" + points + " очков", Toast.LENGTH_SHORT).show();
                etQty.setText("");
                updateEstimate();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        static class ActionItem {
            String display, unit, full;
            double co2;

            ActionItem(String d, double c, String u, String f) {
                display = d;
                co2 = c;
                unit = u;
                full = f;
            }
        }
    }

    public static class HistoryFragment extends Fragment {
        private RecyclerView recycler;
        private Spinner spinnerCategory;
        private TextView tvTotalCO2;
        private List<EcoApp.EcoAction> allActions = new ArrayList<>();
        private List<EcoApp.EcoAction> filteredActions = new ArrayList<>();
        private HistoryAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_history, container, false);

            try {
                recycler = v.findViewById(R.id.recycler);
                spinnerCategory = v.findViewById(R.id.spinner_category);
                tvTotalCO2 = v.findViewById(R.id.tv_total_co2);

                if (recycler != null) {
                    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                }

                setupCategorySpinner();
                updateList();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
            }

            return v;
        }

        private void setupCategorySpinner() {
            String[] categories = {"Все", "Транспорт", "Питание", "Покупки", "Дом", "Осознанность"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if (spinnerCategory != null) {
                spinnerCategory.setAdapter(adapter);
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filterByCategory(categories[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }

        private void filterByCategory(String category) {
            if (allActions == null) return;

            filteredActions.clear();

            if (category.equals("Все")) {
                filteredActions.addAll(allActions);
            } else {
                for (EcoApp.EcoAction action : allActions) {
                    if (action.category != null && action.category.equals(category)) {
                        filteredActions.add(action);
                    }
                }
            }

            updateTotalCO2();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        private void updateTotalCO2() {
            double total = 0;
            for (EcoApp.EcoAction action : filteredActions) {
                total += action.co2Saved;
            }
            if (tvTotalCO2 != null) {
                tvTotalCO2.setText(String.format("🌍 Всего: %.1f кг CO₂", total));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            updateList();
        }

        private void updateList() {
            try {
                allActions.clear();
                allActions.addAll(EcoApp.getInstance().getActions());

                String selectedCategory = "Все";
                if (spinnerCategory != null && spinnerCategory.getSelectedItem() != null) {
                    selectedCategory = spinnerCategory.getSelectedItem().toString();
                }

                filterByCategory(selectedCategory);

                adapter = new HistoryAdapter(filteredActions);
                if (recycler != null) {
                    recycler.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
            List<EcoApp.EcoAction> list;

            HistoryAdapter(List<EcoApp.EcoAction> l) {
                list = l != null ? l : new ArrayList<>();
            }

            @NonNull
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
                View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_action, p, false);
                return new VH(v);
            }

            @Override
            public void onBindViewHolder(@NonNull VH h, int i) {
                if (i < list.size()) {
                    EcoApp.EcoAction a = list.get(i);
                    if (a != null) {
                        h.tvAct.setText(a.name != null ? a.name : "Действие");
                        h.tvCat.setText(a.category != null ? a.category : "Другое");
                        h.tvDet.setText(String.format("%.1f %s → %.1f кг", a.quantity, a.unit, a.co2Saved));
                        h.tvDate.setText(a.date != null ? a.date : "");
                        h.tvPts.setText("+" + a.points);
                    }
                }
            }

            @Override
            public int getItemCount() {
                return list != null ? list.size() : 0;
            }

            class VH extends RecyclerView.ViewHolder {
                TextView tvAct, tvCat, tvDet, tvDate, tvPts;

                VH(View v) {
                    super(v);
                    tvAct = v.findViewById(R.id.tv_act);
                    tvCat = v.findViewById(R.id.tv_cat);
                    tvDet = v.findViewById(R.id.tv_det);
                    tvDate = v.findViewById(R.id.tv_date);
                    tvPts = v.findViewById(R.id.tv_pts);
                }
            }
        }
    }

    public static class ChallengesFragment extends Fragment {
        private ListView list;
        private List<ChallengeItem> challenges = new ArrayList<>();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                View v = inflater.inflate(R.layout.fragment_challenges, container, false);
                list = v.findViewById(R.id.list);

                initChallenges();
                loadChallengeProgress();

                if (list != null) {
                    ChallengeAdapter adapter = new ChallengeAdapter(getContext(), challenges);
                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            showChallengeDialog(position);
                        }
                    });
                }
                return v;
            } catch (Exception e) {
                e.printStackTrace();
                return new View(getContext());
            }
        }

        private void initChallenges() {
            challenges.clear();
            challenges.add(new ChallengeItem(
                    "Неделя без мяса",
                    "Откажитесь от мяса на 7 дней. Каждый день без мяса экономит ~2.5 кг CO₂!",
                    7,
                    "7 дней",
                    "🥇 Веган-герой",
                    "Отказ от мяса на неделю"
            ));
            challenges.add(new ChallengeItem(
                    "30 дней на велике",
                    "Используйте велосипед вместо автомобиля. За 30 дней вы сэкономите ~63 кг CO₂!",
                    30,
                    "30 дней",
                    "🚴 Велоактивист",
                    "30 дней без авто"
            ));
            challenges.add(new ChallengeItem(
                    "Откажись от пакетов",
                    "Не используйте пластиковые пакеты при покупках. Каждый пакет экономит ~0.1 кг CO₂!",
                    10,
                    "Без срока",
                    "🛍️ Эко-шопер",
                    "10 отказов от пакетов"
            ));
            challenges.add(new ChallengeItem(
                    "Эко-детокс",
                    "Сократите потребление электроэнергии на 20%. Выключайте свет и приборы из розетки!",
                    30,
                    "1 месяц",
                    "💡 Энергосберегатель",
                    "Снижение энергии на 20%"
            ));
            challenges.add(new ChallengeItem(
                    "Сортировка отходов",
                    "Сортируйте мусор 30 дней подряд. Это спасет ~15 кг CO₂!",
                    30,
                    "30 дней",
                    "♻️ Мастер переработки",
                    "30 дней сортировки"
            ));
            challenges.add(new ChallengeItem(
                    "Веганский вызов",
                    "Попробуйте веганство на 3 дня. Каждый день экономит ~5 кг CO₂!",
                    3,
                    "3 дня",
                    "🌱 Веган-новичок",
                    "3 дня без животных продуктов"
            ));
        }

        private void loadChallengeProgress() {
            String currentUser = MainActivity.getCurrentUser();
            if (!currentUser.isEmpty()) {
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.loadAllChallenges(currentUser, challenges);
            }
        }

        private void saveChallengeProgress(int position) {
            ChallengeItem challenge = challenges.get(position);
            String currentUser = MainActivity.getCurrentUser();
            if (!currentUser.isEmpty()) {
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.updateChallengeProgress(currentUser, challenge.name,
                        challenge.currentProgress, challenge.completed, challenge.isRewardClaimed);
            }
        }

        private void showChallengeDialog(int position) {
            ChallengeItem challenge = challenges.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_challenge, null);

            TextView tvTitle = dialogView.findViewById(R.id.tv_challenge_title);
            TextView tvDescription = dialogView.findViewById(R.id.tv_challenge_description);
            TextView tvDeadline = dialogView.findViewById(R.id.tv_challenge_deadline);
            TextView tvPoints = dialogView.findViewById(R.id.tv_challenge_points);
            TextView tvProgress = dialogView.findViewById(R.id.tv_progress);
            TextView tvRewardTitle = dialogView.findViewById(R.id.tv_reward_title);
            TextView tvRewardDesc = dialogView.findViewById(R.id.tv_reward_desc);
            ProgressBar progressBar = dialogView.findViewById(R.id.challenge_progress);
            Button btnUpdateProgress = dialogView.findViewById(R.id.btn_update_progress);
            Button btnClaimReward = dialogView.findViewById(R.id.btn_claim_reward);
            TextView tvStatus = dialogView.findViewById(R.id.tv_challenge_status);

            tvTitle.setText(challenge.name);
            tvDescription.setText(challenge.description);
            tvDeadline.setText("⏰ Дедлайн: " + challenge.deadline);
            tvPoints.setText("🏆 Награда: " + challenge.points + " очков");
            tvRewardTitle.setText("🎁 Награда за выполнение: " + challenge.rewardTitle);
            tvRewardDesc.setText(challenge.rewardDesc);

            int progressPercent = (int) ((float) challenge.currentProgress / challenge.targetPoints * 100);
            if (progressPercent > 100) progressPercent = 100;
            tvProgress.setText("Прогресс: " + challenge.currentProgress + " / " + challenge.targetPoints + " (" + progressPercent + "%)");
            progressBar.setMax(challenge.targetPoints);
            progressBar.setProgress(challenge.currentProgress);

            if (challenge.completed) {
                if (challenge.isRewardClaimed) {
                    tvStatus.setText("✅ Награда получена!");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    btnUpdateProgress.setEnabled(false);
                    btnClaimReward.setEnabled(false);
                    btnClaimReward.setText("Награда уже получена");
                } else {
                    tvStatus.setText("🏆 Челлендж выполнен! Получите награду!");
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                    btnUpdateProgress.setEnabled(false);
                    btnClaimReward.setEnabled(true);
                }
            } else {
                tvStatus.setText("⏳ В процессе выполнения");
                tvStatus.setTextColor(Color.parseColor("#2196F3"));
                btnUpdateProgress.setEnabled(true);
                btnClaimReward.setEnabled(false);
            }

            btnUpdateProgress.setOnClickListener(v -> {
                showUpdateProgressDialog(position, dialogView, tvProgress, progressBar, btnUpdateProgress, btnClaimReward, tvStatus);
            });

            btnClaimReward.setOnClickListener(v -> {
                claimReward(position, dialogView, btnClaimReward, tvStatus);
            });

            builder.setView(dialogView);
            builder.setNegativeButton("Закрыть", null);
            builder.show();
        }

        private void showUpdateProgressDialog(int position, View dialogView, TextView tvProgress,
                                              ProgressBar progressBar, Button btnUpdateProgress,
                                              Button btnClaimReward, TextView tvStatus) {
            ChallengeItem challenge = challenges.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Обновить прогресс");

            final EditText input = new EditText(getContext());
            input.setHint("Количество выполненных единиц");
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("Добавить", (dialog, which) -> {
                try {
                    int progress = Integer.parseInt(input.getText().toString());
                    if (progress > 0) {
                        challenge.currentProgress += progress;
                        if (challenge.currentProgress > challenge.targetPoints) {
                            challenge.currentProgress = challenge.targetPoints;
                        }

                        saveChallengeProgress(position);

                        int progressPercent = (int) ((float) challenge.currentProgress / challenge.targetPoints * 100);
                        tvProgress.setText("Прогресс: " + challenge.currentProgress + " / " + challenge.targetPoints + " (" + progressPercent + "%)");
                        progressBar.setProgress(challenge.currentProgress);

                        if (challenge.currentProgress >= challenge.targetPoints && !challenge.completed) {
                            challenge.completed = true;
                            saveChallengeProgress(position);
                            tvStatus.setText("🏆 Челлендж выполнен! Получите награду!");
                            tvStatus.setTextColor(Color.parseColor("#FF9800"));
                            btnUpdateProgress.setEnabled(false);
                            btnClaimReward.setEnabled(true);

                            Toast.makeText(getContext(), "Поздравляем! Челлендж выполнен! Получите награду!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Прогресс обновлен! +" + progress + " к выполнению", Toast.LENGTH_SHORT).show();
                        }

                        if (list != null && list.getAdapter() != null) {
                            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Введите корректное число", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Отмена", null);
            builder.show();
        }

        private void claimReward(int position, View dialogView, Button btnClaimReward, TextView tvStatus) {
            ChallengeItem challenge = challenges.get(position);

            if (challenge.completed && !challenge.isRewardClaimed) {
                EcoApp.getInstance().getProfile().totalPoints += challenge.points;
                EcoApp.getInstance().saveData();

                if (!EcoApp.getInstance().getProfile().achievements.contains(challenge.rewardTitle)) {
                    EcoApp.getInstance().getProfile().achievements.add(challenge.rewardTitle);
                    EcoApp.getInstance().saveData();
                }

                challenge.isRewardClaimed = true;
                saveChallengeProgress(position);

                tvStatus.setText("✅ Награда получена! +" + challenge.points + " очков!");
                tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                btnClaimReward.setEnabled(false);
                btnClaimReward.setText("Награда получена");

                Toast.makeText(getContext(), "🎉 Получено: " + challenge.rewardTitle + "!\n+" + challenge.points + " очков!", Toast.LENGTH_LONG).show();

                if (list != null && list.getAdapter() != null) {
                    ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                }

                String currentUser = MainActivity.getCurrentUser();
                if (!currentUser.isEmpty()) {
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    dbHelper.updateProfile(currentUser, EcoApp.getInstance().getProfile());
                }
            }
        }

        static class ChallengeItem {
            String name;
            String description;
            String deadline;
            String rewardTitle;
            String rewardDesc;
            int points;
            int targetPoints;
            int currentProgress = 0;
            boolean completed = false;
            boolean isRewardClaimed = false;

            ChallengeItem(String n, String d, int p, String dl, String rewardTitle, String rewardDesc) {
                name = n;
                description = d;
                points = p;
                targetPoints = p;
                deadline = dl;
                this.rewardTitle = rewardTitle;
                this.rewardDesc = rewardDesc;
            }
        }

        class ChallengeAdapter extends BaseAdapter {
            Context ctx;
            List<ChallengeItem> items;

            ChallengeAdapter(Context c, List<ChallengeItem> i) {
                ctx = c;
                items = i;
            }

            @Override
            public int getCount() {
                return items.size();
            }

            @Override
            public Object getItem(int p) {
                return items.get(p);
            }

            @Override
            public long getItemId(int p) {
                return p;
            }

            @Override
            public View getView(int p, View cv, ViewGroup parent) {
                try {
                    if (cv == null)
                        cv = LayoutInflater.from(ctx).inflate(R.layout.item_challenge, parent, false);
                    ChallengeItem ch = items.get(p);

                    TextView tvAct = cv.findViewById(R.id.tv_act);
                    TextView tvCat = cv.findViewById(R.id.tv_cat);
                    TextView tvDet = cv.findViewById(R.id.tv_det);
                    TextView tvDate = cv.findViewById(R.id.tv_date);
                    TextView tvPts = cv.findViewById(R.id.tv_pts);
                    ProgressBar progressBar = cv.findViewById(R.id.challenge_progress_item);
                    TextView tvProgressText = cv.findViewById(R.id.tv_progress_text);
                    ImageView ivStatus = cv.findViewById(R.id.iv_status);

                    if (tvAct != null) tvAct.setText(ch.name);
                    if (tvCat != null) tvCat.setText("🏆 Челлендж");
                    if (tvDet != null) tvDet.setText(ch.description);
                    if (tvDate != null) tvDate.setText("⏰ " + ch.deadline);
                    if (tvPts != null) tvPts.setText("+" + ch.points);

                    if (progressBar != null) {
                        progressBar.setMax(ch.targetPoints);
                        progressBar.setProgress(ch.currentProgress);
                    }

                    if (tvProgressText != null) {
                        int percent = (int) ((float) ch.currentProgress / ch.targetPoints * 100);
                        tvProgressText.setText(ch.currentProgress + "/" + ch.targetPoints + " (" + percent + "%)");
                    }

                    if (ivStatus != null) {
                        if (ch.completed && ch.isRewardClaimed) {
                            ivStatus.setImageResource(android.R.drawable.btn_star_big_on);
                            ivStatus.setColorFilter(Color.parseColor("#FFD700"));
                        } else if (ch.completed) {
                            ivStatus.setImageResource(android.R.drawable.btn_star_big_on);
                            ivStatus.setColorFilter(Color.parseColor("#FF9800"));
                        } else {
                            ivStatus.setImageResource(android.R.drawable.btn_star_big_off);
                            ivStatus.setColorFilter(Color.parseColor("#9E9E9E"));
                        }
                    }

                    if (ch.completed && ch.isRewardClaimed) {
                        cv.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    } else if (ch.completed) {
                        cv.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return cv;
            }
        }
    }
}