(ns nhp.home
  (:require
    [nhp.layout :as layout]))

(defn home-content []
  [:div.main.home
   [:h1.header "Daniel Janus"]
   [:div.photo
    [:img {:src "img/dj.jpg"}]]
   [:div.content
    [:section.programming
     [:h2 "Programming"]
     [:ul
      [:li [:a {:href "http://smyrna.danieljanus.pl"} "Smyrna"]
       ", a concordancer for Polish"]
      [:li [:a {:href "wladcyslow"} "Word Champions"]
       ", a word game"]
      [:li [:a {:href "https://github.com/nathell"} "GitHub projects"]]
      [:li [:a {:href "documents/cv.pdf"} "My CV"]]]]
    [:section.writing
     [:h2 "Writing"]
     [:ul
      [:li "Blogs: "
       [:a {:href "http://blog.danieljanus.pl"} "English"]
       ", "
       [:a {:href "http://plblog.danieljanus.pl"} "Polish"]]
      [:li
       [:a {:href "translations"} "Poetry translations"]
       " (English → Polish)"]]]
    [:section.speaking
     [:h2 "Speaking"]
     [:ul
      [:li [:a {:href "talks.html"} "List of my talks"]]]]
    [:section.contact
     [:h2 "Contact"]
     [:ul
      [:li "Email me: " [:a {:href "mailto:dj@danieljanus.pl"} "dj@danieljanus.pl"]]
      [:li [:a {:href "https://twitter.com/nathell"} "Twitter"]
       ", "
       [:a {:href "https://www.linkedin.com/in/nathell/"} "LinkedIn"]]]]]])

(defn home-page []
  (layout/page {:content (home-content)}))

(defn generate []
  (layout/output-page "danieljanus.pl/index.html" (home-page)))
