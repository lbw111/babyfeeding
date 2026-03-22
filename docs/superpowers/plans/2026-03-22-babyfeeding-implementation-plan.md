# BabyFeedTracker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a baby feeding tracker Android app with Spring Boot backend - supports bottle feeding and solid food logging, MySQL storage, LAN device discovery.

**Architecture:**
- Spring Boot REST API backend with MySQL database
- Android app with Jetpack Compose UI communicating via Retrofit2
- UDP broadcast for backend service discovery on LAN
- MVVM + Clean Architecture on Android, Controller/Service/Repository on backend

**Tech Stack:**
- Backend: Java 17, Spring Boot 3.x, Spring Data JPA, MySQL 8.0
- Android: Kotlin 1.9+, Jetpack Compose, Material Design 3, Hilt, Retrofit2, Coroutines

---

## Project Structure

```
babyfeeding/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/com/babyfeeding/
│   │   ├── BabyFeedingApplication.java
│   │   ├── controller/
│   │   │   ├── FeedingRecordController.java
│   │   │   ├── BabyController.java
│   │   │   └── HealthController.java
│   │   ├── service/
│   │   │   ├── FeedingRecordService.java
│   │   │   └── BabyService.java
│   │   ├── repository/
│   │   │   ├── FeedingRecordRepository.java
│   │   │   └── BabyRepository.java
│   │   ├── entity/
│   │   │   ├── FeedingRecord.java
│   │   │   └── Baby.java
│   │   ├── dto/
│   │   │   ├── FeedingRecordDto.java
│   │   │   └── BabyDto.java
│   │   ├── enums/
│   │   │   ├── FeedingType.java
│   │   │   ├── MilkType.java
│   │   │   ├── FoodType.java
│   │   │   ├── FoodAmount.java
│   │   │   └── Acceptance.java
│   │   └── discovery/
│   │       └── UdpDiscoveryService.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── schema.sql
│   ├── pom.xml
│   └── Dockerfile
│
└── android/                    # Android app
    ├── app/src/main/java/com/babyfeeding/
    │   ├── BabyFeedingApp.kt
    │   ├── MainActivity.kt
    │   ├── data/
    │   │   ├── api/
    │   │   │   └── ApiService.kt
    │   │   ├── model/
    │   │   │   ├── FeedingRecord.kt
    │   │   │   └── Baby.kt
    │   │   └── repository/
    │   │       └── FeedingRepository.kt
    │   ├── di/
    │   │   └── AppModule.kt
    │   ├── ui/
    │   │   ├── theme/
    │   │   │   ├── Color.kt
    │   │   │   ├── Theme.kt
    │   │   │   └── Type.kt
    │   │   ├── home/
    │   │   │   └── HomeScreen.kt
    │   │   ├── history/
    │   │   │   └── HistoryScreen.kt
    │   │   ├── stats/
    │   │   │   └── StatsScreen.kt
    │   │   ├── settings/
    │   │   │   └── SettingsScreen.kt
    │   │   └── components/
    │   │       ├── BottleFeedForm.kt
    │   │       └── SolidFoodForm.kt
    │   └── discovery/
    │       └── LanDiscovery.kt
    ├── app/src/main/res/
    │   └── values/
    │       └── strings.xml
    └── app/build.gradle.kts
```

---

## Phase 1: Backend (Spring Boot)

### Task 1: Initialize Spring Boot Project

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/babyfeeding/BabyFeedingApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Create pom.xml with dependencies**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.babyfeeding</groupId>
    <artifactId>babyfeeding-backend</artifactId>
    <version>1.0.0</version>
    <name>babyfeeding-backend</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create main application class**

```java
package com.babyfeeding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BabyFeedingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyFeedingApplication.class, args);
    }
}
```

- [ ] **Step 3: Create application.yml**

```yaml
server:
  port: 8765

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/babyfeeding?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: ${MYSQL_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

- [ ] **Step 4: Commit**

```bash
git add backend/pom.xml backend/src/
git commit -m "feat(backend): initialize Spring Boot project"
```

---

### Task 2: Create Enums

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/enums/FeedingType.java`
- Create: `backend/src/main/java/com/babyfeeding/enums/MilkType.java`
- Create: `backend/src/main/java/com/babyfeeding/enums/FoodType.java`
- Create: `backend/src/main/java/com/babyfeeding/enums/FoodAmount.java`
- Create: `backend/src/main/java/com/babyfeeding/enums/Acceptance.java`

- [ ] **Step 1: Create FeedingType.java**

```java
package com.babyfeeding.enums;

public enum FeedingType {
    BOTTLE,
    SOLID_FOOD
}
```

- [ ] **Step 2: Create MilkType.java**

```java
package com.babyfeeding.enums;

public enum MilkType {
    BREAST_MILK,
    FORMULA
}
```

- [ ] **Step 3: Create FoodType.java**

```java
package com.babyfeeding.enums;

public enum FoodType {
    RICE_CEREAL,
    FRUIT_PUREE,
    VEGETABLE_PUREE,
    MEAT_PUREE,
    OTHER
}
```

- [ ] **Step 4: Create FoodAmount.java**

```java
package com.babyfeeding.enums;

public enum FoodAmount {
    SMALL,
    MEDIUM,
    LARGE
}
```

- [ ] **Step 5: Create Acceptance.java**

```java
package com.babyfeeding.enums;

public enum Acceptance {
    LIKED,
    OKAY,
    REFUSED
}
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/enums/
git commit -m "feat(backend): add enum types"
```

---

### Task 3: Create Entities

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/entity/FeedingRecord.java`
- Create: `backend/src/main/java/com/babyfeeding/entity/Baby.java`

- [ ] **Step 1: Create FeedingRecord entity**

```java
package com.babyfeeding.entity;

import com.babyfeeding.enums.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "feeding_records")
public class FeedingRecord {
    @Id
    private String id;

    private String babyId;

    @Enumerated(EnumType.STRING)
    private FeedingType type;

    private Long timestamp;

    private Integer amountMl;

    @Enumerated(EnumType.STRING)
    private MilkType milkType;

    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @Enumerated(EnumType.STRING)
    private FoodAmount foodAmount;

    @Enumerated(EnumType.STRING)
    private Acceptance acceptance;

    private String note;

    private String createdBy;

    private Long createdAt;

    private Long updatedAt;

    private Boolean deleted = false;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBabyId() { return babyId; }
    public void setBabyId(String babyId) { this.babyId = babyId; }
    public FeedingType getType() { return type; }
    public void setType(FeedingType type) { this.type = type; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public Integer getAmountMl() { return amountMl; }
    public void setAmountMl(Integer amountMl) { this.amountMl = amountMl; }
    public MilkType getMilkType() { return milkType; }
    public void setMilkType(MilkType milkType) { this.milkType = milkType; }
    public FoodType getFoodType() { return foodType; }
    public void setFoodType(FoodType foodType) { this.foodType = foodType; }
    public FoodAmount getFoodAmount() { return foodAmount; }
    public void setFoodAmount(FoodAmount foodAmount) { this.foodAmount = foodAmount; }
    public Acceptance getAcceptance() { return acceptance; }
    public void setAcceptance(Acceptance acceptance) { this.acceptance = acceptance; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
```

- [ ] **Step 2: Create Baby entity**

```java
package com.babyfeeding.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "babies")
public class Baby {
    @Id
    private String id;
    private String name;
    private Long birthDate;
    private String avatarPath;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getBirthDate() { return birthDate; }
    public void setBirthDate(Long birthDate) { this.birthDate = birthDate; }
    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/entity/
git commit -m "feat(backend): add JPA entities"
```

---

### Task 4: Create Repositories

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/repository/FeedingRecordRepository.java`
- Create: `backend/src/main/java/com/babyfeeding/repository/BabyRepository.java`

- [ ] **Step 1: Create FeedingRecordRepository**

```java
package com.babyfeeding.repository;

import com.babyfeeding.entity.FeedingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FeedingRecordRepository extends JpaRepository<FeedingRecord, String> {

    @Query("SELECT r FROM FeedingRecord r WHERE r.deleted = false AND r.babyId = :babyId ORDER BY r.timestamp DESC")
    List<FeedingRecord> findByBabyIdAndNotDeleted(@Param("babyId") String babyId);

    @Query("SELECT r FROM FeedingRecord r WHERE r.deleted = false AND r.updatedAt > :since ORDER BY r.updatedAt ASC")
    List<FeedingRecord> findUpdatedSince(@Param("since") Long since);

    @Query("SELECT r FROM FeedingRecord r WHERE r.deleted = false AND r.babyId = :babyId AND r.timestamp >= :startOfDay AND r.timestamp < :endOfDay ORDER BY r.timestamp DESC")
    List<FeedingRecord> findByBabyIdAndDay(@Param("babyId") String babyId, @Param("startOfDay") Long startOfDay, @Param("endOfDay") Long endOfDay);
}
```

- [ ] **Step 2: Create BabyRepository**

```java
package com.babyfeeding.repository;

import com.babyfeeding.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BabyRepository extends JpaRepository<Baby, String> {
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/repository/
git commit -m "feat(backend): add JPA repositories"
```

---

### Task 5: Create Services

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/service/FeedingRecordService.java`
- Create: `backend/src/main/java/com/babyfeeding/service/BabyService.java`

- [ ] **Step 1: Create FeedingRecordService**

```java
package com.babyfeeding.service;

import com.babyfeeding.entity.FeedingRecord;
import com.babyfeeding.repository.FeedingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FeedingRecordService {

    @Autowired
    private FeedingRecordRepository repository;

    public List<FeedingRecord> getAllRecords() {
        return repository.findAll();
    }

    public List<FeedingRecord> getRecordsByBaby(String babyId) {
        return repository.findByBabyIdAndNotDeleted(babyId);
    }

    public List<FeedingRecord> getRecordsSince(Long timestamp) {
        return repository.findUpdatedSince(timestamp);
    }

    public Optional<FeedingRecord> getRecordById(String id) {
        return repository.findById(id);
    }

    public FeedingRecord create(FeedingRecord record) {
        record.setCreatedAt(System.currentTimeMillis());
        record.setUpdatedAt(System.currentTimeMillis());
        return repository.save(record);
    }

    public FeedingRecord update(String id, FeedingRecord record) {
        Optional<FeedingRecord> existing = repository.findById(id);
        if (existing.isPresent()) {
            FeedingRecord entity = existing.get();
            entity.setBabyId(record.getBabyId());
            entity.setType(record.getType());
            entity.setTimestamp(record.getTimestamp());
            entity.setAmountMl(record.getAmountMl());
            entity.setMilkType(record.getMilkType());
            entity.setFoodType(record.getFoodType());
            entity.setFoodAmount(record.getFoodAmount());
            entity.setAcceptance(record.getAcceptance());
            entity.setNote(record.getNote());
            entity.setUpdatedAt(System.currentTimeMillis());
            return repository.save(entity);
        }
        return null;
    }

    public void delete(String id) {
        Optional<FeedingRecord> existing = repository.findById(id);
        if (existing.isPresent()) {
            FeedingRecord entity = existing.get();
            entity.setDeleted(true);
            entity.setUpdatedAt(System.currentTimeMillis());
            repository.save(entity);
        }
    }
}
```

- [ ] **Step 2: Create BabyService**

```java
package com.babyfeeding.service;

import com.babyfeeding.entity.Baby;
import com.babyfeeding.repository.BabyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BabyService {

    @Autowired
    private BabyRepository repository;

    public Iterable<Baby> getAllBabies() {
        return repository.findAll();
    }

    public Optional<Baby> getBabyById(String id) {
        return repository.findById(id);
    }

    public Baby create(Baby baby) {
        return repository.save(baby);
    }

    public Baby update(String id, Baby baby) {
        Optional<Baby> existing = repository.findById(id);
        if (existing.isPresent()) {
            Baby entity = existing.get();
            entity.setName(baby.getName());
            entity.setBirthDate(baby.getBirthDate());
            entity.setAvatarPath(baby.getAvatarPath());
            return repository.save(entity);
        }
        return null;
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/service/
git commit -m "feat(backend): add service layer"
```

---

### Task 6: Create Controllers

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/controller/HealthController.java`
- Create: `backend/src/main/java/com/babyfeeding/controller/FeedingRecordController.java`
- Create: `backend/src/main/java/com/babyfeeding/controller/BabyController.java`

- [ ] **Step 1: Create HealthController**

```java
package com.babyfeeding.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
```

- [ ] **Step 2: Create FeedingRecordController**

```java
package com.babyfeeding.controller;

import com.babyfeeding.entity.FeedingRecord;
import com.babyfeeding.service.FeedingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
public class FeedingRecordController {

    @Autowired
    private FeedingRecordService service;

    @GetMapping
    public List<FeedingRecord> getAll(@RequestParam(required = false) Long since) {
        if (since != null) {
            return service.getRecordsSince(since);
        }
        return service.getAllRecords();
    }

    @GetMapping("/{id}")
    public FeedingRecord getById(@PathVariable String id) {
        return service.getRecordById(id).orElse(null);
    }

    @PostMapping
    public Map<String, String> create(@RequestBody FeedingRecord record) {
        FeedingRecord created = service.create(record);
        return Map.of("id", created.getId());
    }

    @PutMapping("/{id}")
    public Map<String, Boolean> update(@PathVariable String id, @RequestBody FeedingRecord record) {
        FeedingRecord updated = service.update(id, record);
        return Map.of("success", updated != null);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> delete(@PathVariable String id) {
        service.delete(id);
        return Map.of("success", true);
    }
}
```

- [ ] **Step 3: Create BabyController**

```java
package com.babyfeeding.controller;

import com.babyfeeding.entity.Baby;
import com.babyfeeding.service.BabyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/babies")
public class BabyController {

    @Autowired
    private BabyService service;

    @GetMapping
    public Iterable<Baby> getAll() {
        return service.getAllBabies();
    }

    @GetMapping("/{id}")
    public Baby getById(@PathVariable String id) {
        return service.getBabyById(id).orElse(null);
    }

    @PostMapping
    public Map<String, String> create(@RequestBody Baby baby) {
        Baby created = service.create(baby);
        return Map.of("id", created.getId());
    }

    @PutMapping("/{id}")
    public Map<String, Boolean> update(@PathVariable String id, @RequestBody Baby baby) {
        Baby updated = service.update(id, baby);
        return Map.of("success", updated != null);
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/controller/
git commit -m "feat(backend): add REST controllers"
```

---

### Task 7: Add UDP Discovery Service

**Files:**
- Create: `backend/src/main/java/com/babyfeeding/discovery/UdpDiscoveryService.java`

- [ ] **Step 1: Create UdpDiscoveryService**

```java
package com.babyfeeding.discovery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Component
public class UdpDiscoveryService {

    private static final int DISCOVERY_PORT = 8766;
    private static final String DISCOVER_MESSAGE = "BFT_DISCOVER";
    private static final String RESPONSE_PREFIX = "BFT_HERE:";

    @Value("${server.port:8765}")
    private int serverPort;

    @Value("${spring.datasource.url:localhost}")
    private String dbUrl;

    private DatagramSocket socket;

    @PostConstruct
    public void start() {
        new Thread(this::listen).start();
    }

    private void listen() {
        try {
            socket = new DatagramSocket(DISCOVERY_PORT);
            byte[] buffer = new byte[256];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                if (DISCOVER_MESSAGE.equals(message)) {
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    String localIp = InetAddress.getLocalHost().getHostAddress();
                    String response = RESPONSE_PREFIX + localIp + ":" + serverPort;
                    DatagramPacket responsePacket = new DatagramPacket(
                        response.getBytes(), response.length(), address, port);
                    socket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add backend/src/main/java/com/babyfeeding/discovery/
git commit -m "feat(backend): add UDP discovery service"
```

---

### Task 8: Add Dockerfile for Backend

**Files:**
- Create: `backend/Dockerfile`

- [ ] **Step 1: Create Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8765 8766
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 2: Create README for backend**

Create: `backend/README.md` with instructions for running the backend

- [ ] **Step 3: Commit**

```bash
git add backend/Dockerfile backend/README.md
git commit -m "feat(backend): add Dockerfile and README"
```

---

## Phase 2: Android App

### Task 9: Initialize Android Project

**Files:**
- Create: `android/settings.gradle.kts`
- Create: `android/build.gradle.kts`
- Create: `android/gradle.properties`
- Create: `android/app/build.gradle.kts`

- [ ] **Step 1: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BabyFeeding"
include(":app")
```

- [ ] **Step 2: Create build.gradle.kts (root)**

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

- [ ] **Step 3: Create gradle.properties**

```properties
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

- [ ] **Step 4: Create app/build.gradle.kts**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.babyfeeding"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.babyfeeding"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        implementation("androidx.activity:activity-compose:1.8.1")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.navigation:navigation-compose:2.7.5")
        implementation("com.google.dagger:hilt-android:2.48")
        kapt("com.google.dagger:hilt-android-compiler:2.48")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("androidx.datastore:datastore-preferences:1.0.0")
    }
}

kapt {
    correctErrorTypes = true
}
```

- [ ] **Step 5: Commit**

```bash
git add android/
git commit -m "feat(android): initialize Android project"
```

---

### Task 10: Create Android Data Layer

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/data/model/FeedingRecord.kt`
- Create: `android/app/src/main/java/com/babyfeeding/data/model/Baby.kt`
- Create: `android/app/src/main/java/com/babyfeeding/data/api/ApiService.kt`
- Create: `android/app/src/main/java/com/babyfeeding/data/repository/FeedingRepository.kt`

- [ ] **Step 1: Create FeedingRecord.kt**

```kotlin
package com.babyfeeding.data.model

enum class FeedingType { BOTTLE, SOLID_FOOD }
enum class MilkType { BREAST_MILK, FORMULA }
enum class FoodType { RICE_CEREAL, FRUIT_PUREE, VEGETABLE_PUREE, MEAT_PUREE, OTHER }
enum class FoodAmount { SMALL, MEDIUM, LARGE }
enum class Acceptance { LIKED, OKAY, REFUSED }

data class FeedingRecord(
    val id: String,
    val babyId: String,
    val type: FeedingType,
    val timestamp: Long,
    val amountMl: Int?,
    val milkType: MilkType?,
    val foodType: FoodType?,
    val foodAmount: FoodAmount?,
    val acceptance: Acceptance?,
    val note: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

- [ ] **Step 2: Create Baby.kt**

```kotlin
package com.babyfeeding.data.model

data class Baby(
    val id: String,
    val name: String,
    val birthDate: Long,
    val avatarPath: String?
)
```

- [ ] **Step 3: Create ApiService.kt**

```kotlin
package com.babyfeeding.data.api

import com.babyfeeding.data.model.*
import retrofit2.http.*

interface ApiService {
    @GET("health")
    suspend fun health(): Map<String, String>

    @GET("api/records")
    suspend fun getRecords(@Query("since") since: Long? = null): List<FeedingRecord>

    @POST("api/records")
    suspend fun createRecord(@Body record: FeedingRecord): Map<String, String>

    @PUT("api/records/{id}")
    suspend fun updateRecord(@Path("id") id: String, @Body record: FeedingRecord): Map<String, Boolean>

    @DELETE("api/records/{id}")
    suspend fun deleteRecord(@Path("id") id: String): Map<String, Boolean>

    @GET("api/babies")
    suspend fun getBabies(): List<Baby>

    @POST("api/babies")
    suspend fun createBaby(@Body baby: Baby): Map<String, String>

    @PUT("api/babies/{id}")
    suspend fun updateBaby(@Path("id") id: String, @Body baby: Baby): Map<String, Boolean>
}
```

- [ ] **Step 4: Create FeedingRepository.kt**

```kotlin
package com.babyfeeding.data.repository

import com.babyfeeding.data.api.ApiService
import com.babyfeeding.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingRepository @Inject constructor(
    private val api: ApiService
) {
    fun getRecords(since: Long? = null): Flow<Result<List<FeedingRecord>>> = flow {
        try {
            val records = api.getRecords(since)
            emit(Result.success(records))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getBabies(): Flow<Result<List<Baby>>> = flow {
        try {
            val babies = api.getBabies()
            emit(Result.success(babies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createRecord(record: FeedingRecord): Result<String> {
        return try {
            val response = api.createRecord(record)
            Result.success(response["id"] ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBaby(baby: Baby): Result<String> {
        return try {
            val response = api.createBaby(baby)
            Result.success(response["id"] ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/data/
git commit -m "feat(android): add data layer (API + Repository)"
```

---

### Task 11: Create DI Module

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/di/AppModule.kt`

- [ ] **Step 1: Create AppModule.kt**

```kotlin
package com.babyfeeding.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.babyfeeding.data.api.ApiService
import com.babyfeeding.data.repository.FeedingRepository
import com.babyfeeding.discovery.LanDiscovery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8765/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedingRepository(api: ApiService): FeedingRepository {
        return FeedingRepository(api)
    }

    @Provides
    @Singleton
    fun provideLanDiscovery(): LanDiscovery {
        return LanDiscovery()
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/di/
git commit -m "feat(android): add Hilt DI module"
```

---

### Task 12: Create LAN Discovery

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/discovery/LanDiscovery.kt`

- [ ] **Step 1: Create LanDiscovery.kt**

```kotlin
package com.babyfeeding.discovery

import android.content.Context
import android.net.wifi.WifiManager
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LanDiscovery {
    companion object {
        private const val DISCOVERY_PORT = 8766
        private const val DISCOVER_MESSAGE = "BFT_DISCOVER"
        private const val RESPONSE_PREFIX = "BFT_HERE"
        private const val TIMEOUT_MS = 5000L
    }

    suspend fun discoverBackend(): String? = withContext(Dispatchers.IO) {
        try {
            val socket = MulticastSocket(DISCOVERY_PORT)
            socket.soTimeout = TIMEOUT_MS.toInt()

            val broadcastAddr = InetAddress.getByName("255.255.255.255")
            val sendPacket = DatagramPacket(
                DISCOVER_MESSAGE.toByteArray(),
                DISCOVER_MESSAGE.length,
                broadcastAddr,
                DISCOVERY_PORT
            )
            socket.send(sendPacket)

            val buffer = ByteArray(256)
            val responsePacket = DatagramPacket(buffer, buffer.size)
            socket.receive(responsePacket)

            val response = String(responsePacket.data, 0, responsePacket.length)
            if (response.startsWith(RESPONSE_PREFIX)) {
                val parts = response.substringAfter(":").split(":")
                if (parts.size >= 2) {
                    val host = parts[0]
                    val port = parts[1]
                    socket.close()
                    return@withContext "http://$host:$port/"
                }
            }
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/discovery/
git commit -m "feat(android): add LAN discovery service"
```

---

### Task 13: Create UI Theme

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/ui/theme/Color.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/theme/Type.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/theme/Theme.kt`

- [ ] **Step 1: Create Color.kt**

```kotlin
package com.babyfeeding.ui.theme

import androidx.compose.ui.graphics.Color

val CoralPink = Color(0xFFFF8A80)
val CreamWhite = Color(0xFFFFF8E1)
val MintGreen = Color(0xFF80CBC4)
val WarmWhite = Color(0xFFFFFBFA)
val DarkBrown = Color(0xFF4E342E)
val MediumBrown = Color(0xFF8D6E63)
```

- [ ] **Step 2: Create Type.kt**

```kotlin
package com.babyfeeding.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = DarkBrown
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        color = DarkBrown
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = DarkBrown
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = MediumBrown
    )
)
```

- [ ] **Step 3: Create Theme.kt**

```kotlin
package com.babyfeeding.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CoralPink,
    secondary = MintGreen,
    tertiary = CreamWhite,
    background = WarmWhite,
    surface = WarmWhite,
    onPrimary = DarkBrown,
    onSecondary = DarkBrown,
    onTertiary = DarkBrown,
    onBackground = DarkBrown,
    onSurface = DarkBrown
)

@Composable
fun BabyFeedingTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/ui/theme/
git commit -m "feat(android): add Material Design 3 theme"
```

---

### Task 14: Create Home Screen

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/ui/home/HomeScreen.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/home/HomeViewModel.kt`

- [ ] **Step 1: Create HomeViewModel.kt**

```kotlin
package com.babyfeeding.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.data.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<FeedingRecord>>(emptyList())
    val records: StateFlow<List<FeedingRecord>> = _records

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRecords().collect { result ->
                result.onSuccess { _records.value = it }
                _isLoading.value = false
            }
        }
    }

    fun addBottleFeed(amountMl: Int, milkType: String, note: String?) {
        viewModelScope.launch {
            val record = FeedingRecord(
                id = UUID.randomUUID().toString(),
                babyId = "default",
                type = com.babyfeeding.data.model.FeedingType.BOTTLE,
                timestamp = System.currentTimeMillis(),
                amountMl = amountMl,
                milkType = com.babyfeeding.data.model.MilkType.valueOf(milkType),
                foodType = null,
                foodAmount = null,
                acceptance = null,
                note = note,
                createdBy = android.os.Build.MODEL,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.createRecord(record)
            loadRecords()
        }
    }

    fun addSolidFood(foodType: String, foodAmount: String, acceptance: String, note: String?) {
        viewModelScope.launch {
            val record = FeedingRecord(
                id = UUID.randomUUID().toString(),
                babyId = "default",
                type = com.babyfeeding.data.model.FeedingType.SOLID_FOOD,
                timestamp = System.currentTimeMillis(),
                amountMl = null,
                milkType = null,
                foodType = com.babyfeeding.data.model.FoodType.valueOf(foodType),
                foodAmount = com.babyfeeding.data.model.FoodAmount.valueOf(foodAmount),
                acceptance = com.babyfeeding.data.model.Acceptance.valueOf(acceptance),
                note = note,
                createdBy = android.os.Build.MODEL,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.createRecord(record)
            loadRecords()
        }
    }
}
```

- [ ] **Step 2: Create HomeScreen.kt**

```kotlin
package com.babyfeeding.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.ui.components.BottleFeedForm
import com.babyfeeding.ui.components.SolidFoodForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var addType by remember { mutableStateOf<String?>(null) }

    LaunchEffect(Unit) {
        viewModel.loadRecords()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("宝宝喂养") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                val todayRecords = records.filter {
                    val today = System.currentTimeMillis() / 86400000
                    val recordDay = it.timestamp / 86400000
                    recordDay == today
                }

                Text(
                    text = "今日喂养 ${todayRecords.size} 次",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(records.take(10)) { record ->
                        FeedRecordItem(record)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加记录") },
            text = {
                Column {
                    TextButton(onClick = {
                        addType = "BOTTLE"
                        showAddDialog = false
                    }) {
                        Text("瓶喂")
                    }
                    TextButton(onClick = {
                        addType = "SOLID_FOOD"
                        showAddDialog = false
                    }) {
                        Text("辅食")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun FeedRecordItem(record: FeedingRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (record.type == com.babyfeeding.data.model.FeedingType.BOTTLE) "瓶喂" else "辅食",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "时间: ${java.text.SimpleDateFormat("HH:mm").format(record.timestamp)}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (record.amountMl != null) {
                Text(text = "奶量: ${record.amountMl}ml", style = MaterialTheme.typography.bodyMedium)
            }
            record.note?.let { Text(text = "备注: $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/ui/home/
git commit -m "feat(android): add home screen with ViewModel"
```

---

### Task 15: Create Feed Forms

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/ui/components/BottleFeedForm.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/components/SolidFoodForm.kt`

- [ ] **Step 1: Create BottleFeedForm.kt**

```kotlin
package com.babyfeeding.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottleFeedForm(
    onSubmit: (Int, String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var amount by remember { mutableStateOf("120") }
    var milkType by remember { mutableStateOf("FORMULA") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("瓶喂记录", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("奶量 (ml)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("奶类型", style = MaterialTheme.typography.bodyLarge)
        Row {
            RadioButton(
                selected = milkType == "BREAST_MILK",
                onClick = { milkType = "BREAST_MILK" }
            )
            Text("母乳", modifier = Modifier.padding(top = 8.dp))
            RadioButton(
                selected = milkType == "FORMULA",
                onClick = { milkType = "FORMULA" }
            )
            Text("奶粉", modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("备注 (可选)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
            Button(onClick = {
                val amountInt = amount.toIntOrNull() ?: 0
                onSubmit(amountInt, milkType, note.ifBlank { null })
            }) {
                Text("保存")
            }
        }
    }
}
```

- [ ] **Step 2: Create SolidFoodForm.kt**

```kotlin
package com.babyfeeding.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolidFoodForm(
    onSubmit: (String, String, String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var foodType by remember { mutableStateOf("RICE_CEREAL") }
    var foodAmount by remember { mutableStateOf("MEDIUM") }
    var acceptance by remember { mutableStateOf("OKAY") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("辅食记录", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("食物类型", style = MaterialTheme.typography.bodyLarge)
        Column {
            listOf("RICE_CEREAL" to "米粉", "FRUIT_PUREE" to "果泥", "VEGETABLE_PUREE" to "菜泥", "MEAT_PUREE" to "肉泥", "OTHER" to "其他").forEach { (value, label) ->
                Row {
                    RadioButton(selected = foodType == value, onClick = { foodType = value })
                    Text(label, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("食量", style = MaterialTheme.typography.bodyLarge)
        Row {
            listOf("SMALL" to "少量", "MEDIUM" to "中等", "LARGE" to "大量").forEach { (value, label) ->
                RadioButton(selected = foodAmount == value, onClick = { foodAmount = value })
                Text(label, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("接受度", style = MaterialTheme.typography.bodyLarge)
        Row {
            listOf("LIKED" to "喜欢吃", "OKAY" to "一般", "REFUSED" to "拒绝").forEach { (value, label) ->
                RadioButton(selected = acceptance == value, onClick = { acceptance = value })
                Text(label, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("备注 (可选)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
            Button(onClick = {
                onSubmit(foodType, foodAmount, acceptance, note.ifBlank { null })
            }) {
                Text("保存")
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/ui/components/
git commit -m "feat(android): add feeding forms"
```

---

### Task 16: Create MainActivity and Navigation

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/MainActivity.kt`
- Create: `android/app/src/main/java/com/babyfeeding/BabyFeedingApp.kt`

- [ ] **Step 1: Create BabyFeedingApp.kt**

```kotlin
package com.babyfeeding

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BabyFeedingApp : Application()
```

- [ ] **Step 2: Create MainActivity.kt**

```kotlin
package com.babyfeeding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.babyfeeding.ui.theme.BabyFeedingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyFeedingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}
```

- [ ] **Step 3: Create MainNavigation.kt**

```kotlin
package com.babyfeeding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.babyfeeding.ui.home.HomeScreen
import com.babyfeeding.ui.history.HistoryScreen
import com.babyfeeding.ui.stats.StatsScreen
import com.babyfeeding.ui.settings.SettingsScreen

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "首页")
    object History : Screen("history", "历史")
    object Stats : Screen("stats", "统计")
    object Settings : Screen("settings", "设置")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOf(
                    Screen.Home to Icons.Default.Home,
                    Screen.History to Icons.Default.DateRange,
                    Screen.Stats to Icons.Default.BarChart,
                    Screen.Settings to Icons.Default.Settings
                ).forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/MainActivity.kt
git add android/app/src/main/java/com/babyfeeding/BabyFeedingApp.kt
git add android/app/src/main/java/com/babyfeeding/MainNavigation.kt
git commit -m "feat(android): add MainActivity and navigation"
```

---

### Task 17: Create Remaining Screens

**Files:**
- Create: `android/app/src/main/java/com/babyfeeding/ui/history/HistoryScreen.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/stats/StatsScreen.kt`
- Create: `android/app/src/main/java/com/babyfeeding/ui/settings/SettingsScreen.kt`

- [ ] **Step 1: Create HistoryScreen.kt**

```kotlin
package com.babyfeeding.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()

    LaunchEffect(Unit) {
        viewModel.loadRecords()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("历史记录") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val groupedRecords = records.groupBy {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
            }

            groupedRecords.forEach { (date, dateRecords) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(dateRecords) { record ->
                    HistoryRecordItem(record)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryRecordItem(record: FeedingRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (record.type == com.babyfeeding.data.model.FeedingType.BOTTLE) "瓶喂" else "辅食",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(record.timestamp)),
                style = MaterialTheme.typography.bodyMedium
            )
            if (record.amountMl != null) {
                Text(text = "奶量: ${record.amountMl}ml", style = MaterialTheme.typography.bodyMedium)
            }
            record.note?.let { Text(text = "备注: $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
```

- [ ] **Step 2: Create StatsScreen.kt**

```kotlin
package com.babyfeeding.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.ui.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()

    LaunchEffect(Unit) {
        viewModel.loadRecords()
    }

    val todayRecords = records.filter {
        val today = System.currentTimeMillis() / 86400000
        val recordDay = it.timestamp / 86400000
        recordDay == today
    }

    val bottleRecords = todayRecords.filter { it.type == com.babyfeeding.data.model.FeedingType.BOTTLE }
    val totalMl = bottleRecords.mapNotNull { it.amountMl }.sum()
    val avgMl = if (bottleRecords.isNotEmpty()) totalMl / bottleRecords.size else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("喂养统计") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("今日统计", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(title = "喂养次数", value = "${todayRecords.size}")
                StatCard(title = "总奶量", value = "${totalMl}ml")
            }

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(title = "平均奶量", value = "${avgMl}ml")

            Spacer(modifier = Modifier.height(32.dp))

            Text("本周趋势", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "近7天: ${records.size} 次喂养",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.size(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

- [ ] **Step 3: Create SettingsScreen.kt**

```kotlin
package com.babyfeeding.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var babyName by remember { mutableStateOf("宝宝") }
    var serverAddress by remember { mutableStateOf("未连接") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("宝宝信息", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = babyName,
                onValueChange = { babyName = it },
                label = { Text("宝宝姓名") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("连接状态", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("后端服务: $serverAddress")
                    Text("状态: 点击扫描连接")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Trigger LAN discovery */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("扫描并连接")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("关于", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "BabyFeedTracker v1.0",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/java/com/babyfeeding/ui/history/
git add android/app/src/main/java/com/babyfeeding/ui/stats/
git add android/app/src/main/java/com/babyfeeding/ui/settings/
git commit -m "feat(android): add history, stats, and settings screens"
```

---

### Task 18: Add Android Manifest and Resources

**Files:**
- Create: `android/app/src/main/AndroidManifest.xml`
- Create: `android/app/src/main/res/values/strings.xml`

- [ ] **Step 1: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />

    <application
        android:name=".BabyFeedingApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.BabyFeeding"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.BabyFeeding">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 2: Create strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">宝宝喂养</string>
</resources>
```

- [ ] **Step 3: Create colors.xml and themes.xml**

Create: `android/app/src/main/res/values/colors.xml`
Create: `android/app/src/main/res/values/themes.xml`

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/AndroidManifest.xml
git add android/app/src/main/res/values/
git commit -m "feat(android): add manifest and resources"
```

---

## Verification

### Backend Verification
- [ ] Backend starts without errors: `cd backend && ./mvnw spring-boot:run`
- [ ] Health endpoint works: `curl http://localhost:8765/health`
- [ ] Can create/read feeding records via API
- [ ] MySQL connection successful

### Android Verification
- [ ] Android project builds: `./gradlew assembleDebug`
- [ ] App launches without crash
- [ ] Can navigate between screens
- [ ] Forms submit data (even if backend not connected)

### Integration Verification
- [ ] Android can discover backend via UDP broadcast
- [ ] Android can create records via REST API
- [ ] Data persists in MySQL

---

## Post-Implementation

1. Run `git push origin main` to push all commits to GitHub
2. Update README with setup instructions
3. Add .gitignore for both backend and android
