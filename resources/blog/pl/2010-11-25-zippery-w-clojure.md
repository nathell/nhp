---
layout: theme:post
title: Zippery w Clojure
date: 2010-11-25
comments: true
categories: Clojure programowanie
---

Niniejszy artykuł jest pierwszym z dwuczęściowego minicyklu,
stanowiącego demonstrację wykorzystania Clojure w praktyce do prostego,
acz nietrywialnego zadania, na które natknąłem się w codziennej pracy.
Dzisiejsza część może wydać się mało interesująca i mocno teoretyczna,
ale mam nadzieję, że następny odcinek pokaże, jak można ją ciekawie
wykorzystać (na razie nie zdradzę, jakie to wykorzystanie).

Planuję, że takie artykuły lub cykle zdominują Clojurową część tego
bloga – będę się tu dzielił rozwiązaniami praktycznych problemów, na
jakie natrafiam. Nie będzie tu artykułów w stylu „hej, jaki fajny nowy
framework XYZ, napiszmy w nim Hello World przy wykorzystaniu technologii
ABC!”, chyba że XYZ lub ABC będą przydatnymi narzędziami do rozwiązania
problemu z życia wziętego. Bo takie rozwiązywanie w Clojure daje dużo
radości: nie przypadkiem taki właśnie jest tytuł
[książki M. Fogusa i C. Housera][1].

A dziś opowiem o ciekawej strukturze danych, jaką są _zippery_. (Mierzi
mnie trochę używanie angielskiej nazwy, ale nie potrafię wymyślić
dobrego polskiego odpowiednika tego słowa; będę szczęśliwy widząc
propozycje w komentarzach!) Cóż to takiego?

[Zipper][2] jest strukturą danych, dającą _iluzję imperatywności_ przy
manipulowaniu drzewami. Pamiętamy, że Clojurowe natywne struktury danych
są niezmienne (_immutable_): jeśli mamy listę składającą się z liczb 2 i
5, to nie możemy jej w żaden sposób zmienić (np. dodać elementu) --
możemy co najwyżej stworzyć na jej podstawie _nową_ listę, która będzie
zawierać wszystkie elementy listy wyjściowej i jeszcze jakiś.

Niezmienność jest bardzo przydatna (o filozoficznych podstawach takiego
podejścia, które zadecydowały o jego wykorzystaniu w Clojure, można
poczytać w artykule [„On State and Identity”][3]), ale wymusza myślenie
w innych kategoriach. To jest to, co czasami nazywa się „myśleniem
funkcyjnym”: zamiast zastanawiać się, w jaki sposób _zmienić_ wartość
naszej zmiennej, aby doprowadzić ją do pożądanego stanu, pytamy o to,
_jak z jednych wartości robić inne_. Przy tym nigdzie nie jest
powiedziane, że taki sposób myślenia jest koniecznie lepszy od
imperatywnego, do którego przyzwyczajeni są programiści języków takich
jak Java. Jest po prostu inny. Warto się go nauczyć, bo okazuje się, że
wiele problemów się upraszcza, gdy już umysł się przestawi na taki
_modus operandi_. Bywa jednak i tak, że o danym problemie imperatywnie
myśli się wygodniej niż funkcyjnie. Tu właśnie wkraczają zippery.

<img src="/img/blog/zipper.png">

Wyobraźmy sobie, że chcemy dodać liczbę 4 do konkretnego drzewa BST, tak
jak na rysunku powyżej. Załóżmy chwilowo, że nie interesuje nas ogólna
funkcja dodająca do BST, a tylko chcemy wstawić czwórkę w konkretne
miejsce. Jak to zrobić imperatywnie?

* Zejdź dwa razy w dół w prawo („dobierz się” do węzła 6).
* Wstaw czwórkę na lewo od bieżącego węzła.

I już. A teraz funkcyjnie – pamiętamy, że z drzewa robimy nowe drzewo:

* Nowym drzewem jest drzewo, którego korzeniem jest korzeń drzewa
wyjściowego, lewym poddrzewem – lewe poddrzewo drzewa wyjściowego, a
prawym poddrzewem drzewo, którego korzeniem jest korzeń prawego
poddrzewa drzewa wyjściowego, lewym poddrzewem – drzewo składające się
z tylko jednego węzła 4, a prawym poddrzewem – prawe poddrzewo prawego
poddrzewa drzewa wyjściowego.

Pierwsze podejście jest łatwiejsze, prawda? Zipper pozwala nam zachować
je prawie niezmienione, nie rezygnując przy tym z zalet niezmienności.
Oto jak można by opisać dodawanie do drzewa z zipperem:

* Stwórz zipper na podstawie drzewa wyjściowego.
* Wykonaj na tym zipperze operację „zejdź w dół”, otrzymując nowy zipper.
* Wykonaj na tym zipperze operację „przejdź w prawo”, otrzymując nowy zipper.
* Wykonaj na tym zipperze operację „zejdź w dół”, otrzymując nowy zipper.
* Wykonaj na tym zipperze operację „wstaw 4 przed bieżącym elementem”, otrzymując nowy zipper.
* Wykonaj na tym zipperze operację „daj drzewo wynikowe”, otrzymując pożądane drzewo.

Clojure zawiera implementację zipperów w bibliotece standardowej (w
przestrzeni nazw `clojure.zip`). Oto jak można by zapisać powyższy
przykład w Clojure:

``` clojure
(def nowe-drzewo
  (let [z1 (zip drzewo)
        z2 (down z1)
        z3 (right z2)
        z4 (down z3)
        z5 (insert-left z4 4)]
    (root z5)))
```

(tu _explicite_ nazywam kolejne kroki obliczeń). Albo tak, używając
makra `->`:

``` clojure
(def nowe-drzewo
  (-> drzewo
      zip
      down
      right
      down
      (insert-left 4)
      root)))
```

Proste i wygodne. Pod spodem zipper to po prostu oryginalne drzewo plus
informacja o tym, w którym miejscu drzewa w tej chwili jesteśmy, plus
lista „zmian”, jakie do tej pory zostały na nim wykonane.

Rozwiązując mój problem (na razie nie zdradzam, jaki), natknąłem się na
potrzebę posiadania funkcji, która działałaby jak `map`, ale na
drzewach, a nie listach. To znaczy, przekształcałaby każdy element,
niezależnie od tego, jak głęboko w drzewie siedzi, aplikując do niego
jakąś funkcję, i zwracała w wyniku nowe drzewo. Co więcej, chciałem móc
dodatkowo kontrolować, które węzły są zmieniane: funkcja ma dostawać
dodatkowy predykat i tylko kiedy zwróci on `true` na wartości danego
węzła, zmieniać tę wartość. Nie ma chyba takiej funkcji w standardowej
bibliotece, ale dzięki zipperom można ją łatwo napisać.

Tak się szczęśliwie składa, że Clojurowe zippery mają funkcję `next`,
która spaceruje po drzewie „w głąb”. Mając je, mogłem pomyśleć tak o
swojej implementacji:

 Jeśli zipper jest na końcu drzewa, to wynikiem transformacji jest wynikowe drzewo dla tego zippera, w przeciwnym razie wynikiem jest ta sama transformacja wywołana rekurencyjnie dla zippera uzyskanego przez wykonanie operacji „edit” (edycji bieżącego węzła w razie potrzeby) i „next”.

Tłumacząc to na Clojure, dostajemy:

``` clojure
(defn map-zipper [f pred z]
  (if (zip/end? z)
    (zip/root z)
    (recur f pred (-> z (zip/edit #(if (pred %) (f %) %)) zip/next)))))
```

I testujemy:

``` clojure
> (map-zipper inc integer?
              (zip/vector-zip [1 [2 [3 4]] [5] [6 [[7 8 9]]]]))
[2 [3 [4 5]] [6] [7 [[8 9 10]]]]
```

Trzeba podać predykat `integer?`, bo zipper działający na
zagnieżdżonych wektorach działa na poszczególnych poddrzewach, a nie
tylko na liściach (które tu są liczbami). Możemy łatwo zobaczyć, jakie
poddrzewa odwiedza `map-zipper`:

``` clojure
> (map-zipper #(do (println %) %)
              (constantly true)
              (zip/vector-zip [1 [2 [3 4]] [5] [6 [[7 8 9]]]]))
[1 [2 [3 4]] [5] [6 [[7 8 9]]]]
1
[2 [3 4]]
2
[3 4]
3
4
[5]
5
[6 [[7 8 9]]]
6
[[7 8 9]]
[7 8 9]
7
8
9
[1 [2 [3 4]] [5] [6 [[7 8 9]]]]
```

 [1]: http://joyofclojure.com/
 [2]: http://en.wikipedia.org/wiki/Zipper_(data_structure)
 [3]: http://clojure.org/state
 [5]: http://clojure.org/other_libraries
