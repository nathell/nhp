---
layout: theme:post
title: Praktyczne użycie monady state
date: 2012-01-10
comments: true
categories: Clojure Haskell programowanie
---

Wreszcie rozumiem monady!

Pierwszy raz zetknąłem się z nimi dobrych osiem lat temu, przy okazji
nauki Haskella; wtedy jednak nie starczyło mi cierpliwości, aby
zaznajomić się z podstawami teoretycznymi. Sprawy nie ułatwiał fakt,
że trudno o _naprawdę_ przystępne i zrozumiałe wprowadzenie do tego
tematu: wygląda na to, że każdy adept Haskella, zrozumiawszy monady,
pisze na ten temat własny _tutorial_. Ja się powstrzymam od tego
naturalnego odruchu i po prostu odeślę do niesamowicie szczegółowego,
ośmioczęściowego [cyklu artykułów][1] autorstwa Mike'a Vaniera, który
-- wreszcie! – sprawił, że coś mi „zaskoczyło” w umyśle. Zamiast tego
w tym wpisie wynotuję najważniejsze spostrzeżenia, jakie zapamiętałem,
a potem pokażę dwie wersje pewnego kodu operującego na sekwencjach
bitów: niemonadyczną i napisaną z użyciem monady `state`.

 [1]: http://mvanier.livejournal.com/3917.html

Mike zakłada znajomość Haskella, jak pisze we wstępie, na poziomie
typów polimorficznych i klas typów; w praktyce jednak myślę, że
do zrozumienia tekstu wystarczy pewne obycie z haskellową notacją
(bardzo zresztą przypominającą zwykłą notację matematyczną), bo
bardziej zaawansowane rzeczy są wyjaśniane w tekście na
bieżąco. Bardzo polecam.

Oto więc moje notatki:

* Kluczowe dla zrozumienia monad jest pojęcie _funkcji monadycznych_;
  znacznie łatwiej jest je sobie intuicyjnie wyobrazić niż monadyczne
  wartości, zwracane przez `m-return`. Mike podaje pewne intuicje
  także dla tych drugich, ale dla mnie ważne jest, żeby myśleć o
  monadach w kategoriach (wzbogaconych) funkcji, a nie wartości przez
  nie zwracanych.
* Łatwo wydefiniować przy użyciu prostego złożenia operacji `m-bind`
  i `m-return` funkcję składającą ze sobą dwie funkcje monadyczne.
  Przy użyciu tego operatora, który Mike nazywa `<=<`, prawa rządzące
  monadami wyrażają się w elegancki i prosty do zapamiętania sposób:
  jest to operator łączny oraz `m-return` jest jego lewą i prawą
  jedynką.
* I bodaj najistotniejsza rzecz – nie trzeba rozumieć monad, żeby
  ich używać. Niby oczywiste i niby tak właśnie pisałem do tej pory
  kod haskellowy, radośnie używając `do`-notacji tak jakbym pisał kod
  imperatywny; ale okazuje się, że w ten sam sposób można z
  powodzeniem używać monady stanu i wyjaśnić, co ona robi, zupełnie w
  oderwaniu od całej reszty teorii monad. Wiedza na temat tego, co
  się dzieje „pod spodem”, jest konieczna tylko (i aż!) do
  zrozumienia, co poszczególne monady mają ze sobą wspólnego.

Reszta tego wpisu będzie poświęcona właśnie przykładowemu użyciu
monady stanu w Clojure.  [Pisałem wcześniej][2], że nie widziałem
dotąd kodu, który dałby się wyrazić przejrzyściej zapisany z użyciem
monad niż bez nich – i właśnie na taki kod się natknąłem.

Wyobraźmy więc sobie, że mamy strumień bitów. Dla prostoty zdefiniujmy
sobie przykładowy strumień, na którym będziemy testować nasze funkcje,
jako clojurowy wektor zer i jedynek.

``` clojure
(def v [1 1 1 0 1 0 1 0 1 0])
```

Chcemy naddać naszemu strumieniowi pewne uporządkowanie: zdekodować
zera i jedynki, które z niego przychodzą, według jakiegoś kodu o
zmiennej długości. Najbardziej oczywistym, znanym każdemu kodem jest
kod binarny: czytamy ze strumienia ileś bitów, traktujemy je jako
binarną (bez znaku) reprezentację pewnej liczby i zwracamy tę liczbę.
Łatwo napisać w Clojure funkcję, która to robi.

``` clojure
(defn read-binary [n bits]
  (loop [res 0 n n [fst & rst :as bs] bits]
    (if (zero? n)
      [res bs]
      (recur (+ res res fst) (dec n) rst))))
```

Spróbujmy wczytać z naszego strumienia czterobitową liczbę:

``` clojure
(read-binary 4 v)
;=> [14 (1 0 1 0 1 0)]
```

Czternaście. Ale zauważmy, że zdekodowana liczba nie jest jedyną
zwracaną wartością! Żeby dało się z naszego strumienia odczytywać
dalsze liczby, musimy zwrócić parę (wektor dwuelementowy): wartość
odczytana plus _reszta strumienia_, z której będziemy czytać dalej.
Piszemy wszak funkcyjnie: nie zmieniamy naszych wartości w miejscu,
tylko z jednych wartości produkujemy następne.

Innym rodzajem kodu jest _kod unarny_: czytamy po prostu ze strumienia
jedynki, aż napotkamy pierwsze zero – wtedy przestajemy czytać i
naszą wartością jest liczba przeczytanych bitów. W ten sposób dowolną
liczbę dodatnią _n_ da się zareprezentować na _n_ bitach.
Implementacja jest równie prosta:

``` clojure
(defn read-unary [bits]
  (loop [n 1 [fst & rst] bits]
    (if (zero? fst)
      [n rst]
      (recur (inc n) rst))))
```

Sprawdzamy:

``` clojure
(read-unary v)
;=> [4 (1 0 1 0 1 0)]
```

Trzy jedynki i zero, razem cztery skonsumowane bity, więc odczytaną
liczbą jest 4. Działa.

Spróbujmy teraz skleić nasze funkcje, to znaczy odczytać
ze strumienia dwie liczby: najpierw zakodowaną unarnie, a potem
binarnie na sześciu bitach.

``` clojure
(let [[x v1] (read-unary v)
      [y v2] (read-binary 6 v1)]
  [x y])
;=> [4 42]
```

Wynik jest poprawny, ale kod nieelegancki: tak naprawdę nie interesują
nas te wszystkie `v`, `v1`, `v2`, które przepychamy przez nasze
funkcje; potrzebujemy tylko przeczytanych liczb, a poszczególne części
strumienia są nam tylko po to, żeby mieć z czego czytać. Im więcej
składanych ze sobą operacji, tym łatwiej się pomylić.

Tu wkracza monada stanu, która umożliwia ukrycie tych pośrednich
wartości i bardziej eleganckie złożenie `read-unary` i `read-binary`.
Monadycznie zapisalibyśmy to jakoś tak:

``` clojure
(domonad state-m
         [x read-unary
          y (partial read-binary 6)]
         [x y])
;=> #
```

Wygląda to bardzo podobnie. `domonad state-m` jest jak `let`. Tak jak
chcieliśmy, nie przekazujemy naszego stanu (strumienia) _explicite_;
zamiast tego przy `x` i `y` podajemy funkcje, które mają być zawołane
na aktualnym stanie, żeby uzyskać żądaną wartość i nowy stan.

No dobrze, ale gdzie tu miejsce na nasz stan początkowy? Jak go przekazać?
Proste: powyższa formuła, jak widać z mało czytelnego wyniku, zwróciła
jakąś funkcję. Wystarczy ją teraz zawołać na naszym początkowym stanie,
aby uzyskać wynik wraz ze stanem końcowym:

``` clojure
(*1 v)
;=> [[4 42] nil]
```

Jedyne, co wydaje się nadmiarowe w naszym złożeniu, to owo `partial`
pojawiające się wyżej. Jest to efekt uboczny faktu, że Clojure w
odróżnieniu od Haskella rozróżnia funkcje jedno- i więcejargumentowe
(tak dokładniej to w Haskellu występują wyłącznie funkcje
jednoargumentowe). Skoro w `domonad state-m` powinny na przemian
pojawiać się symbole i funkcje jednoargumentowe biorące stan, to gdy
nasza funkcja akceptuje coś jeszcze (tu: liczbę bitów), powinniśmy
zrobić z niej funkcję jednoargumentową za pomocą
`partial`. Alternatywnie, jeżeli chcemy trzymać się stylu
monadycznego, możemy przepisać `read-binary` jako:

``` clojure
(defn read-binary [n]
  (fn [bits]
    (loop [res 0 n n [fst & rst :as bs] bits]
      (if (zero? n)
        [res bs]
        (recur (+ res res fst) (dec n) rst)))))
```

Teraz zamiast `(partial read-binary 6)` możemy napisać
po prostu `(read-binary 6)`.

Teraz, kiedy umiemy już składać operacje stanowe, zilustrujemy to
przy pomocy jeszcze jednego kodu, a właściwie rodziny kodów nazywanych
_kodami Golomba_. Po szczegółowy opis odsyłam do [Wikipedii][3], natomiast
dla potrzeb tego artykułu wystarczy nam wiedza, że te kody są
parametryzowane jedną liczbą _m_. Liczba w kodzie Golomba jest podzielona
na dwie części, z których pierwsza jest zakodowana unarnie, a druga --
nie większa niż _m_ – binarnie. W implementacji możemy więc skorzystać
z naszych gotowych już funkcji `read-unary` i `read-binary`.

Bez dłuższych już komentarzy przedstawię dwie implementacje:
eksplicytną i używającą monady stanu. Obie będą używać pomocniczej
funkcji `read-bit`, czytającej po prostu jeden bit ze strumienia
i używanej tak samo jak `read-unary` czy `read-binary`.

``` clojure
(defn read-bit [[bit & bits]]
  [bit bits])
```

Najpierw wersja niemonadyczna:

``` clojure
(defn read-golomb [m]
  (fn [bits]
    (let [k (clog2 m)
          r (- (bit-shift-left 1 k) m)
          [a bits1] (read-unary bits)
          [b bits2] ((read-binary (dec k)) bits1)
          [ir bits] (if (< b r)
                      [b bits]
                      (let [[nb bits] (read-bit bits)]
                        [(+ b b nb (- r)) bits]))]
      [(+ (* (dec a) m) ir) bits])))
```

A teraz używająca monad. Żeby nie przeplatać `let` i `domonad`,
można po prostu zdefiniować funkcję zwracającą pewną stałą wartość
i niezmieniony stan – nazwę ją `m-const`:

``` clojure
(defn m-const [x]
  (fn [state]
    [x state]))
```

Używając jej, możemy zaimplementować `read-golomb` tak:

``` clojure
(defn read-golomb-monadic [m]
  (domonad state-m
           [k (m-const (clog2 m))
            r (m-const (- (bit-shift-left 1 k) m))
            a read-unary
            b (read-binary (dec k))
            nb (if (< b r) (m-const nil) read-bit)]
           (+ (* (dec a) m)
              (if (< b r) b (+ b b nb (- r))))))
```

Czytelniej?

 [2]: http://plblog.danieljanus.pl/leniwa-wersja-makra
 [3]: http://pl.wikipedia.org/wiki/Kod_Golomba
