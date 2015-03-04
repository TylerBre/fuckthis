(ns fuckthis.routes.home
  (:require [compojure.core :refer :all]
            [fuckthis.layout :as layout]
            [fuckthis.util :as util]
            [clucy.core :as clucy]
            [clojure-watch.core :as clojure-watch]))

(use '[clojure.java.shell :only [sh]])

(defn home-page []
  (layout/render
   "home.html" {:content (util/md->html "/md/docs.md")}))

(def editor (str "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl"))

(defn about-page []
  (layout/render "about.html"))

(def file-path (str (System/getProperty "user.dir") "/files/"))

(defn open-file [filename]
  (sh editor (str file-path filename)))

(defn create-new [filename]
  (let [file (str file-path
                  (clojure.string/replace filename " " "-")
                  ".txt")]
    (sh "touch" file)
    (sh editor file)))

(def index (clucy/memory-index))

(defn reload-file [filename]
  (let [file (.getName (java.io.File. filename))
        add-to-index
          (fn [file content]
            (clucy/add index
                 (with-meta {:file file
                             :content (str (clojure.string/replace (first (clojure.string/split file #"\.")) "-" " ")
                                           "\n"
                                           content)}
                            {:file {:analyzed false}})))]
        (clucy/delete index {:file file})
    ;  I forgot what this is doing - Sean.
    (dorun (map 
             add-to-index
             (repeat file)
             (clojure.string/split (slurp filename) #"\n\n")
             ))))


(defn delete-file [filename]
  (let [filename (.getName (java.io.File. filename))]
    (clucy/delete index {:file filename})))

(def directory (clojure.java.io/file file-path))

(def files (map (memfn getName) (filter #(.isFile %) (file-seq directory))))

(dorun (map #(reload-file (str file-path %)) files))

(def watcher (Thread. (fn [] (clojure-watch/start-watch [{:path file-path
                                                     :event-types [:create :modify :delete]
                                                     :bootstrap (fn [path] (println "Starting to watch " path))
                                                     :callback (fn [event filename]
                                                                 (case event
                                                                   :create (reload-file filename)
                                                                   :modify (reload-file filename)
                                                                   :delete (delete-file filename)))
                                                     :options {:recursive true}}]))))

(.start watcher)

(defn search [search-text]
  (if (= search-text "") (layout/render "results.html" {})
  (layout/render "results.html" {:search-text search-text :results (clucy/search index search-text 10)})))

(defroutes home-routes
  (GET "/" [] (search ""))
  (GET "/search" [search-text] (search search-text))
  (GET "/create-new" [filename] (create-new filename))
  (GET "/open" [filename] (open-file filename)))

