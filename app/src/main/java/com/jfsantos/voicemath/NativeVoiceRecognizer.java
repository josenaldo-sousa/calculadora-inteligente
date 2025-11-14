package com.jfsantos.voicemath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.jfsantos.calculadorainteligente.VoiceCommandProcessor;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Wrapper nativo para Android SpeechRecognizer
 * Suporta reconhecimento em português e inglês
 */
public class NativeVoiceRecognizer {

    private SpeechRecognizer recognizer;
    private Activity context;
    private Locale currentLanguage = new Locale("pt", "BR");
    private VoiceRecognitionListener listener;

    /**
     * Converte entrada de voz em expressão matemática e calcula usando mXparser
     * Integra normalização + conversão + avaliação
     */
    public static String calculateWithMXParser(String input) {
        if (input == null || input.trim().isEmpty()) return "";
        
        // 1. Normaliza removendo prefixos de contexto
        String normalized = normalizeMathPhrase(input);
        
        // 2. Utiliza o novo parser para obter expressão avaliável
        VoiceCommandProcessor.ProcessResult parsed = VoiceCommandProcessor.processVoiceCommandDetailed(normalized);

    if (parsed.getCommand() != VoiceCommandProcessor.CommandType.NONE) {
            return "";
        }

        String mathExpression = parsed.getMathExpression();
        if (mathExpression == null || mathExpression.trim().isEmpty()) {
            return "Expressão inválida";
        }

    Expression mxExpr = new Expression(mathExpression);
    double result = mxExpr.calculate();

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return "Expressão inválida: " + mathExpression;
        }

        String formatted = String.format(Locale.US, "%.10f", result)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "")
                .replace('.', ',');
        if (formatted.isEmpty()) {
            formatted = "0";
        }
        return formatted;
    }
    private boolean isListening = false;

    public NativeVoiceRecognizer(Activity context) {
        this.context = context;
        this.recognizer = SpeechRecognizer.createSpeechRecognizer(context);
    }

    public void setLanguage(Locale locale) {
        this.currentLanguage = locale;
    }

    public void startListening(VoiceRecognitionListener listener) {
        this.listener = listener;
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage.getLanguage());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLanguage.toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        recognizer.setRecognitionListener(new SpeechRecognitionListenerAdapter());
        recognizer.startListening(intent);
        
        isListening = true;
    }

    public void stopListening() {
        if (recognizer != null) {
            recognizer.stopListening();
        }
        isListening = false;
    }

    public void destroy() {
        if (recognizer != null) {
            recognizer.destroy();
        }
    }

    public boolean isListening() {
        return isListening;
    }

    /**
     * Normaliza frases comuns de comando matemático para facilitar o reconhecimento
     * Remove prefixos como "quanto é", "calcule", "qual o resultado de", etc.
     * E remove sufixos como "por favor", "igual", "resultado"
     */
    public static String normalizeMathPhrase(String input) {
        if (input == null) return "";
        String text = input.toLowerCase().trim();
        
        // Remove prefixos de contexto
        text = text.replaceAll("^(quanto é|quanto que é|quanto vale|quanto que vale|calcule|calculate|faça a conta de|qual o resultado de|qual é o resultado de|resultado de|me diga|me mostra|me mostre|me informe|me fala|me fale|quero saber|preciso saber)\\s+", "");
        
        // Remove sufixos de comando
        text = text.replaceAll("\\s+(por favor|pf|pfv|obrigado|obrigada|valeu|resultado|igual|igualmente|calcula|calculate)$", "");
        
        // Remove "interrogação" e "ponto de interrogação" no final
        text = text.replaceAll("\\s+(interrogação|ponto de interrogação|\\?)$", "");
        
        return text.trim();
    }

    private class SpeechRecognitionListenerAdapter implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (listener != null) {
                listener.onReadyForSpeech();
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            if (listener != null) {
                listener.onBeginningOfSpeech();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            if (listener != null) {
                listener.onRmsChanged(rmsdB);
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            if (listener != null) {
                listener.onEndOfSpeech();
            }
        }

        @Override
        public void onError(int error) {
            isListening = false;
            if (listener != null) {
                listener.onError(error);
            }
        }

        @Override
        public void onResults(Bundle results) {
            isListening = false;
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

            if (listener != null && matches != null && !matches.isEmpty()) {
                ArrayList<String> normalized = new ArrayList<>();
                for (String m : matches) {
                    normalized.add(NativeVoiceRecognizer.normalizeMathPhrase(m));
                }
                // Calcula resultado usando mXparser para o melhor match
                String resultado = "";
                if (!normalized.isEmpty()) {
                    resultado = calculateWithMXParser(normalized.get(0));
                }
                ArrayList<String> output = new ArrayList<>(normalized);
                output.add("Resultado: " + resultado);
                listener.onResults(output, scores);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> partial = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (listener != null && partial != null && !partial.isEmpty()) {
                ArrayList<String> normalized = new ArrayList<>();
                for (String m : partial) {
                    normalized.add(NativeVoiceRecognizer.normalizeMathPhrase(m));
                }
                listener.onPartialResults(normalized);
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }

    public interface VoiceRecognitionListener {
        void onReadyForSpeech();
        void onBeginningOfSpeech();
        void onRmsChanged(float rmsdB);
        void onEndOfSpeech();
        void onError(int error);
        void onResults(ArrayList<String> results, float[] scores);
        void onPartialResults(ArrayList<String> partial);
    }

    public static abstract class VoiceRecognitionAdapter implements VoiceRecognitionListener {
        @Override
        public void onReadyForSpeech() {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {}

        @Override
        public void onResults(ArrayList<String> results, float[] scores) {}

        @Override
        public void onPartialResults(ArrayList<String> partial) {}
    }
}
