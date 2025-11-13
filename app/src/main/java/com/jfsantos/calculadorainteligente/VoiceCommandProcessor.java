package com.jfsantos.calculadorainteligente;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VoiceCommandProcessor {
    private static final Map<String, String> numberWords = new HashMap<>();
    private static final Map<String, String> operatorWords = new HashMap<>();

    static {
        // Numbers in Portuguese
        numberWords.put("zero", "0");
        numberWords.put("um", "1");
        numberWords.put("uma", "1");
        numberWords.put("dois", "2");
        numberWords.put("duas", "2");
        numberWords.put("três", "3");
        numberWords.put("quatro", "4");
        numberWords.put("cinco", "5");
        numberWords.put("seis", "6");
        numberWords.put("sete", "7");
        numberWords.put("oito", "8");
        numberWords.put("nove", "9");
        numberWords.put("dez", "10");
        numberWords.put("onze", "11");
        numberWords.put("doze", "12");
        numberWords.put("treze", "13");
        numberWords.put("quatorze", "14");
        numberWords.put("quinze", "15");
        numberWords.put("dezesseis", "16");
        numberWords.put("dezessete", "17");
        numberWords.put("dezoito", "18");
        numberWords.put("dezenove", "19");
        numberWords.put("vinte", "20");
        numberWords.put("trinta", "30");
        numberWords.put("quarenta", "40");
        numberWords.put("cinquenta", "50");
        numberWords.put("sessenta", "60");
        numberWords.put("setenta", "70");
        numberWords.put("oitenta", "80");
        numberWords.put("noventa", "90");
        numberWords.put("cem", "100");
        numberWords.put("cento", "100");
        numberWords.put("duzentos", "200");
        numberWords.put("trezentos", "300");
        numberWords.put("quatrocentos", "400");
        numberWords.put("quinhentos", "500");
        numberWords.put("seiscentos", "600");
        numberWords.put("setecentos", "700");
        numberWords.put("oitocentos", "800");
        numberWords.put("novecentos", "900");
        numberWords.put("mil", "1000");

        // Operators in Portuguese
        operatorWords.put("mais", "+");
        operatorWords.put("adição", "+");
        operatorWords.put("somar", "+");
        operatorWords.put("soma", "+");
        operatorWords.put("menos", "−");
        operatorWords.put("subtração", "−");
        operatorWords.put("subtrair", "−");
        operatorWords.put("subtraia", "−");
        operatorWords.put("vezes", "×");
        operatorWords.put("multiplicação", "×");
        operatorWords.put("multiplicar", "×");
        operatorWords.put("multiplique", "×");
    // Common short forms/transcriptions (e.g. user says "x" or recognizer returns "x")
    operatorWords.put("x", "×");
    operatorWords.put("*", "×");
    operatorWords.put("por", "×");
        operatorWords.put("dividir", "÷");
        operatorWords.put("divisão", "÷");
        operatorWords.put("divida", "÷");
        operatorWords.put("dividido", "÷");
    operatorWords.put("dividido por", "÷");
    operatorWords.put("sobre", "÷");
        operatorWords.put("por cento", "%");
        operatorWords.put("porcento", "%");
        operatorWords.put("porcentagem", "%");
        
        // Special commands
        operatorWords.put("igual", "=");
        operatorWords.put("é igual", "=");
        operatorWords.put("resultado", "=");
        operatorWords.put("calcular", "=");
        operatorWords.put("limpar", "C");
        operatorWords.put("apagar", "DEL");
    }

    public static String processVoiceCommand(String voiceText) {
        if (voiceText == null || voiceText.isEmpty()) {
            return "";
        }

        String processed = voiceText.toLowerCase().trim();
        
        // Handle special commands
        if (processed.contains("limpar") || processed.contains("resetar") || processed.contains("zerar")) {
            return "CLEAR";
        }
        
        if (processed.contains("apagar") || processed.contains("deletar")) {
            return "DELETE";
        }

        // Convert to mathematical expression
        String expression = convertToExpression(processed);
        
        return expression;
    }

    private static String convertToExpression(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");

        int i = 0;
        while (i < words.length) {
            String word = words[i].toLowerCase();

            // Skip filler
            if (word.equals("e")) { i++; continue; }

            // Try compound operator (two-word) first
            if (i + 1 < words.length) {
                String compound = (word + " " + words[i + 1]).toLowerCase();
                if (operatorWords.containsKey(compound)) {
                    String operator = operatorWords.get(compound);
                    if (operator.equals("=")) return result.toString().trim() + " =";
                    if (operator.equals("C")) return "CLEAR";
                    if (operator.equals("DEL")) return "DELETE";
                    result.append(" ").append(operator).append(" ");
                    i += 2;
                    continue;
                }
            }

            // Number parsing (including composed numbers and decimals)
            if (isNumberWord(word) || word.matches("\\d+")) {
                ParseResult pr = parseNumber(words, i);
                if (pr != null) {
                    result.append(pr.numberString);
                    i = pr.nextIndex;
                    // After parsing a number, check if the next word is an operator
                    if (i < words.length && operatorWords.containsKey(words[i].toLowerCase())) {
                        String operator = operatorWords.get(words[i].toLowerCase());
                        if (operator.equals("=")) {
                            return result.toString().trim() + " =";
                        }
                        result.append(" ").append(operator).append(" ");
                        i++;
                    }
                    continue;
                }
            }

            // Single-word operators
            if (operatorWords.containsKey(word)) {
                String operator = operatorWords.get(word);
                if (operator.equals("=")) return result.toString().trim() + " =";
                if (operator.equals("C")) return "CLEAR";
                if (operator.equals("DEL")) return "DELETE";
                result.append(" ").append(operator).append(" ");
                i++;
                continue;
            }

            // Direct digits
            if (word.matches("\\d+")) {
                result.append(word);
                i++;
                continue;
            }

            // Unrecognized word -> skip
            i++;
        }

        return result.toString().trim();
    }

    // Helper to check if a word maps to a number word
    private static boolean isNumberWord(String w) {
        return numberWords.containsKey(w) || w.equals("e") || w.equals("vírgula") || w.equals("ponto");
    }

    // ParseResult holds parsed number string and next index
    private static class ParseResult {
        String numberString;
        int nextIndex;
        ParseResult(String s, int idx) { numberString = s; nextIndex = idx; }
    }


    // Parse a sequence of number words starting at index i. Supports decimals with 'vírgula' or 'ponto'.
    private static ParseResult parseNumber(String[] words, int i) {
        int idx = i;
        // Parse whole part
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal current = BigDecimal.ZERO;
        boolean foundAny = false;

        while (idx < words.length) {
            String w = words[idx].toLowerCase();
            if (w.equals("e")) {
                idx++;
                continue;
            }
            if (numberWords.containsKey(w)) {
                foundAny = true;
                BigDecimal val = new BigDecimal(numberWords.get(w));
                int v = val.intValue();

                if (v >= 1000) { // Handle "mil"
                    if (current.compareTo(BigDecimal.ZERO) == 0) {
                        current = BigDecimal.ONE;
                    }
                    current = current.multiply(val);
                    total = total.add(current);
                    current = BigDecimal.ZERO;
                } else if (v >= 100) { // Handle hundreds
                    if (current.compareTo(BigDecimal.ZERO) > 0) {
                        // Handles cases like "cento e vinte"
                        total = total.add(current.multiply(BigDecimal.valueOf(100)));
                    } else {
                        total = total.add(val);
                    }
                    current = BigDecimal.ZERO;
                } else { // Handle tens and units
                    current = current.add(val);
                }
                idx++;
                continue;
            }
            // If decimal marker, break to fractional parsing
            if (w.equals("vírgula") || w.equals("ponto")) break;
            // not a number word
            break;
        }

        total = total.add(current);

        if (!foundAny && !(idx < words.length && (words[idx].equals("vírgula") || words[idx].equals("ponto")))) return null;

        // If decimal part exists
        if (idx < words.length && (words[idx].equals("vírgula") || words[idx].equals("ponto"))) {
            idx++; // skip marker
            // parse fractional as integer sequence
            StringBuilder fracDigits = new StringBuilder();
            // Try to parse either direct digit words or number words forming an integer
            // We'll try to parse a number segment using same logic but produce an integer string
            // Simple approach: collect subsequent number words until non-number, then convert that number to plain string
            BigDecimal fracTotal = BigDecimal.ZERO;
            BigDecimal fracCurrent = BigDecimal.ZERO;
            boolean foundFrac = false;
            while (idx < words.length) {
                String w = words[idx].toLowerCase();
                if (w.equals("e")) { idx++; continue; }
                if (numberWords.containsKey(w)) {
                    foundFrac = true;
                    int v = Integer.parseInt(numberWords.get(w));
                    if (v >= 1000) {
                        if (fracCurrent.compareTo(BigDecimal.ZERO) == 0) fracCurrent = BigDecimal.ONE;
                        fracCurrent = fracCurrent.multiply(new BigDecimal(v));
                        fracTotal = fracTotal.add(fracCurrent);
                        fracCurrent = BigDecimal.ZERO;
                    } else {
                        fracCurrent = fracCurrent.add(new BigDecimal(v));
                    }
                    idx++;
                    continue;
                }
                if (w.matches("\\d+")) {
                    // append digits directly
                    fracDigits.append(w);
                    foundFrac = true;
                    idx++;
                    continue;
                }
                break;
            }
            fracTotal = fracTotal.add(fracCurrent);
            String fracString = "";
            if (fracDigits.length() > 0) {
                fracString = fracDigits.toString();
            } else if (foundFrac) {
                fracString = fracTotal.toPlainString();
            }

            String wholeString = total.toPlainString();
            if (fracString.isEmpty()) {
                return new ParseResult(wholeString, idx);
            } else {
                return new ParseResult(wholeString + "." + fracString, idx);
            }
        }

        // No decimal
        return new ParseResult(total.toPlainString(), idx);
    }

    public static boolean isCalculationCommand(String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        
        // Check if it contains operators or equals sign
        return expression.contains("+") || expression.contains("−") || 
               expression.contains("×") || expression.contains("÷") ||
               expression.contains("=") || expression.contains("%");
    }

    public static String cleanExpression(String expression) {
        if (expression == null) {
            return "";
        }
        
        // Remove equals sign if present
        expression = expression.replace("=", "").trim();
        
        // Clean up multiple spaces
        expression = expression.replaceAll("\\s+", " ");
        
        return expression;
    }

    public static boolean containsOperator(String text) {
        return text.contains("+") || text.contains("−") || 
               text.contains("×") || text.contains("÷") || text.contains("%");
    }

    /**
     * Convert a processed expression into a human-friendly form suitable for display.
     * Examples:
     *  - "40 − 2" -> "40 − 2"
     *  - "CLEAR" -> "Limpar"
     *  - "123.45" -> "123.45"
     */
    public static String toHumanReadable(String expression) {
        if (expression == null || expression.isEmpty()) {
            return "Comando de voz não reconhecido";
        }
        return expression
                .replace("−", "-")
                .replace("×", "*")
                .replace("÷", "/");
    }
}