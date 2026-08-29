# Grocery B2B — Android (Offline MVP)

## What's built

Kotlin + Jetpack Compose, Clean Architecture, multi-module project:

- **`core:core-common`** — `Result<T>` wrapper, `DispatcherProvider`.
- **`core:core-network`** — Retrofit/OkHttp setup, `AuthInterceptor`, `SessionManager` (token storage).
- **`core:core-ui`** — Shared theme (colors/typography sized for readability), `PrimaryButton`, `ErrorText`.
- **`core:core-database`** — Room database. The `shops` table has a unique mobile number, so a shop cannot be registered twice on the same device.
- **`feature:auth`** — offline local sign-in. Existing local shops go to Home; new numbers go to Shop Setup.
- **`feature:shop-setup`** — stores shop name, owner, verified mobile, address, delivery location and landmark locally.
- **`app`** — `MainActivity` and navigation wiring.

## How to open
1. Open the `GroceryB2B/` folder in Android Studio (Koala or newer).
2. Let Gradle sync — it will download the Gradle wrapper JAR automatically on first sync if you don't have `gradle/wrapper/gradle-wrapper.jar` yet (not included here since this sandbox has no internet access; Android Studio generates it on "Sync Project").
3. Run the `app` module on an emulator or device.

## What happens when you run it

Login screen → enter an 11-digit mobile number → enter the local confirmation code `000000` →

- New number → Shop Setup → profile is saved to Room.
- Existing number → Home placeholder.

The local confirmation code is only for this offline MVP. It is not real SMS OTP and must be replaced with server-side OTP verification before production.

## Notes
- Data is stored only on the current device. Uninstalling the app or clearing its storage deletes it. A backend sync and real OTP are required before public launch.
- No `gradle-wrapper.jar` binary is bundled (binary files can't be generated from this environment) — Android Studio will fetch it on first open, or run `gradle wrapper` locally if you have Gradle installed.

## Next steps (in order, per the roadmap)
1. **Home** feature module (search bar, categories, cart icon, order-status card).
2. **Catalog** feature (categories, search, product list/detail) with Room cache.
3. **Cart + Checkout** feature modules.
4. Server-side authentication and secure data synchronization for production.
