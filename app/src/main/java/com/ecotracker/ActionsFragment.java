package com.ecotracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecotracker.models.EcoAction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsFragment extends Fragment {
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
        if (etQty == null || spCat == null || spAct == null) {
            return;
        }
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
            if (items == null || pos >= items.size()) {
                return;
            }
            ActionItem act = items.get(pos);
            double co2 = qty * act.co2;
            int points = (int) Math.round(co2);
            EcoAction ecoAction = new EcoAction(cat, act.full, qty, act.unit, co2, points);
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