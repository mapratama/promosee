### Login API

End point: `/api/auth/login`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| email     | User's email  |
| password  | User's password  |

Successful response parameters:
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
	]
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
					"banner": "http://promosee.com/image.jpg"
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
					"banner": "http://promosee.com/image.jpg"
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
		},
		{
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
			""
		},
	],
}
```
