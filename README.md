### Perubahan
- tambahkan satu field gcm_token, facebook_token di table tb_customer, dengan tipe data string
- tambahkan satu tabel FAQ isinya question dan answer
- buat satu variable token yang di hardcode di website, random string dengan length lebih dari 10. Ini dipergunakan untuk authentikasi request yang masuk, dan seluruh request API harus mengirimkan ini.



### Login API

End point: `/api/auth/login`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| email     | email user  |
| password  | password user  |
| gcm_token  | google messaging token untuk notif |


### Register API

End point: `/api/auth/register`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| name      | nama user  |
| email     | email user  |
| password  | password user  |
| phone  | phone user  |
| city  | kota user  |
| address  | alamat user  |
| fb_token  | token saat register menggunakan facebook |
| gcm_token  | google messaging token untuk notif |

Ada kemungkinan saat register menggunakan facebook, customer bukan untuk pertama kalinya (data user telah ada), bila terdapat kasus tersebut update saja data yang dikirim dan jangan sampai terdapat data yang duplicate

Sukses response untuk LOGIN dan REGISTER:
```json
{
	"user": {
		"id": 1,
		"email": "test@gmail.com",
		"name": "test",
		"phone": "087829976921",
		"city": "jakarta",
		"address": "jalan monas",
		"image_url": "http://promosee.com/image.jpg",
		"balance": 65000,
	},
	"faqs": [
		{
			"id": 1,
			"question": "Bagaimana cara topup?",
			"answer": "Dengan menklik tombol dan ..."
		},
		{
			"id": 2,
			"question": "Bagaimana cara beli voucher?",
			"answer": "Dengan menklik tombol dan ..."
		}
	],
	"banners": [
		{
			"id": 1,
			"banner_url": "http://promosee.com/image.jpg",
			"link": "http://promosee.com"
		},
		{
			"id": 2,
			"banner_url": "http://promosee.com/image.jpg",
			"link": "http://prodia.com"
		}
	],
	"news_events": [
		{
			"id": 1,
			"title": "News",
			"subject": "Subject News",
			"image_url": "http://promosee.com/image.jpg",
			"description": "It's description news"
		},
		{
			"id": 2,
			"title": "Event",
			"subject": "Subject Event",
			"image_url": "http://promosee.com/image.jpg",
			"description": "It's description events"
		}
	],
	"kategori_tenant": [
		{
			"id": 1,
			"name": "Food",
			"image_url": "http://promosee.com/image.jpg",
			"tenants": [	
				{
					"id": 1,
					"code": "JNJ100",
					"name": "JunNJan",
					"type": "online",
					"phone": "021456789",
					"address": "Jalan Cikutra Bandung", 
					"email": "junnjan@mail.com",
					"logo_url": "http://promosee.com/image.jpg",
					"banner_url": "http://promosee.com/image.jpg",
					"locations": [
						{
							"latitude": -6.144562,
							"longitude": 106.7657,
						},
						{
							"latitude": -6.23334,
							"longitude": 105.12998,
						}
					]
				},
				{
					"id": 2,
					"code": "PRD100",
					"name": "Prodia",
					"type": "offline",
					"phone": "021456789",
					"address": "Jalan Musi Jakarta",
					"email": "prodia@mail.com",
					"logo_url": "http://promosee.com/image.jpg",
					"banner_url": "http://promosee.com/image.jpg",
					"locations": [
						{
							"latitude": -6.144562,
							"longitude": 106.7657,
						},
						{
							"latitude": -6.23334,
							"longitude": 105.12998,
						}
					]
				},
			]

		}
	],
	"vouchers": [
		{
			"id": 1,
			"name": "Voucher Prodia",
			"subject": "Voucher Prodia 50.000",
			"description": "Dapatkan voucher prodia senilai 50000",
			"price": 1000,
			"id_tenant": 32, 
			"start_date": 2017-02-02,
			"end_date": 2017-04-04,
			"voucher_image_url": "http://promosee.com/image.jpg",
			"slide_image_url": "http://promosee.com/image.jpg",
			"big_image_url": "http://promosee.com/image.jpg",
			"min_payment": 500,
			"redeem_code": "ABCD678",
		},
		{
			"id": 2,
			"name": "Voucher JunNJan",
			"subject": "Voucher JunJJan 50.000",
			"description": "Dapatkan voucher JunNjan senilai 50000",
			"price": 1500,
			"id_tenant": 13, 
			"start_date": 2017-02-02,
			"end_date": 2017-04-04,
			"voucher_image_url": "http://promosee.com/image.jpg",
			"slide_image_url": "http://promosee.com/image.jpg",
			"big_image_url": "http://promosee.com/image.jpg",
			"min_payment": 500,
			"redeem_code": "ABCD678",
		},
	],
}
```

### Voucher Details API

End point: `/api/vouchers/details`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |
| id_voucher     | voucher id |

Response:
```json
{
	"id": 1,
	"name": "Voucher Prodia",
	"subject": "Voucher Prodia 50.000",
	"description": "Dapatkan voucher prodia senilai 50000",
	"price": 1000,
	"id_tenant": 32, 
	"start_date": 2017-02-02,
	"end_date": 2017-04-04,
	"voucher_image_url": "http://promosee.com/image.jpg",
	"slide_image_url": "http://promosee.com/image.jpg",
	"big_image_url": "http://promosee.com/image.jpg",
	"min_payment": 500,
	"redeem_code": "ABCD678",
	""
}
```

### My Voucher API

End point: `/api/vouchers/created`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |

Response:
```json
{
	"transactions": [
		{
			"id": 78,
			"id_voucher": 12,
			"date" : "2016-11-16 05:30:00",
			"type_payment": "pulsa",
			"is_used": "yes"
		},
		{
			"id": 79,
			"id_voucher": 14,
			"date" : "2016-11-15 05:30:00",
			"type_payment": "wallet",
			"used": "no"
		}

	]

}
```

### My Member Card API

End point: `/api/membercards/created`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |

Response:
```json
{
	"membercards": [
		{
			"id": 1,
			"register_date": "2017-02-02",
			"nomor": "1234567ABCD",
			"id_tenant": 12,
			"total_stamp": 10,
			"start_date": "2017-02-02",
			"end_date": "2017-10-02",
		},
		{
			"id": 1,
			"register_date": "2017-02-02",
			"nomor": "1234567ABCD",
			"id_tenant": 12,
			"total_stamp": 10,
			"start_date": "2017-02-02",
			"end_date": "2017-10-02",
		}

	]
}
```

### Redemptions History API

End point: `/api/redemptions/created`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |

Response:
```json
{
	"redemptions": [
		{
			"id": 1,
			"date" : "2016-11-16 05:30:00",
			"id_voucher": 12,
			"show_redeem": "yes",
		},
		{
			"id": 2,
			"date" : "2016-12-15 07:30:00",
			"id_voucher": 17,
			"show_redeem": "no"
		}
	]
}
```


### My Wallet API

End point: `/api/wallets/created`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |

Response:
```json
{
	"wallets": [
		{
			"id": 1,
			"nomor": "1234567ABCDE",
			"amount": 50000,
			"type": "deposit",
			"status": "pending",
			"remark": "test",
			"balance": 10000,
			"date" : "2016-12-15 07:30:00",
		},
		{
			"id": 2,
			"nomor": "789789FGHUJK",
			"amount": 20000,
			"type": "withdraw",
			"status": "approve",
			"remark": "test",
			"balance": 10000,
			"date" : "2016-12-15 07:30:00",
		}
	]
}
```

### Buy Voucher API
### Subscribe API
### Redeem API
### Contact Us API
### Add Member Card API
