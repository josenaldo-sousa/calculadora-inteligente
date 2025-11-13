package com.jfsantos.calculadorainteligente;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VoiceCommandProcessor {
    private static final Map<String, String> numberWords = new HashMap<>();
    private static final Map<String, String> operatorWords = new HashMap<>();

    static {
        // Numbers in Portuguese - Basic units
        numberWords.put("zero", "0");
        numberWords.put("um", "1");
        numberWords.put("uma", "1");
        numberWords.put("dois", "2");
        numberWords.put("duas", "2");
        numberWords.put("três", "3");
        numberWords.put("tres", "3");  // Variação sem acento
        numberWords.put("quatro", "4");
        numberWords.put("cinco", "5");
        numberWords.put("seis", "6");
        numberWords.put("sete", "7");
        numberWords.put("oito", "8");
        numberWords.put("nove", "9");
        
        // Numbers 10-19
        numberWords.put("dez", "10");
        numberWords.put("onze", "11");
        numberWords.put("doze", "12");
        numberWords.put("treze", "13");
        numberWords.put("quatorze", "14");
        numberWords.put("quinze", "15");
        numberWords.put("dezesseis", "16");
        numberWords.put("dezasseis", "16");  // Variação portuguesa
        numberWords.put("dezessete", "17");
        numberWords.put("dezassete", "17");  // Variação portuguesa
        numberWords.put("dezoito", "18");
        numberWords.put("dezenove", "19");
        numberWords.put("dezanove", "19");   // Variação portuguesa
        
        // Tens
        numberWords.put("vinte", "20");
        numberWords.put("trinta", "30");
        numberWords.put("quarenta", "40");
        numberWords.put("cinquenta", "50");
        numberWords.put("sessenta", "60");
        numberWords.put("setenta", "70");
        numberWords.put("oitenta", "80");
        numberWords.put("noventa", "90");
        
        // Hundreds
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
        
        // Thousands and higher
        numberWords.put("mil", "1000");
        numberWords.put("milhão", "1000000");
        numberWords.put("milhao", "1000000");  // Sem acento
        numberWords.put("bilhão", "1000000000");
        numberWords.put("bilhao", "1000000000"); // Sem acento
        
        // Alternative pronunciations and common variations
        numberWords.put("meia", "0.5");      // meia dúzia, etc
        numberWords.put("meio", "0.5");       // meio
        numberWords.put("metade", "0.5");     // metade

        // Operators in Portuguese - Addition
        operatorWords.put("mais", "+");
        operatorWords.put("adição", "+");
        operatorWords.put("adicionar", "+");
        operatorWords.put("somar", "+");
        operatorWords.put("soma", "+");
        operatorWords.put("plus", "+");     // English fallback
        
        // Subtraction
        operatorWords.put("menos", "−");
        operatorWords.put("subtração", "−");
        operatorWords.put("subtrair", "−");
        operatorWords.put("subtraia", "−");
        operatorWords.put("subitrai", "−"); // Variação de pronúncia
        operatorWords.put("minus", "−");    // English fallback
        
        // Multiplication
        operatorWords.put("vezes", "×");
        operatorWords.put("multiplicação", "×");
        operatorWords.put("multiplicar", "×");
        operatorWords.put("multiplique", "×");
        operatorWords.put("multiplica", "×");
        operatorWords.put("x", "×");        // letra X
        operatorWords.put("*", "×");        // asterisco
        operatorWords.put("por", "×");      // "5 por 2"
        operatorWords.put("times", "×");    // English fallback
        
        // Division
        operatorWords.put("dividir", "÷");
        operatorWords.put("divisão", "÷");
        operatorWords.put("divida", "÷");
        operatorWords.put("dividido", "÷");
        operatorWords.put("dividido por", "÷");
        operatorWords.put("sobre", "÷");    // "10 sobre 2"
        operatorWords.put("dividida", "÷"); // Variação feminina
        operatorWords.put("divide", "÷");   // Variação de pronúncia
        operatorWords.put("slash", "÷");    // English fallback
        operatorWords.put("barra", "÷");    // Nome da tecla
        
        // Percentage
        operatorWords.put("por cento", "%");
        operatorWords.put("porcento", "%");
        operatorWords.put("porcentagem", "%");
        operatorWords.put("percentual", "%");
        operatorWords.put("percent", "%");  // English
        
        // Decimal separators
        operatorWords.put("virgula", ",");   // Sem acento
        operatorWords.put("vírgula", ",");
        operatorWords.put("ponto", ".");
        operatorWords.put("decimal", ".");
        operatorWords.put("dot", ".");       // English
        operatorWords.put("comma", ",");     // English
        
        // Special commands
        operatorWords.put("igual", "=");
        operatorWords.put("é igual", "=");
        operatorWords.put("resultado", "=");
        operatorWords.put("calcular", "=");
        operatorWords.put("calcula", "=");   // Variação
        operatorWords.put("calculo", "=");   // Variação
        operatorWords.put("enter", "=");     // English
        operatorWords.put("equals", "=");    // English
        operatorWords.put("limpar", "C");
        operatorWords.put("limpe", "C");     // Variação
        operatorWords.put("limpa", "C");     // Variação
        operatorWords.put("resetar", "C");
        operatorWords.put("reset", "C");     // English
        operatorWords.put("zerar", "C");
        operatorWords.put("apagar", "DEL");
        operatorWords.put("apague", "DEL");  // Variação
        operatorWords.put("apaga", "DEL");   // Variação
        operatorWords.put("deletar", "DEL");
        operatorWords.put("delete", "DEL");  // English
        operatorWords.put("remove", "DEL");  // English
    }

    public static String processVoiceCommand(String voiceText) {
        if (voiceText == null || voiceText.isEmpty()) {
            return "";
        }

        String processed = voiceText.toLowerCase().trim();
        
        // Normalizar variações comuns (acentos, etc)
        processed = normalizeText(processed);
        
        // Handle special commands
        if (processed.contains("limpar") || processed.contains("resetar") || processed.contains("zerar") || 
            processed.contains("reset") || processed.contains("clear")) {
            return "CLEAR";
        }
        
        if (processed.contains("apagar") || processed.contains("deletar") || processed.contains("delete") || 
            processed.contains("remove")) {
            return "DELETE";
        }

        // Convert to mathematical expression
        String expression = convertToExpression(processed);
        
        return expression;
    }
    
    /**
     * Normaliza texto removendo/substituindo acentos comuns e variações de pronúncia
     */
    private static String normalizeText(String text) {
        if (text == null) return "";
        
        // Remover espaços extras
        text = text.replaceAll("\\s+", " ");
        
        // Substituir variações comuns de acentos
        text = text.replace("à", "a").replace("á", "a").replace("â", "a").replace("ã", "a")
                   .replace("è", "e").replace("é", "e").replace("ê", "e")
                   .replace("ì", "i").replace("í", "i").replace("î", "i")
                   .replace("ò", "o").replace("ó", "o").replace("ô", "o").replace("õ", "o")
                   .replace("ù", "u").replace("ú", "u").replace("û", "u")
                   .replace("ç", "c");
        
        return text;
    }

    private static String convertToExpression(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");

        int i = 0;
        while (i < words.length) {
            String word = words[i].toLowerCase();

            // Skip filler words
            if (word.equals("e") || word.equals("de") || word.isEmpty()) { 
                i++; 
                continue; 
            }

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
                    if (result.length() > 0 && !result.toString().endsWith(" ")) {
                        result.append(" ");
                    }
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
                if (result.length() > 0 && !result.toString().endsWith(" ")) {
                    result.append(" ");
                }
                result.append(word);
                i++;
                continue;
            }

            // Unrecognized word -> skip silently
            i++;
        }

        return result.toString().trim();
    }

    // Helper to check if a word maps to a number word
    private static boolean isNumberWord(String w) {
        if (w == null) return false;
        return numberWords.containsKey(w) || w.equals("virgula") || w.equals("ponto") || 
               w.equals("decimal") || w.equals("dot") || w.equals("comma");
    }

    // ParseResult holds parsed number string and next index
    private static class ParseResult {
        String numberString;
        int nextIndex;
        ParseResult(String s, int idx) { numberString = s; nextIndex = idx; }
    }


    // Parse a sequence of number words starting at index i. Supports decimals with 'virgula', 'ponto', 'vírgula', 'decimal'.
    private static ParseResult parseNumber(String[] words, int i) {
        int idx = i;
        // Parse whole part
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal current = BigDecimal.ZERO;
        boolean foundAny = false;

        while (idx < words.length) {
            String w = words[idx].toLowerCase();
            
            // Skip connector words (e, de)
            if (w.equals("e") || w.equals("de")) {
                idx++;
                continue;
            }
            
            if (numberWords.containsKey(w)) {
                foundAny = true;
                BigDecimal val = new BigDecimal(numberWords.get(w));
                int v = val.intValue();

                if (v >= 1000) { // Handle "mil", "milhão", etc
                    if (current.compareTo(BigDecimal.ZERO) == 0) {
                        current = BigDecimal.ONE;
                    }
                    current = current.multiply(val);
                    total = total.add(current);
                    current = BigDecimal.ZERO;
                } else if (v >= 100) { // Handle hundreds "cento", "duzentos", etc
                    if (current.compareTo(BigDecimal.ZERO) > 0) {
                        // Handles cases like "cem e vinte"
                        total = total.add(current.multiply(BigDecimal.valueOf(100)));
                    } else {
                        total = total.add(val);
                    }
                    current = BigDecimal.ZERO;
                } else { // Handle tens and units (0-99)
                    current = current.add(val);
                }
                idx++;
                continue;
            }
            
            // If decimal marker found, break to fractional parsing
            if (w.equals("virgula") || w.equals("ponto") || w.equals("decimal") || 
                w.equals("dot") || w.equals("comma")) {
                break;
            }
            
            // Not a number word, stop parsing
            break;
        }

        total = total.add(current);

        // Check if we found any numbers
        if (!foundAny && !(idx < words.length && isDecimalMarker(words[idx]))) {
            return null;
        }

        // If decimal part exists
        if (idx < words.length && isDecimalMarker(words[idx])) {
            idx++; // skip marker
            // parse fractional as integer sequence
            StringBuilder fracDigits = new StringBuilder();
            BigDecimal fracTotal = BigDecimal.ZERO;
            BigDecimal fracCurrent = BigDecimal.ZERO;
            boolean foundFrac = false;
            
            while (idx < words.length) {
                String w = words[idx].toLowerCase();
                
                if (w.equals("e") || w.equals("de")) { 
                    idx++; 
                    continue; 
                }
                
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
                // Use comma as decimal separator for Brazilian Portuguese
                return new ParseResult(wholeString + "," + fracString, idx);
            }
        }

        // No decimal
        return new ParseResult(total.toPlainString(), idx);
    }
    
    /**
     * Verifica se a palavra é um marcador de decimal
     */
    private static boolean isDecimalMarker(String word) {
        if (word == null) return false;
        return word.equals("virgula") || word.equals("ponto") || word.equals("decimal") || 
               word.equals("dot") || word.equals("comma");
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