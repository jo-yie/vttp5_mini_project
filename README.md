# Digital Diary 
## by Jo Yie

### Working App:
https://digital-diary.up.railway.app

---
### How to Test the App:
This app uses the Spotify Web API, which limits access to registered email accounts.\n
The following dummy Spotify account is the only account that can be used for testing. 

1. Login with the following credentials:

   Username: testing123
   Password: testing123

2. Connect to dummy Spotify account with the following credentials:

   Email: bitiw13153@nongnue.com
   Password: testing123

---

### REST Endpoints
User must be logged in to access REST endpoints.

1. All Diary Entries: https://digital-diary.up.railway.app/diary/raw/all
2. User Details: https://digital-diary.up.railway.app/user/raw/details

---

### Parameterized Routes 
Get a diary entry from a specific date:

1. https://digital-diary.up.railway.app/diary/date/{DATE}
2. Date format: dd-MM-yyyy (eg. 29-12-2024)
