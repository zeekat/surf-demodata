(ns nl.surf.generators
  (:require [clojure.data.generators :as gen]
            [clojure.spec.alpha :as s]))

(s/def ::name qualified-keyword?)
(s/def ::deps (s/coll-of ::name))

(s/def ::attr (s/keys :req-un [::name ::generator]
                      :opt-un [::deps]))
(s/def ::world (s/map-of keyword? (s/coll-of ::entity)))

(s/def ::gen-state (s/keys :req-un [::entity ::attr ::world]))

(s/def ::generator (s/fspec :args (s/cat :state ::gen-state)))

(s/def ::_id uuid?)
(s/def ::entity (s/keys :opt-un [::_id]))

(defn uuid
  [_]
  (gen/uuid))

(defn string
  [_]
  (gen/string))