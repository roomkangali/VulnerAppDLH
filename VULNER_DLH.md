# 🛡️ Droid-LLM-Hunter: Vulnerability Rules & Definitions

This document provides a comprehensive technical reference for the vulnerability detection rules supported by **Droid-LLM-Hunter (DLH)**. It details the mechanics of each flaw, how DLH detects it, and the standard remediation.

---

## 📋 Comprehensive Rule Reference

### 💉 Injection & Input Validation

#### 1. `sql_injection`
*   **Description:** Raw SQL queries constructed using string concatenation with untrusted user input.
*   **Impact:** **Critical**. Authentication Bypass, Data Leakage, Database Corruption.
*   **Detection Logic:** Looks for `rawQuery()` or `execSQL()` calls containing `+` string concatenation.
*   **Remediation:** Use **Parameterized Queries** (placeholders `?`) instead of direct string building.

#### 2. `graphql_injection`
*   **Description:** GraphQL queries built dynamically by concatenating strings.
*   **Impact:** **High**. Introspection abuse, unauthorized data access.
*   **Detection Logic:** Identifies GraphQL client calls where the query string is assembled at runtime.
*   **Remediation:** Use GraphQL **Variables** (`$variable`) and pass a separate map of arguments.

#### 3. `path_traversal` (Local File Inclusion)
*   **Description:** File operations that trust user-supplied paths without validation.
*   **Impact:** **High**. Arbitrary File Read (e.g., reading `/data/data/pkg/databases`).
*   **Detection Logic:** Flag usage of `FileInputStream(path)` where `path` originates from Intent/UI and contains `../`.
*   **Remediation:** Validate files using `getCanonicalPath()` and check they start with the expected directory prefix.

---

### 🌐 WebView Security

#### 4. `webview_xss`
*   **Description:** WebViews configured with `setJavaScriptEnabled(true)` loading untrusted content.
*   **Impact:** **Medium/High**. Session hijacking, stealing cookies, phasing.
*   **Detection Logic:** Cross-references `setJavaScriptEnabled(true)` with `loadUrl()` calls taking variable inputs.
*   **Remediation:** Disable JavaScript if not needed. If needed, use `addJavascriptInterface` with extreme caution and sanitization.

#### 5. `insecure_webview`
*   **Description:** Dangerous administrative settings enabled on `WebSettings`.
*   **Impact:** **High**. Local File Access via XSS.
*   **Detection Logic:** Flags `setAllowFileAccess(true)` or `setAllowUniversalAccessFromFileURLs(true)`.
*   **Remediation:** Explicitly set these flags to `false` unless absolutely necessary and scoped.

#### 6. `webview_deeplink`
*   **Description:** WebViews that automatically load URLs delivered via Android Intents (Deep Links).
*   **Impact:** **Medium**. Open Redirect, phishing, exposing internal web apps.
*   **Detection Logic:** Traces Intent data flow (`getIntent().getData()`) directly into `webView.loadUrl()`.
*   **Remediation:** Validate the `scheme` and `host` of incoming URLs against an allowlist before loading.

---

### 🔑 Strings, Secrets & Storage

#### 7. `hardcoded_secrets`
*   **Description:** Sensitive API keys, tokens, or credentials found in source code.
*   **Impact:** **Critical**. Service compromise, quota theft, horizontal privilege escalation.
*   **Detection Logic:** Regex pattern matching for high-entropy strings (e.g., `AKIA...`, `eyJ...`) in Java/Kotlin/XML files.
*   **Remediation:** Use `BuildConfig` fields, NDK obfuscation, or (ideally) backend proxying. Never commit secrets to git.

#### 8. `insecure_storage`
*   **Description:** Writing sensitive data to insecurely accessible locations.
*   **Impact:** **High**. Account Takeover (if root/backup accessible), Privacy Violation.
*   **Detection Logic:** Flags `SharedPreferences` storing usage of keywords "password", "token" in plain text.
*   **Remediation:** Use **EncryptedSharedPreferences** (Jetpack Security crypto library).

#### 9. `insecure_file_permissions`
*   **Description:** Creating files with world-readable/writable flags.
*   **Impact:** **Medium**. Data Leakage to other applications on the same device.
*   **Detection Logic:** Usage of `MODE_WORLD_READABLE` or `MODE_WORLD_WRITEABLE` (Deprecated but dangerous).
*   **Remediation:** Use `MODE_PRIVATE` (default) and `FileProvider` for sharing.

---

### 🔐 Cryptography & Authentication

#### 10. `insecure_random_number_generation`
*   **Description:** Using weak Pseudo-Random Number Generators (PRNG) for security contexts.
*   **Impact:** **Medium/High**. Session Token Prediction, ID Guessing.
*   **Detection Logic:** Usage of `java.util.Random` for generating tokens/IDs.
*   **Remediation:** Always use `java.security.SecureRandom`.

#### 11. `biometric_bypass`
*   **Description:** Flawed implementation of BiometricPrompt where authentication logic can be circumvented.
*   **Impact:** **High**. Authentication Bypass.
*   **Detection Logic:** Identification of empty `onAuthenticationFailed` callbacks or reliance on client-side booleans that can be spoofed.
*   **Remediation:** Perform cryptographic operations (e.g., KeyStore unlock) *inside* the `onAuthenticationSucceeded` callback.

---

### 🔄 IPC (Inter-Process Communication)

#### 12. `exported_components`
*   **Description:** Android Components (Activity/Service/Provider) exposed to the public.
*   **Impact:** **High**. Unauthorized access to internal screens, intent injection.
*   **Detection Logic:** Manifest entries with `exported="true"` lacking `android:permission`.
*   **Remediation:** Set `exported="false"` or define malicious signature-level permissions.

#### 13. `intent_spoofing`
*   **Description:** Broadcast Receivers trusting incoming Intents without validation.
*   **Impact:** **Medium/High**. Fake event injection (e.g., "Password Reset Success").
*   **Detection Logic:** Receivers processing actions without checking `getCallingPackage()` or permissions.
*   **Remediation:** Use `LocalBroadcastManager` or enforce permission checks on the receiver.

#### 14. `deeplink_hijack`
*   **Description:** Generic Deep Link handlers that can be claimed by malicious apps.
*   **Impact:** **Medium**. Phishing, Traffic Interception.
*   **Detection Logic:** Intent Filters with `host="*"` or overly broad schemes.
*   **Remediation:** Use **Android App Links** (`autoVerify="true"`) validates ownership via Digital Asset Links JSON.

---

### 🧩 Advanced Logic

#### 15. `universal_logic_flaw`
*   **Description:** Conceptual logic errors not visible via syntax parsing (e.g., Client-Side VIP checks).
*   **Impact:** **Variable**. Business Logic Bypass.
*   **Detection Logic:** **LLM Exclusive**. The AI analyzes the variable names (`isVip`) and control flow to determines if trust is misplaced.
*   **Remediation:** Move critical validation logic to the trusted backend server.

#### 16. `jetpack_compose_security`
*   **Description:** Security misconfigurations in modern UI toolkits.
*   **Impact:** **Low/Medium**. Debug info leakage.
*   **Detection Logic:** Flags `testTag` or debug semantics left in release builds.
*   **Remediation:** Strip debug modifiers in ProGuard/R8 configuration.

---

### 🧨 Deserialization, Reflection & Archive Handling (v2)

#### 17. `insecure_deserialization`
*   **Description:** Deserializing untrusted byte streams into Java objects.
*   **Impact:** **Critical**. Object injection / gadget-chain Remote Code Execution.
*   **Detection Logic:** Usage of `ObjectInputStream.readObject()` on data from an Intent/IPC with no `ObjectInputFilter`/allowlist.
*   **Remediation:** Avoid Java serialization for untrusted data; use JSON with a schema, or set an `ObjectInputFilter` allowlist.

#### 18. `unsafe_reflection`
*   **Description:** Instantiating classes or invoking methods named by attacker-controlled strings.
*   **Impact:** **High/Critical**. Reaching dangerous constructors / gadget classes (potential RCE).
*   **Detection Logic:** `Class.forName()` / `Method.invoke()` / `ClassLoader.loadClass()` where the name comes from an Intent extra.
*   **Remediation:** Map untrusted input to a fixed allowlist of permitted class names; never pass raw input to reflection APIs.

#### 19. `zip_slip`
*   **Description:** Extracting archive entries using their raw names without validating the destination path.
*   **Impact:** **High**. Arbitrary file overwrite via `../` entry names (path traversal on extract).
*   **Detection Logic:** `new File(targetDir, zipEntry.getName())` written out with no `getCanonicalPath()` prefix check.
*   **Remediation:** Resolve each entry's canonical path and verify it starts with the target directory before writing.

---

### 🔗 Advanced IPC (v2)

#### 20. `pending_intent_hijacking`
*   **Description:** Creating a **mutable** `PendingIntent` wrapping an implicit Intent.
*   **Impact:** **High**. Privilege escalation — a receiving app can redirect it and run with this app's identity.
*   **Detection Logic:** `PendingIntent.getActivity/getService/getBroadcast` using `FLAG_MUTABLE` (or missing `FLAG_IMMUTABLE`) with an implicit base Intent.
*   **Remediation:** Use `FLAG_IMMUTABLE`; if mutability is required, set an explicit component on the base Intent.

#### 21. `fragment_injection`
*   **Description:** An exported `PreferenceActivity` that accepts any fragment name from the caller.
*   **Impact:** **High**. Loading arbitrary fragments / bypassing screens (historically RCE-adjacent).
*   **Detection Logic:** A class `extends PreferenceActivity` whose `isValidFragment()` returns `true` (no allowlist).
*   **Remediation:** Override `isValidFragment()` with a strict allowlist, or avoid `PreferenceActivity`.

#### 22. `intent_redirection` 🆕
*   **Description:** An exported component forwards a **nested, attacker-supplied Intent** without validation (confused deputy).
*   **Impact:** **High**. Reaching this app's non-exported components or relaying granted URI permissions.
*   **Detection Logic:** A nested Intent pulled from `getParcelableExtra(...)` is passed to `startActivity()/startService()/sendBroadcast()` with no target validation.
*   **Remediation:** Validate the forwarded Intent's component/target against an allowlist, or set an explicit trusted component; check the caller's identity.

#### 23. `strandhogg`
*   **Description:** Task-affinity / launch-mode configuration that allows task hijacking.
*   **Impact:** **High**. UI overlay / phishing by a malicious app sharing the task affinity.
*   **Detection Logic:** Manifest activity with a custom `android:taskAffinity` + `android:launchMode="singleTask"` (or `allowTaskReparenting`) while exported.
*   **Remediation:** Use the default per-app task affinity; avoid `singleTask`/reparenting for sensitive, exported activities.

---

### 🔑 Strings, Secrets & Storage (v2)

#### 24. `hardcoded_secrets_xml`
*   **Description:** Sensitive keys/tokens embedded in Android resource files (`strings.xml`).
*   **Impact:** **Critical**. Trivially extractable from any released APK (`resources.arsc`).
*   **Detection Logic:** Keyword + high-entropy scan of `res/values/strings.xml` (e.g. `AIza…`, `sk_live_…`, AWS secrets).
*   **Remediation:** Keep secrets out of resources; use `BuildConfig`/NDK or (ideally) a backend proxy. Never ship secrets in the APK.

---

## 🎯 Vulnerabilities in VulnAppDLH (Testbed)

The **VulnAppDLH** application was built specifically to demonstrate the DLH rules below.
Rows **1–7** are the original v1 modules; rows **8–15** are the **v2 testbed expansion**
(the `File` column is the ground-truth answer key used by the Layer-2 golden test).

| ID | Vulnerability Category | Implemented Rules | File(s) | Description |
| :-- | :--- | :--- | :--- | :--- |
| **1** | **Hardcoded Secrets & Insecure Storage** | `hardcoded_secrets`<br>`insecure_storage` | `SecretsActivity` | Contains fake AWS Keys and saves plain-text credentials in SharedPreferences. |
| **2** | **SQL Injection** | `sql_injection` | `SQLInjectionActivity` | A Login page vulnerable to `' OR '1'='1` bypass. |
| **3** | **WebView Exploits** | `webview_xss`<br>`insecure_webview`<br>`webview_file_access` | `WebViewActivity` | A WebView that executes injected JavaScript and allows file access. |
| **4** | **GraphQL Injection** | `graphql_injection` | `GraphQLInjectionActivity` | A query builder that concatenates user input directly into the GraphQL string. |
| **5** | **Insecure File Operations** | `path_traversal`<br>`insecure_file_permissions` | `InsecureFileActivity` | A file reader that accepts `../` to read system files (`/etc/hosts`). |
| **6** | **Weak Crypto & Auth Bypass** | `insecure_random_number_generation`<br>`biometric_bypass`<br>`universal_logic_flaw` | `CryptoActivity` | Predictable session tokens (Time-based seed) and a bypassable Biometric check. |
| **7** | **IPC & Exported Components** | `exported_components`<br>`intent_spoofing`<br>`deeplink_hijack`<br>`webview_deeplink` | `AndroidManifest` | Private activities exposed publicly and a receiver that accepts mocked intents. |
| **8** | **Insecure Deserialization** *(v2)* | `insecure_deserialization` | `DeserializationActivity` | `ObjectInputStream.readObject()` on attacker-supplied Intent bytes. |
| **9** | **Unsafe Reflection** *(v2)* | `unsafe_reflection` | `ReflectionActivity` | `Class.forName()` on a class name taken from an Intent extra. |
| **10** | **PendingIntent Hijacking** *(v2)* | `pending_intent_hijacking` | `PendingIntentActivity` | A `FLAG_MUTABLE` PendingIntent wrapping an implicit Intent. |
| **11** | **Zip Slip** *(v2)* | `zip_slip` | `ZipSlipActivity` | Extracts `File(dir, entry.name)` with no canonical-path check. |
| **12** | **Intent Redirection** *(v2)* 🆕 | `intent_redirection` | `IntentRedirectionActivity` | Forwards a nested Intent from `getParcelableExtra` via `startActivity` (confused deputy). |
| **13** | **Fragment Injection** *(v2)* | `fragment_injection` | `FragmentInjectionActivity` | Exported `PreferenceActivity` with `isValidFragment()` returning `true`. |
| **14** | **StrandHogg** *(v2)* | `strandhogg` | `AndroidManifest` (`HijackTargetActivity`) | Exported activity with custom `taskAffinity` + `launchMode="singleTask"`. |
| **15** | **Hardcoded Secrets (XML)** *(v2)* | `hardcoded_secrets_xml` | `strings.xml` | `google_api_key` / `aws_secret_access_key` / `stripe_secret_key` in resources. |

> **Not covered by this testbed:** `jetpack_compose_security` (requires Jetpack Compose — planned as a separate Compose app) and `deeplink_logic_bypass` (optional/plausible, not a required TP).

---

> [!NOTE]
> This reference guide is maintained automatically by the Droid-LLM-Hunter documentation generator.

> [!TIP]
> **Did You Know?**
> DLH's **Hybrid Engine** can often detect logic flaws that traditional scanners miss because it understands the *context* of the code, not just syntax patterns.