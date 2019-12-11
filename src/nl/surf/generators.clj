(ns nl.surf.generators
  (:refer-clojure :exclude [char int format])
  (:require [clojure.core :as core]
            [clojure.data.generators :as gen]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [nl.surf.markov-chain :as mc]
            [yaml.core :as yaml]))

(defn uuid
  "Build a generator to pick UUIDs."
  []
  (fn [_]
    (gen/uuid)))

(defn string
  "Build a generator to pick random strings of characters."
  []
  (fn [_]
    (gen/string)))

(defn int
  "Build a generator to pick a integer uniformly distributed between `lo` and
  `hi`, both inclusive."
  ([]
   (fn [_]
     (gen/int)))
  ([lo hi]
   (fn [_]
     (gen/uniform lo (inc hi)))))

(defn char
  "Build a generator to pick a character uniformly distributed between `lo` and
  `hi`, both inclusive.  Without boundaries a printable ASCII character is
  picked."
  ([]
   (fn [_]
     (gen/printable-ascii-char)))
  ([lo hi]
   (fn [_]
     (core/char (gen/uniform (core/int lo) (inc (core/int hi)))))))

(defn one-of
  "Build a generator to pick one of `coll`."
  [coll]
  (fn [_]
    (apply gen/one-of coll)))

(defn weighted
  "Build a generator to pick one of weighted `m`.

  For example: with `{\"foo\" 2, \"bar\" 1}` there's a 2 in 3 chance `\"foo\"`
  will be picked."
  [m]
  (fn [_]
    (gen/weighted m)))

(defn format
  "Build a generator for strings using a `fmt` as in `clojure.core/format` and
  `arg-gens` generators as arguments."
  [fmt & arg-gens]
  (fn [world]
    (let [args (map (fn [gen] (gen world)) arg-gens)]
      (apply core/format fmt args))))

(defn text
  [corpus & {:keys [lines lookback] :or {lines 3, lookback 2}}]
  (let [state-space (mc/analyse-text corpus)]
    (fn [world]
      (->> (repeatedly #(mc/generate-text state-space))
           (take lines)
           (s/join "  ")))))

;;;; Helpers

(defn resource
  "Return the full text from the named resource `n`."
  [n]
  (-> n io/resource slurp))

(defn lines-resource
  "Returns a collection by reading named resource `n` and spliting lines."
  [n]
  (-> n io/resource io/reader line-seq))

(defn yaml-resource
  "Returns YAML data from resource `n`."
  [n]
  (-> n resource (yaml/parse-string :keywords false)))
