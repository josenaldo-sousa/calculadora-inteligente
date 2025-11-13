# Calculadora Inteligente para Android

Uma calculadora completa para Android com suporte a reconhecimento de voz em português brasileiro.

## Funcionalidades

### Operações Básicas
- ✅ Adição (+)
- ✅ Subtração (−)
- ✅ Multiplicação (×)
- ✅ Divisão (÷)
- ✅ Porcentagem (%)
- ✅ Números decimais

### Reconhecimento de Voz
- 🎤 Entrada por voz em português brasileiro
- 🗣️ Reconhecimento de números por extenso
- 🔢 Suporte a operações matemáticas faladas
- 📱 Comandos como "limpar", "apagar", "calcular"

### Interface
- 📱 Design moderno com Material Design
- 🎨 Cores diferenciadas para números, operadores e funções
- 📊 Display grande para visualização
- ⌨️ Layout intuitivo de calculadora

## Exemplos de Comandos de Voz

### Números
- "Cinco mais três"
- "Dez vezes dois"
- "Vinte dividido por quatro"
- "Quinze menos sete"

### Operações Completas
- "Dois mais dois igual" → Calcula automaticamente
- "Cinco vezes três" → Mostra no display
- "Cem dividido por quatro resultado" → Calcula

### Comandos Especiais
- "Limpar" → Limpa tudo
- "Apagar" → Deleta último dígito

## Requisitos

- Android 7.0 (API 24) ou superior
- Permissão de microfone para reconhecimento de voz
- Google Play Services para Speech Recognition

## Como Compilar

### Usando Android Studio
1. Abra o Android Studio
2. Selecione "Open an Existing Project"
3. Navegue até a pasta `calculadora-inteligente`
4. Aguarde o Gradle sincronizar
5. Clique em "Run" ou pressione Shift+F10

### Usando Linha de Comando
```bash
# No diretório do projeto
./gradlew assembleDebug

# Instalar no dispositivo conectado
./gradlew installDebug
```

## Estrutura do Projeto

```
calculadora-inteligente/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/calculadorainteligente/
│   │       │   ├── MainActivity.java          # Activity principal
│   │       │   ├── Calculator.java            # Lógica de cálculo
│   │       │   └── VoiceCommandProcessor.java # Processamento de voz
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml      # Layout da calculadora
│   │       │   └── values/
│   │       │       ├── strings.xml            # Strings do app
│   │       │       ├── colors.xml             # Cores
│   │       │       ├── themes.xml             # Temas
│   │       │       └── dimens.xml             # Dimensões
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Classes Principais

### MainActivity.java
- Gerencia a interface do usuário
- Implementa os listeners dos botões
- Controla o reconhecimento de voz
- Gerencia permissões

### Calculator.java
- Implementa a lógica de cálculo
- Avalia expressões matemáticas
- Gerencia o estado da calculadora
- Formata resultados

### VoiceCommandProcessor.java
- Processa comandos de voz
- Converte números por extenso
- Identifica operadores matemáticos
- Traduz comandos para expressões

## Permissões

O app solicita as seguintes permissões:

- `RECORD_AUDIO` - Necessária para reconhecimento de voz
- `INTERNET` - Para serviços de reconhecimento de voz

## Recursos Implementados

- ✅ Calculadora completa com todas operações básicas
- ✅ Reconhecimento de voz em português
- ✅ Interface Material Design moderna
- ✅ Tratamento de erros (divisão por zero, etc.)
- ✅ Suporte a números decimais
- ✅ Porcentagem
- ✅ Funções limpar e deletar
- ✅ Display de expressão e resultado

## Próximas Melhorias Sugeridas

- 📊 Histórico de cálculos
- 🌙 Modo escuro
- 🔬 Funções científicas (seno, cosseno, raiz, potência)
- 💾 Memória (M+, M-, MR, MC)
- 🌍 Suporte a outros idiomas
- 📱 Vibração ao pressionar botões
- 🎵 Sons personalizados

## Licença

Este projeto é livre para uso educacional e pessoal.
