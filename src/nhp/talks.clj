(ns nhp.talks
  (:require
    [nhp.layout :as layout]))

(def upcoming-talks
  [{:title "Golfing Clojure: Check Checker under 280 characters of Clojure"
    :event "Dutch Clojure Days"
    :event-url "https://clojuredays.org/"
    :city "Amsterdam"
    :date "2022-10-29"}])

(def past-talks
  [{:title "Skyscraper 0.3: Ascending to the next floor"
    :url "talks/2020-clojured"
    :event ":clojureD"
    :event-url "https://clojured.de/media/videos/videos-2020/"
    :city "Berlin"
    :date "2020-02-29"}
   {:title "NVC: protokół komunikacji człowiek–człowiek"
    :url "talks/2019-wrug"
    :event "WRUG"
    :event-url "https://wrug.eu"
    :city "Warsaw"
    :date "2019-06-19"}
   {:title "NVC: protokół komunikacji człowiek–człowiek"
    :url "talks/2019-warsawjs"
    :event "WarsawJS"
    :event-url "https://warsawjs.com"
    :city "Warsaw"
    :date "2019-05-08"}
   {:title "Skyscraper: Restructuring the web"
    :url "talks/reveal.js/2016-clojurex.html"
    :event "Clojure eXchange"
    :event-url "https://skillsmatter.com/conferences/7430-clojure-exchange-2016"
    :city "London"
    :date "2016-12-02"}
   {:title "A case study of natural language processing in Clojure"
    :url "talks/reveal.js/2016-euroclojure.html"
    :event "EuroClojure"
    :event-url "http://2016.euroclojure.org/"
    :city "Bratislava"
    :date "2016-10-26"}
   {:title "React in the Land of Parentheses: ClojureScript, Reagent, re-frame"
    :url "talks/2016-warsawjs"
    :event "WarsawJS"
    :event-url "https://warsawjs.com"
    :city "Warsaw"
    :date "2016-08-10"}
   {:title "Od nieustrukturyzowanych danych do przeszukiwalnego korpusu bogatego w metadane: Skyscraper, P4, Smyrna"
    :url "talks/reveal.js/2016-ipi.html"
    :event "ICS PAS Seminar"
    :event-url "http://zil.ipipan.waw.pl/seminarium"
    :city "Warsaw"
    :date "2016-05-09"}
   {:title "Kontynuacje w Rubym: ciąg dalszy nastąpił"
    :url "talks/reveal.js/2016-continuations.html"
    :event "4Developers"
    :event-url "http://2016.4developers.org.pl/en/"
    :city "Warsaw"
    :date "2016-04-11"}
   {:title "Wprowadzenie do Clojure dla Rubiowców"
    :url "talks/reveal.js/2015-wrug.html"
    :event "WRUG"
    :event-url "http://wrug.eu/"
    :city "Warsaw"
    :date "2015-02-02"}
   {:title "Smyrna: An easy Polish concordancer in Clojure"
    :url "talks/reveal.js/2014-lambdadays.html"
    :event "Lambda Days"
    :event-url "http://www.lambdadays.org/lambdadays2014"
    :city "Kraków"
    :date "2014-02-28"}
   {:title "Lithium: a small Clojure-inspired Lisp for the bare metal"
    :url "talks/reveal.js/2013-euroclojure.html"
    :event "EuroClojure"
    :event-url "http://euroclojure.org/"
    :city "Berlin"
    :date "2013-10-15"}
   {:title "Nawiasem mówiąc: Clojure"
    :url "talks/2011-java4people"
    :event "Java4People"
    :event-url "http://devcrowd.pl/"
    :city "Szczecin"
    :date "2011-04-16"}
   {:title "Of Caves, Programming, and Poetry: Domain-specific languages in interactive fiction"
    :url "talks/pdf/2008-lshift-if.pdf"
    :event "LShift"
    :city "London"
    :date "2008-08-20"}
   {:title "Obsługa błędów, czyli jak sobie radzić z prawem Murphy’ego"
    :url "talks/pdf/2008-aula-errorhandling.pdf"
    :event "TechAula"
    :event-url "https://aulapolska.pl/"
    :city "Warsaw"
    :date "2008-06-26"}])

(defn talk [{:keys [title event url event-url city date]}]
  [:div.talk
   [:div.title (layout/link url title)]
   [:div.event-line
    [:span.event (layout/link event-url event)]
    " • "
    [:span.city city]
    ", "
    [:span.date date]]])

(defn talks-list
  [talks]
  (if-not (seq talks)
    [:p "No upcoming talks."]
    [:div.timeline
     (for [[year group] (group-by #(subs (:date %) 0 4) talks)]
       [:div.timeline-item
        [:div.year year]
        [:div.items
         (map talk group)]])]))

(defn talks-content []
  [:div.main.content.talks
   [:h1 "Daniel Janus’s Talks"]
   [:section.upcoming
    [:h2 "Upcoming"]
    (talks-list upcoming-talks)]
   [:section.past
    [:h2 "Past"]
    (talks-list past-talks)]
   (layout/back)])

(defn talks-page []
  (layout/page {:title "DJ’s Talks"
                :content (talks-content)}))

(defn generate []
  (layout/output-page "danieljanus.pl/talks.html" (talks-page)))
