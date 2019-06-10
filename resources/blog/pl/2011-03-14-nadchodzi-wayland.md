---
layout: theme:post
title: Nadchodzi Wayland
date: 2011-03-14
comments: true
categories: Linux
---

Podczas jednego z ostatnich surfathlonów rzuciła mi się w oczy
informacja o nowym kawałku uniksowego oprogramowania. Rozwijany bez
wielkiego rozgłosu i dziś niemal nieznany, za kilka lat prawdopodobnie
będzie jednym z kluczowych elementów absolutnie każdego biurkowego
Linuksa. Mowa o serwerze wyświetlania [Wayland][1], który ma wielką
szansę stać się sensowną alternatywą dla [X Window System][2].

X-y mają jedną kluczową zaletę. To wielki kawał oprogramowania bardzo
intensywnie testowanego od dziesiątków lat, który działa. A jednak
ilekroć uruchamiam `ps ax` lub `top` i widzę działający na moim
komputerze, zżerający kilka procent czasu procesora i kilkanaście
procent pamięci serwer X, mierzi mnie, gdy pomyślę, co tam się dzieje
pod spodem.

X Window System narodził się jako protokół wyświetlania, pomyślany z
wielkim rozmachem na początku lat 80. tak, aby w pełni wykorzystać moc
ówczesnych stacji roboczych z wielkimi, monochromatycznymi
wyświetlaczami. Bezstanowy model wyświetlania X jest już na starcie
bardzo skomplikowany. Żeby zrobić coś tak prostego jak narysowanie
kreski na ekranie, trzeba kolejno: otworzyć połączenie do serwera X;
utworzyć okienko; zamapować je; utworzyć maskę notyfikacji; zaczekać
na zdarzenie powiadamiające, że można rysować; wywołać właściwą
funkcję rysującą kreskę; wyczyścić bufor połączenia (jak to opisuje
[ten tutorial][3]). System czcionek bitmapowych, wiele sposobów
określania współrzędnych ekranowych, wielki zestaw prymitywów
graficznych, biblioteki Athena czy Xt -- wszystko to jest bagażem
historycznym, z którego współczesne oprogramowanie już nie korzysta, a
mimo to każdy Linux przychodzi z całym tym dobrodziejstwem
inwentarza. Dwa wzajemnie niekompatybilne podsystemy renderowania
tekstu? Taka jest rzeczywistość. A próbowaliście kiedyś zapanować nad
tym w czystym C? [Powodzenia.][4]

Do tego X przez lata obrastał w sadełko w postaci różnych rozszerzeń
protokołu, jak XRandR czy XRENDER, potrzebnych do jako takiego
sensownego działania. Współczesne pulpity z bajeranckimi efektami jak
falujące okna czy choćby półprzezroczyste terminale nie byłyby bez
nich możliwe.

Przezroczystość sieciowa, powiadacie? Tak, ale spróbujcie zdalnie
uruchomić glxgears albo pograć w Quake'a po sesji X. Nawet Firefox,
gdy go uruchamiam przez `ssh -X` po szybkim łączu na serwerze oddalonym
o kilka kilometrów od domu, ślimaczy się i jest nieużywalny. Kwestię
zdalnego uruchamiania aplikacji graficznych znacznie lepiej rozwiązał
Microsoft ze swoim Remote Desktop.

Wayland ma szansę uprościć cały okołoiksowy bałagan. Jest napisany
tak, by mógł wykorzystać sterowniki dla nowych kart graficznych, już
istniejące dla X.org. Ma bardzo prosty model wyświetlania: to nie sam
serwer wyświetla grafikę, ale po prostu udostępnia klientom swoje
bufory. Współczesne biblioteki, jak Cairo, GTK+ czy Qt, już potrafią z
niego korzystać. Chciałoby się powiedzieć: nareszcie!

Z niecierpliwością czekam dnia, w którym na żadnej z moich linuksowych
maszyn nie będzie działał żaden serwer X.

 [1]: http://wayland.freedesktop.org/
 [2]: http://x.org
 [3]: http://tronche.com/gui/x/xlib-tutorial/
 [4]: http://www.pps.jussieu.fr/~jch/software/XinC.html
