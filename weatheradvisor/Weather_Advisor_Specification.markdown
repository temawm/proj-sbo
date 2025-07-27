# Требования к мобильному приложению Weather Advisor

## Описание проекта

Мобильное Android-приложение, которое анализирует прогноз погоды и предоставляет персонализированные рекомендации пользователю для адаптации планов и поведения в зависимости от погодных условий.

## Технический стек

### Основные технологии

- **UI**: Jetpack Compose + Material Design 3
- **Архитектура**: MVI (Model-View-Intent) + Clean Architecture
- **DI**: Hilt (Dagger)
- **База данных**: Room + SQLite
- **Реактивность**: Kotlin Flow + Coroutines
- **Изображения**: Glide/Coil
- **Сеть**: Retrofit + OkHttp + Gson/Moshi
- **Модульность**: Multimodule architecture

### Дополнительные технологии

- **Навигация**: Navigation Compose
- **Настройки**: DataStore (Preferences)
- **Работа в фоне**: WorkManager
- **Уведомления**: Android Notification API
- **Геолокация**: Google Play Services Location
- **Карты**: Google Maps SDK
- **Конфигурация**: Kotlin DSL + buildSrc
- **Тестирование**: JUnit, Mockito, Espresso, Compose Testing
- **Аналитика**: Firebase Analytics
- **Логирование**: Timber

## Архитектура проекта

### Модульная структура

```plaintext
weather-advisor/
├── app/                          # Главный модуль приложения
├── core/
│   ├── common/                   # Общие утилиты и расширения
│   ├── database/                 # Room база данных
│   ├── network/                  # Сетевой слой
│   ├── datastore/               # Настройки и кеширование
│   └── ui/                      # Общие UI компоненты
├── feature/
│   ├── weather/                 # Основной функционал погоды
│   ├── recommendations/         # Логика рекомендаций
│   ├── notifications/           # Уведомления
│   ├── settings/               # Настройки приложения
│   └── profile/                # Пользовательский профиль
└── data/
    ├── weather/                # Репозитории погоды
    ├── location/               # Работа с геолокацией
    └── user/                   # Пользовательские данные
```

### Слои архитектуры

1. **Presentation Layer** (UI + ViewModels)
2. **Domain Layer** (Use Cases + Entities)
3. **Data Layer** (Repositories + Data Sources)

## Функциональные требования

### Основные функции

1. **Получение прогноза погоды**
   - Интеграция с OpenWeatherMap API
   - Получение почасового прогноза на 24 часа
   - Поддержка метрической и имперской систем
2. **Генерация рекомендаций**
   - Анализ температуры, осадков, влажности, ветра
   - Рекомендации по времени дня (утро, день, вечер)
   - Персонализация на основе пользовательских предпочтений
3. **Уведомления**
   - Утренние уведомления с рекомендациями
   - Уведомления при изменении погоды
   - Настраиваемое расписание уведомлений
4. **Кеширование и оффлайн режим**
   - Кеширование данных на 12 часов
   - Работа с последними данными при отсутствии интернета
5. **Настройки местоположения**
   - Автоопределение геолокации
   - Ручной ввод города
   - Поддержка нескольких городов
6. **История рекомендаций**
   - Сохранение последних 30 дней
   - Просмотр архива советов

### Дополнительные функции

1. **Пользовательский профиль**
   - Настройки чувствительности к погоде
   - Предпочтения по типам активности
   - Обратная связь по полезности советов
2. **Виджеты**
   - Домашний экран виджет с текущими рекомендациями
   - Различные размеры виджетов
3. **Темная тема**
   - Поддержка системной темы
   - Ручное переключение тем
4. **Интерактивная карта**
   - Отображение погоды в регионе
   - Радарная карта осадков
5. **ML-модель**
   - Обучение на основе пользовательских откликов
   - Улучшение точности рекомендаций

## Нефункциональные требования

### Безопасность

- API ключи в .env файле
- Шифрование чувствительных данных

### Совместимость

- Android API 24+ (Android 7.0)
- Поддержка различных размеров экранов
- Поддержка разных плотностей экрана

## Планирование разработки

### Этапы разработки

| Этап | Описание | Длительность |
| --- | --- | --- |
| 1\. Настройка проекта | Создание модульной структуры, настройка DI, подключение основных библиотек | 1-2 дня |
| 2\. Core функциональность | Интеграция с погодным API, реализация базы данных, создание репозиториев | 3-4 дня |
| 3\. Основные фичи | Экран погоды, генерация рекомендаций, уведомления | 5-7 дней |
| 4\. Дополнительные фичи | Настройки, история, профиль пользователя | 3-5 дней |
| 5\. Тестирование и оптимизация | Unit тесты, UI тесты, оптимизация производительности | 2-3 дня |

### Оценка времени

- **Общее время разработки**: 14-21 день
- **Команда**: 1-2 Android разработчика
- **Дополнительные задания**: +7-10 дней

## Диаграмма классов

### Presentation Layer

#### ViewModels

```plaintext
WeatherViewModel
├── Fields:
│   ├── - getWeatherUseCase: GetWeatherUseCase
│   ├── - getRecommendationsUseCase: GetRecommendationsUseCase
│   ├── - _uiState: MutableStateFlow<WeatherUiState>
│   └── + uiState: StateFlow<WeatherUiState>
├── Methods:
│   ├── + handleIntent(intent: WeatherIntent): Unit
│   ├── + loadWeather(): Unit
│   └── + refreshWeather(cityName: String): Unit
└── Dependencies: GetWeatherUseCase, GetRecommendationsUseCase
```

#### UI States

```plaintext
WeatherUiState
├── + isLoading: Boolean
├── + weather: Weather?
├── + recommendations: List<Recommendation>
└── + error: String?
```

#### Intents (Sealed Class)

```plaintext
WeatherIntent
├── LoadWeather (object)
├── RefreshWeather
│   └── + cityName: String
└── UpdateLocation
    └── + location: Location
```

### Domain Layer

#### Entities

```plaintext
Weather
├── + cityName: String
├── + temperature: Double
├── + humidity: Int
├── + windSpeed: Double
├── + precipitation: Double
├── + weatherType: WeatherType
├── + description: String
└── + timestamp: Long

Recommendation
├── + id: String
├── + title: String
├── + description: String
├── + category: RecommendationCategory
├── + priority: Int
├── + timestamp: Long
└── + isUseful: Boolean?

UserPreferences
├── + userId: String
├── + temperatureSensitivity: Double
├── + enableRainAlerts: Boolean
├── + enableWindAlerts: Boolean
├── + preferredActivities: List<String>
└── + schedule: NotificationSchedule
```

#### Enumerations

```plaintext
WeatherType (enum)
├── SUNNY
├── CLOUDY
├── RAINY
├── SNOWY
├── STORMY
└── FOGGY

RecommendationCategory (enum)
├── CLOTHING
├── ACCESSORIES
├── ACTIVITY
├── HEALTH
└── TRANSPORT
```

#### Use Cases

```plaintext
GetWeatherUseCase
├── Fields:
│   └── - weatherRepository: WeatherRepository
├── Methods:
│   └── + invoke(cityName: String): Flow<Resource<Weather>>
└── Dependencies: WeatherRepository

GetRecommendationsUseCase
├── Fields:
│   ├── - recommendationRepository: RecommendationRepository
│   └── - recommendationEngine: RecommendationEngine
├── Methods:
│   └── + invoke(weather: Weather, preferences: UserPreferences): Flow<List<Recommendation>>
└── Dependencies: RecommendationRepository, RecommendationEngine

RecommendationEngine
├── Methods:
│   ├── + generateRecommendations(weather: Weather, preferences: UserPreferences): List<Recommendation>
│   ├── - analyzeTemperature(weather: Weather, preferences: UserPreferences): Recommendation?
│   ├── - analyzePrecipitation(weather: Weather): Recommendation?
│   ├── - analyzeWind(weather: Weather): Recommendation?
│   └── - analyzeActivity(weather: Weather, preferences: UserPreferences): Recommendation?
└── Dependencies: None
```

### Data Layer

#### Repository Interfaces

```plaintext
WeatherRepository (interface)
├── + getCurrentWeather(cityName: String?): Flow<Resource<Weather>>
├── + getHourlyForecast(cityName: String): Flow<Resource<List<Weather>>>
└── + getCachedWeather(cityName: String): Weather?

RecommendationRepository (interface)
├── + saveRecommendation(recommendation: Recommendation): Unit
├── + getRecommendationHistory(): Flow<List<Recommendation>>
└── + markRecommendationUseful(id: String, isUseful: Boolean): Unit
```

#### Repository Implementations

```plaintext
WeatherRepositoryImpl (implements WeatherRepository)
├── Fields:
│   ├── - weatherApiService: WeatherApiService
│   ├── - weatherDao: WeatherDao
│   └── - locationProvider: LocationProvider
├── Methods:
│   ├── + getCurrentWeather(cityName: String?): Flow<Resource<Weather>>
│   ├── + getHourlyForecast(cityName: String): Flow<Resource<List<Weather>>>
│   ├── + getCachedWeather(cityName: String): Weather?
│   └── - isCacheExpired(timestamp: Long): Boolean
└── Dependencies: WeatherApiService, WeatherDao, LocationProvider
```

#### Data Sources

##### Network

```plaintext
WeatherApiService (interface)
├── + getCurrentWeather(cityName: String, apiKey: String, units: String): WeatherResponse
└── + getHourlyForecast(cityName: String, apiKey: String, units: String): ForecastResponse

WeatherResponse
├── + main: MainData
├── + weather: List<WeatherData>
├── + wind: WindData
├── + name: String
└── + toDomain(): Weather
```

##### Database

```plaintext
WeatherDatabase (abstract class extends RoomDatabase)
├── + weatherDao(): WeatherDao
├── + recommendationDao(): RecommendationDao
└── + userPreferencesDao(): UserPreferencesDao

WeatherDao (interface)
├── + getWeatherByCity(cityName: String): WeatherEntity?
├── + insertWeather(weather: WeatherEntity): Unit
├── + deleteOldWeather(timestamp: Long): Unit
└── + getAllWeather(): Flow<List<WeatherEntity>>

RecommendationDao (interface)
├── + insertRecommendation(recommendation: RecommendationEntity): Unit
├── + getRecommendationHistory(): Flow<List<RecommendationEntity>>
└── + updateRecommendationFeedback(id: String, isUseful: Boolean): Unit
```

#### Database Entities

```plaintext
WeatherEntity
├── + id: String (PrimaryKey)
├── + cityName: String
├── + timestamp: Long
├── + temperature: Double
├── + humidity: Int
├── + windSpeed: Double
├── + precipitation: Double
├── + weatherType: String
├── + description: String
└── + toDomain(): Weather

RecommendationEntity
├── + id: String (PrimaryKey)
├── + weatherId: String
├── + recommendation: String
├── + category: String
├── + priority: Int
├── + timestamp: Long
├── + isUseful: Boolean?
└── + toDomain(): Recommendation
```

### Infrastructure Layer

#### Background Work

```plaintext
WeatherNotificationWorker (extends CoroutineWorker)
├── Fields:
│   ├── - weatherRepository: WeatherRepository
│   ├── - recommendationEngine: RecommendationEngine
│   └── - notificationManager: WeatherNotificationManager
├── Methods:
│   └── + doWork(): Result
└── Dependencies: WeatherRepository, RecommendationEngine, WeatherNotificationManager

WeatherNotificationManager
├── Fields:
│   └── - context: Context
├── Methods:
│   ├── + sendRecommendationNotification(recommendations: List<Recommendation>): Unit
│   ├── + scheduleNotifications(schedule: NotificationSchedule): Unit
│   └── + cancelNotifications(): Unit
└── Dependencies: Context
```

#### Utilities

```plaintext
LocationProvider
├── + getCurrentLocation(): Location
├── + getCurrentCityName(): String
└── + getCityByCoordinates(lat: Double, lon: Double): String

Resource<T> (sealed class)
├── Loading<T> : Resource<T>
├── Success<T> : Resource<T>
│   └── + data: T
└── Error<T> : Resource<T>
    └── + message: String
```

### Основные связи

#### Dependency Flow (сверху вниз)

1. WeatherViewModel → GetWeatherUseCase → WeatherRepository → WeatherRepositoryImpl
2. WeatherRepositoryImpl → WeatherApiService + WeatherDao + LocationProvider
3. WeatherDao → WeatherEntity → Weather (через toDomain())
4. RecommendationEngine → Recommendation
5. WeatherNotificationWorker → WeatherRepository + RecommendationEngine + WeatherNotificationManager

#### Data Flow

1. API Response → WeatherResponse → Weather (domain) → WeatherEntity (database)
2. User Input → WeatherIntent → WeatherViewModel → Use Cases
3. Background Work → WeatherNotificationWorker → Notification

#### Composition Relationships

- WeatherUiState содержит Weather и List
- WeatherDatabase содержит все DAO
- WeatherRepositoryImpl использует WeatherApiService, WeatherDao, LocationProvider