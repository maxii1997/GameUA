package com.example.juego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener {

    private TextView instructionTextView, scoreTextView, livesTextView, levelTextView;
    private MediaPlayer musicWin, musicFondo, musicLoss;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int currentLevel = 1;
    private int score = 0;
    private int lives = 3;
    private Handler handler;
    public SharedPreferences sharedPreferences;

    private boolean isGameRunning = false;

    //public Runnable changeInstructionRunnable;
    private GestureDetector gestureDetector, gestos;
    private boolean delay = true;

    private Runnable changeInstructionRunnable = new Runnable() {
        @Override
        public void run() {
            handleInstructionTimeLimit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionTextView = findViewById(R.id.instructionTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        livesTextView = findViewById(R.id.livesTextView);
        levelTextView = findViewById(R.id.levelTextView);

        musicFondo = MediaPlayer.create(this, R.raw.fondo);
        musicWin = MediaPlayer.create(this, R.raw.win);
        musicLoss = MediaPlayer.create(this, R.raw.loss);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        handler = new Handler();

        sharedPreferences = getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);

        gestureDetector = new GestureDetector(this, this);
        gestos = new GestureDetector(this, new ListenerGestos());

        startGame();
    }

    private void startGame() {
        score = 0;
        lives = 3;
        updateScore();
        updateLives();
        musicFondo.start();
        showNewInstruction();
    }

    private void resetGame() {
        currentLevel = 1;
        score = 0;
        lives = 3;
        levelTextView.setText("Level: 1");
        updateScore();
        updateLives();
        showNewInstruction();
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score);
    }

    private void updateLives() {
        livesTextView.setText("Lives: " + lives);
    }

    private void showNewInstruction() {
        String instruction = getRandomInstruction();
        instructionTextView.setText(instruction);
        startInstructionTimer();
    }

    private String getRandomInstruction() {
        // Generate a random instruction based on the current level
        switch (currentLevel) {
            case 1:
                return getRandomInstructionFromList("Tap", "Swipe", "Doble Tap");
            default:
                return getRandomInstructionFromList("Tap", "Swipe", "Doble Tap", "Presión Larga", "Shake");
        }
    }

    private String getRandomInstructionFromList(String... instructions) {
        int randomIndex = new Random().nextInt(instructions.length);
        return instructions[randomIndex];
    }

    //private void startInstructionTimer() {
    //    handler.postDelayed(changeInstructionRunnable, getInstructionTimeLimit());
    //}

    private void startInstructionTimer() {
        handler.removeCallbacks(changeInstructionRunnable); // Elimina cualquier llamada previa del Runnable
        handler.postDelayed(changeInstructionRunnable, getInstructionTimeLimit());
    }

    private void handleInstructionTimeLimit() {
        if (!isGameRunning) {
            return;
        }
        if (lives > 0) {
            lives--;
            updateLives();
            Toast.makeText(this, "Se ha agotado el tiempo límite. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            // Reproducir el sonido de derrota
            musicLoss.start();
            if (lives == 0) {
                gameOver();
            } else {
                showNewInstruction();
            }
        }
    }
    private int getInstructionTimeLimit() {
        switch (currentLevel) {
            case 1:
                return 10000; // 10 segundos para el nivel 1
            default:
                return 7000; // 7 segundos para los demás niveles
        }
    }

    private void checkGesture(String Gesture) {
        handler.removeCallbacks(changeInstructionRunnable);
        String expectedGesture = instructionTextView.getText().toString();
        if (Gesture.equals(expectedGesture)) {
            score++;
            updateScore();
            // Reproducir el sonido de victoria cada vez que se aumente el puntaje
            musicWin.start();
            if (score % 3 == 0) {
                levelCompleted();
            } else {
                showNewInstruction();
            }
        } else {
            if (lives > 0) {
                lives--;
                updateLives();
                Toast.makeText(this, "Acción equivocada. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                // Reproducir el sonido de derrota
                musicLoss.start();
                if (lives == 0) {
                    gameOver();
                } else {
                    showNewInstruction();
                }
            }
        }
    }

    private void levelCompleted() {
        currentLevel++;
        levelTextView.setText("Level: "+currentLevel);

        // Reproducir el sonido de victoria
        musicWin.start();

        Toast.makeText(this, "¡Nivel completado! Pasa al siguiente nivel.", Toast.LENGTH_SHORT).show();
        showNewInstruction();
    }

    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over")
                .setMessage("Has perdido todas tus vidas. Puntaje obtenido: " + score + "\n¿Deseas reiniciar el juego?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void gameOver() {
        Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show();

        // Reproducir el sonido de derrota
        musicLoss.start();

        showRestartDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isGameRunning = true;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGameRunning = false;
        sensorManager.unregisterListener(this);
        if (musicFondo.isPlaying()) {
            musicFondo.pause();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if ((event.values[0] > 8 || event.values[0] < -8) && delay) {
                showNewInstructionWithDelay(3000);
                delay = false;
            }
        }
    }
    private void showNewInstructionWithDelay(long delayMillis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkGesture("Shake");
                delay = true;
            }
        }, delayMillis);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Do nothing
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Do nothing
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Do nothing
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        checkGesture("Presión Larga");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean acertar = e2.getY() != e1.getY();
        if(acertar){
            checkGesture("Swipe");
        }
        return true;
    }

    class ListenerGestos extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            checkGesture("Tap");
            return true;
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            checkGesture("Doble Tap");
            return true;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicFondo.release();
    }

    private int getInstructionCount() {
        return sharedPreferences.getInt("instructionCount", 3);
    }

    private int getInitialSpeed() {
        return sharedPreferences.getInt("initialSpeed", 3);
    }
}