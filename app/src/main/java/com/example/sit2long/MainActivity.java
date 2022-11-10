package com.example.sit2long;

import static android.os.Build.VERSION_CODES;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    boolean resetted = false;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    private TextView counterDownHours;
    private TextView counterDownMin;
    private TextView timerStatus;
    private Button counterDownBtn;
    private Button setTimerBtn;
    private Button resetTimerBtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliSeconds = 0;
    private boolean isTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        counterDownHours = findViewById(R.id.hours);
        counterDownMin = findViewById(R.id.min);
        counterDownBtn = findViewById(R.id.button);
        timerStatus = findViewById(R.id.timer_status);
        setTimerBtn = findViewById(R.id.set_timer_btn);
        resetTimerBtn = findViewById(R.id.reset);
        setTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startingHours = Integer.parseInt(counterDownHours.getText().toString());
                int StartingMinutes = Integer.parseInt(counterDownMin.getText().toString());

                long totalTime = (startingHours * 3600000) + (StartingMinutes * 60000);
                timeLeftInMilliSeconds = totalTime;

                int hours = (int) timeLeftInMilliSeconds / 3600000;
                int minutes = (int) (timeLeftInMilliSeconds % 3600000) / 60000;
                int seconds = (int) (timeLeftInMilliSeconds % 60000) / 1000;
                String timeLeftTxt = "";
                if (hours < 10)
                    timeLeftTxt += "0";

                timeLeftTxt += hours;
                timeLeftTxt += ":";

                if (minutes < 10)
                    timeLeftTxt += "0";
                timeLeftTxt += minutes;
                timeLeftTxt += ":";
                if (seconds < 10)
                    timeLeftTxt += "0";
                timeLeftTxt += seconds;
                System.out.println("=====================" + timeLeftTxt);
                timerStatus.setText(timeLeftTxt);
            }
        });

        counterDownBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startStop();
            }
        });
        resetTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterDownHours.setText("0");
                counterDownMin.setText("0");
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                timeLeftInMilliSeconds = 0;
                timerStatus.setText("00:00:00");
                resetted = true;
                counterDownBtn.setText("Start");
            }
        });

    }

    public void startStop() {
        if (isTimerRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliSeconds = millisUntilFinished;
                timerStatus.setText(Long.toString(timeLeftInMilliSeconds));
                updateTimer(timeLeftInMilliSeconds);
            }

            @Override
            public void onFinish() {
                sendNotification();
                counterDownBtn.setText("Start");
            }
        }.start();
        counterDownBtn.setText("Pause");
        isTimerRunning = true;

    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            counterDownBtn.setText("Start");
            isTimerRunning = false;
        }
    }

    public void updateTimer(long timeLeft) {
        int hours = (int) timeLeft / 3600000;
        int minutes = (int) (timeLeft % 3600000) / 60000;
        int seconds = (int) (timeLeft % 60000) / 1000;
        String timeLeftTxt = "";
        if (hours < 10)
            timeLeftTxt += "0";

        timeLeftTxt += hours;
        timeLeftTxt += ":";

        if (minutes < 10)
            timeLeftTxt += "0";
        timeLeftTxt += minutes;
        timeLeftTxt += ":";
        if (seconds < 10)
            timeLeftTxt += "0";
        timeLeftTxt += seconds;
        System.out.println("=====================" + timeLeftTxt);
        timerStatus.setText(timeLeftTxt);

    }

    public void sendNotification() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sit2LongChanel",
                    "Sit2Long", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sit2LongChanel")
                    .setSmallIcon(R.drawable.sit)
                    .setContentTitle("Sit2Long")
                    .setContentText("You have sat too long, you need to stand up and walk");
            notification = builder.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
            if (!resetted) {
                notificationManagerCompat.notify(1, notification);
            }
        }
    }
}