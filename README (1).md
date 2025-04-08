# Electricity Breakdown Alert System

A real-time power outage reporting and notification system for electricity boards and residents.

## Features

- **Admin Panel**: Electricity board staff can report power outages in specific sectors
- **Resident App**: Residents see real-time power outage status for their area
- **Push Notifications**: Instant alerts when power outage is reported in a resident's area
- **Live Map**: Visual representation of affected areas

## Technical Stack

- **Frontend**: Native Android (Java + XML)
- **Backend & Realtime Database**: Firebase Realtime Database
- **Notifications**: Firebase Cloud Messaging (FCM)
- **Map Integration**: Google Maps API

## Setup Instructions

### Prerequisites
- Android Studio
- Firebase Account
- Google Maps API Key

### Configuration
1. Clone this repository
2. Create a Firebase project and add your `google-services.json` to the app directory
3. Enable Firebase Realtime Database and set up with the provided database rules
4. Deploy Firebase Functions for push notification handling
5. Add your Google Maps API key in the AndroidManifest.xml

## Screenshots

- Admin View: Power outage reporting interface
- Resident View: Status display with notifications
- Map View: Visual representation of affected sectors

## License
MIT