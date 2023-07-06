---
date: 2022-12-24
title: Jak zostałem programistą
categories: programowanie wspominki
---

Nazywam się Daniel Janus, mam 38 lat i od 31 lat jestem programistą.

Czasem się tak przedstawiam i czasem budzi to zaciekawienie, powątpiewanie albo jedno i drugie naraz. Wracam więc myślą do samych początków.

Pamiętam, jechaliśmy z ojcem autobusem. Mogłem mieć pięć, może sześć lat. Tata ni stąd, ni zowąd zaczął opowiadać mi o komputerach. Nowe, nieznane mi wcześniej słowo. Nie mam pojęcia, jaka była treść tej opowieści, za to dokładnie pomiętam żarówki, które zapaliły mi się w oczach. I ten kawałek rozmowy:

– Jakby czarodziejski???<br>
– Jakby czarodziejski.

Minęło kilkadziesiąt milionów sekund. Zdaje się, że łaziłem w międzyczasie i mówiłem, że chcę komputer. I w końcu, w 1991, tata przyjechał z saksów w Niemczech i mówi: kupiłem ci komputer! Nazywał się Commodore 64 i pachniał plastikiem, elektroniką i świeżością. Dzisiejsze komputery już tak nie pachną.

<figure><img src="/img/blog/c64-box.jpg" alt="Pudełko od C64"><figcaption>To nie jest dokładnie TO pudełko – zdjęcie wygrzebałem z jakiejś aukcji na Allegro – ale tak wyglądało.</figcaption></figure>

Zanim go uruchomiliśmy, upłynęły jeszcze prawie dwa tygodnie. W tym czasie w domu nastał nowy, kolorowy telewizor, a ja siedziałem jak na szpilkach, czekałem na ten telewizor, fantazjowałem o tym, jak to będzie, kiedy całość już podłączymy, obmacywałem zawartość pudełek (było jeszcze drugie, ze stacją dyskietek), zgadywałem, który klawisz do czego służy, i czytałem dołączone materiały. Oczywiście po niemiecku. I oczywiście nic z tego nie rozumiałem – niemieckiego miałem się zacząć uczyć w podstawówce dopiero od piątej klasy.

Były tam też wypisane różne rzeczy, które komputer może wyświetlić na ekranie, i takie, które trzeba do niego wklepać z klawiatury, żeby jakoś zareagował.

<figure><img src="/img/blog/c64-book.jpg" alt="Commodore 64 Bedienungshandbuch: okładka"><figcaption>Zdjęcie tym razem z eBaya.</figcaption></figure>

W końcu nadszedł TEN dzień. Komputer podłączony, telewizor włączony, szukamy odpowiedniej częstotliwości wideo… i wreszcie z telewizyjnego białego szumu wyłonił się obraz, znany doskonale wielu ludziom z mojego pokolenia:

<figure><img src="/img/blog/c64-start.webp" alt="Ekran startowy C64"><figcaption>Ekran startowy C64. Kto go pamięta?</figcaption></figure>

Nie pamiętam, co zrobiłem potem. Pewnie nacisnąłem wszystkie klawisze po kolei i nauczyłem się wyłączać i włączać całość. Jakoś intuicyjnie wykminiłem, jak działają klawisze kursora, co robi spacja, a co RETURN. Potem próbowałem przepisywać z tego niemieckiego podręcznika różne polecenia i patrzyć, co się stanie.

W którymś momencie odkryłem, że po wpisaniu `LOAD "$",8` (dlaczego akurat 8? dlaczego akurat dolar? strasznie mnie to ciekawiło, ale nie wnikałem w szczegóły) i naciśnięciu RETURN komputer wypisuje na ekranie nowy, nieznany mi komunikat błędu, ale nie robi tego od razu – najpierw stacja dysków wydaje kilka dźwięków i zaczyna mrugać diodą na czerwono. Włożyłem dyskietkę do napędu (z komputerem przyjechało kilka dyskietek demonstracyjnych) i spróbowałem jeszcze raz. Tym razem zamigotało na zielono i komunikat był inny!

Wpisałem `LIST`, tak jak kazał podręcznik (nadal nie bardzo rozumiejąc, co właściwie się dzieje). Commodore wyświetlił długą serię komunikatów. Były tam jakieś liczby, skróty i nazwy.
Wtedy po raz kolejny wpatrzyłem się w ten kawałek podręcznika:

<figure><img src="/img/blog/c64-instrukcja.jpg">W Internecie wbrew powiedzeniu czasem giną rzeczy, ale instrukcja do C64 na szczęście ocalała.</figure>

Zaraz… `PROGRAMMNAME`? NAME? Imię? Nazwa?

Napisałem `LOAD "LASER FORCE",8`. Instrukcji o dokładnie takim brzmieniu nie było w podręczniku! Ale `"LASER FORCE"` to była jedna z nazw, bodaj ostatnia, w tym długim komunikacie – i pomyślałem, że można wstawić ją zamiast owego enigmatycznego `PROGRAMMNAME`.

Strzał w dziesiątkę. Komputer odpowiedział komunikatem z podręcznika i zamyślił się na dłuższą chwilę, stacja dysków znowu zaśpiewała zgrzytliwą piosenkę. Czułem, że jestem na dobrym tropie.

Wreszcie wpisałem `RUN`. Uruchomiłem grę! Zbiegła się cała rodzina.

<iframe width="100%" height="500" src="https://www.youtube.com/embed/M5tpEAW_zzA" frameborder="0" allowfullscreen></iframe>

Dokonałem wtedy swoich dwóch pierwszych odkryć programistycznych. Być może były to najdonioślejsze odkrycia w całej mojej karierze.

1. Mogę wpisać inną nazwę i załaduje się inny program. A mówiąc ogólniej: różnym parametrom polecenia odpowiadają różne skutki. Inne wejście – inne wyjście.
2. Warto eksperymentować! Prawdopodobnie nic nie popsuję, najwyżej wyskoczy jakiś błąd i zawsze mogę spróbować jeszcze raz.

Było też odkrycie trzecie, zupełnie niezwiązane z programowaniem:

<ol start="3"><li>Lubię grać w gry.</li></ol>

<hr>

W następnych tygodniach wypróbowałem wszystkie programy i gry na tych kilku dyskietkach, które miałem. Ale skąd wziąć następne dyskietki? W roku 1991 w małym miasteczku (które jeszcze wtedy było wsią) nie było łatwo o dostęp do oprogramowania. Równolegle z graniem próbowałem więc przepisywać następne programy z podręcznika.

Niedługo potem kilku innych chłopaków z sąsiedztwa też dostało C64. Byłem jednak jedyną osobą ze stacją dysków i ten fakt zaważył na mojej przyszłości – wszyscy inni mieli magnetofony. O ile łatwo było po prostu wymieniać się kasetami, o tyle w moim przypadku pozyskanie nowych gier wymagało wyprawy do kolegi i zabrania ze sobą stacji. Rodzice mi nie pozwalali. Samemu? Ruchliwą ulicą? Z drogim sprzętem?

W międzyczasie tata wytrzasnął skądś stare numery „Bajtka”. Był tego pokaźny stosik, archiwa z kilku lat. „Bajtek” odegrał ogromną rolę w mojej wczesnej edukacji informatycznej: były tam małe kawałki wiedzy podane w przystępny sposób. Nie wszystko rozumiałem, ale przynajmniej było po polsku. I rubryka „Tylko dla przedszkolaków” tłumaczyła nie tylko to, co robią pomieszczone tam programy, ale również – dlaczego tak, a nie inaczej.

Dostałem też rozmaite książki – o BASIC-u i o samym komputerze, w tym polską biblię C64, „Commodore 64” Bohdana Frelka. Różne kawałki wiedzy zaczęły składać mi się w głowie w całość. Eksperymentowałem. Próbowałem zmieniać różne rzeczy w programach z Bajtka i w końcu pisać własne. Pamiętam, że jeden z moich programików z tamtych czasów nazywał się `ABC FIZYKI` i wyświetlał definicje jednostek SI, które przepisałem z jednej z książek ojca.

Kiedy kilka lat później – bodaj w roku 1996 – przesiadłem się na PC, znałem już zupełnie nieźle BASIC i zaczynałem kumać, o co chodzi w asemblerze 6502. Stary komputer jednak poszedł w odstawkę. Rodzice sprzedali go niedługo potem, ale do dziś, po prawie ćwierćwieczu, potrafię bez zaglądania do internetu wyrecytować zgrubną mapę pamięci C64, powiedzieć, do których komórek pamięci trzeba wpisać jaki bajt, żeby tekst wyświetlał się na biało na czarnym tle z czarną ramką (mój ulubiony schemat kolorów z tamtych czasów), jaki jest kod znaku PETSCII odpowiedzialnego za czyszczenie ekranu oraz która procedura Kernala odpowiada za restart systemu, a która za wypisanie znaku na ekran.

Na pececie uczyłem się Pascala. Ale to już zupełnie inny rozdział tej historii.
