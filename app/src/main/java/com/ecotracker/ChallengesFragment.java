package com.ecotracker;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class ChallengesFragment extends Fragment {
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