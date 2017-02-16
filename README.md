### Login API

End point: `/api/auth/login`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Random string of characters |
| email     | User's email  |
| password  | User's password  |

Example request:
```sh
http post http://202.179.188.146/api/auth/login email="email@example.com" password="mypassword"
```


Successful response parameters:

| Parameter     | Description |
| ------------- | ----------- |
| session_key   | Random characters that uniquely identifies a user (similar to HTTP cookies) |
| full_name    | User's full name.  |
| mobile_phone    | Mobile phone in `+628123123` or `08123123` formats  |
| email     | User's email  |
| birthday  | Birthday in `YYYY-MM-DD` format  |
| gender    | `m` or `f`  |

Example response:
```json
{
    "session_key": "123nasdkh3423esad833898475",
    "full_name": "John Doe",
    "mobile_phone": "+628123123",
    "email": "john@doe.com",
    "birthday": "1990-09-23",
    "gender": "f",
    "address": "",
    "news": [
    {
      "event_id": 120,
      "string_id": "relevant-leadership-2016",
      "title": "Relevant Leadership 2016",
      "description": "A leadership and worship conference for pastors and church leaders.",
      "startdate": "2016-05-11",
      "enddate": "2016-07-31",
      "photo_url": "http://202.179.188.146/bannerfolder/20160511092324-20160229091952-MY JPCC_RL 16.jpg",
      "registration_url": null
    },
    ]
}
```
