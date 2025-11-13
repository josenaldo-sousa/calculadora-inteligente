package com.jfsantos.calculadorainteligente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

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

    private TextView tvResult;
    private TextView tvExpression;
    private TextView tvVoiceRaw;
    private TextView tvVoiceConverted;
    private Calculator calculator;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private AdView mAdView;

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
        tvVoiceRaw = findViewById(R.id.tvVoiceRaw);
        tvVoiceConverted = findViewById(R.id.tvVoiceConverted);
        calculator = new Calculator();

        // Initialize speech recognizer
        initializeSpeechRecognizer();

        // Setup button listeners
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
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
                    isListening = false;
                }

                @Override
                public void onError(int error) {
                    isListening = false;
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
                }

                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String voiceText = matches.get(0).trim();
                        if (!voiceText.isEmpty()) {
                            processVoiceInput(voiceText);
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> partial = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (partial == null || partial.isEmpty()) return;
                    String partialText = partial.get(0).trim();
                    if (!partialText.isEmpty()) {
                        // The following lines that update the UI with voice text will be removed.
                        // if (tvVoiceRaw != null) tvVoiceRaw.setText(partialText);
                        // String processedPreview = VoiceCommandProcessor.processVoiceCommand(partialText);
                        // if (tvVoiceConverted != null) tvVoiceConverted.setText(VoiceCommandProcessor.toHumanReadable(processedPreview));
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                }
            });
        }
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
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Decimal point
        findViewById(R.id.btnDecimal).setOnClickListener(v -> {
            calculator.appendDecimal();
            updateDisplay();
        });
    }

    private void setupOperatorButtons() {
        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            calculator.appendOperator("+");
            updateDisplay();
        });

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            calculator.appendOperator("−");
            updateDisplay();
        });

        findViewById(R.id.btnMultiply).setOnClickListener(v -> {
            calculator.appendOperator("×");
            updateDisplay();
        });

        findViewById(R.id.btnDivide).setOnClickListener(v -> {
            calculator.appendOperator("÷");
            updateDisplay();
        });

        // Parênteses
        findViewById(R.id.btnParenOpen).setOnClickListener(v -> {
            calculator.appendParenthesis("(");
            updateDisplay();
        });
        findViewById(R.id.btnParenClose).setOnClickListener(v -> {
            calculator.appendParenthesis(")");
            updateDisplay();
        });
    }

    private void setupFunctionButtons() {
        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            try {
                String result = calculator.calculate();
                // Show the completed expression and result, then reset state keeping result as current number
                tvExpression.setText(calculator.getFullExpression() + " =");
                tvResult.setText(formatNumber(result));
                calculator.clear();
                calculator.setCurrentNumber(result);
            } catch (ArithmeticException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            calculator.clear();
            updateDisplay();
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            calculator.delete();
            updateDisplay();
        });

        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            String result = calculator.calculatePercent();
            tvResult.setText(formatNumber(result));
            calculator.setCurrentNumber(result);
        });

        findViewById(R.id.btnVoice).setOnClickListener(v -> {
            startVoiceRecognition();
        });
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
            return;
        }

        if (!isListening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt));
                // Prefer a single best alternative from the recognizer to avoid
                // ambiguities caused by multiple alternatives.
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                // Tweak silence timeouts to improve recognition stability (milliseconds)
                // These extras may be honored by some recognizers and help avoid
                // early cut-offs or overly long listens.
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1400L);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 700L);
                // Prefer online recognition by default for higher accuracy; if you
                // want offline set EXTRA_PREFER_OFFLINE to true on devices that support it.
                // intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);

            Toast.makeText(this, getString(R.string.voice_prompt), Toast.LENGTH_SHORT).show();
            speechRecognizer.startListening(intent);
        }
    }

    private void processVoiceInput(String voiceText) {
        String processed = VoiceCommandProcessor.processVoiceCommand(voiceText);

        if (processed.isEmpty()) {
            // Silently ignore unrecognized commands
            return;
        }

        // The following lines that update the UI with voice text will be removed.
        // if (tvVoiceRaw != null) tvVoiceRaw.setText(voiceText);
        // if (tvVoiceConverted != null) tvVoiceConverted.setText(VoiceCommandProcessor.toHumanReadable(processed));

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

        String[] parts = processed.split("\\s+");
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (p.matches("\\d+(,\\d+)?")) {
                for (char digit : p.toCharArray()) {
                    if (digit == ',') calculator.appendDecimal();
                    else calculator.appendDigit(String.valueOf(digit));
                }
            } else if (p.matches("[+−×÷%()]")) {
                 if (p.equals("(")) calculator.appendParenthesis("(");
                 else if (p.equals(")")) calculator.appendParenthesis(")");
                 else calculator.appendOperator(p);
            }
        }

        if (calculate) {
            try {
                String result = calculator.calculate();
                tvExpression.setText(calculator.getFullExpression() + " =");
                tvResult.setText(formatNumber(result));
                calculator.clear();
                calculator.setCurrentNumber(result);
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao calcular: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            updateDisplay();
        }
    }

    private void updateDisplay() {
        // Show the expression being built and the incremental evaluated result
        tvExpression.setText(calculator.getFullExpression());
        tvResult.setText(formatNumber(calculator.evaluatePartial()));
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
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}