package com.ecotracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecotracker.models.UserProfile;

public class ProfileFragment extends Fragment {
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
        UserProfile p = EcoApp.getInstance().getProfile();
        int totalTrees = p.totalPoints / 1000;
        int totalForests = totalTrees / 15;
        tvName.setText(p.name);
        tvCO2.setText(String.valueOf(p.totalCO2));
        tvPoints.setText(String.valueOf(p.totalPoints));
        int cyclePoints = p.totalPoints % 1000;
        if (cyclePoints < 100) {
            tvLevel.setText("🌱 Росток");
        } else if (cyclePoints < 300) {
            tvLevel.setText("🌿 Саженец");
        } else if (cyclePoints < 600) {
            tvLevel.setText("🌳 Молодое дерево");
        } else if (cyclePoints < 1000) {
            tvLevel.setText("🍃 Взрослое дерево");
        } else {
            tvLevel.setText("🌲 Лес");
        }
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
        UserProfile p = EcoApp.getInstance().getProfile();
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
            authPrefs.edit().putBoolean("is_logged_in", false)
                    .putString("current_user", "").apply();
            MainActivity.logout();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("Нет", null);
        builder.show();
    }
}