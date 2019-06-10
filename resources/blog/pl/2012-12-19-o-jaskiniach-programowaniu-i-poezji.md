---
layout: theme:post
title: O jaskiniach, programowaniu i poezji
date: 2012-12-19
comments: true
categories: programowanie poezja IF Inform
---

[Emil][1] w swojej ostatniej notce wspomina, że jego marzeniem jest
napisanie przygodówki tekstowej. Moim też! Z tej okazji przypomniał mi
się eseik, który wysłałem pięć lat temu na jedną z grup usenetowych;
publikuję tu bez zmian, dodając tylko odnośniki i poprawiając literówki.
Odnotuję też, że większość materiału historycznego jest zaczerpnięta
z „Inform Designer's Manual” Nelsona (1997), a programik, który
cytuję na końcu, nie kompiluje się już ostatnimi wersjami I7, choć
pewnie wymaga niewielkich przeróbek z zachowaniem formy.

 [1]: http://bronikowski.com/2159/skutki-bycia-ekstremalnie-zajetym

```nohighlight
From: Daniel Janus <przesunmalpe@nathell.korpus.pl>
Newsgroups: pl.hum.poezja
Subject: # o jaskiniach, programowaniu i poezji
Date: Sat, 29 Sep 2007 09:32:45 +0000 (UTC)
Message-ID: <slrnffs6tt.98o.przesunmalpe@students.mimuw.edu.pl>
```

Zaczęło się w 1838 roku.

Wtedy to właściciel Stephena Bishopa, niewolnika „szybkiego, śmiałego,
entuzjastycznego”, wykupił ziemię w stanie Kentucky, na której terenie
leżała Jaskinia Mamucia. Jej rzeczywisty rozmiar nie był wtedy
jeszcze znany: nazwę nadali jej górnicy, wydobywający stąd ongi guano
mamucie, z którego wyrabiano saletrę używaną do produkcji prochu.
Stephenowi, samoukowi, człowiekowi bystremu, znającemu łacinę i grekę,
powierzono opiekę nad jaskinią i w krótkim czasie stał się słynny jako
władca podziemnego królestwa.

W kilka lat podwoił on znaną połać jaskini, nadał też nazwy --
półklasyczne, półamerykańskie -- podziemnym korytarzom, rzekom,
studniom: Rzeka Styks, Sala Lawinowa, Bezdnia, Wielka Kopuła. Wiele z
tych nazw przetrwało do tej pory. Stephen oprowadzał po jaskini
turystów, których zaczęło ściągać tam coraz więcej; mapa, którą
naszkicował posiłkując się tylko własną pamięcią w roku 1842, była
jeszcze w użyciu czterdzieści lat później. Stephen Bishop zmarł w
roku 1857 jako wolny człowiek, uwolniony na mocy testamentu swojego
właściciela.

Minął wiek. W czasie drugiej wojny światowej na terenach Jaskini
ustanowiono park narodowy, co skutecznie odcięło napływ turystów.
Coraz więcej osób jednak podejrzewało, że Jaskinia połączona jest
systemem korytarzy z innymi okolicznymi jaskiniami, i poszukiwało
przejścia; jednak bezskutecznie. Dopiero trzydzieści lat później, w
roku 1972, Patricia Crowther, członkini ekspedycji badającej jaskinię
Flint Ridge, przebrnęła przez ciasne, błotniste przejście i
przedostała się do podziemnych głębi Jaskini Mamuciej; korytarz ten
odkryto później ponownie na mapie Bishopa.

Trzy lata później mąż Patricii, William, programista w BBN (jednym z
pionierów ARPAnetu, z którego później rozwinął się Internet),
współuczestnik tamtej wyprawy, postanowił napisać program komputerowy,
który jemu samemu przypominałby jaskiniowe lata, a zarazem byłby grą
dla jego dzieci. Jak pisał później, miał to być program
„nieodstraszający ludzi nieobeznanych z komputerem”, więc umożliwił
wydawanie poleceń postaci kierowanej przez gracza w naturalnej
angielszczyźnie, zamiast używania jakiegoś sztucznego zestawu komend.
W grze Crowthera przemierzało się korytarze jaskini używając kierunków
geograficznych; opisy pomieszczeń (z nazwami nadanymi jeszcze przez
Bishopa) były zwięzłe, choć wielopoziomowe i bogate w szczegóły
geologiczne. Crowther uczynił też niektóre przejścia niedostępnymi;
aby się do nich dostać, należało pomyśleć nad rozwiązaniem ich zagadki
i wykonać sekwencję określonych czynności.

Tak narodziła się interaktywna fikcja.

Mówię „interaktywna fikcja”, choć na początku używano terminu „gry
przygodowe”, a później „przygodowe gry tekstowe” na określenie takich
programów. I rzeczywiście były to gry przygodowe: pełne smoków,
potworów, magii, ratowania księżniczek, dziejące się w najróżniejszych
światach. (Zresztą program Crowthera został później podlany
magiczno-baśniowym sosem i zorientowany raczej na zagadki niż na
eksplorację, i to ta wersja jest najbardziej znana.) W latach
osiemdziesiątych, wraz z upowszechnieniem się komputerów domowych, ten
rodzaj rozrywki przeżył swoją świetność, głównie za sprawą firmy
Infocom.

Okazało się jednak, że medium wymyślone przez Crowthera potrafi być
czymś więcej, niż nośnikiem tylko dla gier przygodowych. Przykładem
„[Photopia][2]” (Adam Cadre, 1998), opowiadająca historię o wolnej woli i
determinizmie, w której gracz, choć znajduje się w centrum wydarzeń,
nie ma na nie wielkiego wpływu (choć iluzja, że jest odwrotnie, jest
silna); czy „[Galatea][3]” (Emily Short, 2000), dla której najwłaściwszym
określeniem wydaje się być „studium psychologiczne”: jedno
pomieszczenie, jedno światło padające z sufitu, protagonista i...
ożywiona rzeźba na piedestale, z którą można rozmawiać na wiele
sposobów. Interaktywna fikcja (już teraz określana właśnie tym
terminem) odkryta została na nowo, jako rodzaj literatury. Jako --
Sztuka.

 [2]: http://en.wikipedia.org/wiki/Photopia
 [3]: http://en.wikipedia.org/wiki/Galatea_(video_game)

Po minionym boomie lat osiemdziesiątych zaczęły powstawać i
rozpowszechniać się bezpłatnie systemy programistyczne ułatwiające
tworzenie interaktywnej fikcji. Jednym z nich był Inform (Graham
Nelson, 1993–): jego pionierska zasługa to skodyfikowanie ścisłych,
choć poddających się elastycznym modyfikacjom w razie potrzeby,
domyślnych reguł rządzących konstruowanym światem -- modelu świata.
System ten rozwijał się powoli, acz stabilnie, bez rewolucyjnych zmian,
aż do wersji siódmej, kiedy Nelson postanowił radykalnie zmienić
sposób tworzenia IF przy użyciu Informu. Co się zmieniło?

Aby używać wersji 1-6, trzeba było zostać programistą: nauczyć się
specjalnej składni, obiektowo-imperatywnego paradygmatu programowania
i sporej związanej z tym teorii. Inform 7 przyjmuje bardziej
deklaratywne podejście i ukrywa sporą część pracy z tym związanej za
fasadą języka, czyniąc następny krok w używaniu języka naturalnego:
nie tylko w produkcie końcowym -- ale też w kodzie źródłowym programu.
Oto programy w Informie 7 pisze się w języku angielskim (albo do
złudzenia go przypominającym), tyle tylko, że nieco bardziej
skodyfikowanym.

Żeby nie być gołosłownym -- oto pokój zawierający jabłko w Informie 6:

```nohighlight
Constant Story "Hello World";
Constant Headline "^An Interactive Example^";

Include "Parser";
Include "VerbLib";

[ Initialise;
  location = Living_Room;
  "Hello World";
];

Object Room "Living Room"
with description "A comfortably furnished living room.",
has  light;

Object -> Apple "juicy apple"
with name 'juicy' 'apple' 'fruit',
     description "Looks yummy.";
has  edible;

Include "Grammar";
```

A oto ten sam pokój w Informie 7:

```nohighlight
"Hello World" by Daniel Janus

The story headline is "An Interactive Example".

The Living Room is a room. "A comfortably furnished living room."
The apple is an edible thing in the Living Room. Description of
the apple is "Looks yummy." Understand "juicy", "apple" and
"fruit" as the apple.
```

Stąd już niedaleko do poezji. Na pewnym blogu ogłoszone zostało
wyzwanie: czy da się napisać rymujący się wiersz (choćby i z rymami w
stylu Nasha), który jest *jednocześnie* kompilującym się programem w
Informie 7? Okazało się, że tak. I to nie byle jaki wiersz, bo
sonet (a nawet metasonet -- jego tematem są bowiem sonety!)

Oto on:

```nohighlight
Will's Study is a room. The desk is here.
A hastily handwritten note is on it.
Description is "It's from your friend Shakespeare:
'I've gone to lunch. You'll have to write the sonnet.'"
Composing is an action applying to nothing.
The quill is a thing that is in the study.
Understand "write sonnet" as composing.
Description of the quill is "Old and cruddy".
Instead of composing when the player
has no quill, say "You have not got the quill."
Instead of composing, say "And... done. 'Heya',
says Will, returning. You say, 'Hello, Will!'
Says Shakespeare, 'Thank you for the time you've taken!
You really are a pal, Sir Francis Bacon.'"

-- Robin Johnson
```

(Przypis: akcje w Informie są motorem zmiany stanu świata; przebieg
akcji jest podzielony na kilka etapów -- dokładnie specyfikuje to
model świata, o którym pisałem wcześniej. Specyfikacja, że coś ma się
wykonać „zamiast” jakiejś akcji, oznacza interferencję w model na
pierwszym etapie, zanim zaczną działać normalne reguły.)

I moje rudymentarne tłumaczenie:

```nohighlight
Dębowe biurko stoi w gabinecie Willa.
Na nim kartka z notatką skreśloną starannie.
Opis: "Od przyjaciela twojego, Szekspira:
„Poszedłem na kolację, napisz sonet za mnie.”"
Pisanie jest to akcja bezargumentowa.
Gęsie pióro to przedmiot, co jest w gabinecie.
Opis pióra: "Możesz nim ubrać myśli w słowa."
Powiedz: "Nie możesz pisać, pióra nie masz przecie!"
zamiast pisania, gdy gracz nie posiada pióra.
Zamiast pisania, powiedz: "I oto - skończone.
Will powraca z kolacji z uśmiechem ponurym,
lecz wnet się rozpromienia, widząc gotów sonet.
Uśmiechasz się, Will także, chyląc głowę w skłonie:
„Dobry z Waści przyjaciel, Franciszku Baconie.”"
```

Oczywiście, to tylko połowa roboty, ta łatwiejsza -- drugą będzie
przeportowanie Informu 7 na język polski, tak aby powyższy wiersz
dało się skompilować i sobie pograć :-)
