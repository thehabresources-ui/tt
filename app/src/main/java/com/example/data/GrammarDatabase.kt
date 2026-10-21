package com.example.data

import com.example.model.LearnedConcept
import com.example.model.QuizQuestion

object GrammarDatabase {

    val concepts = listOf(
        LearnedConcept(
            id = "kn_am_khas",
            category = "Golongan Kata",
            title = "Kata Nama Am & Khas",
            definition = "Kata Nama Am merujuk benda, manusia, haiwan atau tempat secara umum (huruf kecil). Kata Nama Khas merujuk khusus dengan huruf besar pada pangkal kata.",
            examples = listOf(
                "Kata Nama Am: kereta, kucing, guru, bandar, sekolah",
                "Kata Nama Khas: Sang Kancil, Proton Saga, Cikgu Aiman, Kuala Lumpur"
            ),
            tip = "Kata Nama Khas mestilah sentiasa bermula dengan huruf besar walau di mana kedudukannya dalam ayat."
        ),
        LearnedConcept(
            id = "kata_kerja",
            category = "Golongan Kata",
            title = "Kata Kerja (Transitif & Tak Transitif)",
            definition = "Kata yang menyatakan perbuatan, perlakuan atau aksi. Kata kerja transitif memerlukan objek, manakala tak transitif boleh berdiri sendiri.",
            examples = listOf(
                "Transitif: Ibu 'memasak' kari. (Kari ialah objek)",
                "Tak Transitif: Adik 'menangis'. Burung 'terbang' tinggi."
            ),
            tip = "Uji dengan bertanya 'Buat apa?'. Jawapannya ialah kata kerja."
        ),
        LearnedConcept(
            id = "kata_adjektif",
            category = "Golongan Kata",
            title = "Kata Adjektif (Kata Sifat)",
            definition = "Perkataan yang menerangkan keadaan atau sifat bagi sesuatu kata nama seperti rupa, bentuk, warna, ukuran dan perasaan.",
            examples = listOf(
                "Sifat: rajin, cerdik, berani",
                "Warna & Bentuk: merah, biru, bulat, lonjong",
                "Ukuran: tinggi, tebal, luas, kerdil"
            ),
            tip = "Kata adjektif boleh didahului perkataan 'sangat', 'amat', atau 'sungguh' (cth: sangat pintar)."
        ),
        LearnedConcept(
            id = "imbuhan_men",
            category = "Imbuhan",
            title = "Imbuhan Awalan meN-",
            definition = "Awalan meN- berubah bentuk mengikut huruf awal kata dasar: me-, mem- (p,b), men- (t,d), meng- (k,g,a,e,i,o,u), meny- (s).",
            examples = listOf(
                "Huruf p/t/k/s biasanya lebur: meN- + tulis = menulis, meN- + sapu = menyapu",
                "meN- + baca = membaca, meN- + dengar = mendengar, meN- + gergaji = menggergaji"
            ),
            tip = "Hafal formula lebur: 'P, T, K, S' gugur apabila bertemu imbuhan meN-!"
        ),
        LearnedConcept(
            id = "imbuhan_pen",
            category = "Imbuhan",
            title = "Imbuhan Awalan peN-",
            definition = "Membentuk kata nama yang menyatakan orang yang melakukan, alat, atau sifat peribadi seseorang.",
            examples = listOf(
                "Alat: peN- + sapu = penyapu, peN- + potong = pemotong",
                "Orang/Pelaku: peN- + curi = pencuri, peN- + lari = pelari"
            ),
            tip = "Perubahan awalan peN- menyerupai pola awalan meN- (cth: sapu -> menyapu -> penyapu)."
        ),
        LearnedConcept(
            id = "peribahasa_asas",
            category = "Peribahasa",
            title = "Simpulan Bahasa Popular",
            definition = "Ungkapan ringkas yang mempunyai maksud tersirat untuk memberi nasihat atau menggambarkan perwatakan manusia.",
            examples = listOf(
                "Ulat buku = Orang yang sangat gemar membaca",
                "Kaki ayam = Berjalan tanpa memakai kasut atau selipar",
                "Ringan tulang = Orang yang rajin membantu",
                "Berat mulut = Orang yang pendiam atau malas bercakap"
            ),
            tip = "Kerap digunakan dalam karangan untuk mencantikkan gaya bahasa dan meraih markah cemerlang."
        ),
        LearnedConcept(
            id = "penjodoh_bilangan",
            category = "Penjodoh Bilangan",
            title = "Penjodoh Bilangan Lazim",
            definition = "Kata yang digunakan bersama-sama kata bilangan untuk membilang orang, binatang, benda, atau tempat.",
            examples = listOf(
                "Sikat / Sisir: untuk beberapa biji pisang yang masih bersambung",
                "Utas: untuk benda panjang dan boleh digulung (rantai, tali)",
                "Bentuk: untuk benda kecil yang melengkung (cincin, mata kail)",
                "Helai: untuk benda nipis dan lebar (baju, kertas, daun)"
            ),
            tip = "Pisang sesisir digabung menjadi setandan pisang!"
        ),
        LearnedConcept(
            id = "ejaan_tatabahasa",
            category = "Ejaan",
            title = "Pembetulan Ejaan Lazim",
            definition = "Membezakan ejaan betul mengikut Kamus Dewan terkini berbanding kesalahan ejaan yang kerap berlaku.",
            examples = listOf(
                "SALAH: merbahaya -> BETUL: berbahaya",
                "SALAH: ibubapa -> BETUL: ibu bapa",
                "SALAH: keretapi -> BETUL: kereta api",
                "SALAH: tengahari -> BETUL: tengah hari"
            ),
            tip = "Kebanyakan kata majmuk dieja terpisah kecuali perkataan yang telah mantap seperti antarabangsa, setiausaha, matahari."
        )
    )

    // Master list of all quiz questions
    val allQuestions = listOf(
        // Dunia 1: Golongan Kata & Penjodoh Bilangan
        QuizQuestion(
            id = 1,
            category = "Golongan Kata",
            question = "Apakah golongan kata bagi perkataan 'MELOMPAT'?",
            options = listOf("Kata Kerja", "Kata Nama", "Kata Adjektif"),
            correctIndex = 0,
            explanation = "Tepat sekali! 'Melompat' ialah Kata Kerja kerana ia menunjukkan perlakuan atau aksi fizikal.",
            hint = "Fikirkan sama ada perkataan ini merupakan perbuatan yang boleh dilakukan."
        ),
        QuizQuestion(
            id = 2,
            category = "Golongan Kata",
            question = "Manakah antara berikut ialah Kata Nama Khas?",
            options = listOf("kereta laju", "Sang Belang", "gunung tinggi"),
            correctIndex = 1,
            explanation = "Betul! 'Sang Belang' ialah Kata Nama Khas kerana merujuk kepada nama harimau dan dieja dengan huruf besar.",
            hint = "Cari perkataan yang bermula dengan huruf besar dan merujuk nama spesifik."
        ),
        QuizQuestion(
            id = 3,
            category = "Golongan Kata",
            question = "Pilih perkataan yang merupakan Kata Adjektif:",
            options = listOf("Berlari", "Pantai", "Pintar"),
            correctIndex = 2,
            explanation = "Hebat! 'Pintar' ialah Kata Adjektif (sifat) yang menerangkan kecerdikan fikiran seseorang.",
            hint = "Perkataan ini boleh diletakkan perkataan 'sangat' di hadapannya."
        ),
        QuizQuestion(
            id = 4,
            category = "Penjodoh Bilangan",
            question = "Pak Kassim membeli sesikat _______ di pasar tani.",
            options = listOf("pisang", "rambutan", "tembikai"),
            correctIndex = 0,
            explanation = "Tepat! Penjodoh bilangan 'sikat' atau 'sisir' digunakan khusus untuk gugusan pisang.",
            hint = "Buah kuning yang melengkung dan dimakan monyet."
        ),
        QuizQuestion(
            id = 5,
            category = "Golongan Kata",
            question = "Perkataan 'MERAH' dan 'KUNING' tergolong dalam kata apa?",
            options = listOf("Kata Nama", "Kata Adjektif Warna", "Kata Kerja"),
            correctIndex = 1,
            explanation = "Tepat sekali! Warna merupakan salah satu jenis Kata Adjektif yang menerangkan rupa sifat sesuatu benda.",
            hint = "Warna menerangkan sifat visual benda."
        ),
        QuizQuestion(
            id = 6,
            category = "Penjodoh Bilangan",
            question = "Ibu menghadiahkan se_______ rantai emas kepada kakak.",
            options = listOf("utas", "bentuk", "keping"),
            correctIndex = 0,
            explanation = "Betul! 'Utas' digunakan untuk benda yang panjang, halus atau berangkai seperti rantai dan tali.",
            hint = "Penjodoh bilangan ini digunakan untuk rantai atau tali leher."
        ),

        // Dunia 2: Imbuhan & Pembentukan Kata
        QuizQuestion(
            id = 7,
            category = "Imbuhan",
            question = "Cantumkan imbuhan: meN- + tulis = ?",
            options = listOf("mentulis", "menulis", "mengtulis"),
            correctIndex = 1,
            explanation = "Benar! Apabila awalan meN- bertemu huruf 't', huruf 't' akan lebur menjadi 'n' (menulis).",
            hint = "Huruf 'T' gugur apabila digabungkan dengan awalan meN-."
        ),
        QuizQuestion(
            id = 8,
            category = "Imbuhan",
            question = "Alat untuk menyapu sampah dipanggil apa dengan imbuhan peN-?",
            options = listOf("pensapu", "penyapu", "pengsapu"),
            correctIndex = 1,
            explanation = "Tepat! Huruf 's' dalam kata dasar sapu lebur menjadi 'ny' menjadi 'penyapu'.",
            hint = "Huruf 'S' bertukar menjadi 'ny'!"
        ),
        QuizQuestion(
            id = 9,
            category = "Imbuhan",
            question = "Apakah maksud imbuhan 'ter-' dalam perkataan 'terjatuh'?",
            options = listOf("Sengaja dilakukan", "Tidak sengaja", "Alat perbuatan"),
            correctIndex = 1,
            explanation = "Tepat sekali! Imbuhan 'ter-' pada perbuatan sering membawa erti tidak sengaja atau terjadi secara tiba-tiba.",
            hint = "Bolehkah seseorang merancang untuk jatuh?"
        ),
        QuizQuestion(
            id = 10,
            category = "Imbuhan",
            question = "Cantumkan: peN- + lari = ?",
            options = listOf("pelari", "penlari", "penglari"),
            correctIndex = 0,
            explanation = "Tepat! Bagi huruf dasar 'l', imbuhannya kekal menjadi 'pe-' (pelari).",
            hint = "Huruf 'L' menggunakan awalan 'pe-' sahaja."
        ),
        QuizQuestion(
            id = 11,
            category = "Imbuhan",
            question = "Pilih perkataan berimbuhan meN- yang betul bagi kata dasar 'sapu':",
            options = listOf("mensapu", "menyapu", "mengsapu"),
            correctIndex = 1,
            explanation = "Bagus! 'Sapu' menjadi 'menyapu' kerana huruf 's' luluh menjadi 'ny'.",
            hint = "Ingat formula leburan huruf 'S'."
        ),
        QuizQuestion(
            id = 12,
            category = "Ejaan",
            question = "Manakah antara berikut ejaan yang BETUL mengikut Tatabahasa Dewan?",
            options = listOf("merbahaya", "berbahaya", "bahaya-bahaya"),
            correctIndex = 1,
            explanation = "Tepat! Perkataan yang betul ialah 'berbahaya' kerana menggunakan imbuhan 'ber-' (bukan 'mer-').",
            hint = "Tiada imbuhan 'mer-' dalam sistem tatabahasa Bahasa Melayu."
        ),

        // Dunia 3: Peribahasa & Simpulan Bahasa
        QuizQuestion(
            id = 13,
            category = "Peribahasa",
            question = "Apakah maksud simpulan bahasa 'ULAT BUKU'?",
            options = listOf("Orang yang suka membaca", "Orang yang malas bekerja", "Buku yang rosak dimakan ulat"),
            correctIndex = 0,
            explanation = "Cemerlang! 'Ulat buku' bermaksud seseorang yang sangat tekun dan gemar membaca buku.",
            hint = "Simpulan bahasa ini merujuk kepada orang yang rajin menuntut ilmu."
        ),
        QuizQuestion(
            id = 14,
            category = "Peribahasa",
            question = "Apakah simpulan bahasa bagi orang yang berjalan tanpa memakai kasut?",
            options = listOf("Kaki bangku", "Kaki ayam", "Kaki botol"),
            correctIndex = 1,
            explanation = "Betul! 'Kaki ayam' ialah simpulan bahasa untuk orang yang tidak memakai alas kaki.",
            hint = "Haiwan ayam tidak memakai selipar atau kasut!"
        ),
        QuizQuestion(
            id = 15,
            category = "Peribahasa",
            question = "Lengkapkan: Berat sama dipikul, ringan sama _______.",
            options = listOf("dijinjing", "dilemparkan", "dibiarkan"),
            correctIndex = 0,
            explanation = "Tepat! Peribahasa ini bermaksud susah senang dihadapi bersama-sama dengan semangat gotong-royong.",
            hint = "Benda ringan biasanya kita bawa dengan cara menjinjing."
        ),
        QuizQuestion(
            id = 16,
            category = "Peribahasa",
            question = "Apakah maksud 'Ringan tulang'?",
            options = listOf("Orang yang kurus", "Orang yang rajin bekerja", "Orang yang sakit tulang"),
            correctIndex = 1,
            explanation = "Tepat! 'Ringan tulang' bermaksud orang yang rajin menolong orang lain dan rajin bekerja.",
            hint = "Lawan bagi simpulan bahasa ini ialah 'berat tulang' (malas)."
        ),
        QuizQuestion(
            id = 17,
            category = "Peribahasa",
            question = "Bagai aur dengan tebing membawa maksud:",
            options = listOf("Bergaduh setiap masa", "Tolong-menolong antara satu sama lain", "Tinggal di tepi sungai"),
            correctIndex = 1,
            explanation = "Hebat! Aur dan tebing sentiasa saling memerlukan dan bantu-membantu menjaga kestabilan tanah tebing.",
            hint = "Menggambarkan hubungan masyarakat yang bekerjasama erat."
        ),
        QuizQuestion(
            id = 18,
            category = "Ejaan",
            question = "Pilih kata majmuk yang dieja secara BERCANTUM dengan betul:",
            options = listOf("kerjasama", "keretaapi", "ibubapa"),
            correctIndex = 0,
            explanation = "Betul! 'Kerjasama' ialah salah satu kata majmuk yang telah mantap dan dieja bercantum, manakala 'kereta api' dan 'ibu bapa' dieja terpisah.",
            hint = "Hanya satu perkataan di atas tergolong dalam 15 kata majmuk mantap."
        )
    )
}
