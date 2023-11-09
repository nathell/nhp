---
date: 2023-10-17
title: Programowanie wyborcze
categories: polityka
---

A więc już wszystko prawie wiadomo. Kiedy to piszę, jest jeszcze poniedziałek; policzone są głosy z ponad 99% komisji i ostateczny podział mandatów, jeśli jeszcze się zmieni, to minimalnie. W tej chwili Sejm X kadencji zarysowuje się tak:

<figure>
<table class="entry">
<tr class="header"><th>Komitet</th><th>Mandaty</th></tr>
<tr><td>Prawo i Sprawiedliwość</td><td class="right">194</td></tr>
<tr><td>Koalicja Obywatelska</td><td class="right">157</td></tr>
<tr><td>Trzecia Droga</td><td class="right">65</td></tr>
<tr><td>Lewica</td><td class="right">26</td></tr>
<tr><td>Konfederacja</td><td class="right">18</td></tr>
</table>
<figcaption class="center">Prawdopodobny podział mandatów w Sejmie</figcaption>
</figure>

Oficjalne wyniki poznamy pewnie dopiero we wtorek, 17 października. Ale już dziś od wczesnego popołudnia można było się emocjonować przybliżeniami podziału sejmowych mandatów, generowanych na podstawie danych cząstkowych spływających z PKW. Te dane co kilka minut aktualizowały się na [specjalnej stronie][1], którą zrobiłem w kilka godzin.

Kłębią mi się w głowie rozmaite myśli na temat tego dnia. Ta notka jest próbą retrospekcji i odpowiedzi na kilka pytań. Jedno z nich brzmi: czy warto było poświęcać czas na napisanie programu, którego czas życia to niecała doba?

Otóż tak.

Kiepsko spałem poprzedniej nocy. Długo nie mogłem zasnąć, czekając na late poll i oglądając na stronie PKW wyniki z pierwszych komisji; po przebudzeniu rzuciłem okiem na wyniki cząstkowe (bardzo wtedy pesymistyczne dla opozycji) i pomyślałem: ciekawe, jak to wygląda w przeliczeniu na mandaty? Strona PKW nie przeliczała tego na żywo, więc stwierdziłem, że skorzystam z okazji i zaimplementuję algorytm d'Hondta. Ile w końcu czasu – myślałem – może to zająć, plus prosty scraping strony PKW? Około 9 rano odpaliłem Emacsa, a o 16 strona była gotowa i udostępniłem ją w social mediach.

Gdybym wtedy wiedział o znakomitym serwisie [Michała Kostyka][2], który robi to samo i jeszcze więcej, to pewnie po prostu wgapiałbym się przez resztę dnia w tamtą stronę. Tym niemniej cieszę się, że nie wiedziałem.

Jak to stwierdził Radek Czajka na byłym Twitterze:

<blockquote class="twitter-tweet"><p lang="pl" dir="ltr">Każdy dorosły obywatel powinien przynajmniej raz napisać własną przeliczarkę głosów na mandaty. <a href="https://t.co/HMVxFr2Tvu">https://t.co/HMVxFr2Tvu</a></p>&mdash; Radek Czajka @rcz@101010.pl (@RadekCzajka) <a href="https://twitter.com/RadekCzajka/status/1713960281401888792?ref_src=twsrc%5Etfw">October 16, 2023</a></blockquote> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

Wokół [metody d’Hondta][3] przeliczania głosów na mandaty [narosło wiele mitów][4]. Wydaje mi się, że jako społeczeństwo mało rozumiemy, jak to w praktyce działa. Po zakończeniu liczenia głosów PKW ogłasza wyniki, wykonuje „magiczne szuru-buru” i ogłasza, kto został posłem. Sam nie byłem pewien, czy dobrze to rozumiem, i miałem ochotę sprawdzić swoje rozumienie w praktyce. Jeśli wyjdą mi takie same wyniki, jak koniec końców ogłosi PKW, to znaczy po pierwsze, że dobrze rozumiem – a po drugie, że proces demokratyczny zadziałał transparentnie i można mieć do niego zaufanie. Gdyby zaś coś się nie zgadzało, to warto przyjrzeć się bliżej.

Na razie wychodzą mi takie same wyniki, jakie widać w serwisie Michała, mimo że nie zaglądałem mu w kod. Wygląda więc na to, że zdałem swój własny osobisty egzamin z WOS-u.

To jedna rzecz. Druga sprawa jest taka, że moja strona od chwili debiutu rozniosła się viralem po internecie i cieszyła się niemałym zainteresowaniem (ponad 300 tys. odsłon i ok. 50 tys. unikalnych użytkowników w ciągu kilku godzin). Różnymi kanałami – w komentarzach w social mediach, w prywatnych wiadomościach – dostałem feedback, którego częścią się tu z Wami podzielę:

<img src="/img/blog/feedback-wybory.png" alt="Dużo podziękowań na ex-Twitterze">

I wiecie co? Strasznie fajnie jest dostać tyle miłych słów. Ale najbardziej wzrusza mnie to, że one nadeszły ze wszystkich preferencji politycznych, od lewa do prawa. To mi pokazuje, że – cokolwiek byśmy myśleli o tym, w którą stronę chcemy zmieniać kraj i kogo pogonić na cztery wiatry – nie jest nam wszystko jedno. Są dla nas ważne nasze wybory, chcemy wiedzieć, co z nich wyszło.

Mówi się, że wybory są świętem demokracji. Wzruszałem się wczoraj wysoką frekwencją, widząc, że traktujemy serio to święto. A dzięki tej stronie zupełnie nieoczekiwanie okazało się, że ono dla mnie trwa dwa dni.

I wreszcie trzecia rzecz: samo programowanie. Zdarzył mi się jeden z tych rzadkich dni, kiedy praca nad
kawałkiem kodu pochłania mnie tak bardzo, że prawie zapominam o bożym świecie. Zacząłem, jako się rzekło, od implementacji d’Hondta w Clojure (pomyślałem sobie: „no tak, fajnie będzie napisać funkcję, która złącza _n_ posortowanych leniwych sekwencji – potencjalnie nieskończonych – w jedną, również posortowaną, również leniwą i również potencjalnie nieskończoną”). Zajęło mi to może pół godziny. Potem sięgnąłem po [Skyscrapera][5], żeby pościągać wyniki w poszczególnych okręgach ze stron PKW i powydłubywać je z HTML-i. Jakie było moje zdziwienie, kiedy się okazało, że one ani nie zawierają tych danych już wyrenderowanych, ani kod javascriptowy nie odwołuje się do żadnego API, które by zwracało dane w JSON-ie, no, niechby nawet w XML-u!

Zafrapowany, zajrzałem w zakładkę „Requests” przeglądarkowych devtoolsów. Okazało się, że JS robi requesta AJAX-owego pod adres [`https://wybory.gov.pl/sejmsenat2023/data/obkw/pl_po_okr_sejm.blob`][6]. Różne detektory typów plików mówiły o tych danych tylko `data`. Ewidentnie coś binarnego, ale na bazę SQLite nie wyglądało.

Bliższe przyjrzenie się zdeobfuskowanej paczce javascriptowej rozwiązało zagadkę. Wyszło na to, że nazwy niektórych plików odpowiadają bibliotece [protobuf.js][7]. To były binarne pliki googlowskiego Protocol Buffers! Normalnie do ich przetwarzania potrzebna jest znajomość definicji protokołu (plik `.proto`), ale kol. Marcin podsunął online’owe [narzędzie][8], które umie sparsować takie dane bez tej dodatkowej wiedzy. Kod tego narzędzia, lekko zmieniony, trafił koniec końców do mojego repo: udało mi się zmusić go do wygenerowania topornego, ale jednak, JSON-a, w którym metodą zgadywania i przystawiania do wersji wyrenderowanej na stronie PKW wykminiłem, co jest czym czego i jak szukać danych, których potrzebuję.

Zatem fajne ćwiczenie z reverse-engineeringu. Jak już odwaliłem tę robotę, to się okazało, że na stronie PKW wiszą sobie jakby nigdy nic pliki CSV z danymi z poszczególnych komisji… Zgaduję jednak, że dzięki temu, że wystarczyło pobierać cyklicznie tylko jeden plik, miałem zawsze najświeższe i spójne dane (stąd zapewne chwilowe rozbieżności z serwisem Michała, które się zdarzały w ciągu dnia).

Nie mówcie nikomu, ale nie ustrzegłem się kilku błędów. Na przykład, żeby ułatwić sobie życie, kwestię przekraczania progu wyborczego rozwiązałem metodą zahardkodowania komitetów, które spełniły ten warunek albo nie musiały tego robić, na podstawie wyników exit polli. Potem najpierw się okazało, że wpisałem tam za dużo komitetów (z rozpędu dopisałem BS i PJJ), a potem, że ta lista nie była nigdzie używana… Na szczęście, ponieważ metoda d’Hondta stawia komitetom tak czy owak dość wyśrubowane wymagania, nie wpłynęło to na poprawność wyników.

Za to w którymś momencie z wyników zniknęła Mniejszość Niemiecka i ludzie mnie o to zapytali. Zaniepokojony, poszedłem sprawdzać, co robi algorytm. I okazało się, że jest jednak dobrze: wszystkie znaki na niebie i ziemi wskazują, że nadchodząca kadencja Sejmu będzie pierwszą, w której MN nie będzie miała swojego reprezentanta.

<blockquote class="twitter-tweet"><p lang="pl" dir="ltr">Uwzględnia. Dla danych z 18:21 ostatni, dwunasty iloraz w Opolu jest pierwszym ilorazem Konfederacji (30764/1) i jest on większy niż pierwszy iloraz MN (24861/1), który jest dopiero na szesnastym miejscu.</p>&mdash; Daniel Janus 🏳️‍🌈 🇺🇦 @nathell@mastodon.social (@nathell) <a href="https://twitter.com/nathell/status/1713955405460644036?ref_src=twsrc%5Etfw">October 16, 2023</a></blockquote>

Dostałem też [pytanie][9] o to, jak zmieniłby się rozkład mandatów, gdyby do przeliczenia zamiast d’Hondta została zastosowana metoda Sainte-Laguë. Sam się nad tym zastanawiałem, więc wyciągnąłem w metrze laptopa i poczyniłem szybko odnośną modyfikację kodu. Istnieje kilka wariantów metody Sainte-Laguë – pierwszy dzielnik bywa równy 1 albo 1,4 – sprawdziłem więc te dwa.

I z tymi tabelkami (aktualnymi na godzinę 00:35 we wtorek) Was zostawiam. Warto się zastanowić, jak na naszą demokrację wpłynąłby ewentualny powrót do ordynacji stosowanej ostatni raz w 2001 roku.

<figure>
<table class="entry">
<tr class="header"><th>Komitet</th><th>Mandaty</th></tr>
<tr><td>Prawo i Sprawiedliwość</td><td class="right">171</td></tr>
<tr><td>Koalicja Obywatelska</td><td class="right">140</td></tr>
<tr><td>Trzecia Droga</td><td class="right">70</td></tr>
<tr><td>Lewica</td><td class="right">41</td></tr>
<tr><td>Konfederacja</td><td class="right">37</td></tr>
<tr><td>Mniejszość Niemiecka</td><td class="right">1</td></tr>
</table>
<figcaption class="center">Podział mandatów w Sejmie, gdyby obowiązywała oryginalna ordynacja Sainte-Laguë</figcaption>
</figure>

<figure>
<table class="entry">
<tr class="header"><th>Komitet</th><th>Mandaty</th></tr>
<tr><td>Prawo i Sprawiedliwość</td><td class="right">176</td></tr>
<tr><td>Koalicja Obywatelska</td><td class="right">145</td></tr>
<tr><td>Trzecia Droga</td><td class="right">71</td></tr>
<tr><td>Lewica</td><td class="right">37</td></tr>
<tr><td>Konfederacja</td><td class="right">31</td></tr>
</table>
<figcaption class="center">Podział mandatów w Sejmie, gdyby obowiązywała zmodyfikowana ordynacja Sainte-Laguë (taka jak w wyborach w 2001)</figcaption>
</figure>

 [1]: https://danieljanus.pl/wybory2023/
 [2]: https://mkostyk.github.io/wybory2023-client/
 [3]: https://pl.wikipedia.org/wiki/Metoda_D%E2%80%99Hondta
 [4]: https://oko.press/metoda-dhondta-rozwiewamy-mity
 [5]: https://github.com/nathell/skyscraper
 [6]: https://wybory.gov.pl/sejmsenat2023/data/obkw/pl_po_okr_sejm.blob
 [7]: https://github.com/protobufjs/protobuf.js
 [8]: https://protobuf-decoder.netlify.app/
 [9]: https://twitter.com/coffeetea0/status/1713977372553146740
