import com.jfsantos.calculadorainteligente.VoiceCommandProcessor;

public class TestDecimal {
    public static void main(String[] args) {
        String[] testCases = {
            "dois virgula cinco",
            "dez virgula zero cinco",
            "cinco virgula sete",
            "um virgula dois tres",
            "vinte virgula um"
        };
        
        for (String test : testCases) {
            String result = VoiceCommandProcessor.processVoiceCommand(test);
            System.out.println("Input: '" + test + "' -> Output: '" + result + "'");
        }
    }
}
