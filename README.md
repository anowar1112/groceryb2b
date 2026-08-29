# Grocery B2B — Android (Step 1: Auth Module)

## What's built in this step
Full Kotlin + Jetpack Compose, Clean Architecture, multi-module project:

- **`core:core-common`** — `Result<T>` wrapper, `DispatcherProvider`.
- **`core:core-network`** — Retrofit/OkHttp setup, `AuthInterceptor`, `SessionManager` (token storage).
- **`core:core-ui`** — Shared theme (colors/typography sized for readability), `PrimaryButton`, `ErrorText`.
- **`feature:auth`** — Complete Mobile-OTP login flow:
  - `domain`: `AuthRepository` interface, `RequestOtpUseCase`, `VerifyOtpUseCase` (with input validation)
  - `data`: `AuthApi` (Retrofit), DTOs, `AuthRepositoryImpl` (maps HTTP errors to Bengali user-facing messages)
  - `presentation`: `LoginScreen`, `OtpScreen`, `AuthViewModel` (resend cooldown timer, navigation side-effects)
  - `di`: Hilt module wiring
- **`app`** — `MainActivity`, `AppNavHost` wiring the auth flow to placeholder Shop-Setup/Home screens.

## How to open
1. Open the `GroceryB2B/` folder in Android Studio (Koala or newer).
2. Let Gradle sync — it will download the Gradle wrapper JAR automatically on first sync if you don't have `gradle/wrapper/gradle-wrapper.jar` yet (not included here since this sandbox has no internet access; Android Studio generates it on "Sync Project").
3. Point `core-network`'s `BASE_URL` (in `core/core-network/build.gradle.kts`) at your actual backend once it exists — right now it calls `POST /auth/otp/request` and `POST /auth/otp/verify` per the API plan.
4. Run the `app` module on an emulator or device.

## What happens when you run it
Login screen → enter an 11-digit mobile number → OTP screen (with resend cooldown) → on successful verify:
- New shop → placeholder "Shop Setup" screen (real screen = Step 2)
- Existing shop → placeholder "Home" screen (real screen = Step 3)

Both placeholders are marked with `TODO(step 2)` / `TODO(step 3)` in `AppNavHost.kt` — they'll be replaced as we build those feature modules.

## Notes
- Backend isn't built yet — `AuthApi` expects the contract described in the architecture doc (Section 8). Until a real backend exists, calls will fail; you can point `BASE_URL` at a mock server (e.g. Postman Mock or json-server) to test the UI end-to-end.
- No `gradle-wrapper.jar` binary is bundled (binary files can't be generated from this environment) — Android Studio will fetch it on first open, or run `gradle wrapper` locally if you have Gradle installed.

## Next steps (in order, per the roadmap)
1. **Shop Setup** feature module (shop name, owner, address, delivery location) — completes the "new shop" path.
2. **Home** feature module (search bar, categories, cart icon, order-status card).
3. **core-database** (Room) for cart persistence and offline catalog caching.
4. **Catalog** feature (categories, search, product list/detail).
5. **Cart + Checkout** feature modules.
