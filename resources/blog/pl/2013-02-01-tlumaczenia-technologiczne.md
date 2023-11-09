---
layout: theme:post
title: O książkach technologicznych i ich tłumaczeniach
date: 2013-02-01
comments: true
categories: Clojure programowanie tłumaczenia
---

> _Trudna rzecz jest jeden język drugim językiem dostatecznie wyrznąć._
> <p style="text-align: right;">-- Jan Leopolita (1523--1572)</p>

## Rodzaje książek informatycznych

Mam zasadę dotyczącą książek informatycznych: nie kupuję niczego, co
ma w tytule nazwę konkretnej technologii. (Purystów z góry przepraszam
za użycie tego słowa, mając świadomość jego [pretensjonalności][1];
ale nie znam innego polskiego określenia, które by zbiorczo obejmowało
konkretne języki programowania, systemy operacyjne, platformy
wykonawcze, serwery aplikacji, architektury itd.) Po pierwsze,
technologie pojawiają się i znikają, wciąż ukazują się ich nowe
wersje, i w efekcie czas życia książki opisującej, dajmy na to,
konkretną implementację jakiegoś języka programowania na ogół bywa nie
dłuższy niż kilka lat. Można wręcz mówić o „okresie półtrwania”, po
którym połowa informacji zawartej w książce jest już nieaktualna.  Po
drugie, i może ważniejsze, tego typu publikacje często wnoszą
niewielką wartość dodaną w porównaniu do dokumentacji: od książki
oczekuję, że nie tylko przekaże mi suche instrukcje, ale także idee,
które w jakiś sposób wzbogacą mój warsztat i zostaną ze mną na dłużej.
Dlatego do poduszki wolę poczytać o
[uczeniu maszynowym za pomocą metod jądrowych][2] niż o
[bebechach serwera aplikacyjnego][3].

Rzecz jasna, jak od każdej zasady, tak i od tej istnieją wyjątki.
Bywa, że technologia na tyle wryła się w informatyczny krajobraz, że
nie grozi jej szybkie przeterminowanie, a dana książka uznawana jest
za kanoniczny opis technologii, nie zaś tylko dodatek _post hoc_ (jak
[K&R][4] do C czy [Księga Wielbłądzia][5] do Perla). Zdarza się też i
tak, że książka opisuje język czy środowisko, którego akurat
intensywnie używam lub przymierzam się do intensywnego używania, i
wtedy mam szansę odnieść z niej dużą korzyść. Tak było z
_[Programming Clojure][6]_ Stuarta Hallowaya, której pierwsze wydanie kupiłem
niedługo po jego ukazaniu się z górą trzy lata temu, a drugie,
zmienione i uaktualnione (a jakże!) wkrótce [ukaże się po polsku][7]
nakładem [Helionu][8].

Po przeczytaniu darmowej próbki nasunęły mi się wątpliwości odnośnie
samego tłumaczenia, a w ślad za nimi myśli dotyczące polskich
tłumaczeń literatury informatycznej w ogóle. Spróbuję je poniżej
zebrać.

## O Clojure po polsku

Od razu powiem, że ogólny poziom przekładu Tomasza Walczaka uważam za
wysoki: tekst jest spójny, nośny i pozbawiony błędów
merytorycznych. Czepianie się szczegółów, które za chwilę nastąpi, ma
być nie tyle krytyką, co raczej próbą uświadomienia sobie, dlaczego
mimo wszystko „Programowanie w języku Clojure” czyta mi się mniej
płynnie niż oryginał angielski; a także zwrócenia uwagi na problemy,
z jakimi musi się zmierzyć tłumacz. Zasygnalizuję więc pewne
zagadnienia, ale wcale nie mam pewności, czy potrafiłbym rozwiązać
je lepiej, albo nawet – czy w ogóle się da.

Bo wydawałoby się, że tłumaczenie tekstu informatycznego jest czynnością
bez porównania prostszą niż na przykład poezji, gdzie trzeba ważyć
każde słowo, prawda? Tymczasem rodzi ono zupełnie nowe problemy.
Tłumacz zmuszony jest często wymyślić od nowa terminologię z zakresu
wąskiej dziedziny, o której po polsku się nie pisze lub pisze się
rzadko lub nieformalnie; podjąć pewne nierzadko arbitralne decyzje
i konsekwentnie się ich trzymać; albo – jak zobaczymy – zadecydować,
co zrobić z subtelnymi różnicami odcieni znaczeniowych pewnych pojęć.

### Lispa czy Lispu?

Tłumacz często używa formy _Lispa_ jako dopełniacza od rzeczownika
_Lisp_. A jak naprawdę powinna brzmieć ta forma? Zdania na ten temat
są podzielone. Wydaje się, że tradycyjnie w środowisku utrwalona była
postać _Lispu_ (zapewne nawiązująca do odmiany nazw innych wczesnych
języków programowania: _Fortranu_ i _COBOL-u_), co potwierdza zarówno
[poradnia językowa PWN][9], jak i słowniki:
[Słownik gramatyczny języka polskiego][10] i słownik [sjp.pl][11].
Forma _Lispa_ (być może urobiona przez analogię do odmiany nazw
nowych języków: _Perla_, _Pythona_) też jest żywa w uzusie, choć,
jak się zdaje, nieznacznie ustępuje odmianie _Lispu_ (w
[Narodowym Korpusie Języka Polskiego][12] znajdujemy jej 19 wystąpień
wobec 33 wystąpień _Lispu_).

Dla kontrastu, słowo _Clojure_, którego paradygmat odmiany
(nieodmienne) nie budzi wątpliwości, pojawia się na ogół jako część
sformułowania _język Clojure_. Można by analogiczny zabieg przeprowadzić
z _językiem Lisp_. Albo pozostać przy formie _Lispu_, która, choć
zrazu brzmiała mi dziwnie, teraz wydaje się znacznie naturalniejsza
niż _Lispa_.

### Co to jest instrukcja?

Porównajmy taki fragment oryginału:

> `defrecord` and related functions are covered in Section 6.3, _Protocols_.

z odpowiadającym mu tłumaczeniem:

> Instrukcję `defrecord` i powiązane funkcje omawiamy w podrozdziale 6.3, „Protokoły”.

Tłumacz – słusznie – uznaje, że po polsku to zdanie brzmiałoby
koślawo bez wprowadzenia na początku rzeczownika określającego, cóż to
jest owo `defrecord`. Wybiera słowo „instrukcja”, które nie budziłoby
żadnych sprzeciwów, gdyby to była książka o Pythonie albo C.  Tu
jednak mam wątpliwości. Wiele języków programowania odróżnia wyrażenia
od instrukcji, i w nich te drugie rozumiane są jako autonomiczne
fragmenty wykonywalnego kodu potencjalnie zmieniającego stan programu.
A przecież Clojure kładzie duży nacisk na niezmienność danych!
Co więcej, w Clojure, jak we wszystkich Lispach i wielu innych językach
funkcyjnych, tak rozumianych instrukcji nie ma (albo patrząc z innego
punktu widzenia: termin ten jest tożsamy z terminem „wyrażenie”) --
kod programu po prostu składa się z wyrażeń.

Jeśli więc nie instrukcja, to co? W angielskim [żargonie lispowym][13]
używane jest słowo _form_ na określenie obiektu, który ma być poddany
ewaluacji. Jeśli ten obiekt jest listą, której pierwszy element jest
symbolem (i w takim razie składnia i semantyka listy są wyznaczone przez
ten symbol, który może nazywać funkcję, makro albo wbudowaną konstrukcję
języka), to nazwy tego symbolu używa się często jako przydawki.
Mówi się więc o „`defmacro` forms”, „`defvar` forms”, itd. To właśnie
to określenie mógł tu pominąć Halloway. A jak jest po polsku _form_?
Znowu: nie ma powszechnej zgody. Można by to mechanicznie przełożyć
na _forma_, ale moim zdaniem nazwą mniej przeładowaną znaczeniowo
i bardziej oddającą sens jest _formuła_. W użyciu są obie wersje
i w Google można znaleźć ich przykładowe wystąpienia.

(Na marginesie można oddać tłumaczowi sprawiedliwość, że Halloway tą
elizją zdaje się sugerować, że `defrecord` jest funkcją, gdy w
rzeczywistości jest to makro. Z drugiej strony, makra w Clojure są w
istocie funkcjami, których _vars_ (patrz niżej) mają ustawioną
odpowiednią flagę, co powoduje specjalne traktowanie ich przez
kompilator.)

### Zmienna zmiennej nierówna

Znowu zacznę od cytatu, tym razem najpierw dwóch kawałków tłumaczenia:

> Nie występują tu zmienne, modyfikowalny stan ani rozgałęzienia.

> Przedrostek `#'` oznacza, że funkcję zapisano w _zmiennej_ języka Clojure (...)

A tak wyglądają te fragmenty w oryginale:

> It has no variables, no mutable state, and no branches.

> The prefix `#'` indicates that the function was stored in a Clojure _var_ (...)

W polskim tłumaczeniu w obydwu tych fragmentach pojawia się słowo
_zmienna_.  Tymczasem spojrzenie na oryginał ujawnia, że słowo to ma
dwa różne odcienie znaczeniowe, z których każdy oddawany jest po
angielsku inaczej: raz jest to _variable_, a raz _var_.

Co to jest _variable_, wiadomo z innych języków: nazwane „pudełko”
przechowujące wartość. (Zauważmy, że Halloway używa tego słowa mówiąc
o zmiennych w Javie.)  Jest to termin ogólny i używany w kontekście
wszystkich języków imperatywnych, jak C czy Perl. Natomiast _var_ jest
terminem węższym i oznacza _jedną z clojurowych implementacji_ takiego
pudełka. Dokładniej mówiąc, jest to obiekt klasy
`clojure.lang.Var`. Obiekty takie mają dwie charakterystyczne cechy,
nietypowe dla zmiennych z innych języków, a odróżniające je od atomów,
również podobnych do zmiennych:

* wartości _var_ mogą być różne w różnych wątkach (przy czym istnieje
  koncepcja głównego wiązania, czyli wartości domyślnej, którą nowo
  utworzone wątki widzą dopóki jej sobie nie przesłonią własnym;
  takie wiązanie nie musi istnieć);
* obiekty takie są pamiętane w przestrzeniach nazw: w istocie o
  przestrzeni nazw można myśleć jako o mapie wiążącej symbol z
  obiektem _var_.

(Taka dychotomia nazewnicza jest endemiczna dla Clojure i ma również miejsce
przy funkcjach: w angielskim slangu clojurowym mówi się o _fns_, czytane
„ef-ens”, co oznacza obiekty implementujące interfejs `clojure.lang.IFn`).

To dopiero dylemat dla tłumacza! Nie mam pojęcia, co mógłbym tu zaproponować.
Można by pozostać przy długim, doprecyzowującym określeniu „zmienna _języka Clojure_”
dla odróżnienia od „zmiennej” po prostu, ale ceną za to jest rozwlekłość.
Nie wiem, czy można jej jakoś uniknąć.

### INSERT INTO użytkownik (imię) VALUES ('Judyto')

Ach, kod wypluwający komunikaty w języku naturalnym! Błogosławieni użytkownicy
angielszczyzny, albowiem oni odmianą przez przypadki martwić się nie muszą.
Halloway, na nieszczęście tłumacza, wpada na pomysł uogólnienia kanonicznego
„Hello, world” vel „Witaj, świecie” poprzez sparametryzowanie witanego obiektu.

Chciałoby się, żeby funkcja witająca zwracała się do użytkownika w wołaczu.
Tłumacz osiąga to bez problemu:

``` clojure
(defn hello [name]
  (str "Witaj, " name))
```

I świetnie. Tylko, że trzeba wołać tę funkcję tak: `(hello "Janku")`,
co jest trochę nienaturalne. A co, jeśli oprócz wyświetlenia powitania
chcemy imię zapisać też w bazie danych? Z drugiej strony, _licentia poetica_
tłumacza nie może sięgać tak daleko, żeby walnąć tu

``` clojure
(defn witaj [imię]
  (str "Witaj, " (odmień imię :wołacz)))
```

!

## Warto, nie warto?

Czy warto będzie kupić „Programowanie w języku Clojure” i przeczytać po polsku?
Myślę, że tak, o ile ktoś jeszcze nie czytał po angielsku lub woli ojczysty język
nawet kosztem drobnych nieścisłości. Jak już mówiłem, jestem pełen uznania dla
pracy tłumacza i chylę czoła. Podziwiam!

Mam za to wątpliwości, czy warto _w ogóle tłumaczyć_ książki o technologiach.
W pierwotnym zamyśle reszta niniejszej notki miała stanowić rozważania na
ten temat, ale rozważania techniczne tak się rozrosły, że wpis zrobiłby się
za długi. Więc o tym następnym razem.

 [1]: https://groups.google.com/d/msg/pl.comp.programming/UH4hrQWBCPo/ILTtM_GEfO8J
 [2]: http://www.amazon.co.uk/Kernel-Methods-Pattern-Analysis-Shawe-Taylor/dp/0521813972
 [3]: http://www.amazon.co.uk/Pro-IBM-WebSphere-Application-Professionals/dp/1430219580
 [4]: http://pl.wikipedia.org/wiki/J%C4%99zyk_ANSI_C
 [5]: http://en.wikipedia.org/wiki/Programming_Perl
 [6]: http://pragprog.com/book/shcloj/programming-clojure
 [7]: http://helion.pl/ksiazki/programowanie-w-jezyku-clojure-stuart-halloway-aaron-bedra,proclo.htm
 [8]: http://helion.pl/
 [9]: http://poradnia.pwn.pl/lista.php?id=9081
 [10]: http://sgjp.pl/
 [11]: http://www.sjp.pl/Lispu
 [12]: http://nkjp.pl/
 [13]: http://clhs.lisp.se/Body/26_a.htm
