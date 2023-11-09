---
layout: theme:post
title: ClojureScript
date: 2011-07-21
comments: true
categories: Clojure JavaScript programowanie
---

Rich Hickey, autor Clojure, wyrasta na Steve'a Jobsa nowoczesnych
języków programowania – przynajmniej jeśli idzie o sposób prezentacji
swoich dzieł. Oto kilka dni temu na grupie dyskusyjnej Clojure pojawiło
się [ogłoszenie][1], że Rich wystąpi na najbliższym spotkaniu
nowojorskiej grupy użytkowników Clojure, że strumień wideo z wystąpienia
będzie nadawany na żywo i że zaprezentowane zostanie „coś nowego”.

Tym czymś okazał się [ClojureScript][2]: kompilator Clojure do
JavaScriptu, łączący moc pierwszego języka z powszechnością drugiego.

JavaScript bywa nazywany „czarnym koniem” języków programowania,
najbardziej niedocenianym językiem minionej dekady i najważniejszym
obecnej. Istotnie, silniki JavaScriptu w przeglądarkach – a także
wolnostojące, jak Node.js – poczyniły w ostatnich latach kolosalne
postępy, jeśli chodzi o wydajność, narzędzie to jest coraz chętniej
używane do programowania coraz poważniejszych aplikacji i nie mijając
się z prawdą można powiedzieć, że JavaScript jest wszędzie.

A teraz te same słowa można odnieść również do Clojure. W tegorocznym
[sondażu][3], podobnie jak rok temu, społeczność Clojure najczęściej
wskazywała JavaScript jako hipotetyczną alternatywną wobec JVM
platformę, ale zapewne mało kto się spodziewał, że urzeczywistni się to
tak szybko. Łatwo sobie uzmysłowić możliwości, jakie daje nowa
implementacja. Ten sam kod na serwerze i w przeglądarce? Programowanie
aplikacji iOS w Clojure? Jak najbardziej!

W ramach dodawania łyżki dziegciu do tej beczki miodu trzeba zauważyć
jednak, że dialekt ClojureScript jest okrojony i ma nieco inną semantykę
niż standardowy Clojure: nie ma STM, niezmienność struktur danych nie
jest wymuszana, wektory są zwykłymi JavaScriptowymi tablicami. Czy to
się zmieni, pokaże przyszłość.

Jaka przyszłość czeka Clojure i ClojureScript? Spróbuję pokusić się o
przepowiednię:

* Pojawienie się alternatywnej wobec JVM platformy wymusi
  ukształtowanie się bogatszej niż do tej pory biblioteki
  standardowej Clojure, wspólnej dla obu wersji. Ten proces trwa
  już teraz: tam, gdzie w czasach Clojure 1.0 pisało się
  `(.toUpperCase "foo")` (wychodząc z założenia _where Java isn't
  broken, Clojure doesn't fix it_), teraz kanoniczną formą jest
  `(clojure.string/upper-case "foo")`. W ten sposób Clojure
  stopniowo stanie się językiem niezabawkowym według
  [definicji Maćka Pasternackiego][4].
* ClojureScript otworzy dla Clojure zupełnie nową niszę: stanie
  się popularny wśród programistów-frontendowców, piszących do tej
  pory głównie w JavaScripcie lub CoffeeScripcie.
* Równocześnie pojawienie się ClojureScript nie oznacza, że
  zatrzymają się prace nad „tradycyjnym” Clojure. JVM pozostanie
  główną platformą rozwoju języka i to tam będą pojawiać się
  kolejne innowacyjne cechy. Wersja 1.3 jest już w fazie beta i
  nadanie jej statusu _final_ jest kwestią kilku tygodni.

Patrząc wstecz, aż trudno uwierzyć, jak dużą popularność zdobyło sobie
Clojure. Gdyby ktoś mi cztery lata temu powiedział, że za cztery lata
pojawi się nowy język z rodziny Lisp, który będzie znacznie bardziej
elegancki niż Common Lisp, znacznie praktyczniejszy niż Scheme i używać
go będą wielkie instytucje finansowe, wyśmiałbym go. A jednak: zaczynam
wierzyć, że prawo Kopernika jednak nie stosuje się w informatyce.

 [1]: https://groups.google.com/forum/#!msg/clojure/v2fP1qQQBhQ/Xz836_GafIgJ
 [2]: https://github.com/clojure/clojurescript
 [3]: http://cemerick.com/2011/07/11/results-of-the-2011-state-of-clojure-survey/
 [4]: http://www.3ofcoins.net/2009/01/30/common-lisp-clojure-and-seriousness/
