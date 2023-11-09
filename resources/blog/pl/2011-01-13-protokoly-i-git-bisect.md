---
layout: theme:post
title: "Pierwsze użycie: protokoły i git-bisect"
date: 2011-01-13
comments: true
categories: Clojure Fablo programowanie
---

Lubię rozwiązywać problemy przy użyciu narzędzi, których dotychczas
nie znałem albo znałem tylko teoretycznie, na zasadzie „wiem, że
istnieje coś takiego i do czego z grubsza służy”. Jeszcze przyjemniej
jest, kiedy uzyskane rozwiązanie okazuje się czytelniejsze,
zrozumialsze, szybsze albo pod innymi względami lepsze niż wersja
używająca tylko dotychczasowego „arsenału”. W tych dniach zdarzyło mi
się tego doświadczyć dwukrotnie, gdy pracowałem nad [Fablo][1].

Fablo to napisany w [Clojure][2] silnik wyszukiwarki dla polskich
sklepów internetowych: zna odmianę polskich wyrazów, obsługuje
literówki i błędy ortograficzne. Radzi sobie też z wyrazami wpisanymi
bez polskich znaków, a więc wie, że „papryka zolta” to to samo, co
„papryka żółta”. Działa to na ogół zupełnie dobrze, ale wciąż
ulepszamy wyszukiwarkę i eliminujemy błędy polegające na tym, że na
jakieś zapytanie nie wyszukują się niektóre produkty, choć
powinny. Tym razem błąd brzmiał: zapytanie „sledzie” zwraca inne
wyniki niż „śledzie”.

Analiza problemu ujawniła, że algorytm odpowiedzialny za „polszczenie”
zapytania powinien być stosowany szerzej. Mamy dwie implementacje
pewnej struktury danych używanej w tym algorytmie: jedna z nich jest
czysto funkcyjna i zbudowana z rdzennie clojurowego tworzywa – map i
wektorów; druga zaś jest ukryta za fasadą używanej przez nas
biblioteki Javowej o prostym interfejsie, a więc explicite do tej pory
z niej nie korzystaliśmy. Algorytm polszczenia był uruchamiany tylko
na pierwszej implementacji (co wystarczy w wielu przypadkach, ale nie
zawsze), a powinien być na obu.

Koncepcyjnie te dwie implementacje są podobne, ale mają zupełnie różne
API – zrazu wydawało mi się więc, że będę musiał pisać drugą
wersję funkcji realizującej „polszczenie”. To jednak nie byłoby
optymalne: duplikacja kodu oznacza dwa razy więcej okazji do
popełnienia błędu i konieczność pamiętania o wprowadzeniu zmian
każdorazowo w obu miejscach. Z pomocą przyszły [protokoły][3] – nowy
element języka wprowadzony w Clojure 1.2.

Protokół jest czymś bardzo zbliżonym do znanego z Javy interfejsu: to
zestaw deklaracji nazwanych funkcji i ich argumentów, jednak bez
implementacji. Implementacje definiuje się dla konkretnych typów. Mogą
to być zarówno typy definiowane w Clojure (przy użyciu konstrukcji
`deftype`), jak i istniejące klasy Javowe. Można więc zadeklarować w
protokole jakąś operację (nazwijmy ją blabalizacją), po czym
zdefiniować, co oznacza blabalizowanie liczb, a co napisów. Efekt jest
taki, jak gdybyśmy w Javie jakoś zmusili klasy `java.lang.Integer` i
`java.lang.String` do implementowania interfejsu `Blabalize`; możemy
wołać odpowiednią funkcję bezpośrednio na obiektach odpowiednich klas,
bez żadnych wrapperów!

Zacząłem więc od zdefiniowania protokołu, opisującego dwie operacje,
jakich można dokonywać na mojej strukturze danych. Potem przerobiłem
implementację algorytmu „polszczącego” zapytania tak, aby korzystała
tylko z tych dwóch operacji, co uniezależniło ją od implementacji
„pod spodem”. Napisanie Clojurowej implementacji protokołu było
proste; trudniejsze okazało się odpowiednie skorzystanie z biblioteki
Javowej, ale i to udało się zrobić w miarę szybko.

Efekt: brak duplikacji kodu i tylko nieznacznie zmodyfikowana
dotychczasowa wersja. A przy tym udało się nagiąć bibliotekę Javową do
robienia rzeczy, do których nie była zaprojektowana, bez ingerencji w
jej kod. Clojure nie przestaje mnie zadziwiać.

Innym razem zauważyłem, że od kilku commitów przy próbie uruchomienia
testów jednostkowych i regresji w logach testowania pojawia się
komunikat, którego zdecydowanie nie powinno tam być. Mógłbym zabrać
się do tego testując okolice pojawienia się komunikatu w REPL, ale tym
razem postanowiłem zrobić to inaczej. Ponieważ używamy [Git][4] do
kontroli wersji, wykorzystałem narzędzie [git-bisect][6], aby znaleźć
commit wprowadzający komunikat. Potrzebuje ono do działania trzech
rzeczy: identyfikatorów commitu „dobrego” i „złego” oraz skryptu,
który odpowiada na pytanie, czy dany commit jest „dobry” czy „zły”
(zwracającego kod wyjścia odpowiednio 0 albo 1). Skrypt
wyglądał mniej więcej następująco:

``` bash
cake clean
cake deps
cake proto
sleep 10
cake release
rm *.log
make test
if grep -q "PODEJRZANY_KOMUNIKAT" fablo*log; then
  exit 1
else
  exit 0
fi
```

([Cake][5] jest narzędziem, którego używamy do budowania
oprogramowania; 10-sekundowa przerwa między `cake proto` a
`cake release` jest remedium na błąd w cake.)

Potem wystarczyło tylko:

``` bash
$ git bisect start HEAD dobry_commit --
$ git bisect run check.sh
```

i już wiedziałem, w którym miejscu się zepsuło, a stąd było już
niedaleko do znalezienia przyczyny problemu.

 [1]: http://fablo.pl
 [2]: http://clojure.org
 [3]: http://clojure.org/Protocols
 [4]: http://git-scm.com/
 [5]: http://github.com/ninjudd/cake
 [6]: http://www.kernel.org/pub/software/scm/git/docs/git-bisect.html
