---
layout: theme:post
title: Informatyka jest gałęzią matematyki
date: 2011-05-07
comments: true
categories: informatyka matematyka
---

[Claygirl pisze na blogu GeekGirls][1], że wbrew powszechnemu
mniemaniu, informatyka to nie matematyka – i że informatykowi
znajomość matematyki jest potrzebna w bardzo ograniczonym
zakresie. Zdziwiłem się, bo jestem odmiennego zdania i zawsze wydawało
mi się, że to właśnie pogląd, że informatyka _jest_ częścią
matematyki, jest w mniejszości... Zapewne jednak większość klepaczy
kodu rzadko zadaje sobie to pytanie. Szkoda, bo warto stawiać
fundamentalne pytania o to, czym w istocie jest to, czym się
zajmujemy.

Temat jest mi bliski, postaram się więc w tym wpisie uzasadnić swoją
opinię. Żeby móc sensownie odpowiedzieć na nasze pytanie, powinniśmy
wpierw zdefiniować, co rozumiemy przez informatykę, a co przez
matematykę. Zaczniemy więc od definicji encyklopedycznych.

## Wikipedia dixit

> [Informatyka][2] – dyscyplina naukowa i techniczna zajmująca się
> przetwarzaniem informacji, a w tym technologiami przetwarzania
> informacji oraz technologiami wytwarzania systemów przetwarzających
> informacje.

Myślę, że większość z nas zgodziłaby się z takim opisem. Aplikacje
webowe, bazy danych, systemy operacyjne... czymkolwiek się zajmujemy,
zawsze ma to związek z przetwarzaniem informacji. A matematyka?

(Zanim zajrzymy do Wikipedii, zachęcam do pokuszenia się o własną
definicję. Może to przyprawić o ból głowy, bo jak objąć kilkoma
słowami całe to bogactwo abstrakcyjnych bytów, jakimi matematyka się
zajmuje?  Liczby, zbiory, klasy, relacje, funkcje, punkty, przestrzenie,
formuły, kategorie...)

> [Matematyka][3] – nauka dostarczająca narzędzi do otrzymywania
> ścisłych wniosków z przyjętych założeń, zatem dotycząca
> prawidłowości rozumowania.

## Ścisłość rozumowania i abstrakcje

Ta definicja w ogóle prześlizguje się nad owym bogactwem; jest
szersza, bardziej pierwotna i może budzić sprzeciw. Zdaje się
sugerować, że ilekroć myślimy o czymś – _czymkolwiek_ – w sposób
uporządkowany, zajmujemy się matematyką!  A jednak wielu matematyków
uważa, że to właśnie w myśleniu tkwi sedno ich dziedziny. Włodek
Holsztyński ujmuje to w swoim świetnym [eseju][4] jeszcze zwięźlej:

> Matematyka jest sztuką myślenia

Gdzie jednak przy takim podejściu postawić granicę pomiędzy matematyką
a innymi naukami?  Czy fizyka jest częścią matematyki, czy odrębną
dziedziną wiedzy, która tylko korzysta z metod matematycznych?
Wspominałem wcześniej o bytach abstrakcyjnych: wszyscy mamy mniej
więcej zbliżone pojęcie na temat tego, czym jest liczba 7, mimo że
taki obiekt nie istnieje fizycznie w otaczającym nas świecie. Wydaje
się, że abstrakcyjność jest wpisana w naturę matematyki, i można ją
dopisać do definicji. Wspomniany artykuł w Wikipedii zawiera inne
próby zdefiniowania tej dziedziny przez sławnych matematyków, z
których kilku o tym wspomina. Dirac: „Matematyka jest narzędziem
stworzonym specjalnie do wszelkich abstrakcyjnych koncepcji, i nie ma
ograniczeń dla jej potęgi w tym zakresie”.

A Poincaré mówi: „sztuka nadawania takich samych nazw różnym rzeczom”.
Innymi słowy, zdolność dostrzegania podobieństw i abstrahowania od
różnic. To jabłko jest czymś innym niż tamta gruszka, ale mają ze sobą
coś wspólnego: liczność – tak zapewne brzmiało pierwsze, doniosłe
odkrycie matematyczne.

## Matematyka w szkole

...jest tylko drobnym wycinkiem wszystkich tych działów. To oczywiste,
w końcu poświęca się jej ograniczoną ilość czasu. Ale za duży nacisk
kładzie się na ich przedstawienie, a za mały na _rozumienie_. Nie jest
ważne, żeby wiedzieć, ile jest siedem razy dziewięć. Ważne, żeby umieć
w razie potrzeby się zastanowić i dojść do prawidłowej odpowiedzi.
Rzadko pokazuje się dowody twierdzeń (i potem uczniowie panicznie boją
się słowa „udowodnij”), a szkoda!  Nie jest trudno zobaczyć, dlaczego
twierdzenie Pitagorasa jest prawdziwe, albo dlaczego mnożenie liczb
naturalnych jest przemienne. A tak właśnie ćwiczy się umiejętność
_myślenia_.

## A informatyka?

Clay pisze:

> Ale całe dziesiątki poziomów abstrakcji wyżej siedzą sobie systemy
> operacyjne i maszyny wirtualne z którymi większość informatyków
> widuje się najczęściej. Informatyków &ndash; programistów języków
> wysokopoziomowych (takich jak Java, C#, Ruby, Python etc), którzy z
> matematyką muszą się znać tylko po to, żeby umieć myśleć logicznie i
> na przykład oszacować czas działania napisanego właśnie programu.

Już samo słowo „poziomy abstrakcji” kojarzy się z tym, o czym pisałem
wcześniej. Oto mój program, mam w nim jakąś klasę.

``` java
public class Osoba {
    String imie, nazwisko;
    int wiek;
}
```

Ona modeluje jakiś obiekt rzeczywisty, ale sama w sobie jest równie
_abstrakcyjnym_ bytem jak liczba 7. Jest napisana w języku Java --
mogę sięgnąć po formalny opis jego gramatyki i semantyki i _ściśle opisać_,
czym jest.  I wreszcie, pisząc taki kod, _pomyślałem_ o tym,
co poszczególne osoby mają wspólnego i _abstrahowałem_ od nieistotnych
dla mnie różnic między nimi.

Czy nie tym właśnie zajmuje się matematyka? W jaki sposób mój program
albo język programowania jest mniej obiektem matematycznym, niż
trójkąt albo zbiór {4, 42}?

## Nie we wszystkim jesteśmy dobrzy

Pomijając terminologię, trzeba się zgodzić z tezą artykułu Clay:
fakt, że nie czujemy się mocni w jednej gałęzi matematyki (nazwijmy
ją „licealną”), wcale nie musi oznaczać, że nie rozwiniemy skrzydeł
w innej. Nikt nie jest omnibusem, nierozumienie czegoś nie jest
powodem do wstydu. Srinivasa Ramanujan, bodaj największy geniusz
matematyczny ubiegłego stulecia, wniósł gigantyczny wkład do teorii
liczb i wielu innych dziedzin... ale nie miał zielonego pojęcia,
co to jest funkcja zmiennej zespolonej!

Tak i my nie musimy tego wiedzieć, pisząc kod czy grzebiąc w bebechach
systemu. Miejmy jednak świadomość, że w istocie uprawiamy matematykę,
która jest sztuką myślenia – i cieszmy się tym.

 [1]: http://geekgirls.pl/2011/05/kobieta-tworzaca-internet/
 [2]: http://pl.wikipedia.org/wiki/Informatyka
 [3]: http://pl.wikipedia.org/wiki/Matematyka
 [4]: http://knol.google.com/k/wlodzimierz-holsztynski/matematyka-esej/1jxfhq4x4sw0j/93#
