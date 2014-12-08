(ns web-robot.core
  (require [clj-http.client :as client])
  (:gen-class))

(def host-regex #"^https?:\/\/[\w\.]+")
(def link-regex #"href=\"([\w:\/\.]+)")

(defn get-hosts-from-links [links]
  "Returns all hosts from list of links"
  (distinct  (keep #(re-find host-regex %1) links)))

(defn get-links-from-text [text]
  (distinct (map #(nth %1 1)
                 (re-seq link-regex text))))

(defn get-links-from-url [url]
  (println "Visiting " url)
  (get-links-from-text (:body (client/get url))))

(defn get-hosts-from-url [url]
  (get-hosts-from-links (get-links-from-url url)))

(defn visit [url visited]
  (let [hosts (get-hosts-from-url url)]
    (loop [[host & rest] hosts]
      (cond
       (nil? host) true
       (contains? visited host) (recur rest)
       :else (visit host (conj visited host))))))

(defn start-visiting [start-url]
  (let [visited-urls #{start-url}]
    (visit start-url visited-urls)))

(defn -main [args]
  (start-visiting "http://google.com"))
