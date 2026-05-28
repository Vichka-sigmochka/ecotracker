package com.ecotracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ecotracker.models.EcoAction;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recycler;
    private Spinner spinnerCategory;
    private TextView tvTotalCO2;
    private List<EcoAction> allActions = new ArrayList<>();
    private List<EcoAction> filteredActions = new ArrayList<>();
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
            for (EcoAction action : allActions) {
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
        for (EcoAction action : filteredActions) {
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
        List<EcoAction> list;

        HistoryAdapter(List<EcoAction> l) {
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
                EcoAction a = list.get(i);
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