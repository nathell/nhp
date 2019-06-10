---
layout: theme:post
title: "Leniwa wersja makra ->"
date: 2010-11-11
comments: true
categories: Clojure programowanie
---

[Jacek Laskowski][1] podaje ciekawy [przykład wykorzystania monad w Clojure][2] --
aplikowanie kolejnych funkcji do wyrażenia,
dopóki ma ono wartość nie będącą `nil`.  Przykład mi się
podoba, bo jest prosty, ale nie trywialny -- wykorzystuje monadę
`maybe` do eleganckiego rozwiązania rzeczywistego problemu.  Jest to w
dodatku problem, z którym borykają się czasem programiści
piszący w Javie, co widać choćby w [tym wątku][3].

Nie mogę powiedzieć, że rozumiem monady. Owszem, znam definicję i
potrafię napisać program w Haskellu wykorzystujący monadę `IO`, ale
każdy monadyczny kod, jaki dotychczas widziałem, dawał się zapisać
równie zwięźle bez użycia monad w językach, które mniej religijnie
podchodzą do niewpływania funkcji na stan świata zewnętrznego. Tak
jest również w tym przypadku.

Jacek tak postawił oryginalny problem:

> Napisać metodę, która zwraca walutę, dla pracownika z danego
> departamentu międzynarodowej korporacji. Pracownik jest przypisany
> do departamentu (np. poprzez mapę -- pracownik-departament),
> departament do kraju, a kraj do waluty. Funkcja na wejściu dostaje
> nazwę, identyfikator, lub cokolwiek jednoznacznie reprezentującego
> pracownika, a na wyjściu symbol waluty, np. dla „Jacek” powinno być
> „PLN”, a dla „John” „USD”, a „Tomek” i „Mateusz” dawaliby „CHF”.

Moje pierwotne rozwiązanie jest po prostu złożeniem trzech funkcji:

``` clojure
(defn pracownik->waluta [p]
  (-> p pracownik->departament departament->kraj kraj->waluta))
```

O ile wszystkie z nich są mapami (pamiętamy, że w Clojure mapy
implementują interfejs `IFn`, można je więc uważać za funkcje i wołać
jak funkcje), to dla żadnej wartości wejściowej nie zostanie rzucony
`NullPointerException`, ponieważ wywołanie mapy dla nieistniejącego
klucza zwraca `nil`.

Jacek słusznie zauważa jednak, że wykonujemy w ten sposób więcej pracy
niż trzeba. Jeżeli już w mapie `pracownik->departament` nie ma
departamentu dla danego pracownika, to otrzymany `nil` zostanie
„przepchnięty” przez pozostałe dwie mapy, zanim będzie zwrócony.
Można kontrargumentować, że taka implementacja `pracownik->waluta`
jest bardzo czytelna i mała strata wydajności jest niewielką ceną do
zapłacenia za tę czytelność.  Co jednak, gdy nie możemy lub nie chcemy
zgodzić się na taką stratę, a jednocześnie nie chcemy stracić
czytelności?

Odpowiedzią jest makro, które nazwałem `and->`. Działa ono tak samo,
jak `->`, z tą różnicą, że kolejne wartości oblicza tylko jeśli po
drodze nie pojawiło się `false` lub `nil`, podobnie jak `and` (stąd
nazwa). Oto ono:

``` clojure
(defmacro and->
  ([x] x)
  ([x form] `(when-let [y# ~x] (~form y#)))
  ([x form & more] `(when-let [y# (and-> ~x ~form)]
                      (and-> y# ~@more))))
```

Implementacja jest bardzo zbliżona do zwykłego `->`, którego kod w
Clojure 1.2 wygląda [tak][4]. W stosunku do `->` moje makro jest dla
większej poglądowości trochę uproszczone i nie obsługuje konstrukcji
typu `(and-> mapa (get :klucz))`, które normalnie rozwijane są do
`(get mapa :klucz)`.

Warto zwrócić uwagę na to, że argument makra nigdy nie jest wyliczany
w jego rozwinięciu więcej niż raz; zawsze jest wiązany do lokalnego
symbolu o unikatowej nazwie za pomocą `when-let`. Jest to jedna z
reguł pozwalających unikać błędów w makrach. Szerszy opis tego
problemu, występującego też w Common Lispie, można znaleźć w
[rozdziale 8 książki „Practical Common Lisp”][5].

## Aktualizacja (7.12.2010)

Jak się okazuje, clojure-contrib [zawiera już][6] takie makro! Nazywa
się `-?>` i można je znaleźć w przestrzeni nazw
`clojure.contrib.core`. To już któryś raz, kiedy okazuje się, że
jakieś użyteczne makro lub funkcja już jest w contrib i nie trzeba jej
było pisać.

 [1]: http://jaceklaskowski.pl
 [2]: http://blog.japila.pl/2010/11/monady-maybe-odsona-kolejna-rozwiazanie.html
 [3]: http://groups.google.com/group/pl.comp.lang.java/browse_thread/thread/d290ced113686b34/f4cc877a5053969d
 [4]: https://github.com/clojure/clojure/blob/1.2.0/src/clj/clojure/core.clj#L1284
 [5]: http://gigamonkeys.com/book/macros-defining-your-own.html#plugging-the-leaks
 [6]: http://clojuredocs.org/clojure_contrib/clojure.contrib.core/-_q%3E
