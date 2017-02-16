### Perubahan
- tambahkan satu field gcm_token, facebook_token di table tb_customer
- tambahkan satu tabel FAQ isinya question dan answer
- buat satu variable token yang di hardcode di website



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

Ada kemungkinan saat register menggunakan facebook bukan untuk pertama kalinya (data user telah ada), bila terdapat kasus tersebut update saja data yang dikirim dan jangan sampai terdapat data yang duplicate

Sukses response untuk LOGIN dan REGISTER:
```json
{
	"customer": {
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
			"question": "Bagaimana cara topup?",
			"answer": "Dengan menklik tombol dan ..."
		},
		{
			"question": "Bagaimana cara beli voucher?",
			"answer": "Dengan menklik tombol dan ..."
		}
	],
	"banners": [
		{
			"banner_url": "http://promosee.com/image.jpg",
			"link": "http://promosee.com"
		},
		{
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
			"tenant": [	
				{
					"id": 1,
					"code": "JNJ100",
					"name": "JunNJan",
					"tipe": "online",
					"telp": "021456789",
					"alamat": "Jalan Cikutra Bandung", 
					"email": "junnjan@mail.com",
					"logo_url": "http://promosee.com/image.jpg",
					"banner": "http://promosee.com/image.jpg",
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
					"tipe": "offline",
					"telp": "021456789",
					"alamat": "Jalan Musi Jakarta",
					"email": "prodia@mail.com",
					"logo_url": "http://promosee.com/image.jpg",
					"banner": "http://promosee.com/image.jpg",
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
| id     | voucher id |

Response:
```json
{
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
}```
