---
layout: theme:post
title: "Filmujmy błędy!"
date: 2010-11-19
comments: true
categories: Fablo programowanie
---

Każdy programista (o ile jego oprogramowanie jest używane przez kogoś
jeszcze poza nim samym) dostał kiedyś zgłoszenie błędu w
programie. Wielu pracuje na co dzień z systemami śledzenia błędów,
wielu zgłasza błędy w programach, których sami używają.

Dobre zgłoszenie powinno pomóc programiście odtworzyć błąd. Instrukcja
odtworzenia jest bodaj najważniejszym z trzech nieodzownych elementów
zgłoszenia. (Pozostałe dwa, jak pisze [Joel Spolsky][1], to odpowiedzi
na pytania: _co powinno było się stać_ oraz _co naprawdę się stało_.)
Na ogół w systemach śledzenia błędów opisujemy po prostu prozą kroki,
które wykonaliśmy, a które doprowadziły do błędnej sytuacji.

Dokładne opisanie wykonanych czynności jest jednak trudne. Znaczenie
może mieć każdy, nawet najmniejszy szczegół, łatwo więc o nieścisłość.
Zamiast „otworzyłem plik” lepiej napisać „nacisnąłem Alt-O” albo
„kliknąłem w menu Plik i kliknąłem polecenie Otwórz”, w zależności od
tego, co naprawdę zrobiliśmy. W świetnym eseju
[_Jak efektywnie zgłaszać błędy_][2], z którego zaczerpnąłem ten
przykład, Simon Tatham podaje inną, efektywniejszą metodę zgłaszania:
należy posadzić programistę przed swoim komputerem i pokazać mu, w
jaki sposób doprowadzamy do błędu.

Ta metoda jest dobra, kiedy da się zastosować. Problem pojawia się w
przypadku, kiedy programista jest fizycznie na drugim końcu
świata. Nie może wtedy zobaczyć monitora użytkownika i patrzeć mu na
ręce... albo przynajmniej do niedawna nie mógł.

Od jakiegoś czasu w [Fablo][5] dołączamy do zgłoszeń błędów krótkie
filmiki – _screencasty_ z komentarzem głosowym. Z każdym kolejnym
błędem rozwiązanym dzięki wskazaniu w taki sposób coraz bardziej
przekonuję się do rozlicznych zalet tej metody. Przede wszystkim film
jest dokładnym zapisem sesji. Widać wszystko, co się działo krok po
kroku, i nie trzeba prosić o dodatkowe szczegóły, co zabiera czas
i naprawiającego, i zgłaszającego. Film można odtwarzać wielokrotnie i
dowolnie po nim nawigować, a nawet spowalniać; to nawet lepsze niż
patrzenie na żywo na pracę użytkownika z programem. Jeśli mamy do
czynienia z _heisenbugiem_ („wywala się mniej więcej co pięć minut i
w różnych sytuacjach, za cholerę nie mam pomysłu!”), możemy po prostu
włączyć nagrywanie i zapomnieć o nim, pracując normalnie do momentu
wystąpienia błędu. Film można wtedy obciąć do ostatnich dwóch-trzech
minut i wysłać.

Co więcej, sporządzenie filmiku bardzo mało kosztuje. Na obie używane
przez nas platformy dostępne są narzędzia pozwalające łatwo nagrać
film. Dzięki [Snapz Pro X][3] na Mac OS X nagranie jest tak proste jak
naciśnięcie Shift-Cmd-3 na początku i na końcu filmu;
[Record My Desktop][4] na Linuksa i FreeBSD jest nieco trudniejszy w
obsłudze, ale wciąż łatwy.

Wypadałoby wspomnieć o minusach filmów. Przychodzą mi do głowy
dwa. Jednym jest fakt, że nie każdy błąd można zgłosić w ten sposób, a
mówiąc ściślej: błąd w nie każdym oprogramowaniu. Gdy pracuje się z
bibliotekami zamiast z okienkowym lub przeglądarkowym oprogramowaniem,
wyłapywane błędy są innej natury i polegają na ogół na nieoczekiwanym
zachowaniu konkretnej funkcji lub klasy. To, _nolens volens_, trzeba
opisać.

Druga sprawa to ilość miejsca zajmowanego przez filmiki i czasu
potrzebnego na ich wrzucenie do systemu kontroli błędów. To zapewne
jeszcze do niedawna był powód, dla którego rzadko widzi się takie
zgłoszenia np. w oprogramowaniu _open source_. Jednak problem ten
staje się mało znaczący wraz z upowszechnieniem się szybkich łącz.
Typowa wielkość naszego filmiku to ok. 1.5-2 MB; to naprawdę nie jest
dużo, a [FogBugz][6] pozwala na bardzo łatwe – jednym przeciągnięciem
myszy – dodanie screencastu do raportu błędu. Większe rzeczy z
łatwością można udostępnić w [Dropbox][7].

Filmujmy więc błędy!

 [1]: http://www.joelonsoftware.com/articles/fog0000000029.html
 [2]: http://www.chiark.greenend.org.uk/~sgtatham/bugs-pl.html
 [3]: http://www.ambrosiasw.com/utilities/snapzprox/
 [4]: http://recordmydesktop.sourceforge.net/about.php
 [5]: http://fablo.pl
 [6]: http://fogbugz.com
 [7]: http://dropbox.com
