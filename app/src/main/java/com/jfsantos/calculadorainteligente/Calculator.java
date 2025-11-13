package com.jfsantos.calculadorainteligente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Stack;

public class Calculator {
    private StringBuilder currentNumber;
    private String expression;
    private boolean startNewNumber;

    public Calculator() {
        clear();
    }

    public void appendDigit(String digit) {
        if (startNewNumber) {
            currentNumber = new StringBuilder();
            startNewNumber = false;
        }
        currentNumber.append(digit);
    }

    public void appendDecimal() {
        if (startNewNumber) {
            currentNumber = new StringBuilder("0");
            startNewNumber = false;
        }
        if (!currentNumber.toString().contains(",")) {
            currentNumber.append(",");
        }
    }

    public void appendOperator(String operator) {
        if (currentNumber.length() > 0) {
            expression += currentNumber.toString() + " " + operator + " ";
            currentNumber = new StringBuilder();
            startNewNumber = true;
        } else if (expression.length() > 0) {
            expression = expression.trim();
            if (expression.endsWith("+") || expression.endsWith("-") || expression.endsWith("×") || expression.endsWith("÷") || expression.endsWith("%")) {
                expression = expression.substring(0, expression.length() - 1).trim();
            }
            expression = expression + " " + operator + " ";
        }
    }

    public void appendParenthesis(String parenthesis) {
        if (currentNumber.length() > 0) {
            expression += currentNumber.toString() + " ";
            currentNumber = new StringBuilder();
            startNewNumber = true;
        }
        expression += parenthesis + " ";
    }

    public String getFullExpression() {
        String full = expression;
        if (currentNumber.length() > 0) {
            full += currentNumber.toString();
        }
        return full.trim();
    }

    public String evaluatePartial() {
        String fullExpression = getFullExpression();
        if (fullExpression.isEmpty()) return "0";

        fullExpression = fullExpression.trim();
        if (fullExpression.endsWith("+") || fullExpression.endsWith("-") ||
            fullExpression.endsWith("×") || fullExpression.endsWith("÷") || fullExpression.endsWith("%")) {
            fullExpression = fullExpression.substring(0, fullExpression.length()-1).trim();
        }

        if (fullExpression.isEmpty()) return "0";

        try {
            BigDecimal result = evaluateExpression(fullExpression);
            return formatBigDecimal(result);
        } catch (Exception e) {
            return "Erro";
        }
    }

    public String getCurrentDisplay() {
        if (currentNumber.length() == 0 && startNewNumber) {
            return "0";
        }
        return currentNumber.length() > 0 ? currentNumber.toString() : "0";
    }

    public String getExpression() {
        return expression;
    }

    public void clear() {
        currentNumber = new StringBuilder();
        expression = "";
        startNewNumber = true;
    }

    public void delete() {
        if (currentNumber.length() > 0 && !startNewNumber) {
            currentNumber.deleteCharAt(currentNumber.length() - 1);
        }
    }

    public void setExpression(String expr) {
        this.expression = expr;
    }

    public void setCurrentNumber(String number) {
        this.currentNumber = new StringBuilder(number);
        this.startNewNumber = false;
    }

    public String calculate() throws ArithmeticException {
        String fullExpression = getFullExpression();

        if (fullExpression.isEmpty()) {
            return "0";
        }

        try {
            BigDecimal result = evaluateExpression(fullExpression);
            return formatBigDecimal(result);
        } catch (ArithmeticException e) {
            throw new ArithmeticException(e.getMessage());
        }
    }

    private BigDecimal evaluateExpression(String expr) throws ArithmeticException {
        expr = expr.replace("×", "*").replace("÷", "/").replace("−", "-").replace(',', '.');

        Stack<BigDecimal> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;
        boolean lastTokenWasOperator = true;

        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                operators.push(c);
                lastTokenWasOperator = true;
                i++;
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
                    BigDecimal b = numbers.pop();
                    BigDecimal a = numbers.pop();
                    numbers.push(applyOperation(operators.pop(), b, a));
                }
                if (!operators.isEmpty()) operators.pop(); // Pop '('
                lastTokenWasOperator = false;
                i++;
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                if (c == '-' && lastTokenWasOperator) { // Unary minus
                    i++;
                    StringBuilder num = new StringBuilder("-");
                    while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                        num.append(expr.charAt(i++));
                    }
                    numbers.push(new BigDecimal(num.toString()));
                    lastTokenWasOperator = false;
                } else { // Binary operator
                    while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                        if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
                        BigDecimal b = numbers.pop();
                        BigDecimal a = numbers.pop();
                        numbers.push(applyOperation(operators.pop(), b, a));
                    }
                    operators.push(c);
                    lastTokenWasOperator = true;
                    i++;
                }
            } else if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                numbers.push(new BigDecimal(num.toString()));
                lastTokenWasOperator = false;
            } else {
                i++; // Skip unknown characters
            }
        }

        while (!operators.isEmpty()) {
            if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
            BigDecimal b = numbers.pop();
            BigDecimal a = numbers.pop();
            numbers.push(applyOperation(operators.pop(), b, a));
        }

        return numbers.isEmpty() ? BigDecimal.ZERO : numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        // Para avaliação da esquerda para a direita, todos os operadores (exceto parênteses) têm a mesma precedência.
        return true;
    }

    private BigDecimal applyOperation(char operator, BigDecimal b, BigDecimal a) throws ArithmeticException {
        switch (operator) {
            case '+': return a.add(b);
            case '-': return a.subtract(b);
            case '*': return a.multiply(b);
            case '/':
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Não é possível dividir por zero");
                }
                return a.divide(b, 10, RoundingMode.HALF_UP);
            case '%':
                return a.multiply(b.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        }
        return BigDecimal.ZERO;
    }

    public String calculatePercent() {
        if (currentNumber.length() > 0) {
            try {
                BigDecimal value = new BigDecimal(currentNumber.toString().replace(',', '.'));
                BigDecimal result = value.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                return formatBigDecimal(result);
            } catch (Exception e) {
                return getCurrentDisplay();
            }
        }
        return getCurrentDisplay();
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) return "0";
        String plainString = value.stripTrailingZeros().toPlainString();
        return plainString.replace('.', ',');
    }

    public void appendSubExpression(String subExpr) {
        if (subExpr == null) return;
        String s = subExpr.trim();
        if (s.isEmpty()) return;

        if (currentNumber.length() > 0) {
            expression += currentNumber.toString();
            currentNumber = new StringBuilder();
            startNewNumber = true;
        }

        String[] parts = s.split("\\s+");
        if (parts.length == 0) return;

        String first = parts[0];
        boolean firstIsOperator = first.matches("[+−×÷%]");
        String inner;
        if (firstIsOperator) {
            if (parts.length == 1) return;
            inner = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            expression = expression.trim();
            if (!expression.isEmpty()) {
                expression = expression + " " + first + " (" + inner + ")";
            } else {
                expression = first + " (" + inner + ")";
            }
        } else {
            inner = String.join(" ", parts);
            expression = expression.trim();
            if (!expression.isEmpty()) {
                char last = expression.charAt(expression.length() - 1);
                if (last == '+' || last == '-' || last == '×' || last == '÷' || last == '%' || last == '(') {
                    expression = expression + " (" + inner + ")";
                } else {
                    expression = expression + " (" + inner + ")";
                }
            } else {
                expression = "(" + inner + ")";
            }
        }
    }
}