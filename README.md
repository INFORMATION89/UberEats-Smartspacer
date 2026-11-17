# Uber Eats for Smartspacer

A [Smartspacer](https://github.com/KieronQuinn/Smartspacer) plugin that shows your Uber Eats order on your home and lock screen while it's on its way: Uber Eats' status ("Preparing your order", "Arriving at 7:42 PM"), the restaurant or courier, and the time left or how far along the order is, with the courier's photo and Uber Eats' logo. Tapping the card opens the order in Uber Eats. It can also hide the order notification on the lock screen while the card is showing (with Native Smartspace), and its settings show a live preview of the card. The plugin is in English and Spanish, and the card shows Uber Eats' own text in whatever language Uber Eats uses.

To set it up, install the APK from the [latest release](../../releases/latest), turn on **Notification access** for *Uber Eats for Smartspacer*, then add the **Uber Eats** Target in Smartspacer and place an order.

If Google Play Protect blocks the install ("App blocked to protect your device"), that's because the APK comes from a download and asks for Notification access, not because anything is wrong with it. Either install it from a computer with `adb install uber-eats-smartspacer-<version>.apk`, or open the Play Store → your profile → **Play Protect** → ⚙ and turn off **Scan apps with Play Protect**, install the APK, then turn it back on.

## Why Notification access

Uber Eats has no API for your orders, so the plugin reads the order from Uber Eats' tracking notification, and Android only allows that with Notification access. Here's what it does with it:

- **It reads only Uber Eats' notifications.** Every other notification is ignored: the plugin checks which app a notification comes from, and nothing else, before dropping it. Uber Eats' promos are read and skipped too, since only the order's live tracking notification is shown.
- **It has no internet permission**, so it can't send anything anywhere. The order stays in memory while it's on its way and is gone when it arrives; the only thing saved is your settings.
- **It never dismisses or changes notifications.** Hiding the order notification on the lock screen is done by Smartspacer while the card is showing; the notification stays in your shade.

You can check all of this in the source code, or turn Notification access off at any time (the card just stops appearing).

It's a personal project built with help from Claude, not affiliated with Uber or Smartspacer, and licensed under [GPL-3.0](LICENSE).
