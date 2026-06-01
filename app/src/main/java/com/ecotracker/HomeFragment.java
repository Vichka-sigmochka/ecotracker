package com.ecotracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ecotracker.models.UserProfile;

public class HomeFragment extends Fragment {
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
        UserProfile p = app.getProfile();
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
        if (current >= 1000) {
            levelIndex = 4;
        } else if (current >= 600) {
            levelIndex = 3;
        } else if (current >= 300) {
            levelIndex = 2;
        } else if (current >= 100) {
            levelIndex = 1;
        } else {
            levelIndex = 0;
        }
        ivTree.setImageResource(trees[levelIndex]);
    }
}