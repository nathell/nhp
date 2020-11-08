---
date: 2020-10-02
title: Rozwiązanie zagadki Radia Nowy Świat
categories: RNŚ
---

Dziś na zamkniętej grupie fejsbukowej dla Patronów [Radia Nowy Świat][1] pojawiła się zagadka-szyfr.

<img src="/img/blog/rns.jpg">

Wrzucam rozwiązanie na bloga, bo tak wygodniej i mogę wstawić więcej obrazków, gdzie chcę. Jeśli chcesz, Czytelniku, pogłówkować we własnym zakresie – nie czytaj dalej.

Jeśli interesuje Cię rozwiązanie (i tok myślowy, który mnie do niego zaprowadził), przewiń stronę…

<div style="height: 1000px"></div>

Pierwsza myśl: ta wiadomość naprawdę składa się z takich liter. Sprawdzam “na oko”: jest dużo `A`, a mało `Ź`, czyli tak, jakbyśmy się spodziewali w losowym polskim tekście. Próbuję czytać kolumnami, od tyłu, ruchem konika szachowego – bezskutecznie!

To może przyjrzyjmy się uważniej frekwencji. Akurat mam pod ręką REPL-a Clojurowego:

```clojure
(def rnś "ABCĆĄEĘIODFÓUYAGĄHJKEĘLŁIMNŃOPÓUYRSAĄŚETĘWZIOÓŹUYŻBACĄEĆĘDFIGHJOÓUKYLŁMAĄNŃEĘIPROSŚÓTWZŹUŻBYCĆADĄEFĘGHJKILŁOÓUMYANĄEŃĘIOÓPRSUŚYAĄTEWĘZIOÓŹŻUBYCĆADFGĄEĘIOHJÓUYKLAŁĄEMĘNI")
;=> #'rnś

(frequencies rnś)
;=> {\A 10, \Ą 10, \B 4, \C 4,  \Ć 4, \D 4, \E 10, \Ę 10,
;=>  \F 4,  \G 4,  \H 4, \I 10, \J 4, \K 4, \L 4,  \Ł 4,
;=>  \M 4,  \N 4,  \Ń 3, \O 9,  \Ó 9, \P 3, \R 3,  \S 3,
;=>  \Ś 3,  \T 3,  \U 9, \W 3,  \Y 9, \Ź 3, \Z 3,  \Ż 3}
```

Ha, jednak nie. To wygląda bardzo ciekawie: wszystkie samogłoski występują po 10 lub 9 razy, a wszystkie spółgłoski – po 4 lub 3 razy. W takim razie jest bardzo mało prawdopodobne, że znaczenie mają litery jako takie.

Przyglądam się bliżej: to nie jest przypadek, że tekst zaszyfrowany zaczyna się od `ABCĆ`. Gdyby skreślić wszystkie spółgłoski, to zostaniemy z `AĄEĘIOÓÓUY`…
I analogicznie, gdyby skreślić samogłoski, to dostajemy `BCĆDFGH`…

Wobec tego to nie litery niosą informację w tym tekście, tylko przeplot spółgłosek i samogłosek. Zobaczmy, jak to wygląda:

```clojure
(def vowel? (comp boolean #{\A \Ą \E \Ę \I \O \Ó \U \Y}))
;=> #'vowel?

(map (comp {false 0, true 1} vowel?) rnś)
;=> (1 0 0 0 1 1 1 1 1 0 0 1 1 1 1 0 1 0 0 0 1 1 0 0
;=>  1 0 0 0 1 0 1 1 1 0 0 1 1 0 1 0 1 0 0 1 1 1 0 1
;=>  1 0 0 1 0 1 1 0 1 0 0 1 0 0 0 1 1 1 0 1 0 0 0 1
;=>  1 0 0 1 1 1 0 0 1 0 0 1 0 0 0 0 1 0 0 1 0 0 1 0
;=>  1 1 0 1 0 0 0 0 1 0 0 1 1 1 0 1 1 0 1 1 0 1 1 1
;=>  1 0 0 0 1 0 1 1 1 0 1 0 1 0 1 1 1 0 0 1 0 1 0 0
;=>  1 0 0 0 1 1 1 1 1 0 0 1 1 1 0 0 1 0 1 1 0 1 0 1)

(frequencies (map vowel? rnś))
;=> {true 86, false 82}
```

Tak, to wygląda lepiej. 86 samogłosek i 82 spółgłoski. Tylko co to za kod? Morse? Może spółgłoski to kropki, a samogłoski – kreski? Próbuję online-owych dekoderów. Kod Morse’a bez odstępów jest niejednoznaczny, więc przewijam się przez różne opcje. Nic nie pasuje. Gdyby kreski i kropki ustawić odwrotnie, też nie działa.

To może to jest ASCII? Przyglądam się bliżej: jest prawie regułą, że co ósmy znak to samogłoska. W takim razie gdyby samogłoski odpowiadały zerom, a spółgłoski jedynkom, i gdyby pogrupować je po 8 (tak jak na obrazku), to…

```clojure
(defn to-number [x] (apply + (map * (reverse x) (iterate #(+ % %) 1))))
;=> to-number

(map to-number (partition 8 (map #(if (vowel? %) 0 1) rnś)))
;=> (112 97 115 116 101 98 105 110 46 99 111 109
;=>  47 98 72 116 84 107 112 99 74)
```

Ha! Ciepło, ciepło!

```clojure
(apply str (map char *1))
;=> "pastebin.com/bHtTkpcJ"
```

Gorąco! Jest adres strony!

Wchodzimy na tego pastebina i…

```
ALILMRD = SLWDWASWY SLWDWADZLWSLĄY PLĘĆ ALILNOÓE SLDWASWY
DZLWELĘYOMŚCLW YTSLĘCT SLWDWASWY DEMDZLWŚCLM
```

Miliard ŻE CO plęć tysięcy? To są jakieś liczby, napisane przez kogoś, kto sepleni. Albo zamienia litery miejscami. Można by zgadnąć, według jakiego klucza, ale on akurat jest podany w tytule strony. I na obrazku. MALINOWE BUTY.

To teraz sięgnijmy po `tr`, shellową zamieniarkę znaków:

```shell
tr 'MALINOWE BUTY' 'AMILONEW UBYT' < plik.txt
MILIARD = SIEDEMSET SIEDEMDZIESIĄT PIĘĆ MILIONÓW
SIDEMSET DZIEWIĘTNAŚCIE TYSIĘCY SIEDEMSET DWADZIEŚCIA
```

I to już? To ma być rozwiązanie zagadki? Jaki miliard? Jakie 775719720? Pal sześć literówkę, ale przecież się nie równa!

Wrzucam to na grupę. I dostaję ostateczną podpowiedź od pani Oliwii.

<img src="/img/blog/rns2.png">

Z minusem wychodzi 224280280. I to jest rozwiązanie: [skądinąd][1] znany numer telefonu.

Dzwońcie tam, gdy Wam smutno albo źle w życiu. A jeszcze lepiej [zostańcie patronami][2], to też będziecie mogli rozwiązywać zagadki!

 [1]: https://nowyswiat.online
 [2]: https://patronite.pl/radionowyswiat
