---
layout: theme:post
title: "Clojure: czyszczenie dowiązań lokalnych"
date: 2011-01-31
comments: true
categories: Clojure programowanie
---

O jednej z nowych cech Clojure 1.2 dowiedziałem się dopiero niedawno,
bo jej wprowadzenie przeszło właściwie bez echa. Może dlatego, że to
optymalizacja niewidoczna na zewnątrz; jednak rozwiązuje ważny problem
i dlatego warto mieć świadomość jej istnienia. Chodzi o
_locals clearing_, które tłumaczę na polski jako „czyszczenie lokalnych
dowiązań”.

Wyobraźmy sobie taką sytuację: mamy do wykonania skomplikowane
obliczenie. Możemy je podzielić na mniejsze kroki; na każdym etapie
wyliczamy nowy pośredni wynik na podstawie tylko niewielu ostatnich
kroków -- jednego, może dwóch. Załóżmy jeszcze, że te pośrednie wyniki
są dużych rozmiarów: powiedzmy setek megabajtów. To częsty przypadek,
kiedy wykonuje się np. obliczenia na dużych, gęstych macierzach:
najpierw normalizujemy każdy z wektorów naszej macierzy, potem ją
transponujemy, następnie liczymy jej rozkład własny i odrzucamy część
wektorów własnych.

W Javie wyglądałoby to tak:

``` java
void calculate(Matrix m) {
    Matrix m1 = step1(m);
    Matrix m2 = step2(m1);
    // i tak dalej, każdy krok odwołuje się do poprzedniego
}
```

Jest to jednak prosta droga do pojawienia się `OutOfMemoryError`, o
ile nie dysponujemy wystarczającą ilością pamięci, aby pomieścić
_wszystkie_ kroki. Gdy `m2` jest obliczone, wartość `m1` jest już
niepotrzebna i w zasadzie można by zwolnić zajmowaną przez nią
pamięć. Odśmiecacz jednak tego nie zrobi, dopóki wywołanie funkcji
`calculate` nie zakończy się, ponieważ do tej pamięci odwołuje się
ramka na stosie wywołań.

Zilustrujmy to uruchamialnym przykładem:

``` java
public class foo {
    public static byte[] calculate(byte[] lastStep) {
        return new byte[10485760];
    }

    public static void main(String... args) {
        byte[] a, b, c, d;
        a = calculate(null);
        b = calculate(a);
        c = calculate(b);
        d = calculate(c);
        System.out.println("OK");
    }
}
```

Funkcja `calculate` emuluje skomplikowane obliczenia, alokując za
każdym razem 10-megabajtowy blok pamięci. Jeśli skompilujemy ten
program i uruchomimy go w środowisku z maksymalnym rozmiarem sterty
ograniczonym do 30 MB (`java -Xmx30M foo`), zobaczymy błąd braku
pamięci.

Inaczej jest w Clojure. Odpowiednik powyższego programu wygląda tak:

``` clojure
(defn calculate [last-step]
  (make-array Byte/TYPE 10485760))

(defn -main [& args]
  (let [a (calculate nil)
        b (calculate a)
        c (calculate b)
        d (calculate c)]
    (println "OK")))
```

W Clojure 1.2 i nowszych ten program wypisze `OK`, nawet gdy będzie miał
do dyspozycji tylko 30 MB pamięci.

Dlaczego to działa? Okazuje się, że kompilator Clojure przeprowadza
statyczną analizę każdego kawałka kodu, w którym wartości są dowiązane
do nazw (czyli każdego użycia formuły `let`), i dla każdego dowiązania
sprawdza, w którym miejscu jest ono ostatni raz używane, biorąc pod
uwagę wszystkie możliwe ścieżki wykonania. Natychmiast po ostatnim
użyciu emitowany jest bajtkod zerujący odpowiednią zmienną
(ustawiający wskaźnik na `null`). Innymi słowy, efekt jest taki,
jakbyśmy w przykładowym kodzie w Javie wstawili instrukcję `a = null;`
zaraz po obliczeniu `b` i analogicznie dalej.

O jednej z sytuacji, w których takie czyszczenie się przydaje, już
powiedziałem. Innym częstym przypadkiem jest branie za punkt wyjścia
długiej (być może nawet nieskończonej) leniwej sekwencji, która jest
realizowana w kolejnych krokach obliczeń. Dzięki automatycznemu
czyszczeniu odśmiecacz może pozbyć się pierwszych, niepotrzebnych już
elementów zrealizowanej sekwencji, co umożliwia pisanie klarownego
kodu bez troszczenia się o jego poprawność pamięciową.

Jak jednak [napisał mi Rich Hickey][1], sama alokacja pamięci w
momencie wiązania nazwy _nie jest_ uważana za użycie tej nazwy. Stąd
jeśli nazwa nigdy nie zostanie użyta, to odpowiadająca jej wartość nie
zostanie też odśmiecona aż do opuszczenia ciała bloku `let`. Dlatego
też nie mogłem pominąć w powyższym przykładzie argumentu `last-step`
funkcji `calculate`.  Jednak taki przypadek nie pojawia się w
praktyce: wszak jeśli wyliczamy jakiś wynik, to nie po to, by go do
niczego nie użyć!

 [1]: https://groups.google.com/d/msg/clojure/Xmu3pUMgiJk/Lx1Sq462h4wJ
