# Prompt نقل مشروع Spring Boot إلى محادثة Flutter جديدة

انسخ هذا النص كاملًا إلى محادثة جديدة مع المساعد الذي سيبني/يكمل مشروع Flutter.

---

أنت تعمل على تطبيق Flutter كامل لتطبيق نقل وحجز رحلات اسمه غالبًا `Esselam Transport`. يوجد Backend جاهز بـ Spring Boot يجب أن تبني Flutter فوقه. المطلوب أن تفهم بنية الـ API وتبني الواجهات والـ services والـ models بناءً عليها.

## معلومات عامة عن الـ Backend

- نوع المشروع: Spring Boot REST API.
- لغة backend: Java 17.
- Spring Boot version: `3.2.4`.
- Group/package: `com.transport`.
- قاعدة البيانات الحالية محليًا: H2 in-memory.
- المنفذ المحلي: `9090`.
- Base URL عند التشغيل على نفس الجهاز:

```text
http://localhost:9090
```

- Base URL داخل Android emulator:

```text
http://10.0.2.2:9090
```

- API base المقترح في Flutter:

```text
http://10.0.2.2:9090/api
```

إذا كان التشغيل على هاتف حقيقي في نفس Wi-Fi استخدم IP الجهاز الذي يشغل Spring Boot:

```text
http://YOUR_PC_IP:9090/api
```

## بنية Spring Boot المهمة

المجلد الرئيسي:

```text
src/main/java/com/transport
```

الموديلات الأساسية:

```text
model/User.java
model/Driver.java
model/Trip.java
model/Booking.java
model/TripStatus.java
model/BookingStatus.java
model/TripShare.java
```

الـ controllers:

```text
controller/UserController.java
controller/TripController.java
controller/BookingController.java
controller/DriverController.java
controller/AdminController.java
```

الـ services:

```text
service/UserService.java
service/TripService.java
service/BookingService.java
service/DriverService.java
service/TripShareService.java
```

الـ DTOs:

```text
dto/RegisterDto.java
dto/BookingRequest.java
dto/DriverRequest.java
dto/TripRequest.java
dto/TripLocationUpdateRequest.java
dto/TripTrackingResponse.java
dto/ShareLinkResponse.java
```

## Auth الحالي

لا يوجد Spring Security حقيقي حاليًا. يوجد تسجيل/دخول بسيط عبر:

### Register

```http
POST /auth/register
Content-Type: application/json
```

Body:

```json
{
  "telephone": "22220001",
  "code": "1234"
}
```

Returns string:

```text
COMPTE_CREE
NUMERO_EXISTE
```

### Login

```http
POST /auth/login
Content-Type: application/json
```

Body:

```json
{
  "telephone": "22220001",
  "code": "1234"
}
```

Returns string:

```text
SUCCESS
CODE_INCORRECT
```

ملاحظة مهمة للـ Flutter:

- المطلوب مؤقتًا: إذا كتب المستخدم في صفحة login العادية:

```text
telephone = admin
code = admin
```

افتح صفحة Admin محليًا في Flutter بدون الحاجة إلى backend security.

## Model: Driver

حقول السائق:

```json
{
  "id": "uuid",
  "fullName": "Ahmed Salem",
  "phone": "22220001",
  "licenseNumber": "MR-LIC-1001",
  "vehicleName": "Toyota Hiace",
  "vehiclePlate": "NKC-1001",
  "available": true
}
```

## Model: Trip

حقول الرحلة:

```json
{
  "id": "uuid",
  "tripNumber": "TRIP-1001",
  "driver": {
    "id": "uuid",
    "fullName": "Ahmed Salem",
    "phone": "22220001",
    "licenseNumber": "MR-LIC-1001",
    "vehicleName": "Toyota Hiace",
    "vehiclePlate": "NKC-1001",
    "available": true
  },
  "departureCity": "Rosso",
  "destinationCity": "Nouakchott",
  "departureTime": "2026-06-20T08:00:00",
  "arrivalTime": "2026-06-20T11:00:00",
  "price": 3000,
  "transportType": "Bus",
  "availableSeats": 45,
  "companyName": "Esselam",
  "status": "SCHEDULED",
  "progressPercentage": 0,
  "departureLatitude": 16.5138,
  "departureLongitude": -15.805,
  "destinationLatitude": 18.0735,
  "destinationLongitude": -15.9582,
  "currentLatitude": 17.05,
  "currentLongitude": -15.88,
  "lastLocationUpdate": "2026-06-19T17:30:00"
}
```

TripStatus enum:

```text
SCHEDULED
IN_PROGRESS
ARRIVED
CANCELLED
```

## User Flow المطلوب في Flutter

التطبيق عند البداية يعرض اختيار الدور:

```text
Regular User
Driver
```

### Regular User

يدخل برقم هاتف وكود.

إذا:

```text
telephone = admin
code = admin
```

يتم فتح لوحة Admin.

غير ذلك:

- يفتح تطبيق المستخدم العادي.
- يستطيع البحث عن الرحلات.
- يستطيع حجز مقاعد.
- يستطيع عرض حجوزاته.
- يستطيع الدخول إلى صفحة تتبع رحلة بكتابة `tripNumber`.

### Driver

يدخل برقم هاتف وكود/Pin.

بعد الدخول:

- تظهر الصفحة الرئيسية للسائق.
- توجد صفحة "Mes trajets" أو "رحلاتي المسجلة".
- هذه الصفحة يجب أن تأتي من API:

```http
GET /api/drivers/phone/{phone}/trips
```

لاحقًا يمكن إضافة زر يبدأ تتبع GPS ويرسل موقع الهاتف إلى backend.

### Admin

الـ Admin يصل من login المستخدم العادي إذا كتب:

```text
telephone = admin
code = admin
```

لوحة Admin المطلوبة:

- Dashboard مختصر.
- إدارة السائقين CRUD.
- إدارة الرحلات CRUD.
- عرض رحلات سائق معين.
- ربط رحلة بسائق أو إزالة السائق من رحلة.

## Public/User Trip APIs

### Get all trips

```http
GET /api/trips
```

### Search trips

```http
GET /api/trips/search?from=Rosso&to=Nouakchott&date=2026-06-20T08:00:00
```

### Get trip by ID

```http
GET /api/trips/{id}
```

### Track trip by trip number

```http
GET /api/trips/track/{tripNumber}
```

مثال:

```http
GET /api/trips/track/TRIP-1001
```

Response shape:

```json
{
  "tripId": "uuid",
  "tripNumber": "TRIP-1001",
  "departureCity": "Rosso",
  "destinationCity": "Nouakchott",
  "departureTime": "2026-06-20T08:00:00",
  "arrivalTime": "2026-06-20T11:00:00",
  "status": "IN_PROGRESS",
  "progressPercentage": 35,
  "departureLatitude": 16.5138,
  "departureLongitude": -15.805,
  "destinationLatitude": 18.0735,
  "destinationLongitude": -15.9582,
  "currentLatitude": 17.05,
  "currentLongitude": -15.88,
  "lastLocationUpdate": "2026-06-19T17:30:00",
  "companyName": "Esselam",
  "driverName": "Ahmed Salem",
  "driverPhone": "22220001",
  "vehicleName": "Toyota Hiace",
  "vehiclePlate": "NKC-1001"
}
```

### Update trip location

هذا للـ Driver app لاحقًا:

```http
PUT /api/trips/track/{tripNumber}/location
Content-Type: application/json
```

Body:

```json
{
  "currentLatitude": 18.05,
  "currentLongitude": -15.95,
  "progressPercentage": 58
}
```

### Generate share link

```http
POST /api/trips/track/{tripNumber}/share
```

Response:

```json
{
  "token": "uuid-token",
  "url": "https://transport.mondomaine.com/live/{token}"
}
```

### Public tracking by token

```http
GET /api/trips/public/{token}
```

## Booking APIs

### Create booking

```http
POST /api/bookings
Content-Type: application/json
```

Body:

```json
{
  "tripId": "uuid",
  "passengerName": "Moustapha",
  "passengerPhone": "22223333",
  "seatNumbers": [1, 2]
}
```

### Get user bookings by phone

```http
GET /api/bookings/user/{phone}
```

### Get booking by ID

```http
GET /api/bookings/{id}
```

## Driver APIs

### Get all drivers

```http
GET /api/drivers
```

### Get driver by ID

```http
GET /api/drivers/{id}
```

### Get driver by phone

```http
GET /api/drivers/phone/{phone}
```

### Get driver trips by driver ID

```http
GET /api/drivers/{id}/trips
```

### Get driver trips by phone

```http
GET /api/drivers/phone/{phone}/trips
```

هذا endpoint مهم لصفحة السائق:

```text
Driver login phone -> fetch recorded trips
```

## Admin APIs

كل Admin CRUD موجود تحت:

```text
/api/admin
```

### Drivers CRUD

```http
GET    /api/admin/drivers
GET    /api/admin/drivers/{id}
POST   /api/admin/drivers
PUT    /api/admin/drivers/{id}
DELETE /api/admin/drivers/{id}
GET    /api/admin/drivers/{id}/trips
```

DriverRequest body:

```json
{
  "fullName": "Ahmed Salem",
  "phone": "22220003",
  "licenseNumber": "MR-LIC-1003",
  "vehicleName": "Toyota Hiace",
  "vehiclePlate": "NKC-1003",
  "available": true
}
```

ملاحظات:

- رقم الهاتف unique.
- حذف السائق لا يحذف الرحلات، بل يفصل السائق عن الرحلات المرتبطة به.

### Trips CRUD

```http
GET    /api/admin/trips
GET    /api/admin/trips/{id}
POST   /api/admin/trips
PUT    /api/admin/trips/{id}
DELETE /api/admin/trips/{id}
PUT    /api/admin/trips/{tripId}/driver/{driverId}
DELETE /api/admin/trips/{tripId}/driver
```

TripRequest body:

```json
{
  "tripNumber": "TRIP-1003",
  "driverId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "departureCity": "Nouakchott",
  "destinationCity": "Rosso",
  "departureTime": "2026-06-20T08:00:00",
  "arrivalTime": "2026-06-20T11:00:00",
  "price": 3000,
  "transportType": "Bus",
  "availableSeats": 45,
  "companyName": "Esselam",
  "status": "SCHEDULED",
  "progressPercentage": 0,
  "departureLatitude": 18.0735,
  "departureLongitude": -15.9582,
  "destinationLatitude": 16.5138,
  "destinationLongitude": -15.805,
  "currentLatitude": 18.0735,
  "currentLongitude": -15.9582
}
```

ملاحظات:

- `tripNumber` unique.
- `arrivalTime` يجب أن يكون بعد `departureTime`.
- لا يمكن حذف رحلة عليها حجوزات.
- يمكن إنشاء رحلة بدون `driverId`، لكن الأفضل في Flutter توفير dropdown للسائقين.

## Flutter المطلوب بناؤه

ابنِ تطبيق Flutter كامل ومنظم بهذه البنية المقترحة:

```text
lib/
  main.dart
  models/
    user.dart
    driver.dart
    trip.dart
    booking.dart
    trip_tracking.dart
  services/
    api_service.dart
  screens/
    shared/
      role_selection_screen.dart
    user/
      auth/login_screen.dart
      home/home_screen.dart
      search/search_screen.dart
      bookings/bookings_screen.dart
      tracking/tracking_screen.dart
      profile/profile_screen.dart
    driver/
      login/driver_login_screen.dart
      main/driver_main_screen.dart
      home/driver_home_screen.dart
      trips/my_recorded_trips_screen.dart
      tracking/driver_tracking_screen.dart
    admin/
      admin_main_screen.dart
      admin_dashboard_screen.dart
      admin_drivers_screen.dart
      admin_driver_form_screen.dart
      admin_trips_screen.dart
      admin_trip_form_screen.dart
  widgets/
    reusable cards/forms/buttons
  theme/
    app_theme.dart
```

## Flutter UI Requirements

### Regular user bottom navigation

```text
Accueil
Recherche
Suivi
Mes billets
Profil
```

صفحة التتبع:

- input لإدخال `tripNumber`.
- زر بحث.
- call `GET /api/trips/track/{tripNumber}`.
- عرض خريطة حقيقية Nouakchott/Mauritania باستخدام:

```yaml
flutter_map
latlong2
geolocator
```

- استخدم OpenStreetMap tiles.
- ضع markers:
  - departure
  - destination
  - current bus/driver
- ارسم polyline بين departure/current/destination.
- احسب remaining distance من current إلى destination باستخدام `latlong2`.
- اعرض:
  - status
  - progress percentage
  - driver name
  - vehicle name/plate
  - last update

### Driver app

Driver navigation:

```text
Accueil
Mes trajets
Profil
```

صفحة `Mes trajets`:

- عند الدخول برقم الهاتف، استدع:

```http
GET /api/drivers/phone/{phone}/trips
```

- اعرض الرحلات المسجلة للسائق.
- في كل رحلة اعرض:
  - tripNumber
  - departure/destination
  - departureTime/arrivalTime
  - status
  - availableSeats
  - زر "Start tracking" لاحقًا.

Driver tracking لاحقًا:

- استخدم `geolocator`.
- أرسل موقع السائق كل 5 إلى 10 ثوانٍ إلى:

```http
PUT /api/trips/track/{tripNumber}/location
```

### Admin app

عند login المستخدم العادي:

```dart
if (telephone == 'admin' && code == 'admin') {
  // open AdminMainScreen
}
```

Admin navigation:

```text
Dashboard
Drivers
Trips
Profile/Logout
```

Admin Drivers:

- list drivers.
- add driver.
- edit driver.
- delete driver.
- view driver's trips.

Admin Trips:

- list trips.
- add trip.
- edit trip.
- delete trip.
- assign/remove driver.
- عند إنشاء/تعديل رحلة استخدم dropdown للسائقين من:

```http
GET /api/admin/drivers
```

## Flutter ApiService المطلوب

اكتب methods تقريبًا بهذا الشكل:

```dart
class ApiService {
  static const String baseUrl = 'http://10.0.2.2:9090/api';
  static const String authBaseUrl = 'http://10.0.2.2:9090/auth';

  Future<String> login(String telephone, String code);
  Future<String> register(String telephone, String code);

  Future<List<Trip>> getTrips();
  Future<List<Trip>> searchTrips(String from, String to, DateTime date);
  Future<TripTracking?> trackTrip(String tripNumber);
  Future<TripTracking?> updateTripLocation(...);

  Future<bool> createBooking(...);
  Future<List<Booking>> getBookingsByPhone(String phone);

  Future<List<Driver>> getDrivers();
  Future<Driver?> getDriverByPhone(String phone);
  Future<List<Trip>> getDriverTripsByPhone(String phone);

  Future<List<Driver>> getAdminDrivers();
  Future<Driver> createAdminDriver(DriverRequest request);
  Future<Driver> updateAdminDriver(String id, DriverRequest request);
  Future<void> deleteAdminDriver(String id);

  Future<List<Trip>> getAdminTrips();
  Future<Trip> createAdminTrip(TripRequest request);
  Future<Trip> updateAdminTrip(String id, TripRequest request);
  Future<void> deleteAdminTrip(String id);
  Future<Trip> assignDriverToTrip(String tripId, String driverId);
  Future<Trip> removeDriverFromTrip(String tripId);
}
```

## ملاحظات مهمة

- التاريخ في JSON يستخدم ISO مثل:

```text
2026-06-20T08:00:00
```

- السعر `price` رقم.
- UUIDs strings في Flutter.
- لا تعتمد على Security tokens الآن.
- Admin مؤقتًا محلي داخل Flutter عبر `admin/admin`.
- يجب إضافة loading/error/empty states في كل صفحة.
- إذا فشل الاتصال على emulator غيّر `localhost` إلى `10.0.2.2`.
- إذا اشتغلت على هاتف حقيقي، استخدم IP الكمبيوتر بدل `10.0.2.2`.

## المطلوب من المساعد في المحادثة الجديدة

ابدأ من مشروع Flutter الموجود في:

```text
D:\developmant\projet flutter\projet-programmation-mobile
```

أو إذا لم يكن موجودًا، أنشئ مشروع Flutter كاملًا بنفس البنية أعلاه.

ابدأ بتنفيذ:

1. Models كاملة.
2. ApiService كامل للـ endpoints أعلاه.
3. Login عادي مع admin shortcut.
4. Role selection.
5. User navigation.
6. Driver navigation وصفحة الرحلات من API.
7. Admin navigation وCRUD للسائقين والرحلات.
8. Tracking screen بخريطة حقيقية.

لا تغيّر Spring Boot إلا إذا احتجت endpoint ناقص. الـ backend الحالي جاهز بما يكفي لبناء Flutter.
