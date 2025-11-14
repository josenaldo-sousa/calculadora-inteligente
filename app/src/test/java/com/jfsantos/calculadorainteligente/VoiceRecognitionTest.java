package com.jfsantos.calculadorainteligente;

import org.junit.Test;
import static org.junit.Assert.*;

import com.jfsantos.voicemath.NativeVoiceRecognizer;

/**
 * Testes unitários para reconhecimento de voz de expressões matemáticas
 */
public class VoiceRecognitionTest {

    @Test
    public void testSimpleAddition() {
        String input = "cinco mais três";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("8", result);
    }

    @Test
    public void testSimpleSubtraction() {
        String input = "dez menos quatro";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("6", result);
    }

    @Test
    public void testSimpleMultiplication() {
        String input = "seis vezes sete";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("42", result);
    }

    @Test
    public void testSimpleDivision() {
        String input = "vinte dividido por quatro";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("5", result);
    }

    @Test
    public void testCompositeNumbers() {
        String input = "vinte e dois mais oito";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("30", result);
    }

    @Test
    public void testCompositeNumbersMultiplication() {
        String input = "trinta e cinco vezes dois";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("70", result);
    }

    @Test
    public void testSquare() {
        String input = "cinco ao quadrado";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("25", result);
    }

    @Test
    public void testCube() {
        String input = "três ao cubo";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("27", result);
    }

    @Test
    public void testSquareRoot() {
        String input = "raiz quadrada de dezesseis";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("4", result);
    }

    @Test
    public void testComplexExpression() {
        String input = "dez mais cinco vezes dois";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("20", result); // (10 + 5) * 2 or 10 + (5*2)?  mXparser respects precedence: 10 + (5*2) = 20
    }

    @Test
    public void testWithPrefix() {
        String input = "quanto é quinze mais cinco";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("20", result);
    }

    @Test
    public void testWithSuffix() {
        String input = "oito vezes três por favor";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("24", result);
    }

    @Test
    public void testDecimalNumbers() {
        String input = "três vírgula cinco mais um vírgula cinco";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("5", result);
    }

    @Test
    public void testHundreds() {
        String input = "cento e vinte mais trinta";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("150", result);
    }

    @Test
    public void testSingleHundredWord() {
        String input = "cem";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("100", result);
    }

    @Test
    public void testSingleThousandWord() {
        String input = "mil";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("1000", result);
    }

    @Test
    public void testThousandDivision() {
        String input = "mil dividido por dois";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("500", result);
    }

    @Test
    public void testThousandDivisionThroughCalculator() {
        Calculator calculator = new Calculator();
        String processed = VoiceCommandProcessor.processVoiceCommand("mil dividido por dois");
    assertEquals("1000 ÷ 2", processed);
        String[] tokens = processed.split("\\s+");
        for (String token : tokens) {
            if (token.matches("\\d+(,\\d+)?")) {
                for (char digit : token.toCharArray()) {
                    if (digit == ',') {
                        calculator.appendDecimal();
                    } else {
                        calculator.appendDigit(String.valueOf(digit));
                    }
                }
            } else if ("(".equals(token)) {
                calculator.appendParenthesis("(");
            } else if (")".equals(token)) {
                calculator.appendParenthesis(")");
            } else {
                calculator.appendOperator(token);
            }
        }
        String result = calculator.calculate();
        assertEquals("500", result);
    }

    @Test
    public void testNormalization() {
        String input = "calcule cinco mais três";
        String normalized = NativeVoiceRecognizer.normalizeMathPhrase(input);
        assertEquals("cinco mais três", normalized);
    }

    @Test
    public void testNormalizationWithSuffix() {
        String input = "dois vezes quatro igual";
        String normalized = NativeVoiceRecognizer.normalizeMathPhrase(input);
        assertEquals("dois vezes quatro", normalized);
    }

    @Test
    public void testLargeNumbers() {
        String input = "noventa e nove mais um";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("100", result);
    }

    @Test
    public void testMultipleOperations() {
        String input = "cem dividido por dois mais dez";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        // 100/2 + 10 = 50 + 10 = 60
        assertEquals("60", result);
    }

    @Test
    public void testZero() {
        String input = "zero mais zero";
        String result = NativeVoiceRecognizer.calculateWithMXParser(input);
        assertEquals("0", result);
    }
}
