[TR] Kendi Maceram
Kendi Maceram, kullanıcıların hikayenin gidişatını kendi seçimleriyle belirlediği, her kararın farklı bir yola saptığı interaktif bir "kendi maceranı seç" (choose your own adventure) mobil platformudur. Modern Android geliştirme standartları (Jetpack Compose, Clean Architecture) ile inşa edilen uygulama, sadece bir okuma platformu değil, aynı zamanda TTS (seslendirme) ve zengin bir admin yönetim paneli içeren kapsamlı bir ekosistemdir.

🚀 Öne Çıkan Özellikler
Dallanan Hikaye Kurgusu: Her hikaye, kullanıcının yaptığı seçimlere bağlı olarak tam 32 farklı sondan birine evrilir.
Akıllı TTS (Metin Okuma) Sistemi: Hikayeler sadece okunmakla kalmaz; dahili seslendirme motoru ve kullanıcı dostu Play/Pause kontrolleriyle dinlenebilir.
Kapsamlı Firebase Entegrasyonu: - Authentication: Google hesabı ile hızlı giriş ve "Hesabı Sil" gibi kullanıcı haklarını koruyan sistemler.
Firestore & Storage: Hikaye metinleri, metadata ve özgün kapak resimlerinin bulut üzerinden anlık senkronizasyonu.
Gelişmiş Admin Paneli: Hikaye yöneticisi modülü sayesinde içeriklerin dinamik olarak yönetilebilmesi.
Kişiselleştirilmiş Deneyim: - Kütüphane: Okunan veya beğenilen hikayeleri kişisel alana ekleme.
Keşfet: "Yeni Hikayeler" ekranı üzerinden sürekli güncel içeriklere erişim.
Modern Görsel Dil: Gradyan arka planlar, Edge-to-Edge tam ekran deneyimi ve kullanıcıyı içine çeken UI bileşenleri.

🛠️ Teknik Stack & Mimari
Uygulama, ölçeklenebilirlik ve sürdürülebilirlik odaklı en güncel teknolojilerle geliştirilmiştir:

Dil: %100 Kotlin
Mimari: MVVM (Model-View-ViewModel) ile ayrıştırılmış katmanlı yapı (Data, Domain, UI).
UI Framework: Jetpack Compose (Tamamen deklaratif arayüz).
Dependency Injection: Hilt (Dagger tabanlı modern DI).
Veri İşleme: - KSP (Kotlin Symbol Processing): Yüksek performanslı kod üretimi.
Repository Pattern: Veri kaynaklarının (Local & Remote) soyutlanması.
Navigasyon: Navigation Compose ile tip güvenli (type-safe) ekran geçişleri.
Backend: Firebase (Auth, Firestore, Storage, Cloud Messaging).
Analiz & Monitoring: Google Cloud OpenTelemetry.


[ENG] Kendi Maceram (My Own Adventure)
Kendi Maceram is an interactive "choose your own adventure" mobile platform where users shape the narrative through their decisions. Built with modern Android standards (Jetpack Compose, Clean Architecture), it is a comprehensive ecosystem featuring TTS (Text-to-Speech) capabilities and a robust administrative management panel.

🚀 Key Features
Branching Narratives: Every story evolves based on user choices, leading to one of 32 unique endings.
Integrated TTS Engine: Stories can be listened to using a built-in reader with intuitive Play/Pause controls.
Full Firebase Integration: - Authentication: Rapid Google Sign-In and user rights management, including account deletion.
Firestore & Storage: Real-time synchronization of story scripts, metadata, and unique cover images.
Advanced Admin Panel: A dedicated "Story Manager" module for dynamic content management.
Personalized User Experience: - Library: Save favorite or ongoing stories to a personal collection.
Discovery: Access fresh content via the "New Stories" screen.
Modern Visual Language: Immersive UI with gradient backgrounds and Edge-to-Edge full-screen design.

🛠️ Tech Stack & Architecture
The app is built with a focus on scalability and maintainability using the latest technologies:

Language: 100% Kotlin
Architecture: MVVM (Model-View-ViewModel) with a layered structure (Data, Domain, UI).
UI Framework: Jetpack Compose (Fully declarative UI).
Dependency Injection: Hilt.
Data Processing: - KSP (Kotlin Symbol Processing): High-performance code generation.
Repository Pattern: Abstraction of data sources (Local & Remote).

Navigation: Navigation Compose for type-safe screen transitions.

Backend: Firebase (Auth, Firestore, Storage, Cloud Messaging).

Monitoring: Google Cloud OpenTelemetry for performance tracking.
