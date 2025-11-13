package com.jfsantos.calculadorainteligente;

/**
 * Classe de teste para VoiceCommandProcessor
 * Teste os parsing de números compostos
 */
public class VoiceCommandProcessorTest {
    
    public static void main(String[] args) {
        testVoiceProcessing();
    }
    
    private static void testVoiceProcessing() {
        // Teste números simples
        test("cinco", "5");
        test("dez", "10");
        test("vinte", "20");
        test("cem", "100");
        test("mil", "1000");
        
        // Testes números compostos (PRINCIPAIS)
        test("vinte e um", "21");
        test("vinte e cinco", "25");
        test("trinta e dois", "32");
        test("quarenta e sete", "47");
        test("noventa e nove", "99");
        
        // Testes com centenas
        test("cento e vinte", "120");
        test("duzentos e trinta e quatro", "234");
        test("trezentos", "300");
        test("quatrocentos e cinquenta e seis", "456");
        
        // Testes com "mil" - CRÍTICOS
        test("mil", "1000");
        test("dois mil", "2000");
        test("dois mil trezentos", "2300");
        test("dois mil trezentos e quarenta e cinco", "2345");
        test("cinco mil", "5000");
        test("dez mil", "10000");
        test("cento e vinte e três mil", "123000");
        
        // Testes com milhão
        test("um milhao", "1000000");
        test("dois milhao", "2000000");
        
        // Testes com decimais
        test("dois virgula cinco", "2,5");
        test("dez virgula zero cinco", "10,05");
        test("mil virgula dois", "1000,2");
        
        // Testes com operadores
        test("cinco mais três", "5 + 3");
        test("vinte e um menos dez", "21 − 10");
        test("dois mil vezes três", "2000 × 3");
        test("cem dividido por cinco", "100 ÷ 5");
    }
    
    private static void test(String input, String expectedOutput) {
        String result = VoiceCommandProcessor.processVoiceCommand(input);
        boolean passed = result.equals(expectedOutput);
        String status = passed ? "✓ PASS" : "✗ FAIL";
        System.out.println(status + " | Input: \"" + input + "\" | Expected: \"" + expectedOutput + "\" | Got: \"" + result + "\"");
    }
}
