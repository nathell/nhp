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
      [:li [:a {:href "https://smyrna.danieljanus.pl"} "Smyrna"]
       ", a concordancer for Polish"]
      [:li [:a {:href "wladcyslow"} "Word Champions"]
       ", a word game"]
      [:li [:a {:href "autosummarized-hn"} "Autosummarized Hacker News"]
       " (with GPT-3, no longer updated)"]
      [:li [:a {:href "https://github.com/nathell"} "GitHub projects"]]]]
    [:section.writing
     [:h2 "Writing"]
     [:ul
      [:li "Blogs: "
       [:a {:href "https://blog.danieljanus.pl"} "English"]
       ", "
       [:a {:href "https://plblog.danieljanus.pl"} "Polish"]]
      [:li "My "
       [:a {:href "https://danieljanus.substack.com"} "Land’s End to John o’ Groats 2023 bike tour newsletter"]
       " (Polish)"]
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
      [:li "Social media: "
       [:a {:href "https://mastodon.social/@nathell"} "Mastodon"]
       ", "
       [:a {:href "https://bsky.app/profile/nathell.bsky.social"} "Bluesky"]
       ", "
       [:a {:href "https://www.linkedin.com/in/nathell/"} "LinkedIn"]
       ", "
       [:a {:href "https://www.facebook.com/daniel.janus"} "Facebook"]]]]]])

(defn home-page []
  (layout/page {:content (home-content)}))

(defn generate []
  (layout/output-page "danieljanus.pl/index.html" (home-page)))
