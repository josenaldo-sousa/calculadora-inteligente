## Quick orientation for AI coding agents

This repository is an Android app (Java) named "Calculadora Inteligente" with voice-driven math input (Portuguese primary). The goal of this file is to give an AI agent immediate, actionable context so it can make safe code edits, fix builds, and extend voice/math behavior.

- Language & build: Java (Android), Gradle wrapper. Key build commands:
  - ./gradlew build   (full build)
  - ./gradlew assembleDebug
  - ./gradlew installDebug (installs on connected device)

- High level architecture (big picture):
  - UI layer: `app/src/main/.../MainActivity` and view bindings handle display and user interactions.
  - Voice capture: `app/src/main/java/com/jfsantos/voicemath/NativeVoiceRecognizer.java` — wraps Android SpeechRecognizer, normalizes phrases with `normalizeMathPhrase`, and forwards text to the processor.
  - Voice-to-expression: `app/src/main/java/com/jfsantos/calculadorainteligente/VoiceCommandProcessor.java` — maps spoken words to numbers/operators (`numberWords`, `operatorWords`), applies textual transforms in `convertToExpression`, and evaluates expressions (there is also a local parser `ExpressionEvaluator`, but mXparser is now used elsewhere).
  - Math engine: mXparser dependency added in `app/build.gradle` (`org.mariuszgromada.math:MathParser.org-mXparser:6.1.0`) — used for robust expression evaluation.

- Key files to inspect/edit:
  - `app/build.gradle` — SDK versions, dependencies (mXparser), and any local JARs.
  - `app/src/main/java/com/jfsantos/voicemath/NativeVoiceRecognizer.java` — normalization, language settings, and where speech results are packaged.
  - `app/src/main/java/com/jfsantos/calculadorainteligente/VoiceCommandProcessor.java` — number/operator mapping and expression conversion rules.
  - `README.md` — project overview and standard dev instructions.

- Common change patterns and examples (do this when modifying behavior):
  - Add new spoken operator: update `operatorWords.put("frase", "+")` in `VoiceCommandProcessor.initOperators()` and add any replacement rules in `convertToExpression` if phrase contains spaces (longest match wins).
  - Add new number words or composite-handling: extend `numberWords` in `VoiceCommandProcessor.initNumbers()` and ensure composite patterns (e.g. "vinte e três") are handled by `convertToExpression` transforms.
  - Adjust normalization rules: edit `NativeVoiceRecognizer.normalizeMathPhrase()` to only remove contextual prefixes (keep it conservative — over-aggressive normalizing breaks recognition).
  - Use mXparser: call `new org.mariuszgromada.math.mxparser.Expression(expr).calculate()`; check for NaN to detect invalid expressions.

- Integrations & runtime details:
  - Permissions: app requires `RECORD_AUDIO`. Tests targeting voice must run on a device/emulator with microphone and Google recognition available.
  - Language: default locale set in `NativeVoiceRecognizer` (Portuguese). To add English support, set `setLanguage(Locale.ENGLISH)` on the recognizer instance and ensure `VoiceCommandProcessor` maps include English words.
  - mXparser: dependency present in `app/build.gradle`. If build can't fetch it, fall back by adding a JAR to `app/libs/` and referencing it with `implementation files('libs/...jar')`.

- Developer workflows & debugging tips:
  - Build: run `./gradlew build` (CI runs this). If failing on Android imports, confirm Android SDK is available in environment running the build.
  - Quick iteration: use `./gradlew assembleDebug` and `installDebug` to push to device.
  - Logs: add `Log.d(TAG, ...)` in `NativeVoiceRecognizer.onResults()` and `VoiceCommandProcessor.processVoiceCommand()` to trace the voice -> normalize -> convert -> evaluate flow.
  - Repro steps for voice issues: record sample recognized strings (the adapter already normalizes candidates), run `VoiceCommandProcessor.convertToExpression(...)` locally to see converted expression, then run mXparser evaluation.

- Patterns & conventions specific to this repo:
  - Conversions are driven by large `Map<String,String>` tables for numbers/operators — modify these maps rather than scattering string matches across the codebase.
  - `convertToExpression()` applies regex transforms, then does longest-key replacement for operators and numbers. Keep transforms before replacement.
  - Keep normalization conservative in `NativeVoiceRecognizer.normalizeMathPhrase()` — it should remove only context prefixes (e.g., "quanto é", "calcule").

- Safety and testing notes for AI edits:
  - Avoid changing voice-model behavior and normalizers in bulk; small, iterative edits are easier to test on device.
  - When adding new replacements, include unit-like checks: e.g., sample input -> expected expression -> expected evaluation (you can run small Java methods locally or add Android instrumentation tests).

If anything above is unclear or you want the instructions to include additional examples (e.g., a short checklist for adding a new operator with tests), tell me which area to expand and I will iterate.
