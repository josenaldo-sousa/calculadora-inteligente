package com.jfsantos.calculadorainteligente;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class VoiceCommandProcessor {
    private static final Map<String, String> numberWords = new HashMap<>();
    private static final Map<String, String> operatorWords = new HashMap<>();
    private static final Set<String> fillerWords = new HashSet<>(Arrays.asList(
            "e", "da", "de", "do", "das", "dos", "com", "por", "a", "o", "os", "as", "ao", "aos", "uma", "um"
    ));

    static {
        numberWords.put("zero", "0");
        numberWords.put("um", "1");
        numberWords.put("uma", "1");
        numberWords.put("dois", "2");
        numberWords.put("duas", "2");
    operatorWords.put("+", "+");
        numberWords.put("três", "3");
        numberWords.put("tres", "3");
        numberWords.put("quatro", "4");
        numberWords.put("cinco", "5");
        numberWords.put("seis", "6");
        numberWords.put("sete", "7");
        numberWords.put("oito", "8");
        numberWords.put("nove", "9");
    operatorWords.put("-", "-");
    operatorWords.put("−", "-");

        numberWords.put("dez", "10");
        numberWords.put("onze", "11");
        numberWords.put("doze", "12");
        numberWords.put("treze", "13");
        numberWords.put("quatorze", "14");
        numberWords.put("catorze", "14");
        numberWords.put("quinze", "15");
        numberWords.put("dezesseis", "16");
    operatorWords.put("×", "×");
        numberWords.put("dezasseis", "16");
        numberWords.put("dezessete", "17");
        numberWords.put("dezassete", "17");
        numberWords.put("dezoito", "18");
        numberWords.put("dezenove", "19");
        numberWords.put("dezanove", "19");

        numberWords.put("vinte", "20");
        numberWords.put("trinta", "30");
        numberWords.put("quarenta", "40");
        numberWords.put("cinquenta", "50");
        numberWords.put("sessenta", "60");
        numberWords.put("setenta", "70");
    operatorWords.put("/", "÷");
    operatorWords.put("÷", "÷");
        numberWords.put("oitenta", "80");
        numberWords.put("noventa", "90");

        numberWords.put("cem", "100");
        numberWords.put("cento", "100");
        numberWords.put("duzentos", "200");
        numberWords.put("duzentas", "200");
        numberWords.put("trezentos", "300");
        numberWords.put("trezentas", "300");
        numberWords.put("quatrocentos", "400");
        numberWords.put("quatrocentas", "400");
        numberWords.put("quinhentos", "500");
        numberWords.put("quinhentas", "500");
        numberWords.put("seiscentos", "600");
        numberWords.put("seiscentas", "600");
        numberWords.put("setecentos", "700");
        numberWords.put("setecentas", "700");
        numberWords.put("oitocentos", "800");
        numberWords.put("oitocentas", "800");
        numberWords.put("novecentos", "900");
        numberWords.put("novecentas", "900");

        numberWords.put("mil", "1000");
        numberWords.put("milhao", "1000000");
        numberWords.put("milhão", "1000000");
        numberWords.put("bilhao", "1000000000");
        numberWords.put("bilhão", "1000000000");
        numberWords.put("pi", "3.1415926535");
        numberWords.put("euler", "2.7182818284");

        numberWords.put("meia", "0.5");
        numberWords.put("meio", "0.5");
        numberWords.put("metade", "0.5");

        operatorWords.put("mais", "+");
        operatorWords.put("adição", "+");
        operatorWords.put("adicao", "+");
        operatorWords.put("adicionar", "+");
        operatorWords.put("somar", "+");
        operatorWords.put("soma", "+");
        operatorWords.put("plus", "+");

        operatorWords.put("menos", "-");
        operatorWords.put("subtração", "-");
        operatorWords.put("subtracao", "-");
        operatorWords.put("subtrair", "-");
        operatorWords.put("subtraia", "-");
        operatorWords.put("subitrai", "-");
        operatorWords.put("minus", "-");

        operatorWords.put("vezes", "×");
        operatorWords.put("multiplicação", "×");
        operatorWords.put("multiplicacao", "×");
        operatorWords.put("multiplicar", "×");
        operatorWords.put("multiplique", "×");
        operatorWords.put("multiplica", "×");
        operatorWords.put("x", "×");
        operatorWords.put("*", "×");
        operatorWords.put("times", "×");

        operatorWords.put("dividir", "÷");
        operatorWords.put("divisão", "÷");
        operatorWords.put("divisao", "÷");
        operatorWords.put("divida", "÷");
        operatorWords.put("dividido", "÷");
        operatorWords.put("dividida", "÷");
        operatorWords.put("dividido por", "÷");
        operatorWords.put("dividida por", "÷");
        operatorWords.put("sobre", "÷");
        operatorWords.put("divide", "÷");
        operatorWords.put("slash", "÷");
        operatorWords.put("barra", "÷");

    operatorWords.put("por cento", "%");
    operatorWords.put("por cento de", "%");
    operatorWords.put("por cento do", "%");
    operatorWords.put("por cento da", "%");
    operatorWords.put("por cento dos", "%");
    operatorWords.put("por cento das", "%");
    operatorWords.put("porcento", "%");
    operatorWords.put("porcento de", "%");
    operatorWords.put("porcento do", "%");
    operatorWords.put("porcento da", "%");
    operatorWords.put("porcento dos", "%");
    operatorWords.put("porcento das", "%");
    operatorWords.put("porcentagem", "%");
    operatorWords.put("percentual", "%");
    operatorWords.put("percent", "%");

        operatorWords.put("abre parenteses", "(");
        operatorWords.put("abre parentese", "(");
        operatorWords.put("abre parênteses", "(");
        operatorWords.put("abre parêntese", "(");
        operatorWords.put("fecha parenteses", ")");
        operatorWords.put("fecha parentese", ")");
        operatorWords.put("fecha parênteses", ")");
        operatorWords.put("fecha parêntese", ")");

        operatorWords.put("raiz quadrada", "√");
        operatorWords.put("raiz", "√");
        operatorWords.put("radiciacao", "√");
        operatorWords.put("radiciação", "√");
        operatorWords.put("seno", "sin");
        operatorWords.put("sin", "sin");
        operatorWords.put("coseno", "cos");
        operatorWords.put("cosseno", "cos");
        operatorWords.put("cos", "cos");
        operatorWords.put("tangente", "tan");
        operatorWords.put("tan", "tan");
        operatorWords.put("logaritmo", "log");
        operatorWords.put("log", "log");
        operatorWords.put("log natural", "ln");
        operatorWords.put("ln", "ln");
        operatorWords.put("potencia", "^");
        operatorWords.put("potência", "^");
        operatorWords.put("elevado a", "^");
        operatorWords.put("ao quadrado", "^2");
        operatorWords.put("ao cubo", "^3");
        operatorWords.put("fatorial", "!");

        operatorWords.put("igual", "=");
        operatorWords.put("é igual", "=");
        operatorWords.put("eh igual", "=");
        operatorWords.put("resultado", "=");
        operatorWords.put("calcular", "=");
        operatorWords.put("calcula", "=");
        operatorWords.put("calculo", "=");
        operatorWords.put("enter", "=");
        operatorWords.put("equals", "=");
        operatorWords.put("limpar", "C");
        operatorWords.put("limpe", "C");
        operatorWords.put("limpa", "C");
        operatorWords.put("resetar", "C");
        operatorWords.put("reset", "C");
        operatorWords.put("zerar", "C");
        operatorWords.put("zera", "C");
        operatorWords.put("apagar", "DEL");
        operatorWords.put("apague", "DEL");
        operatorWords.put("apaga", "DEL");
        operatorWords.put("deletar", "DEL");
        operatorWords.put("delete", "DEL");
        operatorWords.put("remove", "DEL");

        addNormalizedOperators();
    }

    private static void addNormalizedOperators() {
        Map<String, String> toAdd = new HashMap<>();
        for (Map.Entry<String, String> entry : operatorWords.entrySet()) {
            String key = entry.getKey();
            String normalized = key
                    .replace("à", "a").replace("á", "a").replace("â", "a").replace("ã", "a")
                    .replace("è", "e").replace("é", "e").replace("ê", "e")
                    .replace("ì", "i").replace("í", "i").replace("î", "i")
                    .replace("ò", "o").replace("ó", "o").replace("ô", "o").replace("õ", "o")
                    .replace("ù", "u").replace("ú", "u").replace("û", "u")
                    .replace("ç", "c");
            if (!normalized.equals(key) && !operatorWords.containsKey(normalized)) {
                toAdd.put(normalized, entry.getValue());
            }
        }
        operatorWords.putAll(toAdd);
    }

    public static ProcessResult processVoiceCommandDetailed(String voiceText) {
        if (voiceText == null) {
            return ProcessResult.empty();
        }
        String processed = voiceText.toLowerCase(Locale.ROOT).trim();
        if (processed.isEmpty()) {
            return ProcessResult.empty();
        }
        processed = normalizeText(processed);
        return SpeechExpressionParser.parse(processed);
    }

    public static String processVoiceCommand(String voiceText) {
        ProcessResult result = processVoiceCommandDetailed(voiceText);
        if (result.getCommand() == CommandType.CLEAR) {
            return "CLEAR";
        }
        if (result.getCommand() == CommandType.DELETE) {
            return "DELETE";
        }
        if (result.getUiTokens().isEmpty()) {
            return "";
        }
        String expression = result.getUiExpression();
        if (result.shouldEvaluate()) {
            return expression + " =";
        }
        return expression;
    }

    public static boolean isCalculationCommand(String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        return expression.contains("+") || expression.contains("-") ||
                expression.contains("×") || expression.contains("÷") ||
                expression.contains("=") || expression.contains("%") || expression.contains("√") ||
                expression.contains("sin") || expression.contains("cos") || expression.contains("tan") ||
                expression.contains("log") || expression.contains("ln") || expression.contains("^");
    }

    public static String cleanExpression(String expression) {
        if (expression == null) {
            return "";
        }
        expression = expression.replace("=", "").trim();
        return expression.replaceAll("\\s+", " ");
    }

    public static boolean containsOperator(String text) {
        if (text == null) return false;
        return text.contains("+") || text.contains("−") || text.contains("-") ||
                text.contains("×") || text.contains("÷") || text.contains("%") || text.contains("^") ||
                text.contains("√") || text.contains("sin") || text.contains("cos") || text.contains("tan") ||
                text.contains("log") || text.contains("ln");
    }

    public static String toHumanReadable(String expression) {
        if (expression == null || expression.isEmpty()) {
            return "Comando de voz não reconhecido";
        }
        return expression.replace("×", "*").replace("÷", "/");
    }

    static String normalizeText(String text) {
        if (text == null) return "";
        String normalized = text.replaceAll("\\s+", " ");
        normalized = normalized.replace("à", "a").replace("á", "a").replace("â", "a").replace("ã", "a")
                .replace("è", "e").replace("é", "e").replace("ê", "e")
                .replace("ì", "i").replace("í", "i").replace("î", "i")
                .replace("ò", "o").replace("ó", "o").replace("ô", "o").replace("õ", "o")
                .replace("ù", "u").replace("ú", "u").replace("û", "u")
                .replace("ç", "c");
        return normalized.trim();
    }

    static class ParseResult {
        final String uiString;
        final String mathString;
        final int nextIndex;

        ParseResult(String uiString, String mathString, int nextIndex) {
            this.uiString = uiString;
            this.mathString = mathString;
            this.nextIndex = nextIndex;
        }
    }

    static ParseResult parseNumber(String[] words, int i) {
        int idx = i;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal current = BigDecimal.ZERO;
        boolean foundAny = false;

        if (idx >= words.length) {
            return null;
        }

        boolean startsWithDecimal = isDecimalMarker(words[idx]);

        if (!startsWithDecimal) {
            while (idx < words.length) {
                String w = words[idx];
                String wNormalized = normalizeText(w);
                if (isDecimalMarker(wNormalized)) {
                    break;
                }

                if (numberWords.containsKey(wNormalized)) {
                    foundAny = true;
                    BigDecimal val = new BigDecimal(numberWords.get(wNormalized));

                    if (val.scale() > 0) {
                        current = current.add(val);
                        idx++;
                        continue;
                    }

                    int intVal = val.intValue();
                    if (intVal >= 1000) {
                        BigDecimal base = total.add(current);
                        if (base.compareTo(BigDecimal.ZERO) == 0) {
                            base = BigDecimal.ONE;
                        }
                        total = base.multiply(val);
                        current = BigDecimal.ZERO;
                    } else {
                        current = current.add(val);
                    }
                    idx++;
                    continue;
                }

                if (fillerWords.contains(wNormalized)) {
                    if (startsWithOperator(words, idx)) {
                        break;
                    }
                    idx++;
                    continue;
                }

                if (!numberWords.containsKey(wNormalized)) {
                    break;
                }
            }
            total = total.add(current);
        }

        if (!foundAny && !startsWithDecimal) {
            return null;
        }

        if (idx < words.length && isDecimalMarker(words[idx])) {
            idx++;
            StringBuilder fractional = new StringBuilder();
            while (idx < words.length) {
                String w = words[idx];
                String normalized = normalizeText(w);
                if (isDecimalMarker(normalized) || operatorWords.containsKey(normalized)) {
                    break;
                }
                if (numberWords.containsKey(normalized)) {
                    BigDecimal val = new BigDecimal(numberWords.get(normalized));
                    if (val.scale() > 0) {
                        break;
                    }
                    fractional.append(val.toPlainString().replace(".0", ""));
                    idx++;
                    continue;
                } else if (normalized.matches("\\d+")) {
                    fractional.append(normalized);
                    idx++;
                    continue;
                }
                if (fillerWords.contains(normalized)) {
                    if (startsWithOperator(words, idx)) {
                        break;
                    }
                    idx++;
                    continue;
                } else {
                    break;
                }
            }
            String whole = total.toPlainString();
            String math = fractional.length() == 0 ? whole : whole + "." + fractional;
            String ui = fractional.length() == 0 ? whole.replace('.', ',') : (whole.replace('.', ',') + "," + fractional);
            return new ParseResult(ui, math, idx);
        }

        if (!foundAny && startsWithDecimal) {
            return null;
        }

        String math = total.toPlainString();
        String ui = math.replace('.', ',');
        return new ParseResult(ui, math, idx);
    }

    private static boolean startsWithOperator(String[] words, int index) {
        if (words == null || index < 0 || index >= words.length) {
            return false;
        }
        int max = Math.min(words.length, index + 3);
        for (int end = max; end > index; end--) {
            StringBuilder candidate = new StringBuilder();
            for (int i = index; i < end; i++) {
                if (i > index) {
                    candidate.append(' ');
                }
                candidate.append(words[i]);
            }
            if (operatorWords.containsKey(candidate.toString())) {
                return true;
            }
        }
        return false;
    }

    static boolean isDecimalMarker(String word) {
        if (word == null) return false;
        String normalized = normalizeText(word);
        return normalized.equals("virgula") || normalized.equals("vírgula") || normalized.equals("ponto") ||
                normalized.equals("decimal") || normalized.equals("dot") || normalized.equals("comma");
    }

    public enum CommandType { NONE, CLEAR, DELETE }

    public static final class ProcessResult {
        private static final ProcessResult EMPTY = new ProcessResult(CommandType.NONE, false, Collections.emptyList(), "");

        private final CommandType command;
        private final boolean shouldEvaluate;
        private final List<String> uiTokens;
        private final String mathExpression;

        ProcessResult(CommandType command, boolean shouldEvaluate, List<String> uiTokens, String mathExpression) {
            this.command = command;
            this.shouldEvaluate = shouldEvaluate;
            this.uiTokens = uiTokens;
            this.mathExpression = mathExpression;
        }

        static ProcessResult empty() {
            return EMPTY;
        }

        public CommandType getCommand() {
            return command;
        }

        public boolean shouldEvaluate() {
            return shouldEvaluate;
        }

        public List<String> getUiTokens() {
            return uiTokens;
        }

        public String getUiExpression() {
            return String.join(" ", uiTokens);
        }

        public String getMathExpression() {
            return mathExpression;
        }
    }

    private enum TokenType {
        NONE,
        NUMBER,
        OPERATOR,
        FUNCTION,
        PAREN_OPEN,
        PAREN_CLOSE,
        PERCENT,
        FACTORIAL,
        CONSTANT
    }

    private enum FunctionType {
        SQRT,
        SIN,
        COS,
        TAN,
        LOG,
        LN
    }

    private static final class BuildContext {
        final List<String> mathTokens = new ArrayList<>();
        final List<TokenType> mathTokenTypes = new ArrayList<>();
        final List<String> uiTokens = new ArrayList<>();
        final Deque<FunctionType> functionStack = new ArrayDeque<>();
        int manualParentheses = 0;
        TokenType prevType = TokenType.NONE;

        void addNumber(String ui, String math) {
            maybeInsertImplicitMultiplication();
            mathTokens.add(math);
            mathTokenTypes.add(TokenType.NUMBER);
            uiTokens.add(ui);
            prevType = TokenType.NUMBER;
        }

        void addOperator(String math, String ui) {
            if (mathTokens.isEmpty()) {
                if ("-".equals(math)) {
                    addNumber("0", "0");
                } else {
                    return;
                }
            }
            if (!mathTokens.isEmpty() && lastMathTokenType() == TokenType.OPERATOR) {
                mathTokens.remove(mathTokens.size() - 1);
                mathTokenTypes.remove(mathTokenTypes.size() - 1);
                if (!uiTokens.isEmpty()) {
                    uiTokens.remove(uiTokens.size() - 1);
                }
            }
            mathTokens.add(math);
            mathTokenTypes.add(TokenType.OPERATOR);
            uiTokens.add(ui);
            prevType = TokenType.OPERATOR;
        }

        void addPowerOperator(String powerLiteral) {
            if ("^2".equals(powerLiteral) || "^3".equals(powerLiteral)) {
                addOperator("^", "^");
                mathTokens.add(powerLiteral.substring(1));
                mathTokenTypes.add(TokenType.NUMBER);
                uiTokens.add(powerLiteral.substring(1));
                prevType = TokenType.NUMBER;
            } else {
                addOperator("^", "^");
            }
        }

        void addPercent() {
            if (mathTokens.isEmpty()) {
                return;
            }
            if (prevType != TokenType.NUMBER && prevType != TokenType.PAREN_CLOSE && prevType != TokenType.CONSTANT) {
                return;
            }
            mathTokens.add("*0.01");
            mathTokenTypes.add(TokenType.PERCENT);
            uiTokens.add("%");
            prevType = TokenType.PERCENT;
        }

        boolean addFactorial() {
            if (mathTokens.isEmpty()) {
                return false;
            }
            mathTokens.add("!");
            mathTokenTypes.add(TokenType.FACTORIAL);
            uiTokens.add("!");
            prevType = TokenType.FACTORIAL;
            return true;
        }

        void addFunction(FunctionType functionType) {
            maybeInsertImplicitMultiplication();
            mathTokens.add(toMathFunction(functionType));
            mathTokenTypes.add(TokenType.FUNCTION);
            uiTokens.add(toUiFunction(functionType));
            functionStack.push(functionType);
            prevType = TokenType.FUNCTION;
        }

        void addOpenParenthesis() {
            maybeInsertImplicitMultiplication();
            mathTokens.add("(");
            mathTokenTypes.add(TokenType.PAREN_OPEN);
            uiTokens.add("(");
            manualParentheses++;
            prevType = TokenType.PAREN_OPEN;
        }

        void addCloseParenthesis() {
            if (manualParentheses > 0) {
                mathTokens.add(")");
                mathTokenTypes.add(TokenType.PAREN_CLOSE);
                uiTokens.add(")");
                manualParentheses--;
                prevType = TokenType.PAREN_CLOSE;
                return;
            }
            closeFunction(false);
        }

        void closeFunction(boolean forceUiParenthesis) {
            if (functionStack.isEmpty()) {
                return;
            }
            functionStack.pop();
            mathTokens.add(")");
            mathTokenTypes.add(TokenType.PAREN_CLOSE);
            if (forceUiParenthesis) {
                uiTokens.add(")");
            }
            prevType = TokenType.PAREN_CLOSE;
        }

        void closeAll() {
            while (manualParentheses > 0) {
                addCloseParenthesis();
            }
            while (!functionStack.isEmpty()) {
                closeFunction(false);
            }
            trimTrailingOperator();
        }

        void trimTrailingOperator() {
            while (!mathTokens.isEmpty()) {
                TokenType lastType = lastMathTokenType();
                if (lastType == TokenType.OPERATOR) {
                    mathTokens.remove(mathTokens.size() - 1);
                    mathTokenTypes.remove(mathTokenTypes.size() - 1);
                    if (!uiTokens.isEmpty()) {
                        uiTokens.remove(uiTokens.size() - 1);
                    }
                    prevType = mathTokens.isEmpty() ? TokenType.NONE : lastMathTokenType();
                } else if (lastType == TokenType.FUNCTION) {
                    closeFunction(false);
                } else {
                    break;
                }
            }
        }

        TokenType lastMathTokenType() {
            if (mathTokenTypes.isEmpty()) {
                return TokenType.NONE;
            }
            return mathTokenTypes.get(mathTokenTypes.size() - 1);
        }

        private void maybeInsertImplicitMultiplication() {
            if (mathTokens.isEmpty()) {
                return;
            }
            TokenType last = lastMathTokenType();
            if (last == TokenType.NUMBER || last == TokenType.PAREN_CLOSE || last == TokenType.PERCENT ||
                    last == TokenType.FACTORIAL || last == TokenType.CONSTANT) {
                mathTokens.add("*");
                mathTokenTypes.add(TokenType.OPERATOR);
                uiTokens.add("×");
                prevType = TokenType.OPERATOR;
            }
        }

        private String toMathFunction(FunctionType functionType) {
            switch (functionType) {
                case SQRT:
                    return "sqrt(";
                case SIN:
                    return "sin(";
                case COS:
                    return "cos(";
                case TAN:
                    return "tan(";
                case LOG:
                    return "log10(";
                case LN:
                    return "ln(";
            }
            return "";
        }

        private String toUiFunction(FunctionType functionType) {
            switch (functionType) {
                case SQRT:
                    return "√";
                case SIN:
                    return "sin";
                case COS:
                    return "cos";
                case TAN:
                    return "tan";
                case LOG:
                    return "log";
                case LN:
                    return "ln";
            }
            return "";
        }

        String buildMathExpression() {
            StringBuilder builder = new StringBuilder();
            for (String token : mathTokens) {
                builder.append(token);
            }
            return builder.toString();
        }
    }

    private static final class OperatorMatch {
        final String phrase;
        final int length;

        OperatorMatch(String phrase, int length) {
            this.phrase = phrase;
            this.length = length;
        }
    }

    private static final class SpeechExpressionParser {
        private static final int MAX_OPERATOR_WORDS = 3;

        private SpeechExpressionParser() {
        }

        static ProcessResult parse(String normalizedText) {
            if (normalizedText == null || normalizedText.isEmpty()) {
                return ProcessResult.empty();
            }

            String[] rawWords = normalizedText.split(" ");
            List<String> words = new ArrayList<>();
            for (String word : rawWords) {
                if (word == null) continue;
                String trimmed = word.trim();
                if (!trimmed.isEmpty()) {
                    words.add(trimmed);
                }
            }

            if (words.isEmpty()) {
                return ProcessResult.empty();
            }

            BuildContext ctx = new BuildContext();
            CommandType command = CommandType.NONE;
            boolean shouldEvaluate = false;

            String[] wordArray = words.toArray(String[]::new);
            int i = 0;
            while (i < words.size()) {
                String word = words.get(i);
                if (word.isEmpty()) {
                    i++;
                    continue;
                }

                OperatorMatch match = matchOperator(words, i);
                if (match != null) {
                    String operator = operatorWords.get(match.phrase);
                    if (operator == null) {
                        i += match.length;
                        continue;
                    }
                    if ("C".equals(operator)) {
                        command = CommandType.CLEAR;
                        ctx = new BuildContext();
                        break;
                    }
                    if ("DEL".equals(operator)) {
                        command = CommandType.DELETE;
                        ctx = new BuildContext();
                        break;
                    }
                    if ("=".equals(operator)) {
                        shouldEvaluate = true;
                        i += match.length;
                        continue;
                    }
                    if ("+".equals(operator)) {
                        ctx.addOperator("+", "+");
                        i += match.length;
                        continue;
                    }
                    if ("-".equals(operator)) {
                        ctx.addOperator("-", "−");
                        i += match.length;
                        continue;
                    }
                    if ("×".equals(operator)) {
                        ctx.addOperator("*", "×");
                        i += match.length;
                        continue;
                    }
                    if ("÷".equals(operator)) {
                        ctx.addOperator("/", "÷");
                        i += match.length;
                        continue;
                    }
                    if ("%".equals(operator)) {
                        ctx.addPercent();
                        i += match.length;
                        continue;
                    }
                    if ("!".equals(operator)) {
                        boolean applied = ctx.addFactorial();
                        if (!applied) {
                            int nextIndex = i + match.length;
                            ParseResult factorialNumber = parseNumber(wordArray, nextIndex);
                            if (factorialNumber != null) {
                                ctx.addNumber(factorialNumber.uiString, factorialNumber.mathString);
                                ctx.addFactorial();
                                i = factorialNumber.nextIndex;
                                continue;
                            }
                        }
                        i += match.length;
                        continue;
                    }
                    if ("^".equals(operator)) {
                        ctx.addOperator("^", "^");
                        i += match.length;
                        continue;
                    }
                    if (operator.startsWith("^")) {
                        ctx.addPowerOperator(operator);
                        i += match.length;
                        continue;
                    }
                    if ("(".equals(operator)) {
                        ctx.addOpenParenthesis();
                        i += match.length;
                        continue;
                    }
                    if (")".equals(operator)) {
                        ctx.addCloseParenthesis();
                        i += match.length;
                        continue;
                    }
                    if ("√".equals(operator)) {
                        ctx.addFunction(FunctionType.SQRT);
                        i += match.length;
                        continue;
                    }
                    if ("sin".equals(operator)) {
                        ctx.addFunction(FunctionType.SIN);
                        i += match.length;
                        continue;
                    }
                    if ("cos".equals(operator)) {
                        ctx.addFunction(FunctionType.COS);
                        i += match.length;
                        continue;
                    }
                    if ("tan".equals(operator)) {
                        ctx.addFunction(FunctionType.TAN);
                        i += match.length;
                        continue;
                    }
                    if ("log".equals(operator)) {
                        ctx.addFunction(FunctionType.LOG);
                        i += match.length;
                        continue;
                    }
                    if ("ln".equals(operator)) {
                        ctx.addFunction(FunctionType.LN);
                        i += match.length;
                        continue;
                    }
                    i += match.length;
                    continue;
                }
                String normalized = normalizeText(word);
                if (normalized.matches("\\d+([.,]\\d+)?%")) {
                    String numberPart = normalized.substring(0, normalized.length() - 1);
                    String math;
                    String ui;
                    boolean hasDot = numberPart.contains(".");
                    boolean hasComma = numberPart.contains(",");

                    if (hasDot && !hasComma && VoiceCommandProcessor.isThousandGrouping(numberPart)) {
                        String digitsOnly = numberPart.replace(".", "");
                        math = digitsOnly;
                        ui = digitsOnly;
                    } else if (hasDot && hasComma && VoiceCommandProcessor.isThousandGroupingWithDecimal(numberPart)) {
                        String digitsOnly = numberPart.replace(".", "");
                        math = digitsOnly.replace(',', '.');
                        ui = digitsOnly;
                        if (ui.contains(".")) {
                            ui = ui.replace('.', ',');
                        }
                    } else {
                        math = numberPart.replace(',', '.');
                        ui = numberPart.replace('.', ',');
                    }

                    ctx.addNumber(ui, math);
                    ctx.addPercent();
                    i++;
                    continue;
                }
                ParseResult number = parseNumber(wordArray, i);
                if (number != null) {
                    ctx.addNumber(number.uiString, number.mathString);
                    i = number.nextIndex;
                    continue;
                }

                if (fillerWords.contains(normalized)) {
                    i++;
                    continue;
                }

                if (normalized.matches("\\d+([,.]\\d+)?")) {
                    String math;
                    String ui;

                    boolean hasDot = normalized.contains(".");
                    boolean hasComma = normalized.contains(",");

                    if (hasDot && !hasComma && VoiceCommandProcessor.isThousandGrouping(normalized)) {
                        String digitsOnly = normalized.replace(".", "");
                        math = digitsOnly;
                        ui = digitsOnly;
                    } else if (hasDot && hasComma && VoiceCommandProcessor.isThousandGroupingWithDecimal(normalized)) {
                        String digitsOnly = normalized.replace(".", "");
                        math = digitsOnly.replace(',', '.');
                        ui = digitsOnly;
                        if (ui.contains(".")) {
                            ui = ui.replace('.', ',');
                        }
                    } else {
                        math = normalized.replace(',', '.');
                        ui = normalized.replace('.', ',');
                    }

                    ctx.addNumber(ui, math);
                    i++;
                    continue;
                }

                i++;
            }

            ctx.closeAll();
            List<String> uiTokens = Collections.unmodifiableList(new ArrayList<>(ctx.uiTokens));
            String mathExpression = ctx.buildMathExpression();
            if (command != CommandType.NONE) {
                return new ProcessResult(command, false, Collections.emptyList(), "");
            }
            if (uiTokens.isEmpty() || mathExpression.isEmpty()) {
                return ProcessResult.empty();
            }
            return new ProcessResult(command, shouldEvaluate, uiTokens, mathExpression);
        }

        private static OperatorMatch matchOperator(List<String> words, int index) {
            int max = Math.min(words.size(), index + MAX_OPERATOR_WORDS);
            for (int end = max; end > index; end--) {
                StringBuilder candidate = new StringBuilder();
                for (int i = index; i < end; i++) {
                    if (i > index) {
                        candidate.append(' ');
                    }
                    candidate.append(words.get(i));
                }
                String phrase = candidate.toString();
                if (operatorWords.containsKey(phrase)) {
                    return new OperatorMatch(phrase, end - index);
                }
            }
            return null;
        }
    }

    private static boolean isThousandGrouping(String token) {
        return token.matches("\\d{1,3}(\\.\\d{3})+");
    }

    private static boolean isThousandGroupingWithDecimal(String token) {
        return token.matches("\\d{1,3}(\\.\\d{3})+,\\d+");
    }
}
