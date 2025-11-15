package com.jfsantos.calculadorainteligente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.button.MaterialButton;

import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;
    private static final String KEY_CALC_EXPRESSION = "key_calc_expression";
    private static final String KEY_CALC_CURRENT = "key_calc_current";
    private static final String KEY_CALC_START_NEW = "key_calc_start_new";
    private static final String KEY_DISPLAY_EXPRESSION = "key_display_expression";
    private static final String KEY_DISPLAY_RESULT = "key_display_result";
    private static final String KEY_ADVANCED_EXPANDED = "key_advanced_expanded";
    private static final String CONST_PI = "3.141592653589793";
    private static final String CONST_E = "2.718281828459045";
    private static final String CONST_RAD = "0.017453292519943295";
    private static final long BUTTON_SPEECH_COOLDOWN_MS = 500L;

    private TextView tvResult;
    private TextView tvExpression;
    private MaterialButton btnVoice;
    private MaterialButton btnToggleAdvanced;
    private MaterialButton btnOpenSettings;
    private View advancedContainer;
    private Calculator calculator;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private boolean advancedExpanded = false;
    private AdView mAdView;
    private TextToSpeech textToSpeech;
    private boolean isTextToSpeechReady = false;
    private String lastSpokenIntermediate = "";
    private final Handler speechHandler = new Handler(Looper.getMainLooper());
    private String scheduledIntermediateResult = "";
    private long lastButtonFeedbackTimestamp = 0L;
    private boolean voiceFeedbackEnabled = false;
    private boolean showAdvancedControls = false;
    private final Runnable speakIntermediateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isTextToSpeechReady) {
                return;
            }
            if (scheduledIntermediateResult == null || scheduledIntermediateResult.isEmpty()) {
                return;
            }
            speakPartialResult(scheduledIntermediateResult);
            scheduledIntermediateResult = "";
        }
    };

    private void setVoiceListeningState(boolean listening) {
        isListening = listening;
        if (btnVoice == null) {
            return;
        }
        btnVoice.setEnabled(!listening);
        updateVoiceButtonVisualState();
    }

    private void updateVoiceButtonVisualState() {
        if (btnVoice == null) {
            return;
        }
        @ColorInt int background = ContextCompat.getColor(this,
                isListening ? R.color.btn_voice_active : R.color.btn_voice);
        @ColorInt int textColor = ContextCompat.getColor(this,
                isListening ? R.color.btn_voice_active_text : R.color.btn_voice_text);
        @ColorInt int strokeColor = ContextCompat.getColor(this,
                isListening ? R.color.btn_voice_active_outline : R.color.btn_voice_outline);

        btnVoice.setBackgroundTintList(ColorStateList.valueOf(background));
        btnVoice.setStrokeColor(ColorStateList.valueOf(strokeColor));
        btnVoice.setTextColor(textColor);
    btnVoice.setText("");
    btnVoice.setContentDescription(isListening
        ? getString(R.string.btn_voice_listening)
        : getString(R.string.btn_voice));
    btnVoice.setIconSize(getResources().getDimensionPixelSize(R.dimen.voice_button_icon_size));
    btnVoice.setIconPadding(0);
        btnVoice.setIconResource(isListening ? R.drawable.ic_mic_active : R.drawable.ic_mic_idle);
        btnVoice.setIconTint(ColorStateList.valueOf(textColor));
    }

    private void releaseSpeechRecognizer() {
        if (speechRecognizer == null) {
            return;
        }

        try {
            speechRecognizer.stopListening();
        } catch (IllegalStateException ignored) {
            // Recognizer not yet started; safe to ignore.
        }

        speechRecognizer.cancel();
        speechRecognizer.destroy();
        speechRecognizer = null;
        setVoiceListeningState(false);
    }

    private void updateAdvancedVisibility() {
        // No landscape, sempre mostra os botões avançados
        boolean isLandscape = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
        
        if (!showAdvancedControls) {
            if (advancedContainer != null) {
                advancedContainer.setVisibility(View.GONE);
            }
            if (btnToggleAdvanced != null) {
                btnToggleAdvanced.setVisibility(View.GONE);
            }
            return;
        }

        if (advancedContainer != null) {
            // No landscape, sempre visível; no portrait, depende do toggle
            advancedContainer.setVisibility((isLandscape || advancedExpanded) ? View.VISIBLE : View.GONE);
        }

        if (btnToggleAdvanced != null) {
            // Toggle só aparece no portrait
            btnToggleAdvanced.setVisibility(isLandscape ? View.GONE : View.VISIBLE);
            if (!isLandscape) {
                btnToggleAdvanced.setText(advancedExpanded
                        ? getString(R.string.advanced_toggle_hide)
                        : getString(R.string.advanced_toggle_show));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {});

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Initialize views
        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);
        btnVoice = findViewById(R.id.btnVoice);
        btnToggleAdvanced = findViewById(R.id.btnToggleAdvanced);
    btnOpenSettings = findViewById(R.id.btnOpenSettings);
        advancedContainer = findViewById(R.id.advancedContainer);
        calculator = new Calculator();

        setVoiceListeningState(false);

        showAdvancedControls = getResources().getBoolean(R.bool.show_advanced_controls);
        advancedExpanded = showAdvancedControls;

        if (btnToggleAdvanced != null) {
            if (showAdvancedControls) {
                btnToggleAdvanced.setOnClickListener(v -> {
                    advancedExpanded = !advancedExpanded;
                    updateAdvancedVisibility();
                });
            } else {
                btnToggleAdvanced.setVisibility(View.GONE);
            }
        }

        if (btnOpenSettings != null) {
            btnOpenSettings.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        updateAdvancedVisibility();

        // Initialize speech recognizer
        initializeSpeechRecognizer();
        initializeTextToSpeech();

        // Setup button listeners
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            updateDisplay();
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int languageStatus = textToSpeech.setLanguage(new Locale("pt", "BR"));
                isTextToSpeechReady = languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                        languageStatus != TextToSpeech.LANG_NOT_SUPPORTED;
                textToSpeech.setSpeechRate(1.0f);
                if (!isTextToSpeechReady) {
                    Toast.makeText(this, R.string.voice_feedback_unavailable, Toast.LENGTH_SHORT).show();
                }
            } else {
                isTextToSpeechReady = false;
                Toast.makeText(this, R.string.voice_feedback_unavailable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speakButtonFeedback(String rawToken) {
        if (!voiceFeedbackEnabled || !isTextToSpeechReady || rawToken == null) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        if (now - lastButtonFeedbackTimestamp < BUTTON_SPEECH_COOLDOWN_MS) {
            return;
        }
        String message = describeToken(rawToken);
        if (message.isEmpty()) {
            return;
        }
        lastButtonFeedbackTimestamp = now;
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null,
                "btn_" + System.currentTimeMillis());
    }

    private void speakResult(String result) {
        if (!voiceFeedbackEnabled) {
            return;
        }
        speakResultInternal(result, TextToSpeech.QUEUE_ADD, "result_");
    }

    private void speakPartialResult(String result) {
        if (!voiceFeedbackEnabled) {
            return;
        }
        speakResultInternal(result, TextToSpeech.QUEUE_ADD, "partial_");
    }

    private void speakResultInternal(String result, int queueMode, String utterancePrefix) {
        if (!voiceFeedbackEnabled || !isTextToSpeechReady || result == null || result.isEmpty()) {
            return;
        }
        String normalized = result
                .replace("-", " menos ")
                .replace(",", " vírgula ")
                .replace("%", " por cento");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return;
        }
        String message = getString(R.string.voice_result_prefix, normalized).trim();
        if (message.isEmpty()) {
            return;
        }
        textToSpeech.speak(message, queueMode, null,
                utterancePrefix + System.currentTimeMillis());
        if (queueMode == TextToSpeech.QUEUE_ADD) {
            lastSpokenIntermediate = result;
        }
    }

    private String describeToken(String rawToken) {
        if (rawToken == null) {
            return "";
        }
        switch (rawToken) {
            case "+":
                return "mais";
            case "−":
            case "-":
                return "menos";
            case "×":
            case "*":
                return "vezes";
            case "÷":
            case "/":
                return "dividido";
            case "^":
                return "elevado";
            case "%":
                return "por cento";
            case ".":
            case ",":
                return "vírgula";
            case "√":
                return "raiz quadrada";
            case "(":
                return "abre parêntese";
            case ")":
                return "fecha parêntese";
            case "sin":
                return "seno";
            case "cos":
                return "cosseno";
            case "tan":
                return "tangente";
            case "log":
                return "logaritmo";
            case "ln":
                return "logaritmo natural";
            case "π":
            case "pi":
                return "pi";
            case "e":
                return "constante e";
            case "RAD":
            case "rad":
                return "radiano";
            case "!":
                return "fatorial";
            case "C":
                return "limpar";
            case "DEL":
                return "apagar";
            case "=":
                return "igual";
            default:
                return rawToken;
        }
    }

    private void maybeSpeakIntermediateResult(String expression, String partialResult) {
        if (!voiceFeedbackEnabled || !isTextToSpeechReady) {
            scheduledIntermediateResult = "";
            speechHandler.removeCallbacks(speakIntermediateRunnable);
            return;
        }
        if (expression == null || expression.trim().isEmpty()) {
            lastSpokenIntermediate = "";
            scheduledIntermediateResult = "";
            speechHandler.removeCallbacks(speakIntermediateRunnable);
            return;
        }
        if (partialResult == null || partialResult.isEmpty()
                || "Erro".equalsIgnoreCase(partialResult) || "-".equals(partialResult)) {
            scheduledIntermediateResult = "";
            speechHandler.removeCallbacks(speakIntermediateRunnable);
            return;
        }
        if (partialResult.equals(lastSpokenIntermediate) || partialResult.equals(scheduledIntermediateResult)) {
            return;
        }
        scheduledIntermediateResult = partialResult;
        speechHandler.removeCallbacks(speakIntermediateRunnable);
        speechHandler.postDelayed(speakIntermediateRunnable, 60);
    }

    private void initializeSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = null;
            setVoiceListeningState(false);
            return;
        }

        releaseSpeechRecognizer();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    setVoiceListeningState(true);
                }

                @Override
                public void onBeginningOfSpeech() {
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    setVoiceListeningState(false);
                }

                @Override
                public void onError(int error) {
                    setVoiceListeningState(false);
                    String message;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            message = "Erro de áudio";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            message = "Erro do cliente";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            message = getString(R.string.permission_required);
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            message = "Erro de rede";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            message = "Nenhuma correspondência encontrada";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            message = "Reconhecedor ocupado";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            message = "Erro do servidor";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            message = "Tempo esgotado";
                            break;
                        default:
                            message = getString(R.string.voice_error);
                            break;
                    }
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> startListening(), 400);
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    setVoiceListeningState(false);
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String best = null;
                        int bestScore = -1;
                        for (String m : matches) {
                            if (m == null) continue;
                            String t = m.trim();
                            if (t.isEmpty()) continue;
                            String processed = VoiceCommandProcessor.processVoiceCommand(t);
                            int score = 0;
                            if (VoiceCommandProcessor.containsOperator(processed)) score += 2;
                            score += Math.min(processed.length(), 40);
                            if (processed.endsWith("=")) score += 3;
                            if (score > bestScore) { bestScore = score; best = t; }
                        }
                        if (best != null) {
                            processVoiceInput(best);
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    // Preview text removed per UX request; no intermediate rendering needed.
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                }
        });
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = v -> {
            MaterialButton button = (MaterialButton) v;
            calculator.appendDigit(button.getText().toString());
            updateDisplay();
            speakButtonFeedback(button.getText().toString());
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Decimal point
        findViewById(R.id.btnDecimal).setOnClickListener(v -> {
            calculator.appendDecimal();
            updateDisplay();
            speakButtonFeedback(".");
        });
    }

    private void setupOperatorButtons() {
        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            calculator.appendOperator("+");
            updateDisplay();
            speakButtonFeedback("+");
        });

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            calculator.appendOperator("−");
            updateDisplay();
            speakButtonFeedback("−");
        });

        findViewById(R.id.btnMultiply).setOnClickListener(v -> {
            calculator.appendOperator("×");
            updateDisplay();
            speakButtonFeedback("×");
        });

        findViewById(R.id.btnDivide).setOnClickListener(v -> {
            calculator.appendOperator("÷");
            updateDisplay();
            speakButtonFeedback("÷");
        });

        View power = findViewById(R.id.btnPower);
        if (power != null) {
            power.setOnClickListener(v -> {
                calculator.appendOperator("^");
                updateDisplay();
                speakButtonFeedback("^");
            });
        }

        // Parênteses
        findViewById(R.id.btnParenOpen).setOnClickListener(v -> {
            calculator.appendParenthesis("(");
            updateDisplay();
            speakButtonFeedback("(");
        });
        findViewById(R.id.btnParenClose).setOnClickListener(v -> {
            calculator.appendParenthesis(")");
            updateDisplay();
            speakButtonFeedback(")");
        });
    }

    private void setupFunctionButtons() {
        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            try {
                android.util.Log.d("MainActivity", "Equals clicked - expression: " + calculator.getFullExpression());
                String result = calculator.calculate();
                android.util.Log.d("MainActivity", "Result: " + result);
                // Show the completed expression and result, then reset state keeping result as current number
                tvExpression.setText(calculator.getFullExpression() + " =");
                tvResult.setText(formatNumber(result));
                calculator.clear();
                calculator.setCurrentNumber(result);
                speakButtonFeedback("=");
                speakResult(result);
            } catch (ArithmeticException e) {
                android.util.Log.e("MainActivity", "Error calculating: " + e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                speakButtonFeedback(e.getMessage());
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Unexpected error: " + e.getMessage(), e);
                Toast.makeText(this, "Erro ao calcular", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            calculator.clear();
            updateDisplay();
            speakButtonFeedback("C");
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            calculator.delete();
            updateDisplay();
            speakButtonFeedback("DEL");
        });

        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            String result = calculator.calculatePercent();
            tvResult.setText(formatNumber(result));
            calculator.setCurrentNumber(result);
            speakButtonFeedback("%");
        });

        findViewById(R.id.btnVoice).setOnClickListener(v -> {
            prepareForVoiceInput();
            startVoiceRecognition();
            speakButtonFeedback("microfone");
        });

        View sqrt = findViewById(R.id.btnSqrt);
        if (sqrt != null) {
            sqrt.setOnClickListener(v -> {
                String inner = calculator.getCurrentDisplay();
                calculator.appendFunction("√", inner);
                updateDisplay();
                speakButtonFeedback("√");
            });
        }

        View sin = findViewById(R.id.btnSin);
        if (sin != null) {
            sin.setOnClickListener(v -> {
                String inner = calculator.getCurrentDisplay();
                android.util.Log.d("MainActivity", "Sin clicked - inner: " + inner);
                calculator.appendFunction("sin", inner);
                android.util.Log.d("MainActivity", "After appendFunction - expression: " + calculator.getExpression());
                updateDisplay();
                speakButtonFeedback("sin");
            });
        }

        View cos = findViewById(R.id.btnCos);
        if (cos != null) {
            cos.setOnClickListener(v -> {
                String inner = calculator.getCurrentDisplay();
                android.util.Log.d("MainActivity", "Cos clicked - inner: " + inner);
                calculator.appendFunction("cos", inner);
                android.util.Log.d("MainActivity", "After appendFunction - expression: " + calculator.getExpression());
                updateDisplay();
                speakButtonFeedback("cos");
            });
        }

        View tan = findViewById(R.id.btnTan);
        if (tan != null) {
            tan.setOnClickListener(v -> {
                String inner = calculator.getCurrentDisplay();
                android.util.Log.d("MainActivity", "Tan clicked - inner: " + inner);
                calculator.appendFunction("tan", inner);
                android.util.Log.d("MainActivity", "After appendFunction - expression: " + calculator.getExpression());
                updateDisplay();
                speakButtonFeedback("tan");
            });
        }

        View log = findViewById(R.id.btnLog);
        if (log != null) {
            log.setOnClickListener(v -> {
                String inner = calculator.getCurrentDisplay();
                android.util.Log.d("MainActivity", "Log clicked - inner: " + inner);
                calculator.appendFunction("log", inner);
                android.util.Log.d("MainActivity", "After appendFunction - expression: " + calculator.getExpression());
                updateDisplay();
                speakButtonFeedback("log");
            });
        }

        View pi = findViewById(R.id.btnPi);
        if (pi != null) {
            pi.setOnClickListener(v -> {
                calculator.appendConstant(CONST_PI);
                updateDisplay();
                speakButtonFeedback("π");
            });
        }

        View eButton = findViewById(R.id.btnE);
        if (eButton != null) {
            eButton.setOnClickListener(v -> {
                calculator.appendConstant(CONST_E);
                updateDisplay();
                speakButtonFeedback("e");
            });
        }

        View rad = findViewById(R.id.btnRad);
        if (rad != null) {
            rad.setOnClickListener(v -> {
                calculator.appendConstant(CONST_RAD);
                updateDisplay();
                speakButtonFeedback("RAD");
            });
        }

        View factorial = findViewById(R.id.btnFactorial);
        if (factorial != null) {
            factorial.setOnClickListener(v -> {
                calculator.appendFactorial();
                updateDisplay();
                speakButtonFeedback("!");
            });
        }
    }

    private void restoreState(@NonNull Bundle state) {
        String calcExpression = state.getString(KEY_CALC_EXPRESSION, "");
        String calcCurrent = state.getString(KEY_CALC_CURRENT, "");
        boolean shouldStartNewNumber = state.getBoolean(KEY_CALC_START_NEW, true);

        if (calculator != null) {
            calculator.restoreState(calcExpression, calcCurrent, shouldStartNewNumber);
        }

        advancedExpanded = showAdvancedControls && state.getBoolean(KEY_ADVANCED_EXPANDED, showAdvancedControls);
        updateAdvancedVisibility();
        updateDisplay();

        if (tvExpression != null) {
            tvExpression.setText(state.getString(KEY_DISPLAY_EXPRESSION, ""));
        }
        if (tvResult != null) {
            tvResult.setText(state.getString(KEY_DISPLAY_RESULT, ""));
        }
    }

    private void prepareForVoiceInput() {
        calculator.clear();
        if (tvExpression != null) {
            tvExpression.setText("");
        }
        if (tvResult != null) {
            tvResult.setText("");
        }
    }

    private void startVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            startListening();
        }
    }

    private void startListening() {
        if (speechRecognizer == null) {
            Toast.makeText(this, getString(R.string.voice_not_available), Toast.LENGTH_SHORT).show();
            setVoiceListeningState(false);
            return;
        }

        if (!isListening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt));
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1800L);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 900L);

            Toast.makeText(this, getString(R.string.voice_prompt), Toast.LENGTH_SHORT).show();
            setVoiceListeningState(true);
            speechRecognizer.startListening(intent);
        }
    }

    private void processVoiceInput(String voiceText) {
        voiceFeedbackEnabled = true;
        String processed = VoiceCommandProcessor.processVoiceCommand(voiceText);

        try {
            if (processed.isEmpty()) {
                // Silently ignore unrecognized commands
                return;
            }

            if (processed.equals("CLEAR")) {
                calculator.clear();
                updateDisplay();
                return;
            }

            if (processed.equals("DELETE")) {
                calculator.delete();
                updateDisplay();
                return;
            }

            boolean calculate = processed.endsWith("=");
            if (calculate) {
                processed = VoiceCommandProcessor.cleanExpression(processed);
            }

            // Process the expression with better handling of numbers with commas
            String[] parts = processed.split("\\s+");
            for (int idx = 0; idx < parts.length; idx++) {
                String token = parts[idx];
                if (token == null || token.isEmpty()) {
                    continue;
                }

                if (token.matches("\\d+(,\\d+)?")) {
                    for (char digit : token.toCharArray()) {
                        if (digit == ',') {
                            calculator.appendDecimal();
                        } else {
                            calculator.appendDigit(String.valueOf(digit));
                        }
                    }
                    continue;
                }

                switch (token) {
                    case "(":
                        calculator.appendParenthesis("(");
                        continue;
                    case ")":
                        calculator.appendParenthesis(")");
                        continue;
                    case "+":
                    case "−":
                    case "×":
                    case "÷":
                    case "^":
                        calculator.appendOperator(token);
                        continue;
                    case "%":
                        String percentValue = calculator.calculatePercent();
                        calculator.setCurrentNumber(percentValue);
                        continue;
                    default:
                        break;
                }

                if ("π".equals(token) || "pi".equalsIgnoreCase(token)) {
                    calculator.appendConstant(CONST_PI);
                    continue;
                }

                if ("e".equalsIgnoreCase(token) || "euler".equalsIgnoreCase(token)) {
                    calculator.appendConstant(CONST_E);
                    continue;
                }

                if ("RAD".equalsIgnoreCase(token) || "radiano".equalsIgnoreCase(token)) {
                    calculator.appendConstant(CONST_RAD);
                    continue;
                }

                if (token.equals("√") || token.equals("sin") || token.equals("cos") || token.equals("tan") || token.equals("log") || token.equals("ln")) {
                    String argument = "";
                    if (idx + 1 < parts.length) {
                        argument = parts[idx + 1];
                        idx++;
                    }
                    if (argument != null && !argument.isEmpty()) {
                        String functionName = token.equals("√") ? "√" : token;
                        calculator.appendFunction(functionName, argument);
                    }
                    continue;
                }

                if (token.equals("!")) {
                    calculator.appendFactorial();
                }
            }

            if (calculate) {
                try {
                    String result = calculator.calculate();
                    tvExpression.setText(calculator.getFullExpression() + " =");
                    tvResult.setText(formatNumber(result));
                    calculator.clear();
                    calculator.setCurrentNumber(result);
                    speakResult(result);
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao calcular: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    speakButtonFeedback(e.getMessage());
                }
            } else {
                updateDisplay();
                if (calculator.hasCompleteExpression()) {
                    String partialResult = calculator.evaluatePartial();
                    if (partialResult != null && !partialResult.isEmpty()
                            && !"Erro".equalsIgnoreCase(partialResult)) {
                        speakResult(partialResult);
                    }
                }
            }
        } finally {
            voiceFeedbackEnabled = false;
            scheduledIntermediateResult = "";
            speechHandler.removeCallbacks(speakIntermediateRunnable);
        }
    }

    private void updateDisplay() {
        // Show the expression being built and the incremental evaluated result
        String expression = calculator.getFullExpression();
        tvExpression.setText(expression);

        if (calculator.hasCompleteExpression()) {
            String partialResult = calculator.evaluatePartial();
            tvResult.setText(formatNumber(partialResult));
            maybeSpeakIntermediateResult(expression, partialResult);
        } else {
            tvResult.setText(expression.isEmpty() ? getString(R.string.display_zero) : "");
            lastSpokenIntermediate = "";
        }
    }

    private String formatNumber(String number) {
        if (number == null || number.isEmpty() || number.equals("Erro") || number.equals("-")) {
            return number;
        }

        boolean isNegative = number.startsWith("-");
        String positiveNumber = isNegative ? number.substring(1) : number;

        String integerPart;
        String fractionalPart = null;

        if (positiveNumber.contains(",")) {
            String[] parts = positiveNumber.split(",", 2);
            integerPart = parts[0];
            if (parts.length > 1) {
                fractionalPart = parts[1];
            }
        } else {
            integerPart = positiveNumber;
        }

        if (integerPart.isEmpty()) {
            integerPart = "0";
        }

        try {
            BigDecimal intValue = new BigDecimal(integerPart);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
            DecimalFormat df = new DecimalFormat("#,##0", symbols);
            String formattedIntegerPart = df.format(intValue);

            String result;
            if (fractionalPart != null) {
                result = formattedIntegerPart + "," + fractionalPart;
            } else if (positiveNumber.endsWith(",")) {
                result = formattedIntegerPart + ",";
            } else {
                result = formattedIntegerPart;
            }

            return isNegative ? "-" + result : result;

        } catch (NumberFormatException e) {
            return number;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (calculator != null) {
            outState.putString(KEY_CALC_EXPRESSION, calculator.getExpression());
            outState.putString(KEY_CALC_CURRENT, calculator.getCurrentNumberRaw());
            outState.putBoolean(KEY_CALC_START_NEW, calculator.isReadyForNewNumber());
        }
        if (tvExpression != null) {
            outState.putString(KEY_DISPLAY_EXPRESSION, tvExpression.getText().toString());
        }
        if (tvResult != null) {
            outState.putString(KEY_DISPLAY_RESULT, tvResult.getText().toString());
        }
        outState.putBoolean(KEY_ADVANCED_EXPANDED, showAdvancedControls && advancedExpanded);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseSpeechRecognizer();
        speechHandler.removeCallbacksAndMessages(null);
        scheduledIntermediateResult = "";
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            isTextToSpeechReady = false;
        }
    }
}
