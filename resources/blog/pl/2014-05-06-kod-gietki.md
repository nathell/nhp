---
layout: theme:post
title: Kod giętki
date: 2014-05-06
comments: true
categories: poezja programowanie
---

Ludzie dziwnie na mnie patrzą, kiedy powiadam, że programowanie i poezja mają ze sobą wiele wspólnego. A jednak uważam, że tak właśnie jest, i to co najmniej na kilku poziomach.

Jaki jest cel poezji? Na ten temat napisano morze: parafrazując stary dowcip -- gdzie dwóch poetów, tam co najmniej trzy odpowiedzi na to pytanie. Spróbuję jednak uogólnić: poezja ma za zadanie _wzbudzić_ w odbiorcy określone _emocje_ lub przemyślenia, używając do tego celu _słów_.

Warto zwrócić uwagę na tę drugą część, oczywistą przecież, poprzedniego zdania. Język, słowa, to najważniejszy i jedyny środek, jaki poezja ma do dyspozycji do osiągnięcia swojego celu. Dobór słów, kolejność słów, ułożenie słów -- wszystko to ma w poezji kolosalne i fundamentalne znaczenie. Pojedyncze słowa są na wagę złota. Kilka celnie dobranych słów potrafi wyindukować mocny i poruszający obraz; i odwrotnie -- jedno niepasujące słowo może ów obraz zaprzepaścić.

Kojarzycie ten wyimek z Norwida? Pewnie, że kojarzycie, w końcu zdobił pracownię języka polskiego w co drugiej szkole.

> Ponad wszystkie wasze uroki,<br>
> Ty! Poezjo, i Ty, Wymowo,<br>
> Jeden – wiecznie będzie wysoki:<br>
> Odpowiednie dać rzeczy – słowo!

Zastanówmy się nad tym chwilę. Norwid próbuje tu wyrazić właśnie to: owo właściwe poetom maksymalistyczne dążenie do przekazania myśli w sposób możliwie najbardziej oddziałujący na odbiorcę przy użyciu _odpowiednich słów_. Tak i tylko tak.

Spójrzmy na kilka przykładów poszukiwania takich słów. Oto przykład zaczerpnięty z „Ocalonego w tłumaczeniu” Barańczaka, Gerard Manley Hopkins w liście do Roberta Bridgesa z 7 września 1888:

> By the bye you misquote the same modern author in writing ‘airy between towers’: what is not so? it should be ‘branchy between towers’.

Gwoli wyjaśnienia: „the same modern author” to sam Hopkins, a wiersz, o którym mowa, to „[Duns Scotus's Oxford][1]”, którego incipit adresat listu błędnie cytuje gdzie indziej. Poeta czepia się jednego jedynego słowa, uznając jego zamianę za niefortunną i istotnie osłabiającą przekazywany obraz, w  znamiennym proteście: „powietrzny pomiędzy wieżami -- a cóż takie nie jest?” W domyśle: jeśli nad jakimś krajobrazem górują wieże, to jasne jest, że pomiędzy ich najwyższymi partiami jest dużo przestrzeni, nie ma sensu tego _explicite_ mówić w wierszu; ale Oksford z jego strzelistymi drzewami i rozłożystymi konarami jest w umyśle Hopkinsa bardzo charakterystyczny.

A teraz zacytuję fragment wiersza, o którym Theodore Roosevelt powiedział, że „nie jest pewien, czy go rozumie, ale z całą pewnością go lubi”: „[Luke Havergal][2]” E. A. Robinsona. Wersy 4-5 w pierwszej redakcji tego wiersza, z roku 1896, brzmiały:

> The wind will moan, the leaves will whisper some,— <br>
> Whisper of her, and strike you as they fall; <br>

Obraz jest sugestywny: niemal czuje się przenikliwy wiatr i  słyszy szelest liści układający się w szept. Ale czytając ostateczny, późniejszy o ćwierć wieku wariant wiersza, widzimy, że Robinson ten fragment nieznacznie przerobił:

> The leaves will whisper there of her, and some, <br>
> Like flying words, will strike you as they fall; <br>

W tej wersji wiatr obecny jest tylko w sposób dorozumiany: skoro mamy szept liści, to, jak uznał Robinson, wiadomo, że wieje i wspominanie o tym wprost jest redundantne; pominięte jest też powtórzenie „szeptu”, którego efekt wzmacniający przekaz był wątpliwy. Wyrzucając te dwie rzeczy, poeta robi miejsce na intrygującą i mocną metaforę „smagających słów w locie”, którą można interpretować na rozmaite sposoby i która interesująco współgra z dalszymi częściami wiersza („God slays Himself with every leaf that flies” w strofie drugiej).

Mieliśmy więc działający, mocny tekst; dzięki przeróbce i znalezieniu _odpowiedniego słowa_ działa on _lepiej_, efektywniej. Punkt dla tych, którym przychodzi do głowy słowo „refactor”.

Zastanawialiście się, co by było, gdyby Mickiewicz używał gita? „Stepy akermańskie” były refaktorowane wielokrotnie w karkołomnych próbach uchwycenia balansu między wzburzonym oceanem a wysuszoną, jałową pustynią. Jesteśmy niemal w stanie zrekonstruować wynik działania `git log -p` na tym hipotetycznym repozytorium.

```nohighlight
commit 90023f50e466ecf9339c1348f92bd855ccd58750
Author: Adam Mickiewicz <adam@mickiewicz.lt>
Date:   Wed Mar 12 19:42:12 1828 +0100

    pierwszy wers - wersja ostateczna

diff --git a/01-stepy-akermanskie.txt b/01-stepy-akermanskie.txt
index 019e416..0c741b8 100644
--- a/01-stepy-akermanskie.txt
+++ b/01-stepy-akermanskie.txt
@@ -1 +1 @@
-Okrążyły mię stepy na kształt oceanu
+Wpłynąłem na suchego przestwór oceanu

commit 1fee11618a8373ae8b8a435c6ff0c7009e3c4554
Author: Adam Mickiewicz <adam@mickiewicz.lt>
Date:   Fri Jun 2 17:15:26 1826 +0400

    poprawka do pierwszego wersu

diff --git a/01-stepy-akermanskie.txt b/01-stepy-akermanskie.txt
index 4b7d669..019e416 100644
--- a/01-stepy-akermanskie.txt
+++ b/01-stepy-akermanskie.txt
@@ -1 +1 @@
-Wjechałem na suchego stepy oceanu
+Okrążyły mię stepy na kształt oceanu

commit 416f8d15382b805def5e9e80928216bc86d56eec
Author: Adam Mickiewicz <adam@mickiewicz.lt>
Date:   Fri Jun 2 11:44:52 1826 +0400

    kolejna przeróbka pierwszego wersu

diff --git a/01-stepy-akermanskie.txt b/01-stepy-akermanskie.txt
index d3b53c2..4b7d669 100644
--- a/01-stepy-akermanskie.txt
+++ b/01-stepy-akermanskie.txt
@@ -1 +1 @@
-Żeglowałem i w suchych stepach oceanu
+Wjechałem na suchego stepy oceanu

commit c196b9483f501992ed9ce887b5ab4f1279c1b946
Author: Adam Mickiewicz <adam@mickiewicz.lt>
Date:   Thu Jun 1 18:03:31 1826 +0400

    przeróbka pierwszego wersu

diff --git a/01-stepy-akermanskie.txt b/01-stepy-akermanskie.txt
index d490bdf..d3b53c2 100644
--- a/01-stepy-akermanskie.txt
+++ b/01-stepy-akermanskie.txt
@@ -1 +1 @@
-Poznałem stepowego podróż oceanu
+Żeglowałem i w suchych stepach oceanu
```

To, co próbuję pokazać na tych przykładach, to to, że dążenie do takiego użycia języka (polskiego, angielskiego, C, Javy...), żeby możliwie klarownie _wyrazić_ w nim to, o co chodzi, i żeby -- cytując C.A.R. Hoare'a -- rezultat miał „obviously no deficiencies”, jest wspólną cechą poetów i programistów.

Gdyby było inaczej, nie toczylibyśmy długich dyskusji na temat wyższości paradygmatu obiektowego nad funkcyjnym albo odwrotnie, nie wymyślalibyśmy takich rzeczy jak _dependency injection_ albo _fluent APIs_, nie mielibyśmy tak bogatego katalogu wzorców projektowych, nie mielibyśmy indywidualnych upodobań stylistycznych, nie pisalibyśmy dokumentów o nazwie `CodingStyle`, nie rozróżnialibyśmy pomiędzy kodem obleśnym i ładnym, wreszcie -- widząc ten pierwszy -- nie mielibyśmy odruchu, żeby go natychmiast przepisać.

„Chodzi mi o to, aby język giętki powiedział wszystko, co pomyśli głowa”, pisał Słowacki. Kiedy o tym myślę, przychodzi mi do głowy Lisp i często powtarzane zdanie, że to programowalny język programowania. Homoikoniczność i makra sprawiają, że pisząc program w Lispie, tworzymy nie tylko kod odpowiedzialny za rozwiązanie naszego problemu w z góry istniejącym języku, ale również rozwijamy i naginamy język, używając neologizmów: nowych, powstałych _ad hoc_ konstrukcji składniowych, o ile tylko pozwalają naturalniej, klarowniej wyrazić sens danego fragmentu kodu. Programowanie w Lispie jest jak bycie Leśmianem.

Czasem efekt estetyczny pojawia się w kodzie niejako mimochodem. Przypomina mi się wywiad, jakiego Guy Steele udzielił Peterowi Seibelowi w jego świetnej książce „Coders at Work” (polecam!) Steele wspomina tam o jednym z najbardziej satysfakcjonujących momentów w jego karierze programisty, kiedy po dwudziestu latach udało mu się skrócić napisaną przez Billa Gospera jedenastosłowową procedurę obliczającą sinusy i cosinusy liczb rzeczywistych o jedno słowo maszynowe. Osiągnąć, wydawałoby się, nieosiągalne, dzięki właściwej poezji zwięzłości i nadorganizacji języka: oto czysta radość programowania.

Warto się rozejrzeć po GitHubie. Poezja tam się czai.

 [1]: http://www.bartleby.com/122/20.html
 [2]: http://www.poetryfoundation.org/poem/175762
