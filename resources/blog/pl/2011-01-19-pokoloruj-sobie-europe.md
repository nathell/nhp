---
layout: theme:post
title: "Pokoloruj sobie Europę!"
date: 2011-01-19
comments: true
categories: Clojure programowanie
---

To jest druga część minicyklu zapoczątkowanego [artykułem o zipperach][1].
Temat ten zaprezentowałem również podczas wystąpienia
na spotkaniu [Warszawa JUG][3]. Dostępne są [slajdy z tej prezentacji][4]
(_uwaga:_ slajdy mają postać oskryptowanego HTML;
należy je oglądać w Firefoksie lub którejś przeglądarce WebKitowej).
Można również obejrzeć [nagranie wideo][2] wystąpienia, niestety ze
słabą jakością dźwięku.

## Problem

Jakiś czas temu zostałem poproszony o sporządzenie kilku mapek
Europy pokolorowanych w różny sposób. Dostałem kilka zestawów
danych, w których każdemu krajowi UE przypisana była jakaś
liczba: im większa ta wartość, tym ciemniejszym odcieniem państwo
miało być zaznaczone na mapce. Przykładowa pokolorowana mapka miała
wyglądać tak:

<img src="/img/blog/europa.png">

Rozpocząłem od ściągnięcia łatwo edytowalnej [mapy][6] ze stron
Wikimedia Commons, obliczyłem potrzebne intensywności kolorów dla
pierwszego zestawu, uruchomiłem [Inkscape][7] i zabrałem się do
kolorowania. Po półgodzinie żmudnego klikania zdałem sobie sprawę, że
prościej i szybciej byłoby mi napisać programik w Clojure, który
wygeneruje mapkę za mnie. Okazało się to zadaniem dość prostym: reszta
tego postu będzie rekonstrukcją przedsięwziętych przeze mnie kroków.

## SVG

Formatem źródłowego obrazka jest SVG. Wiedziałem, że to XML-owy
otwarty format grafiki wektorowej, często widywałem na Wikipedii
obrazy w tym formacie -- jednak jego ręczna edycja to coś, z czym nie
miałem do tej pory do czynienia. Szczęśliwie okazało się, że obrazek z
mapkami ma prostą strukturę. Krzywa-obwiednia każdego kraju jest
opisana elementem `path`, wyglądającym mniej więcej tak:

``` xml
<path
  id="pl"
  class="eu europe"
  d="współrzędne kolejnych węzłów krzywej (bardzo dużo)" />
```

Warto zwrócić uwagę na atrybut `id` -- jest to dwuliterowy
skrót ISO-3166-1-ALPHA2 nazwy kraju. Na początku kodu obrazka znajduje
się zresztą komentarz, który wyjaśnia użyte konwencje
nazewnicze. Dysponowanie tak porządnie przygotowanymi danymi
wejściowymi znakomicie ułatwiło mi pracę.

Jak się okazuje, SVG, podobnie jak HTML, [używa stylów CSS][8] do
definiowania wyglądu elementów. Wszystko, co trzeba zrobić do nadania
Polsce koloru czerwonego, to nadanie odpowiedniej wartości atrybutowi
stylu `fill`:

``` xml
<path
  id="pl"
  style="fill: #ff0000;"
  class="eu europe"
  d="współrzędne kolejnych węzłów krzywej (bardzo dużo)" />
```

Uzbrojeni w taką wiedzę, możemy rozpocząć pracę.

## XML w Clojure

Podstawowym sposobem obsługi XML w Clojure jest wykorzystanie
biblioteki `clojure.xml`, pozwalającej na parsowanie XML-a (na
zasadzie DOM, tzn. do struktury w pamięci) oraz serializację,
tzn. działanie w odwrotną stronę. Uruchommy więc REPL i zacznijmy od
wczytania naszej mapki i sparsowania jej:

``` clojure
> (use 'clojure.xml)
nil
> (def m (parse "/home/nathell/eur/Blank_map_of_Europe.svg"))
[...dłuuugie oczekiwanie...]
Unexpected end of file from server
  [Thrown class java.net.SocketException]
```

Zaraz! Jaki znowu `SocketException`? Firefox wyświetla tę mapkę
poprawnie, Chromium też, WTF? W tak świetnym języku, jakim jest
Clojure, wszystko powinno działać od ręki?!

Cóż, język jest tak dobry, jak dobre są jego biblioteki -- a jeśli
chodzi o Clojure, można to rozwinąć: biblioteki Clojure są tak dobre,
jak dobre są biblioteki Javy, których używają pod spodem. W tym
wypadku natrafiliśmy na cechę standardowego (z pakietu `javax.xml`)
parsera XML w Javie. Jest on restrykcyjny i stara się odrzucać
dokumenty, które nie są poprawne (w sensie _valid_, a nie tylko
_well-formed_). Jeśli tylko dany plik zawiera deklarację `DOCTYPE`, to
parser javowy, a za nim `clojure.xml/parse`, próbuje ściągnąć z
podanego adresu schemat DTD i zwalidować dokument według tego
schematu. Jest to działanie z wielu względów niepożądane, zwłaszcza z
punktu widzenia [World Wide Web Consortium][9], bo to na serwerach
tej organizacji przechowywane są schematy opisujące sieciowe
standardy. Łatwo sobie wyobrazić, jak duży ruch sieciowy generują
nadgorliwe parsery: traktuje o tym [post na blogu W3C][10].
Zapewne wielu programistów Javy zetknęło się kiedyś z tym
problemem. Jest kilka rozwiązań; użyjemy najprostszego z nich, czyli
po prostu... ręcznie usuniemy przeszkadzającą deklarację `DOCTYPE`.

``` clojure
> (def m (parse "/home/nathell/eur/bm.svg"))
#'user/m
> m
[...wiele ekranów liczb...]
```

No, tym razem udało się wczytać. Obejrzenie sparsowanej
struktury jest utrudnione z uwagi na jej wielkość (można było się tego
spodziewać: plik SVG waży ponad 0,5 MB!), ale z samego początku tego,
co nam wypluwa REPL, widzimy, że jest to mapa. Zobaczmy, z jakich
kluczy się składa:

``` clojure
> (keys m)
(:tag :attrs :content)
```

Jest to więc mapa o trzech polach: z nazw (albo z dokumentacji) można
się domyślić, co zawierają. Pole `:tag` to nazwa elementu XML,
`:attrs` jest mapą atrybutów tego elementu, a `:content` -- sekwencją
jego podelementów, z których każdy jest znów reprezentowany przez mapę
o takiej samej strukturze (względnie przez napis, jeśli jest to
element napisowy):

``` clojure
> (:tag m)
:svg
> (:attrs m)
{:xmlns "http://www.w3.org/2000/svg", :width "680", :height "520", :viewBox "1754 161 9938 7945", :version "1.0", :id "svg2"}
> (count (:content m))
68
```

Tak dla wprawki, spróbujmy zapisać sparsowaną reprezentację z powrotem
jako XML. Funkcja `emit` powinna być w stanie to zrobić, ale wypisujekl
ona XML na standardowe wyjście. Możemy użyć makra `with-out-writer` z
pakietu `clojure.contrib.io`, aby zrzucić XML-a do pliku:

``` clojure
> (use 'clojure.contrib.io)
nil
> (with-out-writer "/tmp/a.xml" (emit m))
nil
```

Próbujemy otworzyć wynikowy obrazek w Firefoksie i...

```
Błąd parsowania XML: nieprawidłowo sformowany
Obszar: file:///tmp/a.xml
Numer linii: 15, kolumna 44: Updated to reflect dissolution of Serbia &amp; Montenegro: http://commons.wikimedia.org/wiki/User:Zirland
----------------------------------------------------------------------^
```

Argh! Wygląda na to, że znaleźliśmy błąd w `clojure.xml`: elementy
zawierające ampersandy nie są poprawnie serializowane, przez co
późniejsze parsowanie się nie powodzi. Zgłosimy ten błąd później, a
teraz usuńmy znowu doraźnie wskazaną przez Firefoksa linijkę (możemy
to bezpiecznie zrobić, bo to tylko komentarz). Jeśli teraz powtórzymy
nasze dotychczasowe kroki, zobaczymy, że wygenerowany XML jest
wreszcie poprawny.

## Kolorujemy Polskę

Widzieliśmy wcześniej, że główny element XML w naszym pliku zawiera 68
podelementów. Zobaczmy, jakie one są -- wystarczą nam tagi:

``` clojure
> (map :tag (:content m))
(:title :desc :defs :rect :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :path :g :path :path :g :path :path :path)
```

Jak dotąd jest świetnie. Wygląda na to, że wszystkie opisy krajów są
bezpośrednio zawarte w głównym elemencie jako podelementy. Spróbujmy
znaleźć Polskę:

``` clojure
> (count (filter #(and (= (:tag %) :path)
                       (= ((:attrs %) :id) "pl"))
                 (:content m)))
1
```

(Ten kawałek kodu wybiera z listy podelementów `m` takie, których tag
to `path`, a wartość atrybutu `id` to `"pl"` i zwraca długość takiej
podlisty). Spróbujmy teraz dodać do tego elementu atrybut `style`,
zgodnie z tym, co powiedzieliśmy wcześniej. Ze względu na niezmienność
struktur danych musimy zdefiniować nowy nadrzędny element, który
będzie taki sam, jak `m`, z tym, że odpowiedniemu podelementowi
ustawimy styl:

``` clojure
> (def m2 (assoc m
                :content
                (map #(if (and (= (:tag %) :path)
                               (= ((:attrs %) :id) "pl"))
                        (assoc % :attrs (assoc (:attrs %) :style "fill: #ff0000;"))
                        %)
                     (:content m))))
#'user/m2
> (with-out-writer "/tmp/a.svg" (emit m2))
nil
```

Otwieramy utworzony plik i widzimy mapkę z Polską zaznaczoną na
czerwono. Hura!

## Generalizacja

Uogólnimy trochę nasz kod. Napiszemy funkcję, która pokoloruje jedno
państwo, dostając na wejściu element `path` (podelement `svg`):

``` clojure
(defn color-state
  [{:keys [tag attrs] :as element} colorize-fn]
  (let [state (:id attrs)]
    (if-let [color (colorize-fn state)]
      (assoc element :attrs (assoc attrs :style (str "fill:" color)))
      element)))
```

Jest to funkcja bardzo podobna do tej anonimowej, użytej powyżej w
wywołaniu `map`, ale różni się w kilku szczegółach. Po pierwsze,
bierze dwa argumenty. Jednym jest, jak powiedzieliśmy, element XML-owy
(od razu rozbity na `tag` i `attrs`: więcej o takiej notacji, zwanej
_destructuring_ („dzielenie struktury”) można przeczytać w
[odpowiedniej części dokumentacji Clojure][11]), a drugim... funkcja,
która dostanie jako argument dwuliterowy kod państwa i zwróci HTML-owy
opis jego koloru (lub `nil`, jeśli kolor dla tego państwa nie jest
określony -- `color-state` poradzi sobie z tym i zwróci element w
stanie nienaruszonym).

Mając `color-state`, możemy łatwo napisać funkcję wyższego poziomu,
która w jednym kroku przetworzy i zapisze XML:

``` clojure
(defn save-color-map
  [svg colorize-fn outfile]
  (let [colored-map (assoc svg :content (map #(color-state % colorize-fn) (:content svg)))]
    (with-out-writer out
      (emit colored-map))))
```

Testujemy:

``` clojure
> (save-color-map m {"pl" "#00ff00"} "/tmp/a.svg")
nil
```

Tym razem Polska jest zielona (jako argumentu `color-state` użyliśmy
mapy kraj&rarr;kolor, bo mapy są w Clojure wołalne jak funkcje). Spróbujmy
dorzucić niebieskie Niemcy:

``` clojure
> (save-color-map m {"pl" "#00ff00", "de" "#0000ff"} "/tmp/a.svg")
nil
```

Działa!

## Problem z Wielką Brytanią

Zachęceni sukcesem, próbujemy kolorować różne kraje. Dla większości
działa, ale Wielka Brytania jak była szara, tak jest, niezależnie od
tego, czy podamy jej kod jako „uk”, czy jako „gb”. Zaglądamy jeszcze
raz do źródła naszego obrazka. Komentarze na początku jak zwykle
okazują się pomocne:

> Certain countries are further subdivided the United Kingdom has
> gb-gbn for Great Britain and gb-nir for Northern Ireland. Russia is
> divided into ru-kgd for the Kaliningrad Oblast and ru-main for the
> Main body of Russia. There is the additional grouping #xb for the
> „British Islands” (the UK with its Crown Dependencies -
> Jersey, Guernsey and the Isle of Man)

Może więc trzeba podać „gb-gbn” i „gb-nir”, zamiast samego „gb”?
Próbujemy i... też nie działa. Po chwili namysłu: no jasne! Nasze
początkowe założenie, że _wszystkie_ definicje krajów są bezpośrednimi
podelementami `path`, okazuje się fałszywe. Trzeba to naprawić.

Dotychczas robiliśmy „płaską” transformację drzewa SVG: zamienialiśmy
wszystkie podelementy elementu głównego i nic głębiej. Trzeba by
zmienić wszystkie elementy `path` (i `g`, jeśli chcemy kolorować
grupy takie jak Wielka Brytania), niezależnie od tego, jak głęboko
siedzą w drzewie. Z pomocą przychodzi funkcja `map-zipper` z
[poprzedniego odcinka][1].

Zważywszy, że funkcja `clojure.xml/zip-xml` zwraca zipper działający
na drzewach XML-owych, przepisujemy `save-color-map` jako:

``` clojure
(defn save-color-map
  [svg colorize-fn outfile]
  (let [colored-map (map-zipper #(color-state % colorize-fn) (fn [x] (#{:g :path} (:tag x))) (zip/xml-zip svg))]
    (with-out-writer out
      (emit colored-map))))
```

Tym razem Wielka Brytania już daje się pomalować.

## Koloryzatory

Mamy już zautomatyzowany sam proces kolorowania, ale tłumaczenie
konkretnych wartości liczbowych na RGB jest żmudne. W ostatniej
części tego artykułu zobaczymy, jak je ułatwić: stworzymy funkcję
zwracającą _koloryzator_, czyli funkcję nadającą się do przekazania
jako argument do `color-state` i `save-color-map` (dotąd używaliśmy w
tym celu map).

Zaczniemy od napisania funkcji tłumaczącej trójkę liczb na HTML-owy
zapis RGB, będzie nam bowiem łatwiej pracować z liczbami całkowitymi
niż napisami:

``` clojure
(defn htmlize-color
  [[r g b]]
  (format "#%02x%02x%02x" r g b))
```

Wstawmy jeszcze wywołanie `htmlize-color` w odpowiednim miejscu w
`color-state`:

``` clojure
(defn color-state
  [{:keys [tag attrs] :as element} colorize-fn]
  (let [state (:id attrs)]
    (if-let [color (colorize-fn state)]
      (assoc element :attrs (assoc attrs :style (str "fill:" (htmlize-color color))))
      element)))
```

Wyobraźmy sobie teraz, że mamy tabelkę z wartościami liczbowymi dla
państw, np. taką:

<table class="entry">
<tr class="header"><th>Państwo</th><th>Wartość</th></tr>
<tr><td>Polska</td><td class="center">20</td></tr>
<tr><td>Niemcy</td><td class="center">15</td></tr>
<tr><td>Holandia</td><td class="center">30</td></tr>
</table>

Chcemy mieć funkcję, która przypisze każdemu krajowi kolor tym
intensywniejszy, im większa wartość, przy czym intensywność ma być
proporcjonalna do wartości. Dla większej ogólności załóżmy, że mamy
daną parę kolorów, c1 i c2, i dla każdej ze składowych R, G i B danemu
państwu przypisujemy taką wartość tej składowej, jaka jest odległość
rozważanej wartości od najmniejszej wartości w naszym zestawie danych
(oczywiście znormalizowana biorąc pod uwagę długości odpowiednich
przedziałów).

Brzmi to zagmatwanie, ale mam nadzieję, że na przykładzie będzie widać
lepiej. Tak wygląda implementacja clojurowa powyższej funkcji:

``` clojure
(defn make-colorizer
  [dataset ranges]
  (let [minv (apply min (vals dataset))
        maxv (apply max (vals dataset))
        progress (map (fn [[min-col max-col]] (/ (- max-col min-col) (- maxv minv))) ranges)]
    (into {}
          (map (fn [[k v]] [(.toLowerCase k) (map (fn [progress [min-color _]] (int (+ min-color (* (- v minv) progress)))) progress ranges)])
               dataset))))
```

Zobaczmy, jak działa na naszych przykładowych danych:

``` clojure
> (make-colorizer {"pl" 20, "de" 15, "nl" 30} [[0 255] [0 0] [0 0]])
{"pl" (85 0 0), "de" (0 0 0), "nl" (255 0 0)}
```

Drugi argument oznacza, że składowa czerwona koloru ma się zmieniać od
0 do 255, a zielona i niebieska mają pozostać stałe (na poziomie 0).

Tak jak żądaliśmy, Niemcy wychodzą najciemniejsze (bo mają najmniejszą
wartość), Holandia najjaśniejsza (bo ma największą wartość), a Polska
jest w 1/3 jasności między Niemcami a Holandią (bo 20 jest w 1/3 drogi
między 15 a 30).

## Podsumowanie

Aplikację stworzoną w tym artykule można dalej rozwijać w wielu
kierunkach. Można stworzyć jej wersję sieciową (jak się do tego zabrać
-- opowiedziałem w skrócie na spotkaniu Warszawa JUG). Można pisać
różne koloryzatory, np. dyskretny (stałe wartości dla różnych
podprzedziałów wejściowego przedziału danych) albo „temperaturowy”
(płynnie przechodzący od błękitu do czerwieni przez biel: trzeba w tym
celu przejść przez przestrzeń kolorów HSV).

A jaki Wy macie pomysł na rozwinięcie przykładu? Zapraszam do
komentowania i eksperymentowania! Dla tych, którym się nie chce
przeklejać kodu do REPL, kompletne źródło działające z Leiningenem
umieszczam na [GitHub][12]. Forki będą bardzo mile widziane.

 [1]: http://plblog.danieljanus.pl/zippery-w-clojure
 [2]: http://jaceklaskowski.blip.tv/file/4573404/
 [3]: http://warszawa.jug.pl
 [4]: http://danieljanus.pl/slides/2010/wjug-clojure/
 [6]: http://commons.wikimedia.org/wiki/File:Blank_map_of_Europe.svg
 [7]: http://www.inkscape.org
 [8]: http://www.w3.org/TR/SVG/styling.html
 [9]: http://www.w3.org
 [10]: http://www.w3.org/blog/systeam?cat=68
 [11]: http://clojure.org/special_forms
 [12]: https://github.com/nathell/color-europe
