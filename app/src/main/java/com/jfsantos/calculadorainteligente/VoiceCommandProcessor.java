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
        
        // NOTE: Decimal separators (virgula, ponto, etc.) are NOT in operatorWords
        // They are handled specially within parseNumber() method
        
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
        
        // Adicionar variações normalizadas (sem acentos) para operadores com acentos
        addNormalizedOperators();
    }
    
    /**
     * Adiciona versões normalizadas (sem acentos) de operadores que têm acentos
     */
    private static void addNormalizedOperators() {
        Map<String, String> toAdd = new HashMap<>();
        
        for (Map.Entry<String, String> entry : operatorWords.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Criar versão normalizada
            String normalized = key
                .replace("à", "a").replace("á", "a").replace("â", "a").replace("ã", "a")
                .replace("è", "e").replace("é", "e").replace("ê", "e")
                .replace("ì", "i").replace("í", "i").replace("î", "i")
                .replace("ò", "o").replace("ó", "o").replace("ô", "o").replace("õ", "o")
                .replace("ù", "u").replace("ú", "u").replace("û", "u")
                .replace("ç", "c");
            
            // Se for diferente, adicionar
            if (!normalized.equals(key) && !operatorWords.containsKey(normalized)) {
                toAdd.put(normalized, value);
            }
        }
        
        // Adicionar todas as variações normalizadas
        operatorWords.putAll(toAdd);
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
            String wordNormalized = normalizeText(word);

            // Skip filler words
            if (wordNormalized.equals("e") || wordNormalized.equals("de") || wordNormalized.isEmpty()) { 
                i++; 
                continue; 
            }

            // Try compound operator (two-word) first
            if (i + 1 < words.length) {
                String compound = (wordNormalized + " " + normalizeText(words[i + 1].toLowerCase()));
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
            if (isNumberWord(wordNormalized) || word.matches("\\d+")) {
                ParseResult pr = parseNumber(words, i);
                if (pr != null) {
                    if (result.length() > 0 && !result.toString().endsWith(" ")) {
                        result.append(" ");
                    }
                    result.append(pr.numberString);
                    i = pr.nextIndex;
                    // After parsing a number, check if the next word(s) is an operator
                    if (i < words.length) {
                        // First try compound operator (e.g., "dividido por")
                        if (i + 1 < words.length) {
                            String nextWordNorm = normalizeText(words[i].toLowerCase());
                            String nextNextWordNorm = normalizeText(words[i + 1].toLowerCase());
                            String compound = nextWordNorm + " " + nextNextWordNorm;
                            if (operatorWords.containsKey(compound)) {
                                String operator = operatorWords.get(compound);
                                if (operator.equals("=")) {
                                    return result.toString().trim() + " =";
                                }
                                result.append(" ").append(operator).append(" ");
                                i += 2;
                                continue;
                            }
                        }
                        // Then try single-word operator
                        String nextWordNormalized = normalizeText(words[i].toLowerCase());
                        if (operatorWords.containsKey(nextWordNormalized)) {
                            String operator = operatorWords.get(nextWordNormalized);
                            if (operator.equals("=")) {
                                return result.toString().trim() + " =";
                            }
                            result.append(" ").append(operator).append(" ");
                            i++;
                        }
                    }
                    continue;
                }
            }

            // Single-word operators
            if (operatorWords.containsKey(wordNormalized)) {
                String operator = operatorWords.get(wordNormalized);
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
        String normalized = normalizeText(w);
        return numberWords.containsKey(normalized) || normalized.equals("virgula") || normalized.equals("ponto") || 
               normalized.equals("decimal") || normalized.equals("dot") || normalized.equals("comma");
    }

    // ParseResult holds parsed number string and next index
    private static class ParseResult {
        String numberString;
        int nextIndex;
        ParseResult(String s, int idx) { numberString = s; nextIndex = idx; }
    }


    // Parse a sequence of number words starting at index i. Supports decimals with 'virgula', 'ponto', 'vírgula', 'decimal'.
    // Portuguese number composition rules:
    // - "vinte e um" = 20 + 1 = 21
    // - "cento e trinta" = 100 + 30 = 130
    // - "dois mil" = 2 * 1000 = 2000
    // - "dois mil trezentos e quarenta e cinco" = 2 * 1000 + 300 + 40 + 5 = 2345
    private static ParseResult parseNumber(String[] words, int i) {
        int idx = i;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal current = BigDecimal.ZERO;
        boolean foundAny = false;

        while (idx < words.length) {
            String w = words[idx].toLowerCase();
            String wNormalized = normalizeText(w);
            
            // Skip connector words
            if (wNormalized.equals("e") || wNormalized.equals("de")) {
                idx++;
                continue;
            }
            
            // If decimal marker found, break
            if (isDecimalMarker(wNormalized)) {
                break;
            }
            
            if (!numberWords.containsKey(wNormalized)) {
                // Not a number word, stop parsing
                break;
            }
            
            foundAny = true;
            String numStr = numberWords.get(wNormalized);
            BigDecimal val = new BigDecimal(numStr);
            int v = val.intValue();

            // Composição em português:
            // - Unidades e dezenas (0-99): acumula em 'current'
            // - Centenas (100-900): adiciona ao current (não ao total)
            // - Milhares+ (1000+): multiplica (current + total) e reseta
            
            if (v >= 1000) {
                // mil, milhão, etc
                // Combina current e total para multiplicar
                BigDecimal base = total.add(current);
                if (base.compareTo(BigDecimal.ZERO) == 0) {
                    base = BigDecimal.ONE;
                }
                // Multiplica: "cento e vinte e três mil" = (100 + 20 + 3) * 1000
                total = base.multiply(val);
                current = BigDecimal.ZERO;
            } 
            else if (v >= 100) {
                // Centenas: "cento", "duzentos", etc - acumula em current, não em total
                current = current.add(val);
            } 
            else {
                // Unidades e dezenas (0-99): apenas acumula
                current = current.add(val);
            }
            
            idx++;
        }

        // Adiciona qualquer valor restante em 'current'
        total = total.add(current);

        // Verificar se encontrou algo
        if (!foundAny) {
            return null;
        }

        // If decimal part exists
        if (idx < words.length && isDecimalMarker(normalizeText(words[idx].toLowerCase()))) {
            idx++; // skip marker
            
            // Parse fractional part
            StringBuilder fracDigits = new StringBuilder();
            BigDecimal fracTotal = BigDecimal.ZERO;
            
            while (idx < words.length) {
                String w = words[idx].toLowerCase();
                String wNormalized = normalizeText(w);
                
                // Skip connectors
                if (wNormalized.equals("e") || wNormalized.equals("de")) { 
                    idx++; 
                    continue; 
                }
                
                // Check for another decimal marker (shouldn't happen but safety)
                if (isDecimalMarker(wNormalized)) {
                    break;
                }
                
                if (numberWords.containsKey(wNormalized)) {
                    String numStr = numberWords.get(wNormalized);
                    int v = Integer.parseInt(numStr);
                    fracTotal = fracTotal.add(new BigDecimal(v));
                    idx++;
                    continue;
                }
                
                if (w.matches("\\d+")) {
                    // Direct digit sequence
                    fracDigits.append(w);
                    idx++;
                    continue;
                }
                
                // Not a number, stop
                break;
            }
            
            // Build fractional string
            String fracString = "";
            if (fracDigits.length() > 0) {
                fracString = fracDigits.toString();
            } else if (fracTotal.compareTo(BigDecimal.ZERO) > 0) {
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