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
        if (fullExpression.endsWith("+") || fullExpression.endsWith("-") || fullExpression.endsWith("−") ||
            fullExpression.endsWith("×") || fullExpression.endsWith("÷") || fullExpression.endsWith("%") ||
            fullExpression.endsWith("^")) {
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
        Stack<String> operators = new Stack<>();
        int i = 0;
        boolean lastTokenWasOperator = true;

        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                operators.push("(");
                lastTokenWasOperator = true;
                i++;
            } else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
                    BigDecimal b = numbers.pop();
                    BigDecimal a = numbers.pop();
                    numbers.push(applyOperation(operators.pop(), b, a));
                }
                if (!operators.isEmpty()) operators.pop();
                if (!operators.isEmpty()) {
                    String top = operators.peek();
                    if (isFunction(top)) {
                        operators.pop();
                        BigDecimal arg = numbers.pop();
                        numbers.push(applyFunction(top, arg));
                    }
                }
                lastTokenWasOperator = false;
                i++;
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^') {
                if (c == '-' && lastTokenWasOperator) {
                    i++;
                    StringBuilder num = new StringBuilder("-");
                    while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                        num.append(expr.charAt(i++));
                    }
                    numbers.push(new BigDecimal(num.toString()));
                    lastTokenWasOperator = false;
                } else { // Binary operator
                    String op = String.valueOf(c);
                    while (!operators.isEmpty() && hasPrecedence(op, operators.peek())) {
                        if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
                        BigDecimal b = numbers.pop();
                        BigDecimal a = numbers.pop();
                        numbers.push(applyOperation(operators.pop(), b, a));
                    }
                    operators.push(op);
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
            } else if (Character.isLetter(c) || c == '√') {
                StringBuilder name = new StringBuilder();
                if (c == '√') {
                    name.append('√');
                    i++;
                } else {
                    while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                        name.append(expr.charAt(i++));
                    }
                }
                String func = name.toString();
                if (isFunction(func)) {
                    operators.push(func);
                    lastTokenWasOperator = true;
                }
            } else if (c == '!') {
                if (numbers.isEmpty()) throw new ArithmeticException("Expressão inválida");
                BigDecimal a = numbers.pop();
                numbers.push(applyFactorial(a));
                i++;
            } else {
                i++; // Skip unknown characters
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (isFunction(op)) {
                if (numbers.isEmpty()) throw new ArithmeticException("Expressão inválida");
                BigDecimal a = numbers.pop();
                numbers.push(applyFunction(op, a));
            } else {
                if (numbers.size() < 2) throw new ArithmeticException("Expressão inválida");
                BigDecimal b = numbers.pop();
                BigDecimal a = numbers.pop();
                numbers.push(applyOperation(op, b, a));
            }
        }

        return numbers.isEmpty() ? BigDecimal.ZERO : numbers.pop();
    }

    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(")) return false;
        int p1 = precedence(op1);
        int p2 = precedence(op2);
        if (p2 > p1) return true;
        if (p2 == p1 && !isRightAssociative(op1)) return true;
        return false;
    }

    private int precedence(String op) {
        if (op.equals("^") ) return 3;
        if (op.equals("*") || op.equals("/") || op.equals("%")) return 2;
        if (op.equals("+") || op.equals("-")) return 1;
        return 0;
    }

    private boolean isRightAssociative(String op) {
        return op.equals("^");
    }

    private BigDecimal applyOperation(String operator, BigDecimal b, BigDecimal a) throws ArithmeticException {
        switch (operator) {
            case "+": return a.add(b);
            case "-": return a.subtract(b);
            case "*": return a.multiply(b);
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Não é possível dividir por zero");
                }
                return a.divide(b, 10, RoundingMode.HALF_UP);
            case "%":
                return a.multiply(b.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
            case "^":
                int exp = b.intValue();
                BigDecimal pow = BigDecimal.ONE;
                for (int k = 0; k < exp; k++) pow = pow.multiply(a);
                return pow;
        }
        return BigDecimal.ZERO;
    }

    private boolean isFunction(String name) {
        return name.equals("sin") || name.equals("cos") || name.equals("tan") ||
               name.equals("sqrt") || name.equals("log") || name.equals("ln") || name.equals("√");
    }

    private BigDecimal applyFunction(String name, BigDecimal a) {
        double v = a.doubleValue();
        if (name.equals("sin")) return new BigDecimal(Math.sin(Math.toRadians(v)));
        if (name.equals("cos")) return new BigDecimal(Math.cos(Math.toRadians(v)));
        if (name.equals("tan")) return new BigDecimal(Math.tan(Math.toRadians(v)));
        if (name.equals("sqrt") || name.equals("√")) return new BigDecimal(Math.sqrt(v));
        if (name.equals("log")) return new BigDecimal(Math.log10(v));
        if (name.equals("ln")) return new BigDecimal(Math.log(v));
        return a;
    }

    private BigDecimal applyFactorial(BigDecimal a) {
        int n = a.intValue();
        if (n < 0) throw new ArithmeticException("Expressão inválida");
        BigDecimal res = BigDecimal.ONE;
        for (int k = 2; k <= n; k++) res = res.multiply(BigDecimal.valueOf(k));
        return res;
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

    public void appendFunction(String funcName, String inner) {
        if (funcName == null || inner == null) return;
        String f = funcName.trim();
        String in = inner.trim();
        if (f.isEmpty() || in.isEmpty()) return;
        if (currentNumber.length() > 0) {
            expression += currentNumber.toString();
            currentNumber = new StringBuilder();
            startNewNumber = true;
        }
        expression = expression.trim();
        if (!expression.isEmpty()) {
            expression = expression + " " + f + "(" + in + ")";
        } else {
            expression = f + "(" + in + ")";
        }
    }

    public void appendConstant(String value) {
        if (value == null || value.isEmpty()) return;
        String v = value.replace('.', ',');
        if (startNewNumber) {
            currentNumber = new StringBuilder();
            startNewNumber = false;
        }
        currentNumber.append(v);
    }

    public void appendFactorial() {
        String full = getFullExpression();
        if (currentNumber.length() > 0) {
            expression += currentNumber.toString() + "!";
            currentNumber = new StringBuilder();
            startNewNumber = true;
        } else if (!full.isEmpty()) {
            expression = full + "!";
        }
    }

    public String getCurrentNumberRaw() {
        return currentNumber.toString();
    }

    public boolean isReadyForNewNumber() {
        return startNewNumber;
    }

    public void restoreState(String expressionValue, String currentNumberValue, boolean shouldStartNewNumber) {
        expression = expressionValue != null ? expressionValue : "";
        currentNumber = new StringBuilder();
        if (currentNumberValue != null && !currentNumberValue.isEmpty()) {
            currentNumber.append(currentNumberValue);
        }
        startNewNumber = shouldStartNewNumber;
    }
}
