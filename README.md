**Di JPCC, kami percaya bahwa untuk membangun `generasi` bintang berikutnya, diperlukan sinergi antara gereja dengan keluarga. Sebuah sinergi terciptakan apabila gereja dan keluarga bekerjasama untuk mencapai sebuah tujuan yang sama, terutama dalam memenuhi kebutuhan dasar dalam mengembangkan iman anak-anak kepada Tuhan.**\r\n\r\n***Ada 5 kebutuhan dasar anak-anak yang harus kita perhatikan bersama.***\r\n\r\n# 1. Tuhan yang besar\r\n\r\n\r\nApakah kita menciptakan suasana di gereja dan di rumah yang mencerminkan kebesaran Tuhan? Atau apakah kita menciptakan suasana yang tidak aman dan membangun rasa khawatir akan masa depan? Adalah sebuah pilihan bagi setiap keluarga untuk membentuk dan mempertahankan suasana dan lingkungan yang penuh dengan iman, pengharapan, dan kasih dalam Kristus dan karakter Allah.\r\n\r\n# 2. Orang lain\r\n\r\nApakah kita mendukung anak-anak untuk memiliki hubungan yang lebih erat dengan lingkungan orang-orang percaya? Manfaat keberadaan JPCC Kids dan JPCC Youth terletak dalam meningkatkan kemungkinan untuk anak-anak mendapatkan sahabat dalam iman, agar saling menjaga dan menginspirasi satu sama lain dalam pertumbuhan mereka.\r\n\r\n# 3. Suara yang lain\r\n\r\nSebaik apapun kemampuan orang tua, kenyataannya adalah: tidak semua anak mau dan nyaman menceritakan segalanya ke orang tua mereka sendiri. Dan tidak semua anak mau mendengar apa yang dikatakan oleh orang tua. Keberadaan orang dewasa lain dalam kehidupan anak-anak dapat memperkuat nilai yang sudah diajarkan oleh Firman Tuhan.\r\n\r\n# 4. Akal yang berbeda\r\n\r\nDi tengah standar moral dunia yang tidak jelas, dan tekanan sosial yang begitu tinggi, sangat mudah bagi anak-anak untuk terbawa ke arah keputusan dan kebiasan-kebiasan yang buruk melalui arus pergaulan mereka. Kita harus bersama-sama membantu mereka untuk berpikir beda – agar mereka terinspirasi, mengerti, dan melakukan prinsip, nilai, dan kebenaran Firman Tuhan. Cara bepikir yang berbeda akan menghasilkan gaya hidup yang berbeda, yaitu kehidupan yang baik dan benar.\r\n\r\n# 5. Orang tua yang mau terlibat\r\n\r\nGereja memiliki waktu yang sangat singkat untuk mempengaruhi kehidupan iman anak-anak. Tidak bisa dipungkiri bahwa tanggung jawab terbesar tetap ada di setiap orang tua dalam mendidik, menginspirasi, dan menjadi teladan karakter Kristus bagi anak-anak. Hal ini menuntut setiap orang tua untuk nyaman terlibat setiap hari dalam membicarakan hal-hal yang bersifat “rohani” dengan anak-anak mereka.\r\n\r\nHarapan kami di JPCC Kids dan JPCC Youth agar kita bisa bersinergi bersama-sama untuk memenuhi kebutuhan anak-anak yang Tuhan percayakan kepada kita. Bersama-sama kita membangun generasi bintang berikutnya agar rencana Tuhan terpenuhi atas generasi mereka.\r\n\r\n[https://www.youtube.com/watch?v=OVRixfameGY](https://www.youtube.com/watch?v=OVRixfameGY)

### Perubahan
- tambahkan satu field gcm_token, facebook_token di table tb_customer, dengan tipe data string
- tambahkan satu tabel FAQ isinya question dan answer
- tambahkan id_voucher di tabel pro_berita_acara
- tambahkan id_voucher di tabel banner
- tambahkan order_id di tabel kategori
- tambahkan order_id di tabel tenant
- buat tabel subscribe dengan field (id, id_user, start_date, lama_hari)
- buat tabel bank dengan field (id, nama_bank, norek, nama_rekening, logo_url)
- tambahkan field metode, id_bank_promosee, nama_pengirim, no_rekening_pengirim di tabel pro_dpwd_trans
- tambahkan field no_ktp di tabel pro_member_card
- buat satu variable token yang di hardcode di website, random string dengan length lebih dari 10. Ini dipergunakan untuk - authentikasi request yang masuk, dan seluruh request API harus mengirimkan ini, token : "9090opop"


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

Login seperti biasa sesuai dengan email dan password, gcm_token hanya disimpan saya di tabel customer bila email dan password valid dan bisa login.


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

Seluruh data termasuk FB token hanya disimpan saja di tabel customer.
Sebelum disimpan cek terlebih dahulu bila memang fb_token yang dikirim telah terdapat di database, maka update saja data yang dikirim tanpa membuat satu row customer baru.

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
			"link": "http://promosee.com",
			"id_voucher": 1,
		},
		{
			"id": 2,
			"banner_url": "http://promosee.com/image.jpg",
			"link": "http://prodia.com",
			"id_voucher": 2,
		}
	],
	"banks": [
		{
			"id": 1,
			"nama": "BCA",
			"nomor_rekening": "1234567EFGH",
			"nama_rekening": "promosee account",
			"logo_url": "http://promosee.com/image.jpg",
		},
		{
			"id": 2,
			"nama": "BNI",
			"nomor_rekening": "456789KLMNOP",
			"nama_rekening": "promosee account",
			"logo_url": "http://promosee.com/image.jpg",
		}
	],
	"news_events": [
		{
			"id": 1,
			"title": "News",
			"subject": "Subject News",
			"image_url": "http://promosee.com/image.jpg",
			"description": "It's description news",
			"id_voucher": 1,
		},
		{
			"id": 2,
			"title": "Event",
			"subject": "Subject Event",
			"image_url": "http://promosee.com/image.jpg",
			"description": "It's description events",
			"id_voucher": 2,
		}
	],
	"categories": [
		{
			"id": 1,
			"name": "Food",
			"image_url": "http://promosee.com/image.jpg",
			"order_id": 1,
			"tenants": [	
				{
					"id": 1,
					"code": "JNJ100",
					"name": "JunNJan",
					"type": "online",
					"phone": "021456789",
					"address": "Jalan Cikutra Bandung", 
					"email": "junnjan@mail.com",
					"order_id": 1,
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
					"order_id": 2,
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
			"id_voucher": 1,
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
			"id_voucher": 2,
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

### Voucher List API

End point: `/api/vouchers/list`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |

API ini hanya untuk mengambil semua list data voucher yang masih aktif ataupun didalam periode aktif (diantara start date dan end date)

Response:
```json
{
	"vouchers": [
		{
			"id": 1,
			"name": "Voucher Prodia",
			"subject": "Voucher Prodia 50.000",
			"description": "Dapatkan voucher prodia senilai 50000",
			"price": 1000,
			"id_tenant": 32,
			"id_voucher": 1,
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
			"id_voucher": 2,
			"start_date": 2017-02-02,
			"end_date": 2017-04-04,
			"voucher_image_url": "http://promosee.com/image.jpg",
			"slide_image_url": "http://promosee.com/image.jpg",
			"big_image_url": "http://promosee.com/image.jpg",
			"min_payment": 500,
			"redeem_code": "ABCD678",
		},
	]
}
```

### Category List API

End point: `/api/categories/list`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |

API ini hanya untuk mengambil semua list data category yang masih aktif, beserta detail tenant didalamnya.
Struktur response sama dengan response login dan register

Response:
```json
{
	"categories": [
		{
			"id": 1,
			"name": "Food",
			"image_url": "http://promosee.com/image.jpg",
			"order_id": 1,
			"tenants": [	
				{
					"id": 1,
					"code": "JNJ100",
					"name": "JunNJan",
					"type": "online",
					"phone": "021456789",
					"address": "Jalan Cikutra Bandung", 
					"email": "junnjan@mail.com",
					"order_id": 1,
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
					"order_id": 2,
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
	]
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

API ini untuk mengambil semua list data transaksi voucher sesuai user id yang dikirimkan

Response:
```json
{
	"transactions": [
		{
			"id": 78,
			"id_voucher": 12,
			"date" : "2016-11-16 05:30:00",
			"type_payment": "pulsa",
			"used": "yes"
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

API ini untuk mengambil semua list data member card yang telah terdaftar berdasarkan user id yang dikirim

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

### My Wallet API

End point: `/api/wallets/created`
Method: `GET`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |

API ini untuk mengambil semua list dpwd transaction yang telah dilakukan oleh user id yang dikirim

Response:
```json
{
	"wallets": [
		{
			"id": 1,
			"nomor": "1234567ABCDE",
			"amount": 50000,
			"id_bank": 1,
			"metode": "atm",
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
			"id_bank": 1,
			"metode": "atm",
			"type": "withdraw",
			"status": "approve",
			"remark": "test",
			"balance": 10000,
			"date" : "2016-12-15 07:30:00",
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

API ini untuk mengambil semua list dpwd transaction yang telah dilakukan oleh user id yang dikirim

Response:
```json
{
	"redemptions": [
		{
			"id": 1,
			"date": 2017-02-02,
			"id_voucher": 1,
			"show_redeem": "yes",
		},
		{
			"id": 2,
			"date": 2017-02-02,
			"id_voucher": 3,
			"show_redeem": "no",
		}
	]
	
}
```

### Buy Voucher API

End point: `/api/vouchers/buy`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |
| id_voucher     | voucher id |
| id_type_payment     | type payment id |

API ini untuk membuat sebuah voucher transaction, type payment id ini disesuaikan dengan tabel pro_type_payment (1/2)
Response yang dikembalikan adalah satu JSON detail dari voucher tersebut.

Response:
```json
{
	"id": 1,
	"name": "Voucher Prodia",
	"subject": "Voucher Prodia 50.000",
	"description": "Dapatkan voucher prodia senilai 50000",
	"price": 1000,
	"id_tenant": 32, 
	"id_voucher": 2,
	"start_date": 2017-02-02,
	"end_date": 2017-04-04,
	"voucher_image_url": "http://promosee.com/image.jpg",
	"slide_image_url": "http://promosee.com/image.jpg",
	"big_image_url": "http://promosee.com/image.jpg",
	"min_payment": 500,
	"redeem_code": "ABCD678",
}
```

### Redemption API

End point: `/api/redemptions/add`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |
| id_voucher     | voucher id |

API ini untuk membuat sebuah redemption. Response yang dikembalikan adalah satu JSON redemption yang terbuat tersebut.

Response:
```json
{
	"id": 1,
	"date": 2017-02-02,
	"id_voucher": 1,
	"show_redeem": "yes",
}
```

### Add Member Card API

End point: `/api/redemptions/add`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |
| id_tenant     | tenant id |
| nomor_membercard     | nomor membercard |
| nomor_ktp     | nomor ktp user |

API ini untuk mendaftarkan sebuah member card. Terdapat dua cara dengan hanya mendaftarkan nomor ktp saja atau menggunakan nomor membercard
Response yang dikembalikan adalah satu JSON member card yang telah terbuat tersebut.

Response:
```json
{
	"id": 1,
	"register_date": "2017-02-02",
	"nomor": "1234567ABCD",
	"id_tenant": 2,
	"total_stamp": 10,
	"start_date": "2017-02-02",
	"end_date": "2017-10-02",
}
```

### Top Up Confirmation API

End point: `/api/topups/confirmation`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user     | user id |
| metode     | metode pengiriman (tunai / atm / e-banking) |
| id_bank     | bank id |
| jumlah     | jumlah yang ditaransfer |
| nama     | nama pemegang rekening |
| no_rekening     | no rekening pengirim |

API ini untuk mengkonfirmasi sebuah pembayaran yang telah dilakukan user
Response yang dikembalikan adalah satu JSON dpwd transaction yang telah terbuat tersebut.

Response:
```json
{
	"id": 1,
	"nomor": "1234567ABCDE",
	"amount": 50000,
	"id_bank": 1,
	"metode": "atm",
	"type": "deposit",
	"status": "pending",
	"remark": "test",
	"balance": 10000,
	"date" : "2016-12-15 07:30:00",
}
```


### Hide Redemption List API

End point: `/api/redemptions/hide`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_redeem     | redemption id |

API ini untuk hide redemption list, atau mengubah field show_redeem dari yes menjadi no
Response yang dikembalikan adalah satu JSON redemption yang telah diubah tersebut.

Response:
```json
{
	"id": 1,
	"date": 2017-02-02,
	"id_voucher": 1,
	"show_redeem": "no",
}
```


### Edit Profile API

End point: `/api/auth/edit-profile`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user      | id user  |
| name      | nama user  |
| email     | email user  |
| password  | password user  |
| phone  | phone user  |
| city  | kota user  |
| address  | alamat user  |

API ini dibuat untuk edit profile data customer sesuai id user yang dikirimkan. Response yang dikembalikan adalah satu JSON yang berisi detail user yang telah berubah.

Response
```json
{
	"id": 1,
	"email": "test@gmail.com",
	"name": "test",
	"phone": "087829976921",
	"city": "jakarta",
	"address": "jalan monas",
	"image_url": "http://promosee.com/image.jpg",
	"balance": 65000,
}
```



### Subscribe API

End point: `/api/subscribe/add`
Method: `POST`

Request parameters:

| Parameter | Description |
| --------- | ----------- |
| token     | Auth TOKEN |
| id_user      | id user  |
| days      | lama hari  |

API ini dibuat untuk subscribe promosee, dan data yang dikirimkan akan disimpan di tabel subscribe yang baru dibuat.
Response mengembalikan satu JSON subscribe yang telah dibuat.

Response
```json
{
	"id": 1,
	"start_date": 2017-02-04,
	"days": 14,
}
```
