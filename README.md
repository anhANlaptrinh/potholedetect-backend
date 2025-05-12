# 🖥️ Pothole Detection – Backend API

This is the backend server for the Android Pothole Detection App. It receives pothole reports sent from the Android client using GPS and sensor data, stores them in MongoDB, and provides REST APIs for data retrieval and visualization via Mapbox.

---

## 🌐 Features

- Receive pothole reports from Android devices via REST API
- Store data with MongoDB (NoSQL)
- Retrieve and visualize pothole reports using API or web map
- Serve a basic web interface with Mapbox to view reported potholes
- CORS enabled for mobile or web frontend integration

---

## 🛠️ Technologies Used

- Java 17+
- Spring Boot
- Spring Web (REST)
- Spring Data MongoDB
- Mapbox (via static map.html)
- Git

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or later
- Maven
- MongoDB (running locally on `mongodb://localhost:27017`)
- A valid [Mapbox Access Token](https://account.mapbox.com/)
