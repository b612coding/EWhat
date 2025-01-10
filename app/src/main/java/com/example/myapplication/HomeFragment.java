package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private AppDatabase db;
    private TextView foodNameTextView;
    private TextView cookMethodTextView;
    private TextView greetingTextView;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = Room.databaseBuilder(requireContext(),
                AppDatabase.class, "food.db").allowMainThreadQueries().build();

        foodNameTextView = view.findViewById(R.id.foodNameTextView);
        cookMethodTextView = view.findViewById(R.id.cookMethodTextView);
        greetingTextView = view.findViewById(R.id.greetingTextView);
        Button randomButton = view.findViewById(R.id.randomButton);

        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        greetingTextView.setText(getGreetingMessage(username));

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                greetingTextView.setVisibility(View.GONE);
                Food randomFood = db.foodDao().getRandomFood();
                if (randomFood != null) {
                    foodNameTextView.setText(randomFood.name);
                    foodNameTextView.setVisibility(View.VISIBLE);
                    if (randomFood.cook_method != null && !randomFood.cook_method.isEmpty()) {
                        cookMethodTextView.setText("【食材】\n    "+randomFood.material+"\n【标签】\n    "+randomFood.tag+"\n【做法】\n    "+randomFood.cook_method);
                        cookMethodTextView.setVisibility(View.VISIBLE);
                    } else {
                        cookMethodTextView.setText("");
                        cookMethodTextView.setVisibility(View.GONE);
                    }
                } else {
                    foodNameTextView.setText("No items found");
                    foodNameTextView.setVisibility(View.VISIBLE);
                    cookMethodTextView.setText("");
                    cookMethodTextView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private String getGreetingMessage(String username) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour >= 6 && hour < 10) {
            greeting = "早安！开启活力满满的一天，一杯香醇的咖啡配上面包，是给味蕾的第一声问候。";
        } else if (hour >= 10 && hour < 12) {
            greeting = "上午好！工作间隙，来杯热茶提提神吧，让思路更加清晰，效率加倍！";

        } else if (hour >= 12 && hour < 14) {
            greeting = "午安！忙碌的上午过去了，一份精致的午餐和一杯水果茶，为身体充电，迎接下午的挑战。";
        }
        else if (hour >= 14 && hour < 17) {
            greeting = "下午好！是时候来点不一样的，一杯暖暖的下午茶，配上小点心，给慵懒的午后增添一丝甜蜜。";
        }
        else if(hour >= 17 && hour < 19) {
            greeting = "傍晚好！一天的工作即将结束，一杯清香的花茶，能让你放松心情，享受这宁静的时刻。";
        }
        else if(hour >= 19 && hour < 23) {
            greeting = "晚上好！晚餐后的一杯草本茶，既健康又助眠，为美好的一天画上温馨的句号。";
        }
        else {
            greeting = "晚安，夜深人静，世界仿佛都沉睡了。如果此时还未眠，不妨来杯温热的牛奶，让心灵在这宁静时刻稍作歇息，愿你快些进入甜美的梦乡。";
        }
        if (!username.isEmpty()) {
            greeting = username + "，" + greeting;
        }
        greeting="    "+greeting;
        return greeting;
    }
}