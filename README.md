# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.

# CSC207

## Team Information
**Team Name:** Twelve  
**Tutorial Section:** TUT0201-12  

---

## Domain
Using weather data to generate outfit suggestions based on a user’s **location**, **style**, and **preferences**.  
The goal of the software is to help users decide what to wear and bring based on daily weather conditions and personal style.

---

## User Stories
- **User Story 1:** As a user, I want to be able to see the weather throughout the day so I know what to bring.  
- **User Story 2:** As a user, I want to know what to wear for the weather so I am comfortable.  
- **User Story 3:** As a user, I want to get suggestions tailored to me so I can look nice when I’m outside.  
- **User Story 4:** As a user, I want to be able to save my profile so I don’t have to rewrite all my preferences.  
- **User Story 5:** As a user, I want to be able to get more than one suggestion if I don’t like the options given.  
- **User Story 6:** As a user, I want to be able to get suggestions for accessories based on the purpose of my travel so I know what to bring.

---

## Use Cases

### Use Case 1: View Daily Weather Forecast
**Actors:** User, Weather API  
**Goal:** View the day’s weather to plan what to bring  

**Preconditions:**
- User has granted location access  

**Main Flow:**
1. User opens the app  
2. App requests the user’s current location  
3. System calls Weather API to fetch current and hourly forecast data  
4. Weather info (temperature, precipitation, wind, etc.) is displayed  

**Postconditions:**
- User can view accurate, location-specific weather information  

---

### Use Case 2: Generate Weather-Based Outfit Suggestion
**Actors:** User, Weather API, Google Gemini API  
**Goal:** Get an outfit suggestion suited to the current weather  

**Preconditions:**
- Weather data is successfully retrieved  

**Main Flow:**
1. User clicks “Get Outfit Suggestion”  
2. System retrieves current and forecasted weather  
3. Weather data is sent to the LLM model  
4. LLM generates appropriate outfit suggestions  
5. Outfit suggestion(s) are displayed  

**Postconditions:**
- User receives weather-appropriate outfit ideas  

---

### Use Case 3: Personalized Style Recommendation
**Actors:** User, Google Gemini API  
**Goal:** Receive outfit suggestions tailored to personal style  

**Preconditions:**
- User has saved a style profile  

**Main Flow:**
1. System retrieves saved preferences (e.g., casual, sporty, formal)  
2. Combines preferences with weather data  
3. LLM generates outfits matching user’s taste  
4. User views and selects preferred options  

**Postconditions:**
- User gets personalized and stylish outfit recommendations  

---

### Use Case 4: Manage User Profile
**Actors:** User, Database / Local Storage  
**Goal:** Save and manage personal style preferences  

**Preconditions:**
- User is logged in or using a local profile  

**Main Flow:**
1. User opens “Profile” section  
2. User inputs info (gender, clothing preferences, colors, etc.)  
3. User saves preferences  
4. System stores data securely  

**Alternative Flow:**
- User edits or deletes preferences later  

**Postconditions:**
- Preferences are stored for future sessions  

---

### Use Case 5: Request Additional Outfit Suggestions
**Actors:** User, LLM Suggestion Engine  
**Goal:** Get multiple outfit options to choose from  

**Preconditions:**
- At least one outfit suggestion has already been generated  

**Main Flow:**
1. User clicks “More Suggestions”  
2. System prompts LLM for new variations  
3. New suggestions (different styles/colors) are displayed  

**Postconditions:**
- User can browse multiple outfit ideas  

---

### Use Case 6: Accessory Recommendation by Purpose
**Actors:** User, LLM Suggestion Engine  
**Goal:** Get accessory recommendations based on travel purpose and weather  

**Preconditions:**
- User provides purpose of outing (e.g., work, gym, travel)  

**Main Flow:**
1. User selects outing purpose  
2. System analyzes purpose + weather conditions  
3. LLM generates relevant accessories (e.g., umbrella, hat, bag)  
4. Accessories appear with outfit suggestions  

**Postconditions:**
- User knows what accessories to bring for the day  

---

## MVP Plan

| **Lead** | **Use Case** | **User Story** |
|-----------|---------------|----------------|
| Name TBD | Use Case 1: View Daily Weather Forecast | User Story 1 |
| Name TBD | Use Case 2: Generate Weather-Based Outfit Suggestion | User Story 2 |
| Name TBD | Use Case 3: Personalized Style Recommendation | User Story 3 |
| Name TBD | Use Case 4: Manage User Profile | User Story 4 |
| Name TBD | Use Case 5: Request Additional Outfit Suggestions | User Story 5 |
| Name TBD | Use Case 6: Accessory Recommendation by Purpose | User Story 6 |

---

## Proposed Entities

### **User**
```
-name: String
-style: ArrayList<String>
-location: String
-gender: String
```

### **Weather**
```
-location: String
-temperature: ArrayList<Integer>
-precipitation: ArrayList<String>
-windspeed: int
-feelsLikeTemperature: ArrayList<Integer>
-uvIndex: int
-sunrise: DateTime
-sunset: DateTime
```

---

## Proposed APIs

| **API** | **Link** | **Purpose / Features** |
|----------|-----------|------------------------|
| **OpenWeatherMap API** | [https://openweathermap.org/api](https://openweathermap.org/api) | Provides UV index, temperature, wind speed, air pressure, and other weather attributes |
| **Google Gemini API** | [https://aistudio.google.com/app/api-keys](https://aistudio.google.com/app/api-keys) | Generates outfit and accessory suggestions using LLMs (free API key for each account) |
| **Supabase** | [https://supabase.com](https://supabase.com) | Free backend storage for user profiles; includes authentication options |
