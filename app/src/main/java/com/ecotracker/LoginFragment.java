package com.ecotracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
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
            authPrefs.edit().putBoolean("is_logged_in", true)
                    .putString("current_user", email).apply();
            MainActivity.setCurrentUser(email);
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
        builder.setTitle("Регистрация").setView(dialogView)
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
                .setNegativeButton("Отмена", null).show();
    }
}