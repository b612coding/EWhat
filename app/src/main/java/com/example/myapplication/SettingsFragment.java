package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private EditText usernameEditText;
    private EditText modelEndpointEditText;
    private EditText modelNameEditText;
    private EditText apiKeyEditText;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        modelEndpointEditText = view.findViewById(R.id.modelEndpointEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        apiKeyEditText = view.findViewById(R.id.apiKeyEditText);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button clearButton = view.findViewById(R.id.clearButton);
        ImageView helpIcon = view.findViewById(R.id.helpIcon);

        sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usernameEditText.setText(sharedPreferences.getString("username", ""));
        modelEndpointEditText.setText(sharedPreferences.getString("modelEndpoint", ""));
        modelNameEditText.setText(sharedPreferences.getString("modelName", ""));
        apiKeyEditText.setText(sharedPreferences.getString("apikey", ""));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", usernameEditText.getText().toString());
                editor.putString("modelEndpoint", modelEndpointEditText.getText().toString());
                editor.putString("modelName", modelNameEditText.getText().toString());
                editor.putString("apikey", apiKeyEditText.getText().toString());
                editor.apply();
                Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
                modelEndpointEditText.setText("");
                modelNameEditText.setText("");
                apiKeyEditText.setText("");
            }
        });

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("碎碎念")
                        .setMessage("    一日三餐，吃饭始终是人们绕不开的一件事，谨以此应用帮助那些像我这样总是不知道吃啥的人0.0\n    填写api key用于DIY阶段使用大语言模型，测试时采用通义千问,其可在阿里云百炼平台获取免费api，讯飞星火的api同样适用；模型接入点不填写时为：https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions，模型名称不填写时为：qwen-plus;\n    本应用由@B612coding独立开发，如有问题请联系作者。")
                        .setPositiveButton("懂了", null)
                        .show();
            }
        });

        return view;
    }
}