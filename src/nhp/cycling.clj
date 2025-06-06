(ns nhp.cycling
  (:require
    [clojure.string :as str]
    [nhp.layout :as layout]))

(def trips
  [{:year 2016, :name "Podlasie", :route "Małkinia – Hajnówka – Białowieża – Czeremcha"}
   {:year 2017, :name "Mazury i Północne Mazowsze", :route "Giżycko – Ruciane-Nida – Warszawa", :days "22–25 czerwca"}
   {:year 2018, :name "Lubuskie i Szlak Stu Jezior", :route "Zielona Góra – Międzyrzecz – Poznań"}
   {:year 2019, :name "Donauradweg", :route "Pasawa – Wiedeń – Bratysława", :days "21–26 lipca", :link "https://www.facebook.com/media/set/?set=a.10205643572085270"}
   {:year 2020, :name "Pandemiczna weekendówka wiślana", :route "Warszawa – Wyszogród – Czerwińsk nad Wisłą – Warszawa"}
   {:year 2021, :name "Zielona Siódemka", :route "Warszawa – Gdańsk", :days "6–10 lipca", :link "https://www.facebook.com/daniel.janus/posts/pfbid0Jd8Hy75w3WTog2rPr64hcoxsM5UMVo2jQ8PP75Yp65myFLceynWh6NN7By7a5LwGl"}
   {:year 2022, :name "Szlak Łaby", :route "Dessau – Boisenburg", :days "26–30 czerwca", :link "https://www.facebook.com/media/set/?set=a.10209264146117358"}
   {:year 2023, :name "Wielka Brytania", :route "Land’s End – John o’Groats", :days "8–28 czerwca", :link "https://danieljanus.substack.com"}
   {:year 2024, :name "Velo Dunajec", :route "Szczawnica – Nowy Targ – Szczawnica – Tarnów", :days "28–30 czerwca", :link "https://www.facebook.com/daniel.janus/posts/pfbid02eJNTNXXXESprzzC6bLdtBsSTLbRsdxFayEtkoMedQRZ7HqjaKvUY8XSWuB7vN15Sl"}])

(defn content []
  [:div.main.content
   [:h1 "Może byś tak Daniel wpadł popedałować?"]
   [:p "Lubię jeździć na rowerze. Mam taką prywatną tradycję, że raz w roku wyjeżdżam na kilkudniową rowerową wyprawę solo."]
   [:p "Na tej stronie zbieram podsumowania i zdjęcia z wycieczek. Linki na razie prowadzą na Facebooka i Substacka, ale docelowo wszystko będzie zebrane tutaj."]
   [:div.timeline
    (for [{:keys [year name days route link]} (sort-by :year > trips)]
      [:div.timeline-item
       [:div.year year]
       [:div.items
        [:div.talk
         [:div.title (if link [:a {:href link} name] name)]
         [:div.event-line
          (str/join " • " (remove nil? [days route]))]]]])]])

(defn cycling []
  (layout/page {:content (content)}))

(defn generate []
  (layout/output-page "danieljanus.pl/cycling.html" (cycling)))
