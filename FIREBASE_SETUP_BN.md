# People Entertained — Firebase Setup

1. Firebase Console-এ নিজের Google account দিয়ে একটি project তৈরি করুন।
2. Android app যোগ করুন: package name `com.peopleentertained.app`
3. Firebase Authentication → Email/Password চালু করুন।
4. Firestore Database তৈরি করুন।
5. Storage চালু করুন।
6. Project settings থেকে `google-services.json` ডাউনলোড করে এই project-এর `app/` ফোল্ডারে রাখুন।
7. Firebase Console-এর Firestore Rules-এ `firestore.rules`-এর নিয়ম বসান।
8. Storage Rules-এ `storage.rules`-এর নিয়ম বসান।
9. প্রথম যে account-টিকে Admin করবেন, তার Firestore document:
   `users/USER_UID`
   এবং field দিন:
   `role: "admin"`
10. Android Studio দিয়ে project খুলে Gradle Sync করে Run/Build APK করুন।

গুরুত্বপূর্ণ:
- `google-services.json` আমি ইচ্ছা করে ZIP-এ দিইনি—এটি আপনার নিজের Firebase project-এর config।
- কোনো password, private key বা Google account password আমাকে দেবেন না।
- Copyright-free হওয়ার নিশ্চয়তা শুধু Firebase ব্যবহার করলেই আসে না। নিজের/অনুমতিপ্রাপ্ত/লাইসেন্সযুক্ত ভিডিওই আপলোড করুন।
- এই starter-এ admin approval status আছে; production version-এ thumbnail, player, reports, search, notifications, transcoding এবং শক্তিশালী server-side admin claims যোগ করা উচিত।
