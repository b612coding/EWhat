package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DIYFragment extends Fragment {

    private RadioGroup dishCountRadioGroup;
    private RadioGroup spicinessRadioGroup;
    private CheckBox meatPork, meatBeef, meatChicken, meatFish, meatLamb, meatDuck, meatShrimp, meatOther;
    private CheckBox vegetableCabbage, vegetableWaterSpinach, vegetableCarrot, vegetableSpinach, vegetableMushroom, vegetableEnoki, vegetableBeanSprout, vegetableOther;
    private CheckBox restrictionOnion, restrictionGinger, restrictionGarlic, restrictionCilantro, restrictionPepper;
    private TextView resultTextView;
    private LinearLayout formLayout, resultLayout;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;

    private String modelEndpoint, modelName, apiKey;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diy, container, false);

        dishCountRadioGroup = view.findViewById(R.id.dishCountRadioGroup);
        spicinessRadioGroup = view.findViewById(R.id.spicinessRadioGroup);
        meatPork = view.findViewById(R.id.meatPork);
        meatBeef = view.findViewById(R.id.meatBeef);
        meatChicken = view.findViewById(R.id.meatChicken);
        meatFish = view.findViewById(R.id.meatFish);
        meatLamb = view.findViewById(R.id.meatLamb);
        meatDuck = view.findViewById(R.id.meatDuck);
        meatShrimp = view.findViewById(R.id.meatShrimp);
        meatOther = view.findViewById(R.id.meatOther);
        vegetableCabbage = view.findViewById(R.id.vegetableCabbage);
        vegetableWaterSpinach = view.findViewById(R.id.vegetableWaterSpinach);
        vegetableCarrot = view.findViewById(R.id.vegetableCarrot);
        vegetableSpinach = view.findViewById(R.id.vegetableSpinach);
        vegetableMushroom = view.findViewById(R.id.vegetableMushroom);
        vegetableEnoki = view.findViewById(R.id.vegetableEnoki);
        vegetableBeanSprout = view.findViewById(R.id.vegetableBeanSprout);
        vegetableOther = view.findViewById(R.id.vegetableOther);
        restrictionOnion = view.findViewById(R.id.restrictionOnion);
        restrictionGinger = view.findViewById(R.id.restrictionGinger);
        restrictionGarlic = view.findViewById(R.id.restrictionGarlic);
        restrictionCilantro = view.findViewById(R.id.restrictionCilantro);
        restrictionPepper = view.findViewById(R.id.restrictionPepper);
        resultTextView = view.findViewById(R.id.resultTextView);
        formLayout = view.findViewById(R.id.formLayout);
        resultLayout = view.findViewById(R.id.resultLayout);
        progressBar = view.findViewById(R.id.progressBar); // Initialize progressBar
        Button askButton = view.findViewById(R.id.askButton);
        TextView closeButton = view.findViewById(R.id.closeButton);
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        modelEndpoint = sharedPreferences.getString("modelEndpoint", "");
        modelName = sharedPreferences.getString("modelName", "");
        apiKey = sharedPreferences.getString("apikey", "");
        progressBar.setVisibility(View.GONE);
        //closeButton.setText("×");
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = getFormInput();
                progressBar.setVisibility(View.VISIBLE);
                callAiApiAsync(input);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLayout.setVisibility(View.GONE);
                formLayout.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private String getFormInput() {
        StringBuilder inputBuilder = new StringBuilder();

        // Get selected dish count
        int selectedDishCountId = dishCountRadioGroup.getCheckedRadioButtonId();
        if (selectedDishCountId != -1) {
            RadioButton selectedDishCount = getView().findViewById(selectedDishCountId);
            inputBuilder.append("想吃的菜数: ").append(selectedDishCount.getText().toString()).append("\n");
        }

        // Get selected spiciness level
        int selectedSpicinessId = spicinessRadioGroup.getCheckedRadioButtonId();
        if (selectedSpicinessId != -1) {
            RadioButton selectedSpiciness = getView().findViewById(selectedSpicinessId);
            inputBuilder.append("想吃的辣度: ").append(selectedSpiciness.getText().toString()).append("\n");
        }

        // Get selected meats
        StringBuilder meatBuilder = new StringBuilder();
        if (meatPork.isChecked()) meatBuilder.append("猪肉, ");
        if (meatBeef.isChecked()) meatBuilder.append("牛肉, ");
        if (meatChicken.isChecked()) meatBuilder.append("鸡肉, ");
        if (meatFish.isChecked()) meatBuilder.append("鱼肉, ");
        if (meatLamb.isChecked()) meatBuilder.append("羊肉, ");
        if (meatDuck.isChecked()) meatBuilder.append("鸭肉, ");
        if (meatShrimp.isChecked()) meatBuilder.append("虾肉, ");
        if (meatOther.isChecked()) meatBuilder.append("随机的肉, ");

        if (meatBuilder.length() > 0) {
            inputBuilder.append("想吃的肉:【 ");
            inputBuilder.append(meatBuilder.toString());
            inputBuilder.append("】\n");
        }

        // Get selected vegetables
        StringBuilder vegBuilder = new StringBuilder();
        if (vegetableCabbage.isChecked()) vegBuilder.append("白菜, ");
        if (vegetableWaterSpinach.isChecked()) vegBuilder.append("空心菜, ");
        if (vegetableCarrot.isChecked()) vegBuilder.append("胡萝卜, ");
        if (vegetableSpinach.isChecked()) vegBuilder.append("菠菜, ");
        if (vegetableMushroom.isChecked()) vegBuilder.append("平菇, ");
        if (vegetableEnoki.isChecked()) vegBuilder.append("金针菇, ");
        if (vegetableBeanSprout.isChecked()) vegBuilder.append("豆芽, ");
        if (vegetableOther.isChecked()) vegBuilder.append("随机的蔬菜, ");

        if (vegBuilder.length() > 0) {
            inputBuilder.append("想吃的蔬菜:【 ");
            inputBuilder.append(vegBuilder.toString());
            inputBuilder.append("】\n");
        }

        // Get selected restrictions
        StringBuilder banBuilder = new StringBuilder();
        if (restrictionOnion.isChecked()) banBuilder.append("葱, ");
        if (restrictionGinger.isChecked()) banBuilder.append("姜, ");
        if (restrictionGarlic.isChecked()) banBuilder.append("蒜, ");
        if (restrictionCilantro.isChecked()) banBuilder.append("香菜, ");
        if (restrictionPepper.isChecked()) banBuilder.append("花椒, ");
        if (banBuilder.length() > 0) {
            inputBuilder.append("忌口的:【 ");
            inputBuilder.append(banBuilder.toString());
            inputBuilder.append("】\n");
        }
        return inputBuilder.toString();
    }

    private void callAiApiAsync(final String input) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return callAiApi(input);
            }

            @Override
            protected void onPostExecute(String result) {
                resultTextView.setText(result);
                formLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    private String callAiApi(String userInput) {
        String API_URL = modelEndpoint.isEmpty()?"https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions":modelEndpoint;
        String API_KEY = apiKey;
        String MODEL_NAME = modelName.isEmpty()?"qwen-plus":modelName;
        try {
            // Build request body
            Message userMessage = new Message("user", userInput);
            Message systemMessage = new Message("system", "你是一个资深美食家，需要根据用户的输入(如吃几道菜，吃辣能力，想吃哪些肉，蔬菜以及忌口)推荐他吃什么,先简单列出原材料，然后介绍你推荐的菜,菜品数量必须满足要求，菜品名不要带上辣度，做法能体现辣度要求就行，尽可能考虑到用户的所有需求。注意你的回答是直接显示给用户的，所以不要输出除菜品外的其他信息");
            RequestBody requestBody = new RequestBody(MODEL_NAME, new Message[]{systemMessage, userMessage});

            // Convert request body to JSON string
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(requestBody);

            // Create URL object and open connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Authorization","Bearer " +  API_KEY);
            connection.setDoOutput(true);

            // Write request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read successful response body
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    // Parse response and extract reply text (assuming response contains a field named 'reply')
                    return parseResponseForReply(responseBuilder.toString());
                }
            } else {
                // Handle error response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorBuilder = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorBuilder.append(errorLine.trim());
                    }
                    return "Error calling API: " + errorBuilder.toString();
                }
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String parseResponseForReply(String jsonResponse) {
        try {
            // Use Gson to parse JSON response
            JsonElement jsonElement = JsonParser.parseString(jsonResponse);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Get choices array's first object
            JsonElement choicesElement = jsonObject.getAsJsonArray("choices").get(0);
            JsonObject choiceObject = choicesElement.getAsJsonObject();

            // Get message object from choice object
            JsonObject messageObject = choiceObject.getAsJsonObject("message");

            // Extract content field as reply text
            return messageObject.get("content").getAsString();
        } catch (Exception e) {
            // If parsing fails, return error message
            return "Error parsing API response: " + e.getMessage();
        }
    }

    // Request body class definition
    static class RequestBody {
        String model;
        Message[] messages;

        public RequestBody(String model, Message[] messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    // Message class definition
    static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}